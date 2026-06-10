package tppe.deduplication;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Tag("Caso4")
public class Case4Test {

    public static class TestData {
        public List<AuthorRecord> input;
        public List<AuthorRecord> expected;

        public TestData(List<AuthorRecord> input, List<AuthorRecord> expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public static Stream<TestData> provideCase4Data() {
        return Stream.of(
            new TestData(
                List.of(
                    new AuthorRecord(763027, "Vanilda Cristina Junior"),
                    new AuthorRecord(763027, "VC Junior")
                ),
                List.of(
                    new AuthorRecord(763027, "Vanilda Cristina Junior"),
                    new AuthorRecord(763027, "Vanilda Cristina Junior")
                )
            ),
            new TestData(
                List.of(
                    new AuthorRecord(243350, "Sérgio Henrique Guaraldi"),
                    new AuthorRecord(954057, "SH Guaraldi")
                ),
                List.of(
                    new AuthorRecord(243350, "Sérgio Henrique Guaraldi"),
                    new AuthorRecord(954057, "Sérgio Henrique Guaraldi")
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCase4Data")
    public void testDeduplicationCase4(TestData data) {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case4Rule()));
        List<AuthorRecord> result = deduplicator.deduplicate(data.input);

        assertNotNull(result);
        assertEquals(data.expected.size(), result.size());
        for (int i = 0; i < data.expected.size(); i++) {
            assertEquals(data.expected.get(i), result.get(i));
        }
    }

    @Test
    public void testOriginalIdsArePreserved() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case4Rule()));
        List<AuthorRecord> input = List.of(
            new AuthorRecord(243350, "Sérgio Henrique Guaraldi"),
            new AuthorRecord(954057, "SH Guaraldi")
        );
        List<AuthorRecord> result = deduplicator.deduplicate(input);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(243350, result.get(0).getId());
        assertEquals(954057, result.get(1).getId());
    }

    @Test
    public void testExceptionOnNullInput() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case4Rule()));
        assertThrows(
            IllegalArgumentException.class,
            () -> deduplicator.deduplicate(null)
        );
    }
}
