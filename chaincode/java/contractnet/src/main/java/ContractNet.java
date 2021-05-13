
import java.util.ArrayList;
import java.util.List;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

@Contract(
        name = "ContractNet",
        info = @Info(
                title = "Contract Net",
                description = "Chaincode implementation of contract net protocol",
                version = "0.0.1-SNAPSHOT"))

@Default
    public final class ContractNet implements ContractInterface{
    private final Genson genson = new Genson();

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        CallForProposal cfp = new CallForProposal();

        String cfpState = genson.serialize(cfp);
        stub.putStringState("cfp001", cfpState);
    }


    @Transaction()
    public CallForProposal getCfp(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String cfpState = stub.getStringState(key);

        if (cfpState.isEmpty()) {
            String errorMessage = String.format("Cfp %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Cfp not found");
        }

        CallForProposal cfp = genson.deserialize(cfpState, CallForProposal.class);

        return cfp;
    }

    @Transaction()
    public CallForProposal createCfp(final Context ctx, final String key, final String id, final String initiator,
                                     final String task, final String status) {
        ChaincodeStub stub = ctx.getStub();

        String cfpState = stub.getStringState(key);
        if (!cfpState.isEmpty()) {
            String errorMessage = String.format("Cfp %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Cfp already exists");
        }

        CallForProposal cfp = new CallForProposal();
        cfpState = genson.serialize(cfp);
        stub.putStringState(key, cfpState);

        return cfp;
    }


    @Transaction()
    public CallForProposal changeCfpStatus(final Context ctx, final String key, final String newStatus) {
        ChaincodeStub stub = ctx.getStub();

        String cfpState = stub.getStringState(key);

        if (cfpState.isEmpty()) {
            String errorMessage = String.format("Cfp %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Cfp not found");
        }

        CallForProposal cfp = genson.deserialize(cfpState, CallForProposal.class);

        CallForProposal newCfp = new CallForProposal();
        String newCfpState = genson.serialize(newCfp);
        stub.putStringState(key, newCfpState);

        return newCfp;
    }


    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllCfps(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<CallForProposal> queryResults = new ArrayList<CallForProposal>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            CallForProposal cfp = genson.deserialize(result.getStringValue(), CallForProposal.class);
            queryResults.add(cfp);
            System.out.println(cfp.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

}
