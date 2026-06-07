package tppe.deduplication;

import java.util.*;

public class Case4Rule implements DeduplicationRule {

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
        if (name1 == null || name2 == null) {
            return false;
        }

        String[] tokens1 = name1.trim().split("\\s+");
        String[] tokens2 = name2.trim().split("\\s+");

        if (isGroupedAbbreviation(tokens1, tokens2) || isGroupedAbbreviation(tokens2, tokens1)) {
            return true;
        }

        if (name1.equals(name2)) {
            return true;
        }

        return false;
    }

    private boolean isGroupedAbbreviation(String[] abbreviatedTokens, String[] fullTokens) {
        if (abbreviatedTokens.length != 2) {
            return false;
        }

        String initialsToken = abbreviatedTokens[0];
        String abbreviatedSurname = abbreviatedTokens[1];

        if (!isAllUpperCase(initialsToken)) {
            return false;
        }

        int expectedInitials = fullTokens.length - 1;
        if (initialsToken.length() != expectedInitials) {
            return false;
        }

        String fullSurname = fullTokens[fullTokens.length - 1];
        if (!normalizeForComparison(abbreviatedSurname).equals(normalizeForComparison(fullSurname))) {
            return false;
        }

        for (int i = 0; i < initialsToken.length(); i++) {
            char initial = Character.toUpperCase(initialsToken.charAt(i));
            String fullToken = fullTokens[i];
            char fullInitial = Character.toUpperCase(normalizeForComparison(fullToken).charAt(0));
            if (initial != fullInitial) {
                return false;
            }
        }

        return true;
    }

    private boolean isAllUpperCase(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }
        for (char c : token.toCharArray()) {
            if (!Character.isUpperCase(c)) {
                return false;
            }
        }
        return true;
    }

    private String normalizeForComparison(String text) {
        if (text == null) {
            return "";
        }
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized.toLowerCase();
    }

    private int getCompletenessScore(String name) {
        if (name == null) {
            return 0;
        }
        String[] tokens = name.trim().split("\\s+");
        int score = 0;
        for (String token : tokens) {
            if (token.length() > 2) {
                score += 10;
            } else {
                score += 1;
            }
        }
        return score;
    }
}
