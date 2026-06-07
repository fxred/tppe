package tppe.deduplication;

import java.util.*;

public class Case5Rule implements DeduplicationRule {

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
            String normalizedName = normalizeName(record.getName());
            boolean added = false;
            for (List<Integer> cluster : clusters) {
                AuthorRecord representative = records.get(cluster.get(0));
                String representativeNormalized = normalizeName(representative.getName());
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

        int[] unifiedIds = new int[records.size()];
        for (List<Integer> cluster : clusters) {
            int minId = Integer.MAX_VALUE;
            for (int idx : cluster) {
                int currentId = records.get(idx).getId();
                if (currentId < minId) {
                    minId = currentId;
                }
            }
            for (int idx : cluster) {
                unifiedIds[idx] = minId;
            }
        }

        List<AuthorRecord> output = new ArrayList<>(records.size());
        for (int i = 0; i < records.size(); i++) {
            output.add(new AuthorRecord(unifiedIds[i], records.get(i).getName()));
        }
        return output;
    }

    private String normalizeName(String name) {
        if (name == null) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(name, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        normalized = normalized.replaceAll("[`'´]", "'");
        normalized = normalized.toLowerCase();
        normalized = normalized.trim().replaceAll("\\s+", " ");
        return normalized;
    }
}
