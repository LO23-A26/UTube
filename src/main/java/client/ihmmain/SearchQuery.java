package client.ihmmain;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Requête de recherche analysée.
 * Exemple : "chat #drôle #animaux author:LeZ31"
 *   → text="chat", tags={drôle, animaux}, author="LeZ31"
 */
record SearchQuery(String text, Set<String> tags, String author) {

    private static final String AUTHOR_PREFIX = "author:";

    SearchQuery {
        text = text == null ? "" : text.strip();
        tags = tags == null ? Set.of() : Set.copyOf(tags);
        author = author == null ? "" : author.strip();
    }

    static SearchQuery parse(String raw) {
        if (raw == null || raw.isBlank()) {
            return new SearchQuery("", Set.of(), "");
        }
        Set<String> tags = new HashSet<>();
        StringJoiner text = new StringJoiner(" ");
        String author = "";

        for (String token : raw.strip().split("\\s+")) {
            if (token.startsWith("#") && token.length() > 1) {
                tags.add(token.substring(1).toLowerCase(Locale.ROOT));
            } else if (isAuthorToken(token)) {
                author = token.substring(AUTHOR_PREFIX.length()); // le dernier author: l'emporte
            } else {
                text.add(token);
            }
        }
        return new SearchQuery(text.toString(), tags, author);
    }

    /** "author:xxx", insensible à la casse du préfixe ("Author:", "AUTHOR:"...). */
    private static boolean isAuthorToken(String token) {
        return token.length() > AUTHOR_PREFIX.length()
                && token.regionMatches(true, 0, AUTHOR_PREFIX, 0, AUTHOR_PREFIX.length());
    }

    boolean hasAuthor() {
        return !author.isEmpty();
    }

    boolean isEmpty() {
        return text.isEmpty() && tags.isEmpty() && author.isEmpty();
    }
}