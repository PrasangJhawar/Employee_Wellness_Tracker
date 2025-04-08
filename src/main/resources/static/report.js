document.addEventListener("DOMContentLoaded", function() {
    const BASE_URL = 'http://localhost:8080';
    const reportForm = document.getElementById("reportForm");
    const reportList = document.getElementById("reportList");

    const adminId = sessionStorage.getItem("employeeId");
    const adminName = sessionStorage.getItem("employeeName");

    if (!adminId || !adminName) {
        alert("Admin not logged in. Redirecting...");
        window.location.href = "login.html"; 
    }


    function loadReports() {
        fetch(`${BASE_URL}/reports`)
            .then(response => response.json())
            .then(data => {
                reportList.innerHTML = "";
                data.forEach(report => {
                    const row = document.createElement("tr");
                    row.innerHTML = `
                        <td>${report.id}</td>
                        <td>${report.reportName}</td>
                        <td>${report.generatedBy}</td>
                        <td>
                            <button class="download-btn" onclick="downloadCSV(${report.id})">CSV</button>
                            <button class="download-btn" onclick="downloadPDF(${report.id})">PDF</button>
                        </td>
                    `;
                    reportList.appendChild(row);
                });
            })
            .catch(error => console.error("Error loading reports:", error));
    }

    reportForm.addEventListener("submit", function(event) {
        event.preventDefault();

        const department = document.getElementById("department").value;
        const location = document.getElementById("location").value;
        let startDate = document.getElementById("startDate").value;
        let endDate = document.getElementById("endDate").value;

        // valid date format
        startDate = startDate ? new Date(startDate).toISOString().slice(0, 19) : null;
        endDate = endDate ? new Date(endDate).toISOString().slice(0, 19) : null;

        const params = new URLSearchParams();
        params.append("adminName", adminName);
        if (department) params.append("department", department);
        if (location) params.append("location", location);
        if (startDate) params.append("startDate", startDate);
        if (endDate) params.append("endDate", endDate);

        fetch(`${BASE_URL}/reports/generate?${params.toString()}`, {
            method: "POST"
        })
        .then(response => response.json())
        .then(() => {
            alert("Report generated successfully!");
            loadReports();
        })
        .catch(error => console.error("Error generating report:", error));
    });

    window.downloadCSV = function(reportId) {
        fetch(`${BASE_URL}/reports/${reportId}/export/csv`)
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = `report_${reportId}.csv`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
            })
            .catch(error => console.error("Error downloading CSV:", error));
    };

    window.downloadPDF = function(reportId) {
        fetch(`${BASE_URL}/reports/${reportId}/export/pdf`)
            .then(response => response.blob())
            .then(blob => {
                const url = window.URL.createObjectURL(blob);
                const a = document.createElement("a");
                a.href = url;
                a.download = `report_${reportId}.pdf`;
                document.body.appendChild(a);
                a.click();
                document.body.removeChild(a);
            })
            .catch(error => console.error("Error downloading PDF:", error));
    };

    loadReports();
});
