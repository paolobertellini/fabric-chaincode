import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.List;

@DataType
public final class Cfp {

    @Property()
    private final String id;

    @Property()
    private final String initiator;

    @Property
    private final String status;

    public String getId() {
        return id;
    }

    public String getInitiator() {
        return initiator;
    }

    public String getStatus() {
        return status;
    }

    public Cfp(@JsonProperty("id") final String id, @JsonProperty("initiator") final String initiator,
                     @JsonProperty("status") final String status) {
        this.id = id;
        this.initiator = initiator;
        this.status = status;
    }
}
