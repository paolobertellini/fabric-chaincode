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
        Partecipant_NOT_FOUND,
        Partecipant_ALREADY_EXISTS
    }

     @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<CallForProposal> calls = new ArrayList<>();
        calls.add(new CallForProposal("Paul", "Paint car", CallForProposal.CREATED, new ArrayList<String>()));
        calls.add(new CallForProposal("John", "Find best path", CallForProposal.CREATED, new ArrayList<String>()));
        calls.add(new CallForProposal("Frank", "Generate random numbers", CallForProposal.CREATED, new ArrayList<String>()));

        List<Partecipant> partecipants = new ArrayList<>();
        partecipants.add(new Partecipant("partecipant1", Partecipant.WAITING, -1));
        partecipants.add(new Partecipant("partecipant2", Partecipant.WAITING, -1));
        partecipants.add(new Partecipant("partecipant3", Partecipant.WAITING, -1));

        for (int i = 0; i < calls.size(); i++) {
            String key = String.format("cfp%d", i+1);
            String callForProposalState = genson.serialize(calls.get(i));
            stub.putStringState(key, callForProposalState);
        }

        for (int i = 0; i < partecipants.size(); i++) {
            String key = String.format("prt%d", i+1);
            String partecipantState = genson.serialize(partecipants.get(i));
            stub.putStringState(key, partecipantState);
        }

    }


    /* Create */

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

        CallForProposal callForProposal = new CallForProposal(initiator, task, state, new ArrayList<String>());
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

        Partecipant partecipant = new Partecipant(name, Partecipant.WAITING, -1);
        partecipantState = genson.serialize(partecipant);
        stub.putStringState(key, partecipantState);

        return partecipant;
    }


    /* Query */

    public CallForProposal getCallForProposal(final Context ctx, final String key){
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

    public Partecipant getPartecipant(final Context ctx, final String key){
        ChaincodeStub stub = ctx.getStub();
        String partecipantState = stub.getStringState(key);
        if (partecipantState.isEmpty()) {
            String errorMessage = String.format("Partecipant %s does not exist", key);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCallForProposalErrors.Partecipant_NOT_FOUND.toString());
        }
        Partecipant partecipant = genson.deserialize(partecipantState, Partecipant.class);
        return partecipant;
    }

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

    @Transaction()
    public String queryAllPartecipants(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "prt1";
        final String endKey = "prt99";
        List<PartecipantQueryResult> queryResults = new ArrayList<>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Partecipant partecipant = genson.deserialize(result.getStringValue(), Partecipant.class);
            queryResults.add(new PartecipantQueryResult(result.getKey(), partecipant));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }


    /* operations */

    @Transaction()
    public String callAllPartecipants(final Context ctx, final String key) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, key);

        final String startKey = "prt1";
        final String endKey = "prt99";

        List<PartecipantQueryResult> queryResults = new ArrayList<>();
        List<String> partecipants = new ArrayList<>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {

            Partecipant partecipant = getPartecipant(ctx, result.getKey());
            if (partecipant.isWaiting()) {
                partecipants.add(result.getKey());
                Partecipant newPartecipant = new Partecipant(partecipant.getName(), Partecipant.CALLED, -1);
                String newPartecipantState = genson.serialize(newPartecipant);
                stub.putStringState(result.getKey(), newPartecipantState);
            }
        }

        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(), callForProposal.getTask(), callForProposal.CALLING, partecipants);
        String newCallForProposalState = genson.serialize(newCallForProposal);
        stub.putStringState(key, newCallForProposalState);

        return partecipants.toString();
    }

    @Transaction()
    public String refuseCallForProposal(final Context ctx, final String partecipantKey, final String cfpKey) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);
        Partecipant partecipant = getPartecipant(ctx, partecipantKey);

        if (!callForProposal.isCalling()) {
            String errorMessage = String.format("Call for proposal %s is not calling", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        Partecipant newPartecipant = new Partecipant(partecipant.getName(), Partecipant.REFUSED, -1);
        String newPartecipantState = genson.serialize(newPartecipant);
        stub.putStringState(partecipantKey, newPartecipantState);

        List<String> partecipants = callForProposal.getPartecipants();
        partecipants.remove(partecipant.getName());

        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(),
                callForProposal.getTask(), CallForProposal.CALLING, partecipants);
        String newCallForProposalState = genson.serialize(newCallForProposal);
        stub.putStringState(cfpKey, newCallForProposalState);

        return partecipants.toString();
    }

    @Transaction()
    public String proposeForCallForProposal(final Context ctx, final String partecipantKey, final String cfpKey, final int offer) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);
        Partecipant partecipant = getPartecipant(ctx, partecipantKey);

        if (!callForProposal.isCalling()) {
            String errorMessage = String.format("Call for proposal %s is not calling", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        Partecipant newPartecipant = new Partecipant(partecipant.getName(), Partecipant.PROPOSED, offer);
        String newPartecipantState = genson.serialize(newPartecipant);
        stub.putStringState(partecipantKey, newPartecipantState);

        List<String> partecipants = callForProposal.getPartecipants();

        CallForProposal newCallForProposal = new CallForProposal(callForProposal.getInitiator(),
                callForProposal.getTask(), CallForProposal.CALLING, partecipants);
        String newCallForProposalState = genson.serialize(newCallForProposal);
        stub.putStringState(cfpKey, newCallForProposalState);

        return partecipants.toString();
    }

    @Transaction()
    public String closeCallForProposal(final Context ctx, final String cfpKey) {
        ChaincodeStub stub = ctx.getStub();

        CallForProposal callForProposal = getCallForProposal(ctx, cfpKey);

        if (!callForProposal.isCalling()) {
            String errorMessage = String.format("Call for proposal %s is not calling", cfpKey);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, errorMessage);
        }

        List<PartecipantQueryResult> proposed = new ArrayList<>();
        for(String key: callForProposal.getPartecipants()){
            Partecipant partecipant = getPartecipant(ctx, key);
            if(partecipant.isProposed()){
                proposed.add(new PartecipantQueryResult(key, partecipant));
            }
            Partecipant newPartecipant = new Partecipant(partecipant.getName(), Partecipant.WAITING, -1);
            String newPartecipantState = genson.serialize(newPartecipant);
            stub.putStringState(key, newPartecipantState);
        }

        final String response = genson.serialize(proposed);

        return response;

    }
}

