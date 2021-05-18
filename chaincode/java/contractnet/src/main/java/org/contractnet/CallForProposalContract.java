/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Java implementation of the Fabric CallForProposal Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "Contractnet",
        info = @Info(
                title = "Contractnet protocol",
                description = "Contractnet protocol",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html")))
@Default
public final class CallForProposalContract implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FabCallForProposalErrors {
        CallForProposal_NOT_FOUND,
        CallForProposal_ALREADY_EXISTS,
        Partecipant_ALREADY_EXISTS
    }

    /**
     * Retrieves a callForProposal with the specified key from the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @return the CallForProposal found on the ledger if there was one
     */
    @Transaction()
    public CallForProposal queryCallForProposal(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();
        String callForProposalState = stub.getStringState(key);

        if (callForProposalState.isEmpty()) {
            String errorMessage = String.format("CallForProposal %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.CallForProposal_NOT_FOUND.toString());
        }

        CallForProposal callForProposal = genson.deserialize(callForProposalState, CallForProposal.class);

        return callForProposal;
    }

    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] callForProposalData = {
                "{ \"initiator\": \"Paul\", \"task\": \"Generate random numbers\" }",
                "{ \"initiator\": \"Paul\", \"task\": \"Paint car\" }",
                "{ \"initiator\": \"John\", \"task\": \"Computations\" }",
                "{ \"initiator\": \"Frank\", \"task\": \"Find best path\" }",
        };

        for (int i = 0; i < callForProposalData.length; i++) {
            String key = String.format("cfp%d", i);

            CallForProposal callForProposal = genson.deserialize(callForProposalData[i], CallForProposal.class);
            String callForProposalState = genson.serialize(callForProposal);
            stub.putStringState(key, callForProposalState);
        }
    }

    @Transaction()
    public CallForProposal createCallForProposal(final Context ctx, final String key, final String initiator, final String task,
            final String state) {
        ChaincodeStub stub = ctx.getStub();

        String callForProposalState = stub.getStringState(key);
        if (!callForProposalState.isEmpty()) {
            String errorMessage = String.format("CallForProposal %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.CallForProposal_ALREADY_EXISTS.toString());
        }

        CallForProposal callForProposal = new CallForProposal(initiator, task, state);
        callForProposalState = genson.serialize(callForProposal);
        stub.putStringState(key, callForProposalState);

        return callForProposal;
    }

    @Transaction()
    public Partecipant createPartecipant(final Context ctx, final String key, final String name) {
        ChaincodeStub stub = ctx.getStub();

        String partecipantState = stub.getStringState(key);
        if (!partecipantState.isEmpty()) {
            String errorMessage = String.format("Partecipant %s already exists", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.Partecipant_ALREADY_EXISTS.toString());
        }

        Partecipant partecipant = new Partecipant(name, Partecipant.WAITING);
        partecipantState = genson.serialize(partecipant);
        stub.putStringState(key, partecipantState);

        return partecipant;
    }


    @Transaction()
    public String callAllPartecipants(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();

        String callForProposalState = stub.getStringState(key);

        if (callForProposalState.isEmpty()) {
            String errorMessage = String.format("CallForProposal %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.CallForProposal_NOT_FOUND.toString());
        }

        CallForProposal callForProposal = genson.deserialize(callForProposalState, CallForProposal.class);

        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(), callForProposal.getTask(), callForProposal.getState());
        newCallForProposal.setCalling();


        final String startKey = "prt1";
        final String endKey = "prt99";

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {

            String partecipantState = stub.getStringState(result.getKey());

            Partecipant partecipant = genson.deserialize(partecipantState, Partecipant.class);

            Partecipant newPartecipant = new Partecipant(partecipant.getName(), Partecipant.CALLED);

            String newPartecipantState = genson.serialize(newPartecipant);
            stub.putStringState(result.getKey(), newPartecipantState);

//            newCallForProposal.addPartecipant(newPartecipant);

//            Partecipant partecipant = genson.deserialize(result.getStringValue(), Partecipant.class);
//            Partecipant newPartecipant = new Partecipant(partecipant.getName());
//            partecipant.setCalled();
//            String newPartecipantState = genson.serialize(newPartecipant);
//            stub.putStringState(key, newPartecipantState);
        }

        String newCallForProposalState = genson.serialize(newCallForProposal);
        stub.putStringState(key, newCallForProposalState);

        return "ok";
    }


    /**
     * Retrieves all callForProposals from the ledger.
     *
     * @param ctx the transaction context
     * @return array of CallForProposals found on the ledger
     */
    @Transaction()
    public String queryAllCallForProposals(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "cfp1";
        final String endKey = "cfp99";
        List<CallForProposalQueryResult> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            CallForProposal callForProposal = genson.deserialize(result.getStringValue(), CallForProposal.class);
            queryResults.add(new CallForProposalQueryResult(result.getKey(), callForProposal));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
     * Retrieves all partecipants from the ledger.
     *
     * @param ctx the transaction context
     * @return array of partecipants found on the ledger
     */
    @Transaction()
    public String queryAllPartecipants(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "prt1";
        final String endKey = "prt99";
        List<PartecipanyQueryResult> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Partecipant partecipant = genson.deserialize(result.getStringValue(), Partecipant.class);
            queryResults.add(new PartecipanyQueryResult(result.getKey(), partecipant));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
     * Changes the owner of a callForProposal on the ledger.
     *
     * @param ctx the transaction context
     * @param key the key
     * @return the updated CallForProposal
     */
    @Transaction()
    public CallForProposal changeCallForProposalOwner(final Context ctx, final String key, final String newTask) {
        ChaincodeStub stub = ctx.getStub();

        String callForProposalState = stub.getStringState(key);

        if (callForProposalState.isEmpty()) {
            String errorMessage = String.format("CallForProposal %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.CallForProposal_NOT_FOUND.toString());
        }

        CallForProposal callForProposal = genson.deserialize(callForProposalState, CallForProposal.class);

        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(), newTask, callForProposal.getState());
        String newCallForProposalState = genson.serialize(newCallForProposal);
        stub.putStringState(key, newCallForProposalState);

        return newCallForProposal;
    }
}
