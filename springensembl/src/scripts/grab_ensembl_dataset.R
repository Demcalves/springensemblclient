library(biomaRt)
library(RPostgreSQL)  # Change from RSQLite to RPostgreSQL

# Function to retrieve species and gene name
# Function to retrieve species, gene name, and gene id
get_arguments <- function() {
  args <- commandArgs(trailingOnly = TRUE)
  species <- ifelse(length(args) > 0, args[1], "homo_sapiens")  # Default to Homo sapiens
  gene_name <- ifelse(length(args) > 1, args[2], "BRCA2")  # Default to BRCA2
  gene_id <- ifelse(length(args) > 2, args[3], "ENSG00000139618")  # Default to BRCA2 id
  return(list(species = species, gene_name = gene_name, gene_id = gene_id))
}

# Function to get BioMart dataset
get_dataset <- function(species = "hsapiens") {
  dataset_name <- paste0(species, "_gene_ensembl")
  mart <- tryCatch(
    useMart("ensembl", dataset = dataset_name),
    error = function(e) {
      cat("Error in connecting to BioMart:", e$message, "\n")
      return(NULL)
    }
  )
  return(mart)
}

# Define your species and gene ID
args <- get_arguments()
species <- args$species
gene_id <- args$gene_id
gene_name <- args$gene_name

# Create a PostgreSQL connection
# Make sure to update the database name, host, username, and password
# Connect to the PostgreSQL database
db <- tryCatch(
  dbConnect(RPostgreSQL::PostgreSQL(), 
            dbname = "gene_transcripts", 
            host = "localhost", 
            port = 5432, 
            user = "postgress", 
            password = ""),
  error = function(e) {
    cat("Error in database connection:", e$message, "\n")
    return(NULL)
  }
)

# Proceed only if database connection is successful
if (!is.null(db)) {
  # Query BioMart for transcript variants of the gene
  ensembl_dataset <- get_dataset(species)
  if (!is.null(ensembl_dataset)) {
    gene_transcripts <- tryCatch(
      getBM(
        attributes = c("ensembl_transcript_id", "transcript_biotype", "cdna"),
        filters = c("ensembl_gene_id"),
        values = gene_id,
        mart = ensembl_dataset
      ),
      error = function(e) {
        cat("Error in querying BioMart:", e$message, "\n")
        return(NULL)
      }
    )
    
    # Insert gene transcript data into the PostgreSQL database
    if (!is.null(gene_transcripts)) {
      for(i in 1:nrow(gene_transcripts)) {
        tryCatch(
          dbExecute(
            db,
            "INSERT INTO gene_transcripts (transcript_id, gene_id, gene_name, species_name, biotype, sequence_text)
             VALUES ($1, $2, $3, $4, $5, $6)",
            list(
              gene_transcripts$ensembl_transcript_id[i],
              gene_id,
              gene_name,
              species,
              gene_transcripts$transcript_biotype[i],
              gene_transcripts$cdna[i]
            )
          ),
          error = function(e) {
            cat("Error in inserting into the database:", e$message, "\n")
          }
        )
      }
    }
  }
  
  # Close the database connection
  dbDisconnect(db)
  print("Data successfully stored in PostgreSQL database.")
} else {
  cat("Database connection failed. Exiting script.\n")
}