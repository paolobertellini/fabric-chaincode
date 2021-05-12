import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

@DataType
public final class Partecipant {

    @Property()
    private final String name;

    @Property()
    private String state;


    public String getName() {
        return name;
    }

    public String getState() {
        return state;
    }

    public void setState(String newState) {
        this.state = newState;
    }

    public Partecipant(@JsonProperty("name") final String name, @JsonProperty("state") final String state) {
        this.name = name;
        this.state = state;
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
