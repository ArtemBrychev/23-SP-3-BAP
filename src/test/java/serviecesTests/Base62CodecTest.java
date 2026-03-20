package serviecesTests;

import org.example.services.Base62Codec;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class Base62CodecTest {

    @DisplayName("Проверка кодировки")
    @Test
    public void encodeTest(){
        long value = new Random().nextLong(0, Long.MAX_VALUE);

        String encodedValue = Base62Codec.encode(value);
        assertThat(encodedValue).isNotNull();
        assertThat(encodedValue).isEqualTo(Base62Codec.encode(value));
    }

    @DisplayName("Проверка декодировки")
    @Test
    public void decodeTest(){
        long value = new Random().nextLong(0, Long.MAX_VALUE);

        String encodedValue = Base62Codec.encode(value);
        long decodedValue = Base62Codec.decode(encodedValue);
        assertThat(decodedValue).isEqualTo(value);
    }

}
