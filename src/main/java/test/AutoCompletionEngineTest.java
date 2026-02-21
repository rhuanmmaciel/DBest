package test;

import gui.utils.AutoCompletionEngine;
import gui.utils.AutoCompletionEngine.CompletionResult;
import gui.utils.AutoCompletionEngine.WordBounds;
import java.util.List;

public class AutoCompletionEngineTest {
    
    public static void main(String[] args) {
        System.out.println("Testing AutoCompletionEngine...");

        List<CompletionResult> results;
        WordBounds bounds;

        // Testa correspondências básicas de prefixos
        results = AutoCompletionEngine.findMatches("sel", 3);
        assert !results.isEmpty() && results.stream().anyMatch(r -> r.completion().contains("selection")) : "Failed: sel -> selection";

        results = AutoCompletionEngine.findMatches("proj", 4);
        assert !results.isEmpty() && results.stream().anyMatch(r -> r.completion().contains("projection")) : "Failed: proj -> projection";

        results = AutoCompletionEngine.findMatches("join", 4);
        assert !results.isEmpty() && results.stream().anyMatch(r -> r.completion().contains("join")) : "Failed: join -> join";

        // Testa correção de erros de digitação
        results = AutoCompletionEngine.findMatches("selecton", 8);
        assert !results.isEmpty() && results.stream().anyMatch(r -> r.completion().contains("selection")) : "Failed: selecton -> selection";

        results = AutoCompletionEngine.findMatches("jion", 4);
        assert !results.isEmpty() && results.stream().anyMatch(r -> r.completion().contains("join")) : "Failed: jion -> join";

        // Testa casos onde não deve haver correspondência
        results = AutoCompletionEngine.findMatches("x", 1);
        assert results.isEmpty() : "Failed: single character should return empty list";

        results = AutoCompletionEngine.findMatches(null, 0);
        assert results.isEmpty() : "Failed: null should return empty list";

        // Testa limites da palavra sob o cursor
        bounds = AutoCompletionEngine.getWordBoundsAt("projection[args](scan)", 10);
        assert bounds.start() == 0 && bounds.end() == 10 : "Failed: word bounds for 'projection'";

        bounds = AutoCompletionEngine.getWordBoundsAt("projection[args](scan)", 22);
        assert bounds.start() == 17 && bounds.end() == 21 : "Failed: word bounds for 'scan'";

        System.out.println("All tests passed!");
    }
}
