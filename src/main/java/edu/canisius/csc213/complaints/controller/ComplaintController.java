package edu.canisius.csc213.complaints.controller;

import edu.canisius.csc213.complaints.model.Complaint;
import edu.canisius.csc213.complaints.service.ComplaintSimilarityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ComplaintController {

    private final List<Complaint> complaints;
    private final ComplaintSimilarityService similarityService;

    public ComplaintController(List<Complaint> complaints, ComplaintSimilarityService similarityService) {
        this.complaints = complaints;
        this.similarityService = similarityService;
    }
    @GetMapping("/search")
public String searchComplaints(@RequestParam(required = false) String company, Model model) {
    System.out.println("Search term: " + company); // Debug: Print the search term

    List<Complaint> filteredComplaints;

    if (company == null || company.isBlank()) {
        filteredComplaints = complaints; // Show all complaints if no company is specified
        System.out.println("No search term provided, please enter a company name."); // Debug: No search term
    } 
    else {
        filteredComplaints = complaints.stream()
            .filter(c -> c.getCompany().toLowerCase().contains(company.toLowerCase()))
            .toList();
    }

    if (filteredComplaints.isEmpty()) {
        model.addAttribute("message", "No complaints found for the specified company.");
        System.out.println("No complaints found for the company: " + company); // Debug: No results
    }


    model.addAttribute("filteredComplaints", filteredComplaints);
    model.addAttribute("company", company);

    return "search"; // This must match the name of the HTML file (search.html)
}

    @GetMapping("/complaint")
    public String showComplaint(@RequestParam(defaultValue = "0") int index, Model model) {
        int max = complaints.size();
        if (index < 0) index = 0;
        if (index >= max) index = max - 1;

        Complaint current = complaints.get(index);
        List<Complaint> similar = similarityService.findTop3Similar(current);

        model.addAttribute("complaint", current);
        model.addAttribute("similarComplaints", similar);
        model.addAttribute("prevIndex", index > 0 ? index - 1 : 0);
        model.addAttribute("nextIndex", index < max - 1 ? index + 1 : max - 1);

        return "complaint"; // ← This maps to complaint.html
    }
    // ...existing code...

    @GetMapping("/jumpToComplaint")
    public String jumpToComplaint(@RequestParam int complaintNumber) {
        int max = complaints.size();
        if (complaintNumber < 0 || complaintNumber >= max) {
        // Redirect to the first complaint if the input is invalid
            return "redirect:/complaint?index=0";
        }
    return "redirect:/complaint?index=" + complaintNumber;
    }

// ...existing code...
}
