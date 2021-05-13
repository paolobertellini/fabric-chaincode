import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import org.json.JSONObject;
import org.json.JSONPropertyIgnore;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.ArrayList;

import ledgerapi.State;

@DataType
public final class CallForProposal extends State {

    @Property()
    private String state="";

    public String getState() {
        return state;
    }

    public CallForProposal setState(String state) {
        this.state = state;
        return this;
    }

    public static CallForProposal deserialize(byte[] data) {
        JSONObject json = new JSONObject(new String(data, UTF_8));

        String state = json.getString("state");
        return createInstance(state);
    }

    public static byte[] serialize(CallForProposal cfp) {
        return State.serialize(cfp);
    }

    public static CallForProposal createInstance(String state) {
        return new CallForProposal().setState(state);
    }
}
