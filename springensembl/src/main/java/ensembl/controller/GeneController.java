package ensembl.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ensembl.entity.GeneTranscript;
import ensembl.service.EnsemblService;
import ensembl.service.GeneService;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;



@RestController
@RequestMapping("/service")

public class GeneController {
    private EnsemblService ensemblService;
    private GeneService geneService;

    @Autowired
    public void setServices(EnsemblService ensemblService, GeneService geneService){
        this.ensemblService = ensemblService;
        this.geneService = geneService;
    }
    @GetMapping("/ensembl/sequence/{ensembl_id}")
    public ResponseEntity<String> requestGene(@PathVariable String ensembl_id){
        String gene = ensemblService.fetchRawGeneData(ensembl_id);
        if(gene == null){
            return ResponseEntity.status(404).header("Status", "No Gene Sequence Found").body(gene);
        }else{
            return ResponseEntity.status(200).header("Status", "Gene Sequence Output: ").body(gene);
        }
    }

    @GetMapping("/ensembl/overlap/{ensembl_id}")
    public ResponseEntity<JSONArray> requestOverlap(@PathVariable String ensembl_id){
        JSONArray output = ensemblService.fetchGeneOverlap(ensembl_id);
        if(output==null){
            return ResponseEntity.status(404).header("Status", "Error fetching overlap data: Ensembl_Id not valid").body(output);
        }else{
            return ResponseEntity.status(200).header("Status", "Overlap Output: ").body(output);
        }
    }
    
    @GetMapping("/ensembl/ensemblid/{species_name}/{gene_name}")
    public ResponseEntity<String> requestEnsemblId(@PathVariable String species_name, @PathVariable String gene_name){
        JSONArray output = ensemblService.fetchEnsemblId(species_name, gene_name);
        if(output==null || output.isEmpty()){
            return ResponseEntity.status(404).header("Status", "Species or Gene name not found").body(null);
        }else{
            // iterate over the JSON Array and extract ENSG value
            String ensembl_id = "";
            for (Object obj : output){
                // assuming each object in the JSON Array is a JSONOBject with keys "type" and "id"
                if (obj instanceof JSONObject){
                    JSONObject json_obj = (JSONObject) obj;
                    //extract the 'id'; field from the JSON object
                    if (json_obj.containsKey("id")){
                        String value = json_obj.getAsString("id");
                        if(value.substring(0, 4).equals("ENSG")){
                            ensembl_id = value;
                            break;
                        }
                    }
                }
            }
            if(!ensembl_id.isEmpty()){
                return ResponseEntity.status(200).header("Status", "Ensembl ID: ").body(ensembl_id);
            }else{
                return ResponseEntity.status(404).header("Status", "Ensembl ID not found").body("Ensembl ID not found");
            }
        }
    }

    @GetMapping("/database/{species_name}/{gene_name}")
    public ResponseEntity<List<GeneTranscript>> retrieveAllTranscripts(@PathVariable String species_name, @PathVariable String gene_name){
        Boolean is_species = geneService.checkSpeciesName(species_name);
        Boolean is_gene = geneService.checkGeneName(gene_name);
        if(is_gene && is_species){
            List<GeneTranscript> output = geneService.getAllTranscripts(species_name, gene_name);
            if(output==null){
                // decide between a redirect or server error response
                // 204 - no content found
                return ResponseEntity.status(204).header("Status", "String inputs valid, however the database does not contain values").body(output);
            }else{
                return ResponseEntity.status(200).header("Status", "List of all transcripts belonging to Species Gene Locus").body(output);
            }
        }else{
            // consider redirect status 
            return ResponseEntity.status(400).header("Status", "String Inputs not found in database, make a web call to the Ensembl REST API to verify inputs").body(null);
        }
    }
    @GetMapping("/database/transcript_id/{transcript_id}")
    public ResponseEntity<GeneTranscript> retrieveTranscriptId(@PathVariable String transcript_id){
        GeneTranscript transcript_target = geneService.getTranscriptById(transcript_id);
        if (transcript_target != null){
            return ResponseEntity.status(200).header("Status", "Gene Transcript found in database, results: ").body(transcript_target);
        }else{
            return ResponseEntity.status(400).header("Status", "Transcript Id not found").body(null);
        }
    }

}
