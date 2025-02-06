package ensembl.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ensembl.entity.GeneTranscript;

@Repository
//Gene Transcript is tied to an String representing TranscriptID
public interface EnsemblRepository extends JpaRepository<GeneTranscript, String> {
    
    @Query("SELECT DISTINCT species_name FROM gene_transcripts")
    List<String> getAllSpecies();

    @Query("SELECT DISTINCT gene_name FROM gene_transcripts")
    List<String> getAllGenes();

    @Query("SELECT gt FROM gene_transcripts WHERE gt.species_name = :species_name AND gt.gene_name = :gene_name")
    Optional<List<GeneTranscript>> findAllByGeneAndSpeciesName(String species_name, String gene_name);

    @Query("SELECT gt FROM gene_transcript gt WHERE gt.transcript_id = :transcript_id")
    Optional<GeneTranscript> findTranscriptById(String transcript_id);
}
