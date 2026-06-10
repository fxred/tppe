package tppe.deduplication;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("Caso1")
public class Case1Test {

    public static class TestData {
        public List<AuthorRecord> input;
        public List<AuthorRecord> expected;

        public TestData(List<AuthorRecord> input, List<AuthorRecord> expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public static Stream<TestData> provideCase1Data() {
        return Stream.of(
            new TestData(
                List.of(
                    new AuthorRecord(31299, "Monica Hirata Sant`anna"),
                    new AuthorRecord(433095, "Mônica Hirata Sant'anna")
                ),
                List.of(
                    new AuthorRecord(31299, "Mônica Hirata Sant'anna"),
                    new AuthorRecord(433095, "Mônica Hirata Sant'anna")
                )
            ),
            new TestData(
                List.of(
                    new AuthorRecord(554799, "Sergio Henrique Guaraldi"),
                    new AuthorRecord(243350, "Sérgio Henrique Guaraldi"),
                    new AuthorRecord(954057, "Sérgio Henrique Guaraldi")
                ),
                List.of(
                    new AuthorRecord(554799, "Sérgio Henrique Guaraldi"),
                    new AuthorRecord(243350, "Sérgio Henrique Guaraldi"),
                    new AuthorRecord(954057, "Sérgio Henrique Guaraldi")
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCase1Data")
    public void testDeduplicationCase1(TestData data) {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case1Rule()));
        List<AuthorRecord> result = deduplicator.deduplicate(data.input);

        assertNotNull(result);
        assertEquals(data.expected.size(), result.size());
        for (int i = 0; i < data.expected.size(); i++) {
            assertEquals(data.expected.get(i), result.get(i));
        }
    }

    @Test
    public void testOriginalIdsArePreserved() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case1Rule()));
        List<AuthorRecord> input = List.of(
            new AuthorRecord(554799, "Sergio Henrique Guaraldi"),
            new AuthorRecord(243350, "Sérgio Henrique Guaraldi")
        );
        List<AuthorRecord> result = deduplicator.deduplicate(input);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(554799, result.get(0).getId());
        assertEquals(243350, result.get(1).getId());
    }

    @Test
    public void testExceptionOnNullInput() {
        Case1Rule rule = new Case1Rule();
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class,
            () -> rule.apply(null)
        );
    }
}
