package edu.canisius.csc213.complaints.storage;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

//import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.*;
import java.util.*;

public class EmbeddingLoader {
    Map<Long, double[]> embeddingsByID = new HashMap<>();
    /**
     * Loads complaint embeddings from a JSONL (newline-delimited JSON) file.
     * Each line must be a JSON object with:
     * {
     *   "complaintId": <long>,
     *   "embedding": [<double>, <double>, ...]
     * }
     *
     * @param jsonlStream InputStream to the JSONL file
     * @return A map from complaint ID to its embedding vector
     * @throws IOException if the file cannot be read or parsed
     */
    public static Map<Long, double[]> loadEmbeddings(InputStream jsonlStream) throws IOException {
        Map<Long, double[]> embeddingsByID = new HashMap<>();
        ObjectMapper objectMapper = new ObjectMapper();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(jsonlStream));

        String line;
        while ((line = reader.readLine()) != null){
            ParsedEntry entry = objectMapper.readValue(line, ParsedEntry.class);
            embeddingsByID.put(entry.complaintId, entry.embedding);
        }

        return embeddingsByID;
    }
    
    static class ParsedEntry{
        @JsonProperty("id")
        public Long complaintId;
        public double[] embedding;
    }
}
