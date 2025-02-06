package ensembl;

import java.util.Scanner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import net.minidev.json.JSONArray;


// Runs the client to communicate with Ensembl
@SpringBootApplication
public class EnsemblClient implements CommandLineRunner {

    private final RestTemplate restTemplate;

    @Autowired
    public EnsemblClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public static void main(String[] args) {
        SpringApplication.run(EnsemblClient.class, args);
    }

    @Override
    public void run(String... args) throws Exception{
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Enter species name (e.g. homo_sapiens): ");
            String species_name = scanner.nextLine().toLowerCase();

            System.out.println("Enter gene name (e.g., BRCA1): ");
            String gene_name = scanner.nextLine().toUpperCase();

            String url = "http://localhost:8080/service/database/" + species_name + "/" + gene_name;

            ResponseEntity<JSONArray> response = restTemplate.getForEntity(url, JSONArray.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                System.out.println("Database Response: " + response.getBody());
            } else {
                System.out.println("No data found in the database. Attempting to fetch from Ensembl...");
                String url_gene_id = "http://localhost:8080/ensembl/" + species_name + "/" + gene_name;
                ResponseEntity<String> gene_id_response = restTemplate.getForEntity(url_gene_id, String.class);

                if (gene_id_response.getStatusCode().is2xxSuccessful() && gene_id_response.getBody() != null) {
                    String response_body = gene_id_response.getBody();
                    String gene_id = extractEnsemblId(response_body);

                    if (gene_id != null) {
                        callRScript(species_name, gene_name, gene_id);
                        // Re-query database after running the R script
                        response = restTemplate.getForEntity(url, JSONArray.class);
                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && !response.getBody().isEmpty()) {
                            System.out.println("Updated Database Response: " + response.getBody());
                        } else {
                            System.out.println("Data still not found in the database.");
                        }
                    } else {
                        System.out.println("No valid Ensembl ID found in the response.");
                    }
                } else {
                    System.out.println("Gene ID request failed. Response: " + gene_id_response.getStatusCode());
                }
            }
        }
    }

    private String extractEnsemblId(String response_body) {
        // Check if the response contains the Ensembl ID.
        if (response_body.contains("Ensembl ID: ")) {
            return response_body.split("Ensembl ID: ")[1].trim();
        }
        return null;
    }

    private void callRScript(String species_name, String gene_name, String gene_id) {
        try {
            String command = "Rscript C:\\Users\\A Dahik\\Documents\\BioinformaticPortfolio\\geneclusteranalysis\\src\\scripts\\grab_ensembl_dataset.R " 
                + species_name + " " + gene_name + " " + gene_id;
            ProcessBuilder processBuilder = new ProcessBuilder(command.split(" "));
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("R script executed successfully.");
            } else {
                System.out.println("R script execution failed.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing R script.");
        }
    }
}