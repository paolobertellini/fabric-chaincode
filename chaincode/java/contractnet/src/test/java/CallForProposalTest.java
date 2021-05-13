import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
public final class CallForProposalTest {

    @Test
    public void isEqual(){
        CallForProposal cfp = new CallForProposal("Cfp001", "Paolo", "open", "task", null);
        assertThat(cfp).isEqualTo(cfp);
    }

    @Test
    public void nonEqual(){
        CallForProposal cfp = new CallForProposal("Cfp001", "Paolo", "open", "task", null);
        CallForProposal otherCfp = new CallForProposal("Cfp001", "Paolo", "close", "task", null);
        assertThat(cfp).isNotEqualTo(otherCfp);
    }
}
