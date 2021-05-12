import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import java.util.List;
import java.util.ArrayList;

@DataType
public final class CallForProposal {

    @Property()
    private final String id;

    @Property()
    private final String initiator;

    @Property()
    private final String status;

    @Property()
    private final ArrayList<Partecipant> partecipants;

    public String getId() {
        return id;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getStatus() {
        return status;
    }

    public ArrayList<Partecipant> getPartecipants() {
        return partecipants;
    }

    public CallForProposal(@JsonProperty("id") final String id, @JsonProperty("initiator") final String initiator,
                           @JsonProperty("status") final String status, @JsonProperty("partecipants") final ArrayList<Partecipant> partecipants) {
        this.id = id;
        this.initiator = initiator;
        this.status = status;
        this.partecipants = partecipants;
    }
}
