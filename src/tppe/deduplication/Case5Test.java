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

@Tag("Caso5")
public class Case5Test {

    public static class TestData {
        public List<AuthorRecord> input;
        public List<AuthorRecord> expected;

        public TestData(List<AuthorRecord> input, List<AuthorRecord> expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public static Stream<TestData> provideCase5Data() {
        return Stream.of(
            new TestData(
                List.of(
                    new AuthorRecord(31298, "Raphael Goncalves Viana"),
                    new AuthorRecord(433094, "Raphael Gonçalves Viana"),
                    new AuthorRecord(549243, "Raphael Gonçalves Viana"),
                    new AuthorRecord(608297, "Raphael Gonçalves Viana"),
                    new AuthorRecord(746938, "Raphael Gonçalves Viana")
                ),
                List.of(
                    new AuthorRecord(31298, "Raphael Goncalves Viana"),
                    new AuthorRecord(31298, "Raphael Gonçalves Viana"),
                    new AuthorRecord(31298, "Raphael Gonçalves Viana"),
                    new AuthorRecord(31298, "Raphael Gonçalves Viana"),
                    new AuthorRecord(31298, "Raphael Gonçalves Viana")
                )
            ),
            new TestData(
                List.of(
                    new AuthorRecord(899639, "Lilian Luíza Viana Vieira"),
                    new AuthorRecord(243351, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(663795, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(663795, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(663795, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(663795, "Lilian Luíza Viana Vieira")
                ),
                List.of(
                    new AuthorRecord(243351, "Lilian Luíza Viana Vieira"),
                    new AuthorRecord(243351, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(243351, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(243351, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(243351, "Lílian Luíza Viana Vieira"),
                    new AuthorRecord(243351, "Lilian Luíza Viana Vieira")
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCase5Data")
    public void testDeduplicationCase5(TestData data) {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case5Rule()));
        List<AuthorRecord> result = deduplicator.deduplicate(data.input);

        assertNotNull(result);
        assertEquals(data.expected.size(), result.size());
        for (int i = 0; i < data.expected.size(); i++) {
            assertEquals(data.expected.get(i), result.get(i));
        }
    }

    @Test
    public void testOriginalNamesArePreserved() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case5Rule()));
        List<AuthorRecord> input = List.of(
            new AuthorRecord(31298, "Raphael Goncalves Viana"),
            new AuthorRecord(433094, "Raphael Gonçalves Viana")
        );
        List<AuthorRecord> result = deduplicator.deduplicate(input);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Raphael Goncalves Viana", result.get(0).getName());
        assertEquals("Raphael Gonçalves Viana", result.get(1).getName());
    }

    @Test
    public void testExceptionOnNullInput() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case5Rule()));
        assertThrows(
            IllegalArgumentException.class,
            () -> deduplicator.deduplicate(null)
        );
    }
}
