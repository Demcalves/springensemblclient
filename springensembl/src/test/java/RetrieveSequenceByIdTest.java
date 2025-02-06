import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import ensembl.EnsemblClient;

public class RetrieveSequenceByIdTest{
    ApplicationContext app;
    HttpClient web_client;
    ObjectMapper object_mapper;

    /**
     * Before every test, restart the Javalin app, and create a new webclient and ObjectMapper
     * @throws InterruptedException
     */
    @BeforeEach
    public void setUp() throws InterruptedException {
        web_client = HttpClient.newHttpClient();
        object_mapper = new ObjectMapper();
        String[] args  = new String[] {};
        app = SpringApplication.run(EnsemblClient.class, args);
        Thread.sleep(500);

    }

    @AfterEach
    public void tearDown() throws InterruptedException {
        Thread.sleep(500);
        SpringApplication.exit(app);
    }

    @Test
    public String getSequenceGivenIdMessageFound() throws IOException, InterruptedException{
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://rest.ensembl.org/sequence/id/ENSG00000157764?"))
                .build();
        HttpResponse<String> response = web_client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        Assertions.assertEquals(200, status, "Expected Status Code 200 - Actual Code was: " + status);
        String result = object_mapper.readValue(response.body(), String.class);
        return result;
    }
}