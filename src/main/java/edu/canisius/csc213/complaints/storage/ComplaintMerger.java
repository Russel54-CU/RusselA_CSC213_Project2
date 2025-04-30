package edu.canisius.csc213.complaints.storage;

import edu.canisius.csc213.complaints.model.Complaint;

import com.opencsv.bean.CsvToBeanBuilder;

import java.util.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class ComplaintMerger {

    /**
     * Matches complaints to their corresponding embedding vectors by complaint ID.
     *
     * @param complaints List of complaints (from CSV)
     * @param embeddings Map from complaintId to embedding vector (from JSONL)
     */
    public static void mergeEmbeddings(List<Complaint> complaints, Map<Long, double[]> embeddings) {
        for (Complaint complaint : complaints) {
            // Get the complaint ID
            Long complaintId = complaint.getComplaintId();
    
            // Check if the embeddings map contains the ID
            if (embeddings.containsKey(complaintId)) {
                // Retrieve the embedding vector
                double[] embedding = embeddings.get(complaintId);
    
                // Set the embedding vector for the complaint
                complaint.setEmbedding(embedding);
            } else {
                // Handle the case where no embedding is found (optional)
                System.err.println("No embedding found for complaint ID: " + complaintId);
            }
        }
    }
}
