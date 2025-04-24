package org.example.service;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Cell;
import org.example.model.EmployeeHealth;
import org.example.model.Report;
import org.example.model.Response;
import org.example.repository.EmployeeHealthRepository;
import org.example.repository.ReportRepository;
import org.example.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private EmployeeHealthRepository employeeHealthRepository; // Add this repository to fetch employee health data

    public Report generateReport(String adminName, String department, String location, LocalDateTime startDate, LocalDateTime endDate) {
        List<Response> responses = responseRepository.findAll().stream()
                .filter(response -> {
                    boolean departmentMatch = (department == null || department.isEmpty() ||
                            (response.getEmployee() != null && department.equalsIgnoreCase(response.getEmployee().getDepartment())));
                    boolean locationMatch = (location == null || location.isEmpty() ||
                            (response.getEmployee() != null && (response.getEmployee().getLocation() == null || location.equalsIgnoreCase(response.getEmployee().getLocation()))));
                    boolean dateMatch = (startDate == null || !response.getSubmittedAt().isBefore(startDate)) &&
                            (endDate == null || !response.getSubmittedAt().isAfter(endDate));
                    return departmentMatch && locationMatch && dateMatch;
                })
                .collect(Collectors.toList());

        StringBuilder reportContent = new StringBuilder("Employee,Survey,Response,SubmittedAt,SurveyType,SurveyResult\n");

        //iterating over each response
        for (Response response : responses) {
            List<EmployeeHealth> healthList = employeeHealthRepository
                    .findByEmployeeIdAndSurveyId(response.getEmployee().getId(), response.getSurvey().getId());

            EmployeeHealth employeeHealth = healthList.isEmpty() ? null : healthList.get(0);

            String surveyType = employeeHealth != null ? employeeHealth.getSurveyType() : "Unknown";
            String surveyResult = employeeHealth != null ? employeeHealth.getSurveyResult() : "Unknown";

            reportContent.append(String.join(",",
                            response.getEmployee().getName(),
                            response.getSurvey().getTitle(),
                            response.getResponseText(),
                            response.getSubmittedAt().toString(),
                            surveyType,
                            surveyResult))
                    .append("\n");
        }

        Report report = new Report();
        report.setReportName("Employee Wellness Report");
        report.setGeneratedBy(adminName);
        report.setDepartmentFilter(department);
        report.setLocationFilter(location);
        report.setStartDate(startDate);
        report.setEndDate(endDate);
        report.setReportData(reportContent.toString());

        if (!responses.isEmpty()) {
            List<EmployeeHealth> healthList = employeeHealthRepository
                    .findByEmployeeIdAndSurveyId(responses.get(0).getEmployee().getId(), responses.get(0).getSurvey().getId());

            if (!healthList.isEmpty()) {
                EmployeeHealth firstEmployeeHealth = healthList.get(0);
                report.setSurveyType(firstEmployeeHealth.getSurveyType());
                report.setSurveyResult(firstEmployeeHealth.getSurveyResult());
            }
        }

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

        //ByteArrayOutputStream  allows to write data in byte array
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            PdfWriter writer = new PdfWriter(byteArrayOutputStream);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);

            document.add(new Paragraph("Employee Wellness Report")
                    .setBold()
                    .setFontSize(18)
                    .setTextAlignment(com.itextpdf.layout.properties.TextAlignment.CENTER));

            document.add(new Paragraph("Generated by: " + report.getGeneratedBy())
                    .setFontSize(12)
                    .setMarginTop(10));
            document.add(new Paragraph("Department: " + (report.getDepartmentFilter() != null ? report.getDepartmentFilter() : "All"))
                    .setFontSize(12));
            document.add(new Paragraph("Location: " + (report.getLocationFilter() != null ? report.getLocationFilter() : "All"))
                    .setFontSize(12));
            document.add(new Paragraph("Report Period: " + report.getStartDate() + " to " + report.getEndDate())
                    .setFontSize(12)
                    .setMarginBottom(15));

            String[] headers = {"Employee Name", "Survey", "Response", "Submitted At", "Survey Type", "Survey Result"};
            Table table = new Table(headers.length);
            for (String header : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(header).setBold()));
            }

            for (String line : report.getReportData().split("\n")) {
                String[] row = line.split(",");
                if (row.length == 6) {
                    for (String cellData : row) {
                        table.addCell(new Cell().add(new Paragraph(cellData).setFontSize(10)));
                    }
                }
            }

            document.add(table);

            document.add(new Paragraph("\n"));
            document.close();

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF report", e);
        }
    }
}