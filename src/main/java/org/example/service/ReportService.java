package org.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.example.model.Report;
import org.example.model.Response;
import org.example.repository.ReportRepository;
import org.example.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ReportRepository reportRepository;

    public Report generateReport(String adminName, String department, String location, LocalDateTime startDate, LocalDateTime endDate) {
        List<Response> responses = responseRepository.findAll().stream()

                //filtering logic
                .filter(response -> {
                    boolean departmentMatch = (department == null || department.isEmpty() ||
                            (response.getEmployee() != null && department.equalsIgnoreCase(response.getEmployee().getDepartment())));
                    boolean locationMatch = (location == null || location.isEmpty() ||
                            (response.getEmployee() != null && (response.getEmployee().getLocation() == null || location.equalsIgnoreCase(response.getEmployee().getLocation()))));
                    boolean dateMatch = (startDate == null || response.getSubmittedAt().isAfter(startDate) || response.getSubmittedAt().isEqual(startDate)) &&
                            (endDate == null || response.getSubmittedAt().isBefore(endDate) || response.getSubmittedAt().isEqual(endDate));
                    return departmentMatch && locationMatch && dateMatch;
                })
                .toList();

        StringBuilder reportContent = new StringBuilder("Employee,Survey,Response,SubmittedAt\n");
        for (Response response : responses) {
            reportContent.append(String.join(",",
                            response.getEmployee().getName(),
                            response.getSurvey().getTitle(),
                            response.getResponseText(),
                            response.getSubmittedAt().toString()))
                    .append("\n");
        }

        //getters and setters
        Report report = new Report();
        report.setReportName("Employee Wellness Report");
        report.setGeneratedBy(adminName);
        report.setDepartmentFilter(department);
        report.setLocationFilter(location);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportData(reportContent.toString());

        return reportRepository.save(report);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    public Report getReportById(Long id) {
        return reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
    }

    public String exportReportAsCSV(Long reportId) {
        Report report = getReportById(reportId);
        return report.getReportData();
    }

    //byte is used to store data in array of bytes(used for pdfs/images etc)
    public byte[] exportReportAsPDF(Long reportId) {
        Report report = getReportById(reportId);
        if (report.getReportData() == null || report.getReportData().isEmpty()) {
            throw new RuntimeException("Report data is empty, cannot generate PDF");
        }

        //byteArrayOutputStream allows to write data into byte array
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Employee Report").setBold().setFontSize(14));
            document.add(new Paragraph("\n"));

            String[] headers = {"Employee Name", "Survey", "Response", "Submitted At"};
            Table table = new Table(headers.length);
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
            }

            for (String line : report.getReportData().split("\n")) {
                String[] row = line.split(",");
                if (row.length == 4) {
                    for (String cellData : row) {
                        table.addCell(new Cell().add(new Paragraph(cellData)));
                    }
                }
            }

            document.add(table);
            document.close();
            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}