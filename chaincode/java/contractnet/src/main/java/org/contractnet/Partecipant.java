/*
 * SPDX-License-Nameentifier: Apache-2.0
 */

package org.contractnet;

import org.contractnet.ledgerapi.State;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;

import static java.nio.charset.StandardCharsets.UTF_8;

@DataType()
public class Partecipant extends State {

    // Enumerate partecipant state values
    public final static String WAITING = "WAITING";
    public final static String CALLED = "CALLED";
    public final static String ENDED = "ENDED";

    @Property()
    private String state="";

    public String getState() {
        return state;
    }

    public Partecipant setState(String state) {
        this.state = state;
        return this;
    }

    @JSONPropertyIgnore()
    public boolean isWaiting() {
        return this.state.equals(Partecipant.WAITING);
    }

    @JSONPropertyIgnore()
    public boolean isCalled() {
        return this.state.equals(Partecipant.CALLED);
    }

    @JSONPropertyIgnore()
    public boolean isEnded() {
        return this.state.equals(Partecipant.ENDED);
    }

    public Partecipant setWaiting() {
        this.state = Partecipant.WAITING;
        return this;
    }

    public Partecipant setCalled() {
        this.state = Partecipant.CALLED;
        return this;
    }

    public Partecipant setEnded() {
        this.state = Partecipant.ENDED;
        return this;
    }

    @Property()
    private String name;

    public Partecipant() {
        super();
    }

    public Partecipant setKey() {
        this.key = State.makeKey(new String[] { this.name });
        return this;
    }

    public String getName() {
        return name;
    }

    public Partecipant setName(String name) {
        this.name = name;
        return this;
    }


    @Override
    public String toString() {
        return "Paper::" + this.key + "   " + this.getName() + " " + getState();
    }

    /**
     * Deserialize a state data to partecipant
     *
     * @param {Buffer} data to form back into the object
     */
    public static Partecipant deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String name = json.getString("name");
        String state = json.getString("state");
        return createInstance(name, state);
    }

    public static byte[] serialize(Partecipant paper) {
        return State.serialize(paper);
    }

    /**
     * Factory method to create a partecipant object
     */
    public static Partecipant createInstance(String name, String state) {
        return new Partecipant().setName(name).setKey().setState(state);
    }


}
