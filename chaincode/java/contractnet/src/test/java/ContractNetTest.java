
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public final class ContractNetTest {

    @Nested
    class InvokeQueryCfpTransaction {

        @Test
        public void whenCfpExists() {
            ContractNet contract = new ContractNet();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("ARG000"))
                    .thenReturn("{\"id\":\"cfp001\",\"initiator\":\"Paolo\",\"status\":\"issued\"}");

            Cfp cfp = contract.getCfp(ctx, "ARG000");

            assertThat(cfp.getId())
                    .isEqualTo("cfp001");
            assertThat(cfp.getInitiator())
                    .isEqualTo("Paolo");
            assertThat(cfp.getStatus())
                    .isEqualTo("issued");
        }

        @Test
        public void whenCarDoesNotExist() {
            ContractNet contract = new ContractNet();
            Context ctx = mock(Context.class);
            ChaincodeStub stub = mock(ChaincodeStub.class);
            when(ctx.getStub()).thenReturn(stub);
            when(stub.getStringState("ARG000")).thenReturn("");

            Throwable thrown = catchThrowable(() -> {
                contract.getCfp(ctx, "ARG000");
            });

            assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                    .hasMessage("Cfp ARG000 does not exist");
        }

    }

}
