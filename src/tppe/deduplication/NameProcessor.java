package tppe.deduplication;

import java.util.*;

public class NameProcessor {

    private static final Set<String> PARTICLES = new HashSet<>(Arrays.asList(
        "de", "da", "do", "das", "dos"
    ));

    public List<String> getNonParticleTokens(String name) {
        if (name == null) {
            return Collections.emptyList();
        }
        String clean = name.replace(".", "").toLowerCase();
        String[] rawTokens = clean.split("\\s+");
        List<String> tokens = new ArrayList<>();
        for (String t : rawTokens) {
            if (!t.isEmpty() && !PARTICLES.contains(t)) {
                tokens.add(t);
            }
        }
        return tokens;
    }
}
