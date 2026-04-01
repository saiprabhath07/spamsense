package preprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TextProcessor {

    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "been", "but", "by",
            "for", "from", "had", "has", "have", "he", "her", "here", "hers",
            "him", "his", "i", "if", "in", "into", "is", "it", "its", "itself",
            "just", "me", "more", "most", "my", "no", "not", "now", "of", "on",
            "or", "our", "ours", "out", "she", "so", "than", "that", "the",
            "their", "theirs", "them", "then", "there", "these", "they", "this",
            "those", "to", "too", "up", "was", "we", "were", "what", "when",
            "where", "which", "who", "why", "will", "with", "you", "your", "yours"
    ));

    /**
     * Converts input text to lowercase, removes punctuation,
     * splits into tokens, and removes stopwords and empty tokens.
     *
     * @param text Raw input message
     * @return Clean token list
     */
    public List<String> process(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }

        String normalized = text.toLowerCase();
        normalized = normalized.replaceAll("[^a-z0-9\\s]", " ");
        normalized = normalized.replaceAll("\\s+", " ").trim();

        if (normalized.isEmpty()) {
            return new ArrayList<>();
        }

        String[] parts = normalized.split(" ");
        List<String> tokens = new ArrayList<>();

        for (String token : parts) {
            if (!token.isBlank() && !STOPWORDS.contains(token)) {
                tokens.add(token);
            }
        }

        return tokens;
    }
}