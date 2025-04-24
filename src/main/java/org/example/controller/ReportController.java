package org.example.controller;

import org.example.model.Report;
import org.example.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/reports")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/generate")
    public Report generateReport(
            @RequestParam String adminName,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return reportService.generateReport(adminName, department, location, startDate, endDate);
    }

    @GetMapping
    public List<Report> getAllReports() {
        return reportService.getAllReports();
    }

    @GetMapping("/{id}")
    public Report getReportById(@PathVariable Long id) {
        return reportService.getReportById(id);
    }

    //exporting apis
    @GetMapping("/{id}/export/csv")
    public ResponseEntity<String> exportCSV(@PathVariable Long id) {
        String csvData = reportService.exportReportAsCSV(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.csv")
                .contentType(MediaType.TEXT_PLAIN)
                .body(csvData);
    }

    @GetMapping("/{id}/export/pdf")
    public ResponseEntity<byte[]> exportPDF(@PathVariable Long id) {
        try {
            byte[] pdfData = reportService.exportReportAsPDF(id);
            return ResponseEntity.ok()
                    //content disposition tells browser to download the file
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report.pdf")
                    //pdf = Application_Pdf
                    .contentType(MediaType.APPLICATION_PDF)

                    //body holds byte array(pdf data basically)
                    .body(pdfData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}