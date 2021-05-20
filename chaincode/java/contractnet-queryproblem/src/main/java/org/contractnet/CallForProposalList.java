/*
SPDX-License-Identifier: Apache-2.0
*/

package org.contractnet;

import org.contractnet.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class CallForProposalList {

    private StateList stateList;

    public CallForProposalList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, org.contractnet.CallForProposalList.class.getSimpleName(), CallForProposal::deserialize);
    }

    public org.contractnet.CallForProposalList addCallForProposal(CallForProposal callForProposal) {
        stateList.addState(callForProposal);
        return this;
    }

    public CallForProposal getCallForProposal(String callForProposalKey) {
        return (CallForProposal) this.stateList.getState(callForProposalKey);
    }

    public org.contractnet.CallForProposalList updateCallForProposal(CallForProposal callForProposal) {
        this.stateList.updateState(callForProposal);
        return this;
    }
}
