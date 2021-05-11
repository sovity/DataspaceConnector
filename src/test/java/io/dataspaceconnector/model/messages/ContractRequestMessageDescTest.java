package io.dataspaceconnector.model.messages;

import java.net.URI;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ContractRequestMessageDescTest {
    @Test
    void defaultConstructor_nothing_emptyDesc() {
        /* ARRANGE */
        // Nothing to arrange here.

        /* ACT */
        final var result = new ContractRequestMessageDesc();

        /* ASSERT */
        assertNull(result.getTransferContract());
        assertNull(result.getRecipient());
    }

    @Test
    void allArgsConstructor_valid_validDesc() {
        /* ARRANGE */
        final var recipient = URI.create("someRecipient");
        final var contract = URI.create("someContract");

        /* ACT */
        final var result = new ContractRequestMessageDesc(recipient, contract);

        /* ASSERT */
        assertEquals(recipient, result.getRecipient());
        assertEquals(contract, result.getTransferContract());
    }

    @Test
    void equals_everything_valid() {
        EqualsVerifier.simple().forClass(ContractRequestMessageDesc.class).verify();
    }
}
