package tppe.deduplication;

import java.util.List;
import java.util.ArrayList;

public class AuthorDeduplicator {
    private final List<DeduplicationRule> rules;

    public AuthorDeduplicator(List<DeduplicationRule> rules) {
        this.rules = rules;
    }

    public List<AuthorRecord> deduplicate(List<AuthorRecord> records) {
        if (records == null) {
            throw new IllegalArgumentException("Records list cannot be null");
        }
        List<AuthorRecord> current = new ArrayList<>(records);
        for (DeduplicationRule rule : rules) {
            current = rule.apply(current);
        }
        return current;
    }
}
