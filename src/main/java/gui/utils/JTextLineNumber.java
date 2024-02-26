package gui.utils;

import java.awt.*;
import java.awt.geom.Rectangle2D;

import java.beans.*;

import java.io.Serial;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;

/**
 * <a href="https://stackoverflow.com/a/52326451">Source</a>
 */
public class JTextLineNumber extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final float LEFT = 0.0f;

    public static final float CENTER = 0.5f;

    public static final float RIGHT = 1.0f;

    private final JTextPane component;

    private boolean updateFont;

    private int borderGap;

    private Color currentLineForeground;

    private float digitAlignment;

    private int minimumDisplayDigits;

    private int lastDigits;

    private int lastHeight;

    private int lastLine;

    private HashMap<String, FontMetrics> fonts;

    public JTextLineNumber(JTextPane component) {
        this(component, 3);
    }

    public JTextLineNumber(JTextPane component, int minimumDisplayDigits) {
        this.component = component;
        this.setBorder(null);

        // setFont(component.getFont());

        this.setBorder(null);
        this.setBorderGap(5);
        this.setCurrentLineForeground(Color.BLACK);
        this.setDigitAlignment(RIGHT);
        this.setMinimumDisplayDigits(minimumDisplayDigits);

        component.getDocument().addDocumentListener(this);
        component.addCaretListener(this);
        component.addPropertyChangeListener("font", this);
    }

    public boolean getUpdateFont() {
        return this.updateFont;
    }

    public void setUpdateFont(boolean updateFont) {
        this.updateFont = updateFont;
    }

    public int getBorderGap() {
        return this.borderGap;
    }

    public void setBorderGap(int borderGap) {
        this.borderGap = borderGap;
        this.lastDigits = 0;
        this.setPreferredWidth();
    }

    public Color getCurrentLineForeground() {
        return this.currentLineForeground == null ? this.getForeground() : this.currentLineForeground;
    }

    public void setCurrentLineForeground(Color currentLineForeground) {
        this.currentLineForeground = currentLineForeground;
    }

    public float getDigitAlignment() {
        return this.digitAlignment;
    }

    public void setDigitAlignment(float digitAlignment) {
        float newDigitAlignment = digitAlignment;

        if (digitAlignment > 1.0f) {
            newDigitAlignment = 1.0f;
        } else if (digitAlignment < 0.0f) {
            newDigitAlignment = -1.0f;
        }

        this.digitAlignment = newDigitAlignment;
    }

    public int getMinimumDisplayDigits() {
        return this.minimumDisplayDigits;
    }

    public void setMinimumDisplayDigits(int minimumDisplayDigits) {
        this.minimumDisplayDigits = minimumDisplayDigits;
        this.setPreferredWidth();
    }

    private void setPreferredWidth() {
        Font componentFont = this.component.getFont();
        Font font = new Font(componentFont.getFamily(), Font.BOLD, componentFont.getSize());

        Element root = this.component.getDocument().getDefaultRootElement();
        int lines = root.getElementCount();
        int digits = Math.max(String.valueOf(lines).length(), this.minimumDisplayDigits);

        if (this.lastDigits != digits) {
            this.lastDigits = digits;
            FontMetrics fontMetrics = this.getFontMetrics(font);
            int iPreferredWidth = 0;

            if (digits <= 1) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("0");
            } else if (digits == 2) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("00");
            } else if (digits == 3) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("000");
            } else if (digits == 4) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("0000");
            } else if (digits == 5) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("00000");
            } else if (digits == 6) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("000000");
            } else if (digits == 7) {
                iPreferredWidth = 10 + fontMetrics.stringWidth("0000000");
            } else {
                iPreferredWidth = 10 + fontMetrics.stringWidth("00000000");
            }

            Dimension dimension = new Dimension(iPreferredWidth, 0);
            this.setPreferredSize(dimension);
            this.setSize(dimension);
        }
    }

    @Override
    public void paintComponent(Graphics graphics) {
        Graphics2D graphics2D = (Graphics2D) graphics;
        graphics2D.setColor(this.component.getBackground());
        graphics2D.fillRect(0, 0, this.getWidth(), this.getHeight());

        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.setColor(new Color(0, 0, 0, 64));
        graphics2D.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight());

        Font componentFont = this.component.getFont();
        Font font = new Font(componentFont.getFamily(), Font.BOLD, componentFont.getSize());

        FontMetrics fontMetrics = this.component.getFontMetrics(this.component.getFont());

        int iRightAlignement = this.getSize().width - 5;

        Rectangle clip = graphics.getClipBounds();
        int rowStartOffset = this.component.viewToModel2D(new Point(0, clip.y));
        int endOffset = this.component.viewToModel2D(new Point(0, clip.y + clip.height));

        graphics2D.setFont(font);

        while (rowStartOffset <= endOffset) {
            try {
                if (this.isCurrentLine(rowStartOffset)) {
                    graphics2D.setColor(new Color(0, 0, 0, 160));
                } else {
                    graphics2D.setColor(new Color(0, 0, 0, 100));
                }

                String lineNumber = this.getTextLineNumber(rowStartOffset);
                int stringWidth = graphics.getFontMetrics().stringWidth(lineNumber);
                int x = iRightAlignement - stringWidth;
                int y = this.getOffsetY(rowStartOffset, fontMetrics);
                graphics2D.drawString(lineNumber, x, y);

                rowStartOffset = Utilities.getRowEnd(this.component, rowStartOffset) + 1;
            } catch (Exception exception) {
                break;
            }
        }
    }

    private boolean isCurrentLine(int rowStartOffset) {
        int caretPosition = this.component.getCaretPosition();
        Element root = this.component.getDocument().getDefaultRootElement();

        return root.getElementIndex(rowStartOffset) == root.getElementIndex(caretPosition);
    }

    protected String getTextLineNumber(int rowStartOffset) {
        Element root = this.component.getDocument().getDefaultRootElement();
        int index = root.getElementIndex(rowStartOffset);
        Element line = root.getElement(index);

        if (line.getStartOffset() == rowStartOffset) {
            return String.valueOf(index + 1);
        } else {
            return "";
        }
    }

    private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) throws BadLocationException {
        Rectangle2D r = this.component.modelToView2D(rowStartOffset);
        int lineHeight = fontMetrics.getHeight();
        int y = (int) (r.getY() + r.getHeight());
        int descent = 0;

        if (r.getHeight() == lineHeight) {
            descent = fontMetrics.getDescent();
        } else {
            if (this.fonts == null) {
                this.fonts = new HashMap<>();
            }

            Element root = this.component.getDocument().getDefaultRootElement();
            int index = root.getElementIndex(rowStartOffset);
            Element line = root.getElement(index);

            for (int i = 0; i < line.getElementCount(); i++) {
                Element child = line.getElement(i);
                AttributeSet as = child.getAttributes();
                String fontFamily = (String) as.getAttribute(StyleConstants.FontFamily);
                Integer fontSize = (Integer) as.getAttribute(StyleConstants.FontSize);
                String key = fontFamily + fontSize;

                this.fonts.computeIfAbsent(key, k -> this.component.getFontMetrics(new Font(fontFamily, Font.PLAIN, fontSize)));

                FontMetrics fm = this.fonts.get(key);

                descent = Math.max(descent, fm.getDescent());
            }
        }

        return y - descent;
    }

    @Override
    public void caretUpdate(CaretEvent event) {
        int caretPosition = this.component.getCaretPosition();
        Element root = this.component.getDocument().getDefaultRootElement();
        int currentLine = root.getElementIndex(caretPosition);

        if (this.lastLine != currentLine) {
            this.repaint();
            this.lastLine = currentLine;
        }
    }

    @Override
    public void changedUpdate(DocumentEvent event) {
        this.documentChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent event) {
        this.documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent event) {
        this.documentChanged();
    }

    private void documentChanged() {

        SwingUtilities.invokeLater(() -> {
            try {
                int endPosition = JTextLineNumber.this.component.getDocument().getLength();
                Rectangle2D rectangle2D = JTextLineNumber.this.component.modelToView2D(endPosition);

                if (rectangle2D != null && rectangle2D.getY() != JTextLineNumber.this.lastHeight) {
                    JTextLineNumber.this.setPreferredWidth();
                    JTextLineNumber.this.repaint();
                    JTextLineNumber.this.lastHeight = (int) rectangle2D.getY();
                }
            } catch (BadLocationException ignored) {

            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (!(event.getNewValue() instanceof Font newFont)) return;

        if (this.updateFont) {
            this.setFont(newFont);
            this.lastDigits = 0;
            this.setPreferredWidth();
        } else {
            this.repaint();
        }
    }
}
