package edu.canisius.csc213.complaints.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.bean.CsvToBeanBuilder;
import edu.canisius.csc213.complaints.model.Complaint;
import jakarta.annotation.PostConstruct;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles loading of complaints and embedding data,
 * and returns a fully hydrated list of Complaint objects.
 */
public class ComplaintLoader {
    private static List<Complaint> complaints;
    /**
     * Loads complaints from a CSV file and merges with embedding vectors from a JSONL file.
     *
     * @param csvPath    Resource path to the CSV file
     * @param jsonlPath  Resource path to the JSONL embedding file
     * @return A list of Complaint objects with attached embedding vectors
     * @throws Exception if file reading or parsing fails
     */
    public static List<Complaint> loadComplaintsWithEmbeddings(String csvPath, String jsonlPath) throws Exception {
        InputStream csvInputStream = ComplaintLoader.class.getResourceAsStream(csvPath);
    if (csvInputStream == null) {
        throw new FileNotFoundException("CSV file not found at path: " + csvPath);
    }
    List<Complaint> complaints = new CsvToBeanBuilder<Complaint>(new InputStreamReader(csvInputStream, StandardCharsets.UTF_8))
            .withType(Complaint.class)
            .build()
            .parse();

    // Load embeddings from the JSONL file
    InputStream jsonlInputStream = ComplaintLoader.class.getResourceAsStream(jsonlPath);
    if (jsonlInputStream == null) {
        throw new FileNotFoundException("JSONL file not found at path: " + jsonlPath);
    }
    Map<Long, double[]> embeddings = new HashMap<>();
ObjectMapper objectMapper = new ObjectMapper();
BufferedReader reader = new BufferedReader(new InputStreamReader(jsonlInputStream, StandardCharsets.UTF_8));

String line;
while ((line = reader.readLine()) != null) {
    if (line.trim().isEmpty()) {
        continue; // Skip empty lines
    }
    try {
        // Parse each line into a ParsedEntry object
        EmbeddingLoader.ParsedEntry entry = objectMapper.readValue(line, EmbeddingLoader.ParsedEntry.class);
        embeddings.put(entry.complaintId, entry.embedding);
    } catch (Exception e) {
        System.err.println("Failed to parse line: " + line);
        e.printStackTrace();
    }
}

    // Merge embeddings into complaints
    ComplaintMerger.mergeEmbeddings(complaints, embeddings);

    return complaints;
    }
}