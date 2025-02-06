package ensembl.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="gene_transcripts")
public class GeneTranscript {


    @Id
    @Column(name = "transcript_id", nullable = false)
    private String transcript_id;

    @Column(name = "gene_id", nullable = false)
    private String gene_id;

    @Column(name = "gene_name", nullable = false)
    private String gene_name;

    @Column(name = "species_name", nullable = false)
    private String species_name;

    @Column(name = "biotype", nullable = false)
    private String biotype;

    @Column(name = "sequence_text", unique = true)
    private String sequence_text;

    //default constructor for JPA
    public GeneTranscript(){};

    // set up constructor
    public GeneTranscript(String gene_id, String gene_name, String species_name, String transcript_id, String biotype, String sequence_text){
        this.gene_id = gene_id;
        this.gene_name = gene_name;
        this.species_name = species_name;
        this.transcript_id = transcript_id;
        this.biotype = biotype;
        this.sequence_text = sequence_text;
    }
    // Getter functions

    public String getGeneId(){
        return gene_id;
    }

    public String getGeneName(){
        return gene_name;
    }

    public String getSpeciesName(){
        return species_name;
    }

    public String getTranscriptId(){
        return transcript_id;
    }

    public String getBioType(){
        return biotype;
    }

    public String getSequenceText(){
        return sequence_text;
    }

    // lets create an override toString for debugging to mimic the SQL database
    @Override
    public String toString(){
        return "Gene Info{" +
                "Gene Id="+ gene_id +
                ", Gene_Name"+ gene_name +
                ", Species_Name"+ species_name +
                ", Transcript_id" + transcript_id +
                "}";
    }
}
