package tppe.deduplication;

import java.util.List;

public interface DeduplicationRule {
    List<AuthorRecord> apply(List<AuthorRecord> records);
}
