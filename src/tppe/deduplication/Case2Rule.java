package tppe.deduplication;

import java.util.*;

public class Case2Rule implements DeduplicationRule {

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
        if (normalize(name1).equals(normalize(name2))) {
            return true;
        }
        return matchesAbbreviatedForm(name1, name2)
                || matchesAbbreviatedForm(name2, name1)
                || matchesInitialsFirst(name1, name2)
                || matchesInitialsFirst(name2, name1);
    }

    // Formato: "Sobrenome I. I." (sobrenome primeiro, iniciais depois)
    private boolean matchesAbbreviatedForm(String abbreviated, String full) {
        String[] abbrTokens = tokenize(abbreviated);
        String[] fullTokens  = tokenize(full);

        if (abbrTokens.length < 2 || fullTokens.length < 2) {
            return false;
        }

        String abbrSurname = stripDots(abbrTokens[0]);
        String fullSurname  = stripDots(fullTokens[fullTokens.length - 1]);

        if (!normalize(abbrSurname).equals(normalize(fullSurname))) {
            return false;
        }

        String[] initials    = Arrays.copyOfRange(abbrTokens, 1, abbrTokens.length);
        String[] firstNames  = Arrays.copyOfRange(fullTokens, 0, fullTokens.length - 1);

        // Filtra partículas do nome completo (de, da, do, das, dos)
        List<String> meaningfulFirstNames = new ArrayList<>();
        for (String t : firstNames) {
            if (!isParticle(t)) {
                meaningfulFirstNames.add(t);
            }
        }

        if (initials.length != meaningfulFirstNames.size()) {
            return false;
        }

        for (int i = 0; i < initials.length; i++) {
            String initial = stripDots(initials[i]);
            if (initial.length() != 1) {
                return false;
            }
            char expectedInitial = normalize(meaningfulFirstNames.get(i)).charAt(0);
            char actualInitial   = normalize(initial).charAt(0);
            if (actualInitial != expectedInitial) {
                return false;
            }
        }

        return true;
    }

    // Formato: "I. I. Sobrenome" (iniciais primeiro, sobrenome no final)
    private boolean matchesInitialsFirst(String abbreviated, String full) {
        String[] abbrTokens = tokenize(abbreviated);
        String[] fullTokens  = tokenize(full);

        if (abbrTokens.length < 2 || fullTokens.length < 2) {
            return false;
        }

        String abbrSurname = stripDots(abbrTokens[abbrTokens.length - 1]);
        String fullSurname  = stripDots(fullTokens[fullTokens.length - 1]);

        if (!normalize(abbrSurname).equals(normalize(fullSurname))) {
            return false;
        }

        String[] initials    = Arrays.copyOfRange(abbrTokens, 0, abbrTokens.length - 1);
        String[] firstNames  = Arrays.copyOfRange(fullTokens, 0, fullTokens.length - 1);

        // Filtra partículas do nome completo (de, da, do, das, dos)
        List<String> meaningfulFirstNames = new ArrayList<>();
        for (String t : firstNames) {
            if (!isParticle(t)) {
                meaningfulFirstNames.add(t);
            }
        }

        // Verifica se todos os tokens abreviados são realmente iniciais
        List<String> cleanInitials = new ArrayList<>();
        for (String t : initials) {
            String clean = stripDots(t);
            if (clean.length() != 1) {
                return false;
            }
            cleanInitials.add(clean);
        }

        if (cleanInitials.size() != meaningfulFirstNames.size()) {
            return false;
        }

        for (int i = 0; i < cleanInitials.size(); i++) {
            char expectedInitial = normalize(meaningfulFirstNames.get(i)).charAt(0);
            char actualInitial   = normalize(cleanInitials.get(i)).charAt(0);
            if (actualInitial != expectedInitial) {
                return false;
            }
        }

        return true;
    }

    private String[] tokenize(String name) {
        return name.trim().split("\\s+");
    }

    private String stripDots(String token) {
        return token.replace(".", "");
    }

    private boolean isParticle(String token) {
        String lower = token.toLowerCase();
        return lower.equals("de") || lower.equals("da") || lower.equals("do")
                || lower.equals("das") || lower.equals("dos");
    }

    private String normalize(String text) {
        if (text == null) return "";
        String normalized = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD);
        normalized = normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
        return normalized.toLowerCase();
    }

    private int getCompletenessScore(String name) {
        if (name == null) return 0;
        String[] tokens = name.trim().split("\\s+");
        int score = 0;
        for (String token : tokens) {
            if (isParticle(token)) continue;
            String clean = stripDots(token);
            score += (clean.length() > 1) ? 10 : 1;
        }
        return score;
    }
}