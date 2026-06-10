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

@Tag("Caso2")
public class Case2Test {

    public static class TestData {
        public List<AuthorRecord> input;
        public List<AuthorRecord> expected;

        public TestData(List<AuthorRecord> input, List<AuthorRecord> expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public static Stream<TestData> provideCase2Data() {
        return Stream.of(
                new TestData(
                        List.of(
                                new AuthorRecord(28372, "Ana de Mattos Seabra"),
                                new AuthorRecord(582585, "Seabra A M")
                        ),
                        List.of(
                                new AuthorRecord(28372, "Ana de Mattos Seabra"),
                                new AuthorRecord(582585, "Ana de Mattos Seabra")
                        )
                ),
                new TestData(
                        List.of(
                                new AuthorRecord(28371, "Cassius de Souza"),
                                new AuthorRecord(746936, "Souza C.")
                        ),
                        List.of(
                                new AuthorRecord(28371, "Cassius de Souza"),
                                new AuthorRecord(746936, "Cassius de Souza")
                        )
                ),
                new TestData(
                        List.of(
                                new AuthorRecord(28372, "Ana de Mattos Seabra"),
                                new AuthorRecord(243349, "Ana de Mattos Seabra"),
                                new AuthorRecord(582585, "Seabra A. M.")
                        ),
                        List.of(
                                new AuthorRecord(28372, "Ana de Mattos Seabra"),
                                new AuthorRecord(243349, "Ana de Mattos Seabra"),
                                new AuthorRecord(582585, "Ana de Mattos Seabra")
                        )
                ),
                new TestData(
                        List.of(
                                new AuthorRecord(31303, "Veronica de Oliveira Moreira"),
                                new AuthorRecord(608303, "Moreira V O")
                        ),
                        List.of(
                                new AuthorRecord(31303, "Veronica de Oliveira Moreira"),
                                new AuthorRecord(608303, "Veronica de Oliveira Moreira")
                        )
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCase2Data")
    public void testDeduplicationCase2(TestData data) {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case2Rule()));
        List<AuthorRecord> result = deduplicator.deduplicate(data.input);

        assertNotNull(result);
        assertEquals(data.expected.size(), result.size());
        for (int i = 0; i < data.expected.size(); i++) {
            assertEquals(data.expected.get(i), result.get(i));
        }
    }

    @Test
    public void testOriginalIdsArePreserved() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case2Rule()));
        List<AuthorRecord> input = List.of(
                new AuthorRecord(28371, "Cassius de Souza"),
                new AuthorRecord(746936, "Souza C.")
        );
        List<AuthorRecord> result = deduplicator.deduplicate(input);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(28371, result.get(0).getId());
        assertEquals(746936, result.get(1).getId());
    }

    @Test
    public void testExceptionOnNullInput() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case2Rule()));
        assertThrows(
                IllegalArgumentException.class,
                () -> deduplicator.deduplicate(null)
        );
    }
}