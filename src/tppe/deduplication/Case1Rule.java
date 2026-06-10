package tppe.deduplication;

import java.util.*;

public class Case1Rule implements DeduplicationRule {

    @Override
    public List<AuthorRecord> apply(List<AuthorRecord> records) {
        if (records == null) {
            throw new IllegalArgumentException("Records list cannot be null");
        }
        if (records.isEmpty()) {
            return new ArrayList<>();
        }

        List<List<Integer>> clusters = new ArrayList<>();

        for (int i = 0; i < records.size(); i++) {
            AuthorRecord record = records.get(i);
            String normalizedName = normalizeForGrouping(record.getName());
            boolean added = false;
            for (List<Integer> cluster : clusters) {
                AuthorRecord representative = records.get(cluster.get(0));
                String representativeNormalized = normalizeForGrouping(representative.getName());
                if (normalizedName.equals(representativeNormalized)) {
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

        String[] unifiedNames = new String[records.size()];
        for (List<Integer> cluster : clusters) {
            String goldName = chooseGoldName(records, cluster);
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

    private String normalizeForGrouping(String name) {
        if (name == null) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        normalized = normalized.replaceAll("[`'´’]", "'");
        normalized = normalized.toLowerCase();
        normalized = normalized.trim().replaceAll("\\s+", " ");
        return normalized;
    }

    private String chooseGoldName(List<AuthorRecord> records, List<Integer> cluster) {
        Map<String, Integer> votes = new LinkedHashMap<>();
        for (int idx : cluster) {
            String name = standardizeApostrophe(records.get(idx).getName());
            votes.merge(name, 1, Integer::sum);
        }

        String best = null;
        int bestVotes = -1;
        int bestDiacritics = -1;
        for (Map.Entry<String, Integer> entry : votes.entrySet()) {
            String name = entry.getKey();
            int count = entry.getValue();
            int diacritics = countDiacritics(name);
            if (count > bestVotes
                    || (count == bestVotes && diacritics > bestDiacritics)) {
                best = name;
                bestVotes = count;
                bestDiacritics = diacritics;
            }
        }
        return best;
    }

    private String standardizeApostrophe(String name) {
        if (name == null) {
            return null;
        }
        return name.replaceAll("[`´’]", "'");
    }

    private int countDiacritics(String name) {
        if (name == null) {
            return 0;
        }
        String decomposed = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD);
        int count = 0;
        for (int i = 0; i < decomposed.length(); i++) {
            if (Character.getType(decomposed.charAt(i)) == Character.NON_SPACING_MARK) {
                count++;
            }
        }
        return count;
    }
}
