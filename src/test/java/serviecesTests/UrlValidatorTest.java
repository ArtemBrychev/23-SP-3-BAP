package serviecesTests;

import org.example.services.UrlValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UrlValidatorTest {

    @DisplayName("Valid url with https/http")
    @Test
    public void validUrlProtocol(){
        String urls = "https://google.com";
        String url = "http://google.com";

        assertThat(UrlValidator.isValidExternalUrl(urls)).isTrue();
        assertThat(UrlValidator.isValidExternalUrl(url)).isTrue();
    }

    @DisplayName("Valid url without protocol")
    @Test
    public void validUrl(){
        String urls = "google.com";

        assertThat(UrlValidator.isValidExternalUrl(urls)).isTrue();
    }

    @DisplayName("Internal link")
    @Test
    public void invalidInternalLink(){
        String url1 = "http://127.0.0.1";
        assertThat(UrlValidator.isValidExternalUrl(url1)).isFalse();
        String url2 = "javascript:alert(1)";
        assertThat(UrlValidator.isValidExternalUrl(url2)).isFalse();
    }

    @DisplayName("Blank")
    @Test
    public void blank(){
        String url1 = "";
        assertThat(UrlValidator.isValidExternalUrl(url1)).isFalse();
    }


}
