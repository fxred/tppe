package tppe.deduplication;

import java.util.*;

public class NameProcessor {

    private static final Set<String> PARTICLES = new HashSet<>(Arrays.asList(
        "de", "da", "do", "das", "dos"
    ));

    public boolean isCompatible(String name1, String name2) {
        List<String> tokens1 = getNonParticleTokens(name1);
        List<String> tokens2 = getNonParticleTokens(name2);

        if (tokens1.size() != tokens2.size()) {
            return false;
        }

        for (int i = 0; i < tokens1.size(); i++) {
            String t1 = tokens1.get(i);
            String t2 = tokens2.get(i);

            if (t1.equals(t2)) {
                continue;
            }

            if (t1.length() == 1 && t2.startsWith(t1)) {
                continue;
            }

            if (t2.length() == 1 && t1.startsWith(t2)) {
                continue;
            }

            return false;
        }

        return true;
    }

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

    public int getCompletenessScore(String name) {
        if (name == null) {
            return 0;
        }
        String clean = name.replace(".", "").toLowerCase();
        String[] rawTokens = clean.split("\\s+");

        int fullTokensCount = 0;
        int particleCount = 0;

        for (String t : rawTokens) {
            if (t.isEmpty()) {
                continue;
            }
            if (PARTICLES.contains(t)) {
                particleCount++;
            } else {
                if (t.length() > 1) {
                    fullTokensCount++;
                }
            }
        }

        return (fullTokensCount * 10) + particleCount;
    }
}
