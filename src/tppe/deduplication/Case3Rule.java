package tppe.deduplication;

import java.util.List;
import java.util.Collections;

public class Case3Rule implements DeduplicationRule {
    @Override
    public List<AuthorRecord> apply(List<AuthorRecord> records) {
        // Stub inicial para a fase falha (Red Phase) do TDD
        return Collections.emptyList();
    }
}
