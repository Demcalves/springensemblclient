package ensembl.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ensembl.entity.GeneTranscript;
import ensembl.repository.EnsemblRepository;

@Service
public class GeneService {
    private EnsemblRepository ensemblRepository;

    //method injector
    @Autowired
    public void setEnsemblRepository(EnsemblRepository ensemblRepository){
        this.ensemblRepository = ensemblRepository;
    }

    public boolean checkSpeciesName(String species_name){
        List<String> species_names_list = ensemblRepository.getAllSpecies();
        for (String list_name : species_names_list){
            if(species_name.equals(list_name)){
                return true;
            }
        }
        return false;
    }

    public boolean checkGeneName(String gene_name){
        List<String> genes_names_list = ensemblRepository.getAllGenes();
        for (String list_name : genes_names_list){
            if(gene_name.equals(list_name)){
                return true;
            }
        }
        return false;
    }

    public List<GeneTranscript> getAllTranscripts(String species_name, String gene_name){
        Optional<List<GeneTranscript>> optional_list_gene_transcripts = ensemblRepository.findAllByGeneAndSpeciesName(species_name, gene_name);
        return optional_list_gene_transcripts.orElse(null);
    }

    public GeneTranscript getTranscriptById(String transcript_id){
        Optional<GeneTranscript> optional_gene_transcript = ensemblRepository.findTranscriptById(transcript_id);
        return optional_gene_transcript.orElse(null);
    }

}
