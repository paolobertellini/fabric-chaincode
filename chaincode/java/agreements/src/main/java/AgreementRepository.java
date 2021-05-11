
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
        name = "Agreements",
        info = @Info(
                title = "Agreements contract",
                description = "A java chaincode example",
                version = "0.0.1-SNAPSHOT"))

@Default
    public final class AgreementRepository implements ContractInterface{
    private final Genson genson = new Genson();

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        Agreement agreement = new Agreement("MyCompany", "OtherCompany", "open");

        String agreementState = genson.serialize(agreement);
        stub.putStringState("ARG001", agreementState);
    }


    @Transaction()
    public Agreement getAgreement(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String agreementState = stub.getStringState(key);

        if (agreementState.isEmpty()) {
            String errorMessage = String.format("Agreement %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Agreement not found");
        }

        Agreement agreement = genson.deserialize(agreementState, Agreement.class);

        return agreement;
    }

    @Transaction()
    public Agreement createAgreement(final Context ctx, final String key, final String party1, final String party2,
                                    final String stats) {
        ChaincodeStub stub = ctx.getStub();

        String agreementState = stub.getStringState(key);
        if (!agreementState.isEmpty()) {
            String errorMessage = String.format("Agreement %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Agreement already exists");
        }

        Agreement agreement = new Agreement(party1, party2, stats);
        agreementState = genson.serialize(agreement);
        stub.putStringState(key, agreementState);

        return agreement;
    }


    @Transaction()
    public Agreement changeAgreementStatus(final Context ctx, final String key, final String newStatus) {
        ChaincodeStub stub = ctx.getStub();

        String agreementState = stub.getStringState(key);

        if (agreementState.isEmpty()) {
            String errorMessage = String.format("Agreement %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Agreement not found");
        }

        Agreement agreement = genson.deserialize(agreementState, Agreement.class);

        Agreement newAgreement = new Agreement(agreement.getParty1(), agreement.getParty2(),newStatus);
        String newAgreementState = genson.serialize(newAgreement);
        stub.putStringState(key, newAgreementState);

        return newAgreement;
    }
    
    /**
     * Retrieves all agreements from the ledger.
     *
     * @param ctx the transaction context
     * @return array of agreements found on the ledger
     */
    @Transaction(intent = Transaction.TYPE.EVALUATE)
    public String GetAllAgreements(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Agreement> queryResults = new ArrayList<Agreement>();

        // To retrieve all assets from the ledger use getStateByRange with empty startKey & endKey.
        // Giving empty startKey & endKey is interpreted as all the keys from beginning to end.
        // As another example, if you use startKey = 'asset0', endKey = 'asset9' ,
        // then getStateByRange will retrieve asset with keys between asset0 (inclusive) and asset9 (exclusive) in lexical order.
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Agreement agreement = genson.deserialize(result.getStringValue(), Agreement.class);
            queryResults.add(agreement);
            System.out.println(agreement.toString());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

}
