package tppe.deduplication;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class NameProcessorTest {

    private final NameProcessor nameProcessor = new NameProcessor();

    @Test
    public void testIsCompatibleSameName() {
        assertTrue(nameProcessor.isCompatible("Luiz de Oliveira de Souza", "Luiz Oliveira Souza"));
        assertTrue(nameProcessor.isCompatible("Luiz de O. de Souza", "Luiz de Oliveira de Souza"));
        assertFalse(nameProcessor.isCompatible("Luiz de Oliveira de Souza", "Carlos de Oliveira de Souza"));
    }

    @Test
    public void testGetNonParticleTokens() {
        List<String> tokens = nameProcessor.getNonParticleTokens("Luiz de Oliveira de Souza");
        assertEquals(3, tokens.size());
        assertTrue(tokens.contains("luiz"));
        assertTrue(tokens.contains("oliveira"));
        assertTrue(tokens.contains("souza"));
    }

    @Test
    public void testGetCompletenessScore() {
        assertEquals(32, nameProcessor.getCompletenessScore("Luiz de Oliveira de Souza"));
        assertEquals(30, nameProcessor.getCompletenessScore("Luiz Oliveira Souza"));
        assertEquals(22, nameProcessor.getCompletenessScore("Luiz de O. de Souza"));
    }
}
