import org.example.configs.EnvConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class EnvConfigTest {

    @Test
    public void getTestValue(){
        String result = EnvConfig.get("ENV_TEST");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("env_test");
    }

    @Test
    public void getDbUrlTest(){
        String url = EnvConfig.get("DB_URL");
        assertThat(url).isEqualTo("jdbc:postgresql://postgres:5432/url_shortener_db");
    }

}
