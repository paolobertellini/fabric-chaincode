/*
SPDX-License-Identifier: Apache-2.0
*/
package org.contractnet;

import com.owlike.genson.Genson;
import org.contractnet.ledgerapi.State;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.CompositeKey;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


/**
 * Define callForProposal smart contract by extending Fabric Contract class
 *
 */
@Contract(name = "org.contractnet.callForProposal", info = @Info(title = "Contract net protocol", description = "", version = "0.0.1", license = @License(name = "SPDX-License-Identifier: ", url = ""), contact = @Contact(email = "java-contract@example.com", name = "java-contract", url = "http://java-contract.me")))
@Default
public class CallForProposalContract implements ContractInterface {

    private final static Logger LOG = Logger.getLogger(org.contractnet.CallForProposalContract.class.getName());
    private final Genson genson = new Genson();

    @Override
    public Context createContext(ChaincodeStub stub) {
        return new CallForProposalContext(stub);
    }

    public CallForProposalContract() {

    }


    /**
     * Instantiate to perform any setup of the ledger that might be required.
     *
     * @param {Context} ctx the transaction context
     */
    @Transaction
    public void instantiate(CallForProposalContext ctx) {
        LOG.info("No data migration to perform");
    }

    /**
     * Create a new callForProposal
     *
     * @param {Context} ctx the transaction context
     * @param {String} initiator callForProposal initiator
     * @param {String} id callForProposal id
     * @param {String} task callForProposal task
     */
    @Transaction
    public CallForProposal create(CallForProposalContext ctx, String initiator, String id, String task) {

        System.out.println(ctx);

        // create an instance of the callForProposal
        CallForProposal callForProposal = CallForProposal.createInstance(initiator, id, task, "");

        // Smart contract, rather than callForProposal, moves callForProposal into ISSUED state
        callForProposal.setCreated();

        System.out.println(callForProposal);
        // Add the callForProposal to the list of all similar callForProposals in the ledger
        // world state
        ctx.callForProposalList.addCallForProposal(callForProposal);

        // Must return a serialized callForProposal to caller of smart contract
        return callForProposal;
    }

    /**
     * Create a new partecipant
     *
     * @param {Context} ctx the transaction context
     * @param {String} name Partecipant name
     */
    @Transaction
    public Partecipant createPartecipant(CallForProposalContext ctx, String name) {

        System.out.println(ctx);

        Partecipant partecipant = Partecipant.createInstance(name, "");
        partecipant.setWaiting();

        System.out.println(partecipant);
        ctx.partecipantList.addPartecipant(partecipant);

        return partecipant;
    }


    @Transaction
    public CallForProposal addPartecipant(CallForProposalContext ctx, String id, String name) {

        System.out.println(ctx);

        String callForProposalKey = State.makeKey(new String[] { id });
        CallForProposal callForProposal = ctx.callForProposalList.getCallForProposal(callForProposalKey);

        String partecipantKey = State.makeKey(new String[] { name });
        Partecipant partecipant = ctx.partecipantList.getPartecipant(partecipantKey);

        callForProposal.addPartecipant(partecipant);

        partecipant.setCalled();

        System.out.println(callForProposal);
        System.out.println(partecipant);

        return callForProposal;
    }

    @Transaction
    public org.hyperledger.fabric.contract.ClientIdentity getAllPartecipants(CallForProposalContext ctx) {

        System.out.println(ctx.callForProposalList);

        return ctx.getClientIdentity();
    }

    @Transaction
    public String getAllCallForProposal(CallForProposalContext ctx, String idStart, String idEnd) {

        ChaincodeStub stub = ctx.getStub();
        String keyStart = State.makeKey(new String[] { idStart });
        String keyEnd = State.makeKey(new String[] { idEnd });

//        String[] splitKey = ctx.callForProposalList. state.getSplitKey();
//        System.out.println("Split key " + Arrays.asList(splitKey));

        CompositeKey ledgerKey = stub.createCompositeKey(org.contractnet.CallForProposalList.class.getSimpleName(), splitKey);

        QueryResultsIterator<KeyValue> results = stub.getStateByPartialCompositeKey(ledgerKey);
        List<String> queryResults = new ArrayList<>();

        for (KeyValue result: results) {
            CallForProposal cfp = genson.deserialize(result.getStringValue(), CallForProposal.class);
            queryResults.add(cfp.getId());
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    @Transaction
    public String test (){
        return "ciao";
    }

    @Transaction
    public CallForProposal getCallForProposal(CallForProposalContext ctx, String id) {

        String callForProposalKey = CallForProposal.makeKey(new String[]{id});

        CallForProposal callForProposal = ctx.callForProposalList.getCallForProposal(callForProposalKey);

        return callForProposal;
    }

        @Transaction
    public CallForProposal call(CallForProposalContext ctx, String id, String initiator) {

        String callForProposalKey = CallForProposal.makeKey(new String[] { id });

        CallForProposal callForProposal = ctx.callForProposalList.getCallForProposal(callForProposalKey);

        if (callForProposal.isEnded()) {
            throw new RuntimeException("CallForProposal " + id + " is ended");
        }

        if (callForProposal.getInitiator().equals(initiator)) {
            callForProposal.setCalling();
        } else {
            throw new RuntimeException("Call for proposal is not initiated by" + initiator);
        }

        ctx.callForProposalList.updateCallForProposal(callForProposal);
        return callForProposal;
    }

}
