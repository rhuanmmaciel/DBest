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
 * This class will display line numbers for a related text component. The text
 * component must use the same line height for each line. TextLineNumber
 * supports wrapped lines and will highlight the line number of the current line
 * in the text component.
 * <p>
 * This class was designed to be used as a component added to the row header of
 * a JScrollPane.
 */
public class JTextLineNumber extends JPanel implements CaretListener, DocumentListener, PropertyChangeListener {

    @Serial
    private static final long serialVersionUID = 1L;

    public static final float LEFT = 0.0f;

    public static final float CENTER = 0.5f;

    public static final float RIGHT = 1.0f;

    // Text component this TextTextLineNumber component is in sync with

    private final JTextPane component;

    // Properties that can be changed

    private boolean updateFont;

    private int borderGap;

    private Color currentLineForeground;

    private float digitAlignment;

    private int minimumDisplayDigits;

    // Keep history information to reduce the number of times the component
    // needs to be repainted

    private int lastDigits;

    private int lastHeight;

    private int lastLine;

    private HashMap<String, FontMetrics> fonts;

    /**
     * Create a line number component for a text component. This minimum display
     * width will be based on 3 digits.
     *
     * @param component the related text component
     */
    public JTextLineNumber(JTextPane component) {
        this(component, 3);
    }

    /**
     * Create a line number component for a text component.
     *
     * @param component            the related text component
     * @param minimumDisplayDigits the number of digits used to calculate the
     *                             minimum width of the component
     */
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

    /**
     * Gets the update font property
     *
     * @return the update font property
     */
    public boolean getUpdateFont() {
        return this.updateFont;
    }

    /**
     * Set the update font property. Indicates whether this Font should be updated
     * automatically when the Font of the related text component is changed.
     *
     * @param updateFont when true update the Font and repaint the line numbers,
     *                   otherwise just repaint the line numbers.
     */
    public void setUpdateFont(boolean updateFont) {
        this.updateFont = updateFont;
    }

    /**
     * Gets the border gap
     *
     * @return the border gap in pixels
     */
    public int getBorderGap() {
        return this.borderGap;
    }

    /**
     * The border gap is used in calculating the left and right insets of the
     * border. Default value is 5.
     *
     * @param borderGap the gap in pixels
     */
    public void setBorderGap(int borderGap) {
        this.borderGap = borderGap;
        this.lastDigits = 0;
        this.setPreferredWidth();
    }

    /**
     * Gets the current line rendering Color
     *
     * @return the Color used to render the current line number
     */
    public Color getCurrentLineForeground() {
        return this.currentLineForeground == null ? this.getForeground() : this.currentLineForeground;
    }

    /**
     * The Color used to render the current line digits. Default is Coolor.RED.
     *
     * @param currentLineForeground the Color used to render the current line
     */
    public void setCurrentLineForeground(Color currentLineForeground) {
        this.currentLineForeground = currentLineForeground;
    }

    /**
     * Gets the digit alignment
     *
     * @return the alignment of the painted digits
     */
    public float getDigitAlignment() {
        return this.digitAlignment;
    }

    /**
     * Specify the horizontal alignment of the digits within the component. Common
     * values would be:
     * <ul>
     * <li>TextLineNumber.LEFT
     * <li>TextLineNumber.CENTER
     * <li>TextLineNumber.RIGHT (default)
     * </ul>
     *
     * @param currentLineForeground the Color used to render the current line
     */
    public void setDigitAlignment(float digitAlignment) {
        float newDigitAlignment = digitAlignment;

        if (digitAlignment > 1.0f) {
            newDigitAlignment = 1.0f;
        } else if (digitAlignment < 0.0f) {
            newDigitAlignment = -1.0f;
        }

        this.digitAlignment = newDigitAlignment;
    }

    /**
     * Gets the minimum display digits
     *
     * @return the minimum display digits
     */
    public int getMinimumDisplayDigits() {
        return this.minimumDisplayDigits;
    }

    /**
     * Specify the mimimum number of digits used to calculate the preferred width of
     * the component. Default is 3.
     *
     * @param minimumDisplayDigits the number digits used in the preferred width
     *                             calculation
     */
    public void setMinimumDisplayDigits(int minimumDisplayDigits) {
        this.minimumDisplayDigits = minimumDisplayDigits;
        this.setPreferredWidth();
    }

