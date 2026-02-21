package gui.frames.dsl;

import gui.utils.AutoCompletionEngine;
import gui.utils.AutoCompletionEngine.CompletionResult;
import gui.utils.AutoCompletionEngine.WordBounds;

import javax.swing.JTextPane;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

/**
 * Controlador para gerenciar interações de auto-completar e interface do usuário.
 * Controla atalhos de teclado, popup de opções e aplicação de completações.
 */
public class AutoCompletionController {

    private static final int MAX_COMPLETIONS = 6; // Limita número de opções no popup

    private final JTextPane textPane; // Do TextEditor
    private final KeyListener keyListener; // Para capturar eventos de teclado, registrado no TextEditor
    private final JPopupMenu completionPopup; // Popup com as opções
    private List<CompletionResult> currentCompletions;
    private int selectedIndex = 0;

    /**
     * Construtor do controlador de auto-completar.
     * @param textPane o painel de texto do editor
     */
    public AutoCompletionController(JTextPane textPane) {
        this.textPane = textPane;

        this.keyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent event) {
                handleKeyEvent(event);
            }
        };
        
        completionPopup = new JPopupMenu();
        completionPopup.setFocusable(true); // Para receber eventos
        completionPopup.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handlePopupKeyEvent(e);
            }
        });
    }

    /**
     * Obtém o listener de teclado para este controlador.
     * @return o listener que captura eventos de completação
     */
    public KeyListener getKeyListener() {
        return keyListener;
    }

    // Trata eventos de teclado no editor principal
    private void handleKeyEvent(KeyEvent event) {
        if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_ENTER) { // Completa direto
            prepareCompletionData();
            applyTopCompletion();
            event.consume();
        }
        else if (event.isControlDown() && event.getKeyCode() == KeyEvent.VK_SPACE) { // Mostra popup
            prepareCompletionData();
            showCompletionPopup();
            event.consume();
        }
    }

    // Trata navegação no popup
    private void handlePopupKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_TAB:
                cycleSelection(event.isShiftDown()); // Tab: próximo, Shift+Tab: anterior
                event.consume();
                break;
            case KeyEvent.VK_SHIFT:
                // Ignora a tecla Shift sozinha
                event.consume();
                break;
            case KeyEvent.VK_DOWN:
                cycleSelection(false); // Seta para baixo: próximo
                event.consume();
                break;
            case KeyEvent.VK_UP:
                cycleSelection(true); // Seta para cima: anterior
                event.consume();
                break;
            case KeyEvent.VK_ENTER:
                applySelectedCompletion(); // Aplica seleção atual
                closePopup();
                event.consume();
                break;
            case KeyEvent.VK_ESCAPE:
                closePopup(); // Cancela popup
                event.consume();
                break;
            default:
                closePopup();

                // Atualiza as completions conforme o usuário escreve
                char keyChar = event.getKeyChar();
                if (Character.isLetterOrDigit(keyChar) || "_.-".indexOf(keyChar) >= 0) {
                    try {
                        textPane.getDocument().insertString(textPane.getCaretPosition(), String.valueOf(keyChar), null);

                        prepareCompletionData();
                        if (currentCompletions.isEmpty()) {
                            textPane.requestFocus(); // Retorna foco para o editor quando não há completions
                        } else {
                            showCompletionPopup();
                        }
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                
                break;
        }
    }

    // Prepara dados baseado na posição atual do cursor
    private void prepareCompletionData() {
        try {
            Document doc = textPane.getDocument();
            String text = doc.getText(0, doc.getLength());
            int caretPosition = textPane.getCaretPosition();

            var completions = AutoCompletionEngine.findMatches(text, caretPosition);
            // Ordena por distância (melhor match primeiro)
            completions.sort((a, b) -> Integer.compare(a.distance(), b.distance()));

            // Limita o número de resultados
            currentCompletions = completions.subList(0, Math.min(MAX_COMPLETIONS, completions.size()));
            selectedIndex = 0; // Sempre inicia na primeira opção
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Aplica a completion selecionada atualmente
    private void applySelectedCompletion() {
        if (selectedIndex >= 0 && selectedIndex < currentCompletions.size()) {
            applyCompletion(currentCompletions.get(selectedIndex).completion());
        }
    }

    // Aplica a melhor completion (primeira da lista)
    private void applyTopCompletion() {
        if (!currentCompletions.isEmpty()) {
            applyCompletion(currentCompletions.get(0).completion());
        }
    }

    // Aplica a completion substituindo a palavra atual
    private void applyCompletion(String completion) {
        try {
            Document doc = textPane.getDocument();
            String text = doc.getText(0, doc.getLength());
            int caretPosition = textPane.getCaretPosition();
            
            // Calcula os limites da palavra atual para substituição
            WordBounds wordBounds = AutoCompletionEngine.getWordBoundsAt(text, caretPosition);
            
            // Remove palavra atual e insere a completion
            doc.remove(wordBounds.start(), wordBounds.end() - wordBounds.start());
            doc.insertString(wordBounds.start(), completion, null);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Mostra o popup na posição do cursor
    private void showCompletionPopup() {
        completionPopup.removeAll(); // Limpa itens anteriores

        // Adiciona cada opção como item do menu
        for (int i = 0; i < currentCompletions.size(); i++) {
            JMenuItem item = new JMenuItem(currentCompletions.get(i).completion());

            // Handler para clique direto no item
            final int index = i;
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    selectedIndex = index;
                    applySelectedCompletion();
                    closePopup();
                }
            });

            completionPopup.add(item);
        }

        updateSelection(); // Destaca a primeira opção

        try {
            // Posiciona popup logo abaixo do cursor
            Rectangle caretBounds = textPane.modelToView(textPane.getCaretPosition());
            completionPopup.show(textPane, caretBounds.x, caretBounds.y + caretBounds.height);
            completionPopup.requestFocus(); // Solicita foco para receber eventos de teclado
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    // Navega entre as opções do popup
    private void cycleSelection(boolean backwards) {
        if (currentCompletions != null && !currentCompletions.isEmpty()) {
            selectedIndex = backwards ?
                (selectedIndex - 1 + currentCompletions.size()) % currentCompletions.size() : // Para trás
                (selectedIndex + 1) % currentCompletions.size(); // Para frente
            updateSelection();
        }
    }

    // Atualiza destaque da opção selecionada
    private void updateSelection() {
        if (completionPopup != null) {
            for (int i = 0; i < completionPopup.getComponentCount(); i++) {
                JMenuItem item = (JMenuItem) completionPopup.getComponent(i);
                item.setArmed(i == selectedIndex); // setArmed destaca visualmente
            }
            completionPopup.repaint();
        }
    }

    // Fecha o popup
    private void closePopup() {
        if (completionPopup != null) {
            completionPopup.setVisible(false); // Apenas oculta, não destrói
        }
    }
}
