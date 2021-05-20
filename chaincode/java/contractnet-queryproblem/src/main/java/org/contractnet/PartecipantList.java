/*
SPDX-License-Identifier: Apache-2.0
*/

package org.contractnet;

import org.contractnet.ledgerapi.StateList;
import org.hyperledger.fabric.contract.Context;

public class PartecipantList {

    private StateList stateList;

    public PartecipantList(Context ctx) {
        this.stateList = StateList.getStateList(ctx, PartecipantList.class.getSimpleName(), Partecipant::deserialize);
    }

    public PartecipantList addPartecipant(Partecipant partecipant) {
        stateList.addState(partecipant);
        return this;
    }

    public Partecipant getPartecipant(String partecipantKey) {
        return (Partecipant) this.stateList.getState(partecipantKey);
    }

    public PartecipantList updatePartecipant(Partecipant partecipant) {
        this.stateList.updateState(partecipant);
        return this;
    }

}
