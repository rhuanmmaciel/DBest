package gui.utils;

import enums.OperationType;
import controllers.MainController;
import entities.cells.TableCell;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Engine for auto-completion functionality.
 */
public class AutoCompletionEngine {
    // Configurações de tolerância: distância máxima permitida e tamanho mínimo do prefixo
    private static final int MAX_EDIT_DISTANCE = 2;

    // Contextos possíveis para completar: operações, argumentos (colunas), fontes (tabelas)
    private enum CompletionContext {
        OPERATIONS,   // Fora de delimitadores
        ARGUMENTS,    // Dentro de [] - nomes de colunas
        SOURCES,      // Dentro de () - nomes de tabelas
    }

    public record CompletionResult(String completion, int distance) {}
    
    public record WordBounds(int start, int end) {}

    /**
     * Finds matching completions for the word being typed at the given caret position and context.
     * @param text the full text content to analyze
     * @param caretPosition the current cursor position
     * @return list of completion results sorted by relevance
     */
    public static ArrayList<CompletionResult> findMatches(String text, int caretPosition) {
        ArrayList<CompletionResult> completions = new ArrayList<>();

        if (text == null) {
            return completions;
        }

        // Extrai a palavra sob o cursor
        WordBounds bounds = getWordBoundsAt(text, caretPosition);
        String prefix = text.substring(bounds.start(), bounds.end());

        String normalizedPrefix = prefix.trim().toLowerCase();

        // Determina contexto baseado na posição dos delimitadores
        CompletionContext completionContext = determineCompletionContext(text, caretPosition);

        // Adiciona específicos do contexto atual
        if (completionContext == CompletionContext.ARGUMENTS) {
            addColumnCompletions(completions, normalizedPrefix);
        } else if (completionContext == CompletionContext.SOURCES) {
            addTableCompletions(completions, normalizedPrefix);
        }

        // Operações estão sempre disponíveis
        addOperationCompletions(completions, normalizedPrefix);

        return completions;
    }

    /**
     * Gets the word boundaries at the given position.
     * @param text the text to analyze
     * @param position the position to find word boundaries for
     * @return word boundaries containing start and end positions
     */
    public static WordBounds getWordBoundsAt(String text, int position) {
        if (text == null || text.isEmpty() || position < 0) {
            return new WordBounds(0, 0);
        }
        
        int pos = Math.min(position, text.length());
        
        // Encontra início e fim da palavra (incluindo _, ., -)
        int start = pos - 1;
        while (start >= 0 && (Character.isLetterOrDigit(text.charAt(start)) || "_.-".indexOf(text.charAt(start)) >= 0)) {
            start--;
        }
        start++;
        
        int end = pos;
        while (end < text.length() && (Character.isLetterOrDigit(text.charAt(end)) || "_.-".indexOf(text.charAt(end)) >= 0)) {
            end++;
        }
        
        return new WordBounds(start, end);
    }

    // Usa uma pilha para determinar o contexto
    private static CompletionContext determineCompletionContext(String text, int caretPosition) {
        if (text == null || text.isEmpty() || caretPosition <= 0) {
            return CompletionContext.OPERATIONS;
        }
        
        Stack<Character> stack = new Stack<>();
        int pos = Math.min(caretPosition, text.length());

        // Processa caracteres até a posição do cursor
        for (int i = 0; i < pos; i++) {
            switch (text.charAt(i)) {
                case '[':
                    stack.push('[');
                    break;
                case ']':
                    if (!stack.isEmpty() && stack.peek() == '[') {
                        stack.pop();
                    } else {
                        // Inválido
                        return CompletionContext.OPERATIONS;
                    }
                    break;
                case '(':
                    stack.push('(');
                    break;
                case ')':
                    if (!stack.isEmpty() && stack.peek() == '(') {
                        stack.pop();
                    } else {
                        // Inválido
                        return CompletionContext.OPERATIONS;
                    }
                    break;
            }
        }
        
        // Retorna o contexto baseado no delimitador mais recente ainda aberto
        if (!stack.isEmpty()) {
            if (stack.peek() == '[') {
                return CompletionContext.ARGUMENTS;
            } else if (stack.peek() == '(') {
                return CompletionContext.SOURCES;
            }
        }
        
        return CompletionContext.OPERATIONS;
    }

