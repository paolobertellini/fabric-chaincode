import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
public final class CfpTest {

    @Test
    public void isEqual(){
        Cfp cfp = new Cfp("Cfp001", "Paolo", "open");
        assertThat(cfp).isEqualTo(cfp);
    }

    @Test
    public void nonEqual(){
        Cfp cfp = new Cfp("Cfp001", "Paolo", "open");
        Cfp otherCfp = new Cfp("Cfp001", "Paolo", "close");
        assertThat(cfp).isNotEqualTo(otherCfp);
    }
}
