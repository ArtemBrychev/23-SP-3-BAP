package servletsTests;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.example.services.Service;
import org.example.servlets.NewServlet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

//TODO: Там по последнему сообщению надо много чего исправить. То как рабоатет мок. Потом мок перезаписывается. МНого чего надо исправить

public class NewServletTest {

    private Server server;
    private int port;
    private String url = "oldUrlOldUrlOldUrlOldUrloldUrloldUrloldUrloldUrl";
    private Service service;

    @BeforeEach
    public void init() throws Exception {
        server = new Server(0);

        ServletContextHandler context = new ServletContextHandler();
        context.setContextPath("/");

        service = Mockito.mock(Service.class);
        Mockito.when(service.add(url)).thenReturn("newUrl");
        NewServlet servlet = new NewServlet(service);

        context.addServlet(new ServletHolder(servlet), "/addurl");

        server.setHandler(context);
        server.start();

        port = ((ServerConnector) server.getConnectors()[0]).getLocalPort();
    }

    @DisplayName("Отправка ссылки на сервис")
    @Test
    public void sendUrl() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:" + port + "/addurl").openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(
                "Content-Type",
                "application/x-www-form-urlencoded; charset=UTF-8"
        );


        try (OutputStream os = connection.getOutputStream()) {
            os.write(String.format("url=%s", url).getBytes(StandardCharsets.UTF_8));
        }

        int status = connection.getResponseCode();
        String contentType = connection.getHeaderField("Content-Type");

        BufferedReader reader;

        if (status >= 400) {
            reader = new BufferedReader(
                    new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8));
        } else {
            reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
        }

        String body = reader.lines().collect(Collectors.joining("\n"));


        assertThat(body).isEqualTo("newUrl");
        assertThat(status).isEqualTo(200);
        assertThat(contentType).contains("text/plain");
        assertThat(body).isEqualTo("newUrl");

        Mockito.verify(service).add(url);
    }

    @AfterEach
    public void destroy() throws Exception {
        server.stop();
        server.join();
    }

}
