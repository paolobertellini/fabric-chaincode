/*
SPDX-License-Identifier: Apache-2.0
*/
package org.contractnet;

import org.contractnet.ledgerapi.State;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.logging.Logger;


/**
 * Define callForProposal smart contract by extending Fabric Contract class
 *
 */
@Contract(name = "org.contractnet.callForProposal", info = @Info(title = "Contract net protocol", description = "", version = "0.0.1", license = @License(name = "SPDX-License-Identifier: ", url = ""), contact = @Contact(email = "java-contract@example.com", name = "java-contract", url = "http://java-contract.me")))
@Default
public class CallForProposalContract implements ContractInterface {

    private final static Logger LOG = Logger.getLogger(org.contractnet.CallForProposalContract.class.getName());

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


    /**
     * Create a new partecipant
     *
     * @param {Context} ctx the transaction context
     * @param {String} name Partecipant name
     */
    @Transaction
    public CallForProposal addPartecipant(CallForProposalContext ctx, String id, String name) {

        System.out.println(ctx);

        String callForProposalKey = State.makeKey(new String[] { id });
        CallForProposal callForProposal = ctx.callForProposalList.getCallForProposal(callForProposalKey);

        String partecipantKey = State.makeKey(new String[] { name });
        Partecipant partecipant = ctx.partecipantList.getPartecipant(partecipantKey);


        partecipant.setCalled();
        
        System.out.println(callForProposal);
        System.out.println(partecipant);

        return callForProposal;
    }


    /**
     * Buy commercial callForProposal
     *
     * @param {Context} ctx the transaction context
     * @param {String} issuer commercial callForProposal issuer
     * @param {Integer} id callForProposal number for this issuer
     * @param {String} currentOwner current owner of callForProposal
     * @param {String} newOwner new owner of callForProposal
     * @param {Integer} price price paid for this callForProposal
     * @param {String} purchaseDateTime time callForProposal was purchased (i.e. traded)
     */
    @Transaction
    public CallForProposal buy(CallForProposalContext ctx, String issuer, String id, String currentOwner,
            String newOwner, int price, String purchaseDateTime) {

        // Retrieve the current callForProposal using key fields provided
        String callForProposalKey = State.makeKey(new String[] { id });
        CallForProposal callForProposal = ctx.callForProposalList.getCallForProposal(callForProposalKey);

        // Validate current owner
        if (!callForProposal.getInitiator().equals(currentOwner)) {
            throw new RuntimeException("CallForProposal " + issuer + id + " is not owned by " + currentOwner);
        }

        // First buy moves state from ISSUED to TRADING
        if (callForProposal.isCreated()) {
            callForProposal.setCalling();
        }

        // Check callForProposal is not already REDEEMED
        if (callForProposal.isCalling()) {
            callForProposal.setInitiator(newOwner);
        } else {
            throw new RuntimeException(
                    "CallForProposal " + issuer + id + " is not trading. Current state = " + callForProposal.getState());
        }

        // Update the callForProposal
        ctx.callForProposalList.updateCallForProposal(callForProposal);
        return callForProposal;
    }

    /**
     * Redeem commercial callForProposal
     *
     * @param {Context} ctx the transaction context
     * @param {String} issuer commercial callForProposal issuer
     * @param {Integer} id callForProposal number for this issuer
     * @param {String} redeemingOwner redeeming owner of callForProposal
     * @param {String} redeemDateTime time callForProposal was redeemed
     */
    @Transaction
    public CallForProposal call(CallForProposalContext ctx, String id, String initiator) {

        String callForProposalKey = CallForProposal.makeKey(new String[] { id });

        CallForProposal callForProposal = ctx.callForProposalList.getCallForProposal(callForProposalKey);

        // Check callForProposal is not REDEEMED
        if (callForProposal.isEnded()) {
            throw new RuntimeException("CallForProposal " + id + " is ended");
        }

        // Verify that the redeemer owns the commercial callForProposal before redeeming it
        if (callForProposal.getInitiator().equals(initiator)) {
            callForProposal.setCalling();
        } else {
            throw new RuntimeException("Call for proposal is not initiated by" + initiator);
        }

        ctx.callForProposalList.updateCallForProposal(callForProposal);
        return callForProposal;
    }

}
