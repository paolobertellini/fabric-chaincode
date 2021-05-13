import ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class CallForProposalList {

    private StateList stateList;

    public CallForProposalList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, CallForProposalList.class.getSimpleName(), CallForProposal::deserialize);
    }

    public CallForProposalList addCallForProposal(CallForProposal callForProposal) {
        stateList.addState(callForProposal);
        return this;
    }

    public CallForProposal getCallForProposal(String callForProposalKey) {
        return (CallForProposal) this.stateList.getState(callForProposalKey);
    }

    public CallForProposalList updateCallForProposal(CallForProposal callForProposal) {
        this.stateList.updateState(callForProposal);
        return this;
    }
}