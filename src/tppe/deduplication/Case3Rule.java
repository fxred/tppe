package tppe.deduplication;

import java.util.*;

public class Case3Rule implements DeduplicationRule {

    private static final Set<String> PARTICLES = new HashSet<>(Arrays.asList(
        "de", "da", "do", "das", "dos"
    ));

    @Override
    public List<AuthorRecord> apply(List<AuthorRecord> records) {
        if (records == null) {
            throw new IllegalArgumentException("Records list cannot be null");
        }
        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        String[] unifiedNames = new String[records.size()];
        List<List<Integer>> clusters = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            AuthorRecord record = records.get(i);
            boolean added = false;
            for (List<Integer> cluster : clusters) {
                AuthorRecord representative = records.get(cluster.get(0));
                if (isCompatible(record.getName(), representative.getName())) {
                    cluster.add(i);
                    added = true;
                    break;
                }
            }
            if (!added) {
                List<Integer> newCluster = new ArrayList<>();
                newCluster.add(i);
                clusters.add(newCluster);
            }
        }

        for (List<Integer> cluster : clusters) {
            String goldName = records.get(cluster.get(0)).getName();
            int maxScore = getCompletenessScore(goldName);
            for (int idx : cluster) {
                String name = records.get(idx).getName();
                int score = getCompletenessScore(name);
                if (score > maxScore) {
                    maxScore = score;
                    goldName = name;
                }
            }
            for (int idx : cluster) {
                unifiedNames[idx] = goldName;
            }
        }

        List<AuthorRecord> output = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); i++) {
            output.add(new AuthorRecord(records.get(i).getId(), unifiedNames[i]));
        }
        return output;
    }

    private boolean isCompatible(String name1, String name2) {
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

    private List<String> getNonParticleTokens(String name) {
        if (name == null) {
            return Collections.emptyList();
        }
        // Remove pontos e converte para minúsculas
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

    private int getCompletenessScore(String name) {
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