    // Adiciona nomes de operações do enum OperationType
    private static void addOperationCompletions(ArrayList<CompletionResult> completions, String normalizedPrefix) {
        for (OperationType operation : OperationType.values()) {
            String normalizedOperationName = operation.name.trim().toLowerCase();
            int distance = calculatePrefixLevenshteinDistance(normalizedPrefix, normalizedOperationName);

            if (distance <= MAX_EDIT_DISTANCE) {
                completions.add(new CompletionResult(operation.dslSyntax, distance));
            }
        }
    }

    // Adiciona nomes de colunas das tabelas ativas (simples e qualificados)
    private static void addColumnCompletions(ArrayList<CompletionResult> completions, String normalizedPrefix) {
        for (TableCell tableCell : MainController.getTables().values()) {
            for (int i = 0; i < tableCell.getColumns().size(); i++) {
                // Nome simples da coluna (ex: "id", "name")
                String normalizedColumnName = tableCell.getColumnNames().get(i).trim().toLowerCase();
                // Nome qualificado (ex: "tabela.coluna")
                String qualifiedColumnName = tableCell.getColumnSourcesAndNames().get(i);
                String normalizedQualifiedName = qualifiedColumnName.trim().toLowerCase();

                // Para considerar "col" ou "tab" como prefixo de "tabela.coluna"
                int distance = Math.min(
                    calculatePrefixLevenshteinDistance(normalizedPrefix, normalizedColumnName),
                    calculatePrefixLevenshteinDistance(normalizedPrefix, normalizedQualifiedName)
                );

                if (distance <= MAX_EDIT_DISTANCE) {
                    completions.add(new CompletionResult(qualifiedColumnName, distance));
                }
            }

        }
    }

    // Adiciona nomes de tabelas ativas
    private static void addTableCompletions(ArrayList<CompletionResult> completions, String normalizedPrefix) {
        for (String tableName : MainController.getTables().keySet()) {
            String normalizedTableName = tableName.trim().toLowerCase();
            int distance = calculatePrefixLevenshteinDistance(normalizedPrefix, normalizedTableName);

            if (distance <= MAX_EDIT_DISTANCE) {
                completions.add(new CompletionResult(tableName, distance));
            }
        }
    }

    // Calcula distância de edição entre prefixos usando algoritmo de programação dinâmica
    private static int calculatePrefixLevenshteinDistance(String prefix, String target) {
        if (prefix.isEmpty()) return 0;
        if (target.isEmpty()) return prefix.length();

        // Compara apenas o prefixo da palavra alvo com mesmo tamanho
        int compareLength = Math.min(prefix.length(), target.length());
        String targetPrefix = target.substring(0, compareLength);

        // Matriz de programação dinâmica para calcular distância mínima
        int[][] dp = new int[prefix.length() + 1][compareLength + 1];

        // Inicializa primeira linha e coluna
        for (int i = 0; i <= prefix.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= compareLength; j++) {
            dp[0][j] = j;
        }

        // Preenche matriz calculando custo mínimo de transformação
        for (int i = 1; i <= prefix.length(); i++) {
            for (int j = 1; j <= compareLength; j++) {
                if (prefix.charAt(i - 1) == targetPrefix.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1]; // Caracteres iguais, sem custo
                } else {
                    dp[i][j] = 1 + Math.min(
                        Math.min(dp[i - 1][j], dp[i][j - 1]),     // Inserção ou remoção
                        dp[i - 1][j - 1]                          // Substituição
                    );
                }
            }
        }

        return dp[prefix.length()][compareLength];
    }
}
