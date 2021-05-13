

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeStub;

class ContractNetContext extends Context {

    public ContractNetContext(ChaincodeStub stub) {
        super(stub);
        this.paperList = new CallForProposalList(this);
    }

    public CallForProposalList paperList;

}