package tppe.deduplication;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Tag("Caso3")
public class Case3Test {

    public static class TestData {
        public List<AuthorRecord> input;
        public List<AuthorRecord> expected;

        public TestData(List<AuthorRecord> input, List<AuthorRecord> expected) {
            this.input = input;
            this.expected = expected;
        }
    }

    public static Stream<TestData> provideCase3Data() {
        return Stream.of(
            // Cenário 1: Exemplo do enunciado (Luiz de Oliveira de Souza)
            new TestData(
                List.of(
                    new AuthorRecord(746937, "Luiz de Oliveira de Souza"),
                    new AuthorRecord(608296, "Luiz Oliveira Souza"),
                    new AuthorRecord(549242, "Luiz de O. de Souza")
                ),
                List.of(
                    new AuthorRecord(746937, "Luiz de Oliveira de Souza"),
                    new AuthorRecord(608296, "Luiz de Oliveira de Souza"),
                    new AuthorRecord(549242, "Luiz de Oliveira de Souza")
                )
            ),
            // Cenário 2: Adicional para satisfazer "no mínimo dois conjuntos de dados" (Ana de Mattos Seabra)
            new TestData(
                List.of(
                    new AuthorRecord(28372, "Ana de Mattos Seabra"),
                    new AuthorRecord(582585, "Ana Mattos Seabra"),
                    new AuthorRecord(999999, "Ana de M. de Seabra")
                ),
                List.of(
                    new AuthorRecord(28372, "Ana de Mattos Seabra"),
                    new AuthorRecord(582585, "Ana de Mattos Seabra"),
                    new AuthorRecord(999999, "Ana de Mattos Seabra")
                )
            )
        );
    }

    @ParameterizedTest
    @MethodSource("provideCase3Data")
    public void testDeduplicationCase3(TestData data) {
        // Testa o deduplicador configurado apenas com a regra do Caso 3
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case3Rule()));
        List<AuthorRecord> result = deduplicator.deduplicate(data.input);
        
        assertNotNull(result);
        assertEquals(data.expected.size(), result.size());
        for (int i = 0; i < data.expected.size(); i++) {
            assertEquals(data.expected.get(i), result.get(i));
        }
    }

    @Test
    public void testOriginalIdsArePreserved() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case3Rule()));
        List<AuthorRecord> input = List.of(
            new AuthorRecord(746937, "Luiz de Oliveira de Souza"),
            new AuthorRecord(608296, "Luiz Oliveira Souza")
        );
        List<AuthorRecord> result = deduplicator.deduplicate(input);
        
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(746937, result.get(0).getId());
        assertEquals(608296, result.get(1).getId());
    }

    @Test
    public void testExceptionOnNullInput() {
        AuthorDeduplicator deduplicator = new AuthorDeduplicator(List.of(new Case3Rule()));
        org.junit.jupiter.api.Assertions.assertThrows(
            IllegalArgumentException.class, 
            () -> deduplicator.deduplicate(null)
        );
    }
}
