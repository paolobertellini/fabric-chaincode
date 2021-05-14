/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.contractnet;

import static java.nio.charset.StandardCharsets.UTF_8;

import org.contractnet.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;

@DataType()
public class CallForProposal extends State {

    // Enumerate commercial paper state values
    public final static String CREATED = "CREATED";
    public final static String CALLING = "CALLING";
    public final static String ENDED = "ENDED";

    @Property()
    private String state="";

    public String getState() {
        return state;
    }

    public org.contractnet.CallForProposal setState(String state) {
        this.state = state;
        return this;
    }

    @JSONPropertyIgnore()
    public boolean isCreated() {
        return this.state.equals(org.contractnet.CallForProposal.CREATED);
    }

    @JSONPropertyIgnore()
    public boolean isCalling() {
        return this.state.equals(org.contractnet.CallForProposal.CALLING);
    }

    @JSONPropertyIgnore()
    public boolean isEnded() {
        return this.state.equals(org.contractnet.CallForProposal.ENDED);
    }

    public org.contractnet.CallForProposal setCreated() {
        this.state = org.contractnet.CallForProposal.CREATED;
        return this;
    }

    public org.contractnet.CallForProposal setCalling() {
        this.state = org.contractnet.CallForProposal.CALLING;
        return this;
    }

    public org.contractnet.CallForProposal setEnded() {
        this.state = org.contractnet.CallForProposal.ENDED;
        return this;
    }

    @Property()
    private String id;

    @Property()
    private String initiator;

    @Property()
    private String task;

    public CallForProposal() {
        super();
    }

    public org.contractnet.CallForProposal setKey() {
        this.key = State.makeKey(new String[] { this.id });
        return this;
    }

    public String getId() {
        return id;
    }

    public org.contractnet.CallForProposal setId(String id) {
        this.id = id;
        return this;
    }

    public String getInitiator() {
        return initiator;
    }

    public org.contractnet.CallForProposal setInitiator(String initiator) {
        this.initiator = initiator;
        return this;
    }

    public String getTask() {
        return task;
    }

    public org.contractnet.CallForProposal setTask(String task) {
        this.task = task;
        return this;
    }

    @Override
    public String toString() {
        return "Paper::" + this.key + "   " + this.getId() + " " + getInitiator() + " " + getTask() + " " + getState();
    }

    /**
     * Deserialize a state data to commercial paper
     *
     * @param {Buffer} data to form back into the object
     */
    public static org.contractnet.CallForProposal deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String initiator = json.getString("initiator");
        String id = json.getString("id");
        String task = json.getString("task");
        String state = json.getString("state");
        return createInstance(initiator, id, task, state);
    }

    public static byte[] serialize(org.contractnet.CallForProposal paper) {
        return State.serialize(paper);
    }

    /**
     * Factory method to create a commercial paper object
     */
    public static org.contractnet.CallForProposal createInstance(String initiator, String id, String task, String state) {
        return new org.contractnet.CallForProposal().setInitiator(initiator).setId(id).setKey().setTask(task).setState(state);
    }


}
