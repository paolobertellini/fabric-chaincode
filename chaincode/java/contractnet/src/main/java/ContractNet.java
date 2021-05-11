
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
        Cfp cfp = new Cfp("cfp001", "Paolo", "Called");

        String cfpState = genson.serialize(cfp);
        stub.putStringState("cfp001", cfpState);
    }


    @Transaction()
    public Cfp getCfp(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String cfpState = stub.getStringState(key);

        if (cfpState.isEmpty()) {
            String errorMessage = String.format("Cfp %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Cfp not found");
        }

        Cfp cfp = genson.deserialize(cfpState, Cfp.class);

        return cfp;
    }

    @Transaction()
    public Cfp createCfp(final Context ctx, final String key, final String id, final String initiator,
                                    final String status) {
        ChaincodeStub stub = ctx.getStub();

        String cfpState = stub.getStringState(key);
        if (!cfpState.isEmpty()) {
            String errorMessage = String.format("Cfp %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Cfp already exists");
        }

        Cfp cfp = new Cfp(id, initiator, status);
        cfpState = genson.serialize(cfp);
        stub.putStringState(key, cfpState);

        return cfp;
    }


    @Transaction()
    public Cfp changeCfpStatus(final Context ctx, final String key, final String newStatus) {
        ChaincodeStub stub = ctx.getStub();

        String cfpState = stub.getStringState(key);

        if (cfpState.isEmpty()) {
            String errorMessage = String.format("Cfp %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Cfp not found");
        }

        Cfp cfp = genson.deserialize(cfpState, Cfp.class);

        Cfp newCfp = new Cfp(cfp.getId(), cfp.getInitiator(), newStatus);
        String newCfpState = genson.serialize(newCfp);
        stub.putStringState(key, newCfpState);

        return newCfp;
    }
    
    /**
     * Retrieves all cfps from the ledger.
     *
     * @param ctx the transaction context
     * @return array of cfps found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllCfps(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Cfp> queryResults = new ArrayList<Cfp>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Cfp cfp = genson.deserialize(result.getStringValue(), Cfp.class);
            queryResults.add(cfp);
            System.out.println(cfp.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

}
