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
        return new Case2Deduplicator(records).compute();
    }
}
