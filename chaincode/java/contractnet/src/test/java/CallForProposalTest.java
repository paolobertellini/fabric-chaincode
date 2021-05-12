import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
public final class CallForProposalTest {

    @Test
    public void isEqual(){
        CallForProposal cfp = new CallForProposal("Cfp001", "Paolo", "open", null);
        assertThat(cfp).isEqualTo(cfp);
    }

    @Test
    public void nonEqual(){
        CallForProposal cfp = new CallForProposal("Cfp001", "Paolo", "open", null);
        CallForProposal otherCfp = new CallForProposal("Cfp001", "Paolo", "close", null);
        assertThat(cfp).isNotEqualTo(otherCfp);
    }
}
