package ensembl.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import net.minidev.json.JSONArray;

@Service
public class EnsemblService {
    private static final Logger logger = LoggerFactory.getLogger(EnsemblService.class);
    private static final String ENSEMBL_API_URL_SEQUENCE = "https://rest.ensembl.org/sequence/id/";
    private static final String ENSEMBL_API_URL_OVERLAP = "https://rest.ensembl.org/overlap/id/";
    private static final String ENSEMBL_API_URL_ID = "https://rest.ensembl.org/xrefs/symbol/";

    private final RestTemplate rest_template; 

    @Autowired
    public EnsemblService(RestTemplate rest_template){
        this.rest_template = rest_template;
    }

    public String fetchRawGeneData(String ensembl_id){
        //proper URL formatting in case the API was picky
        String url = UriComponentsBuilder.fromHttpUrl(ENSEMBL_API_URL_SEQUENCE+ensembl_id)
            .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "text/plain");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> response = rest_template.exchange(url, HttpMethod.GET, entity, String.class);
            return response.getBody();
            
        } catch (Exception e) {
            logger.warn("Error fetching gene data: "+e.getMessage());
            return null; 
        }
    }

    public JSONArray fetchGeneOverlap(String ensembl_id){
        String url = UriComponentsBuilder.fromHttpUrl(ENSEMBL_API_URL_OVERLAP+ensembl_id)
            .queryParam("feature", "gene")
            .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JSONArray> response = rest_template.exchange(url, HttpMethod.GET, entity, JSONArray.class);
            return response.getBody();
            
        } catch (Exception e) {
            logger.warn("Error fetching overlap data: "+e.getMessage());
            return null; 
        }
    }

    public JSONArray fetchEnsemblId(String species_name, String gene_name){
        String url = UriComponentsBuilder.fromHttpUrl(ENSEMBL_API_URL_ID+species_name+"/"+gene_name)
            .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<JSONArray> response = rest_template.exchange(url, HttpMethod.GET, entity, JSONArray.class);
            return response.getBody();
            
        } catch (Exception e) {
            logger.warn("Error fetching ensemblID data: "+e.getMessage());
            return null; 
        }
    }
    
}