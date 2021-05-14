package org.contractnet;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class CallForProposalContext extends Context {

    public CallForProposalContext(ChaincodeStub stub) {
        super(stub);
        this.callForProposalList = new CallForProposalList(this);
    }

    public CallForProposalList callForProposalList;

}