    /**
     * Calculate the width needed to display the maximum line number
     */
    private void setPreferredWidth() {
        // Define the font to display the numbers.
        Font componentFont = this.component.getFont();
        Font font = new Font(componentFont.getFamily(), Font.BOLD, componentFont.getSize());

        Element root = this.component.getDocument().getDefaultRootElement();
        int lines = root.getElementCount();
        int digits = Math.max(String.valueOf(lines).length(), this.minimumDisplayDigits);

        // Update sizes when number of digits in the line number changes

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

    /**
     * Draw the line numbers
     */
    @Override
    public void paintComponent(Graphics g) {
        // Paint the background.
        Graphics2D g2d = (Graphics2D) g;
        g2d.setColor(this.component.getBackground());
        g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
        // Paint a vertical line at right.
        g2d.setStroke(new BasicStroke(1));
        g2d.setColor(new Color(0, 0, 0, 64));
        g2d.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1, this.getHeight());
        // Define the font.
        Font componentFont = this.component.getFont();
        Font font = new Font(componentFont.getFamily(), Font.BOLD, componentFont.getSize());

        // Determine the width of the space available to draw the line number

        FontMetrics fontMetrics = this.component.getFontMetrics(this.component.getFont());
        int iRightAlignement = this.getSize().width - 5;

        // Determine the rows to draw within the clipped bounds.

        Rectangle clip = g.getClipBounds();
        int rowStartOffset = this.component.viewToModel2D(new Point(0, clip.y));
        int endOffset = this.component.viewToModel2D(new Point(0, clip.y + clip.height));
        g2d.setFont(font);

        while (rowStartOffset <= endOffset) {
            try {
                if (this.isCurrentLine(rowStartOffset)) {
                    g2d.setColor(new Color(0, 0, 0, 160));
                } else {
                    g2d.setColor(new Color(0, 0, 0, 100));
                }

                // Get the line number as a string and then determine the
                // "X" and "Y" offsets for drawing the string.

                String lineNumber = this.getTextLineNumber(rowStartOffset);
                int stringWidth = g.getFontMetrics().stringWidth(lineNumber);
                int iX = iRightAlignement - stringWidth;
                int iY = this.getOffsetY(rowStartOffset, fontMetrics);
                g2d.drawString(lineNumber, iX, iY);

                // Move to the next row

                rowStartOffset = Utilities.getRowEnd(this.component, rowStartOffset) + 1;
            } catch (Exception e) {
                break;
            }
        }
    }

    /*
     * We need to know if the caret is currently positioned on the line we are about
     * to paint so the line number can be highlighted.
     */
    private boolean isCurrentLine(int rowStartOffset) {
        int caretPosition = this.component.getCaretPosition();
        Element root = this.component.getDocument().getDefaultRootElement();

        return root.getElementIndex(rowStartOffset) == root.getElementIndex(caretPosition);
    }

    /*
     * Get the line number to be drawn. The empty string will be returned when a
     * line of text has wrapped.
     */
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

    /*
     * Determine the Y offset for the current row
     */
    private int getOffsetY(int rowStartOffset, FontMetrics fontMetrics) throws BadLocationException {
        // Get the bounding rectangle of the row

        Rectangle2D r = this.component.modelToView2D(rowStartOffset);
        int lineHeight = fontMetrics.getHeight();
        int y = (int) (r.getY() + r.getHeight());
        int descent = 0;

        // The text needs to be positioned above the bottom of the bounding
        // rectangle based on the descent of the font(s) contained on the row.

        if (r.getHeight() == lineHeight) // default font is being used
        {
            descent = fontMetrics.getDescent();
        } else // We need to check all the attributes for font changes
        {
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

                FontMetrics fm = this.fonts.get(key);

                if (fm == null) {
                    Font font = new Font(fontFamily, Font.PLAIN, fontSize);
                    fm = this.component.getFontMetrics(font);
                    this.fonts.put(key, fm);
                }

                descent = Math.max(descent, fm.getDescent());
            }
        }

        return y - descent;
    }

    //
    // Implement CaretListener interface
    //
    @Override
    public void caretUpdate(CaretEvent e) {
        // Get the line the caret is positioned on

        int caretPosition = this.component.getCaretPosition();
        Element root = this.component.getDocument().getDefaultRootElement();
        int currentLine = root.getElementIndex(caretPosition);

        // Need to repaint so the correct line number can be highlighted

        if (this.lastLine != currentLine) {
            this.repaint();
            this.lastLine = currentLine;
        }
    }

    //
    // Implement DocumentListener interface
    //
    @Override
    public void changedUpdate(DocumentEvent e) {
        this.documentChanged();
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        this.documentChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        this.documentChanged();
    }

    /*
     * A document change may affect the number of displayed lines of text. Therefore
     * the lines numbers will also change.
     */
    private void documentChanged() {
        // View of the component has not been updated at the time
        // the DocumentEvent is fired

        SwingUtilities.invokeLater(() -> {
            try {
                int endPos = JTextLineNumber.this.component.getDocument().getLength();
                Rectangle2D rect = JTextLineNumber.this.component.modelToView2D(endPos);

                if (rect != null && rect.getY() != JTextLineNumber.this.lastHeight) {
                    JTextLineNumber.this.setPreferredWidth();
                    JTextLineNumber.this.repaint();
                    JTextLineNumber.this.lastHeight = (int) rect.getY();
                }
            } catch (BadLocationException ignored) {
            }
        });
    }

    //
    // Implement PropertyChangeListener interface
    //
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getNewValue() instanceof Font newFont) {
            if (this.updateFont) {
                this.setFont(newFont);
                this.lastDigits = 0;
                this.setPreferredWidth();
            } else {
                this.repaint();
            }
        }
    }
}
