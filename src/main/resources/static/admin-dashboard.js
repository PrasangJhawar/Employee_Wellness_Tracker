document.addEventListener("DOMContentLoaded", function () {
    fetchEmployees();
    fetchLiveSurveys();

    document.getElementById("reports-btn").addEventListener("click", function() {
        window.location.href = "reports.html"; // Redirect to reports.html
    });

    
    document.getElementById("survey-form").addEventListener("submit", createSurvey);
});

function fetchEmployees() {
    fetch("http://localhost:8080/employees")
    .then(response => {
        if (!response.ok) {
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        return response.json();
    })
    .then(data => {
        const employeeTableBody = document.getElementById("employee-table-body");
        employeeTableBody.innerHTML = "";
        
        console.log("Employees data fetched:", data);

        data.forEach(employee => {
            const row = document.createElement("tr");

            console.log(`Processing Employee ID: ${employee.id}, Name: ${employee.name}, Admin: ${employee.admin}`);

            if (employee.admin) {  
                row.innerHTML = `
                    <td>${employee.id}</td>
                    <td>${employee.name}</td>
                    <td>${employee.email}</td>
                    <td>Admin</td>
                `;
            } else {
                row.innerHTML = `
                    <td>${employee.id}</td>
                    <td>${employee.name}</td>
                    <td>${employee.email}</td>
                    <td>
                        <button onclick="promoteToAdmin('${employee.id}', this)" style="margin: 8px;">Promote to Admin</button>
                        <button onclick="deleteEmployee('${employee.id}')" style="margin: 8px;">Remove</button>
                    </td>
                `;
            }

            employeeTableBody.appendChild(row);
        });
    })
    .catch(error => {
        console.error("Error fetching employees:", error.message);
        alert("Failed to fetch employees. Please check the console for more details.");
    });
}


function fetchLiveSurveys() {
    fetch("http://localhost:8080/surveys")
    .then(response => response.json())
    .then(data => {
        const liveSurveysTableBody = document.getElementById("live-surveys-table-body");
        liveSurveysTableBody.innerHTML = "";
        
        data.forEach(survey => {
            if (survey.active) {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${survey.id}</td>
                    <td>${survey.title}</td>
                    <td>${survey.description}</td>
                    <td>${survey.active ? "Live" : "Inactive"}</td>
                `;
                liveSurveysTableBody.appendChild(row);
            }
        });
    })
    .catch(error => console.error("Error fetching surveys:", error));
}

function createSurvey(event) {
    event.preventDefault();

    const title = document.getElementById("survey-title").value;
    const description = document.getElementById("survey-description").value;
    const questions = Array.from(document.querySelectorAll(".survey-question")).map(input => ({
        text: input.value
    }));

    const surveyData = { title, description, questions };

    fetch("http://localhost:8080/surveys", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(surveyData)
    })
    .then(response => response.json())
    .then(() => {
        alert("Survey created successfully!");
        document.getElementById("survey-form").reset();
        document.getElementById("questions-container").innerHTML = `<input type="text" class="survey-question" placeholder="Enter question" required>`;
        fetchLiveSurveys();
    })
    .catch(error => {
        console.error("Error creating survey:", error);
        alert("Failed to create survey: " + error.message);
    });
}

function addQuestion() {
    const container = document.getElementById("questions-container");

    const lastQuestion = container.querySelector(".survey-question:last-of-type");
    if (lastQuestion && lastQuestion.value.trim() === "") {
        alert("Please enter a question before adding a new one.");
        return;
    }

    const questionWrapper = document.createElement("div");
    questionWrapper.className = "question-wrapper";

    const input = document.createElement("input");
    input.type = "text";
    input.className = "survey-question";
    input.placeholder = "Enter question";
    input.required = true;

    const deleteButton = document.createElement("button");
    deleteButton.textContent = "Delete";
    deleteButton.className = "delete-question";
    deleteButton.onclick = function () {
        container.removeChild(questionWrapper);
    };

    questionWrapper.appendChild(input);
    questionWrapper.appendChild(deleteButton);
    container.appendChild(questionWrapper);
}



function deleteEmployee(employeeId) {
    if (confirm("Are you sure you want to remove this employee?")) {
        fetch(`http://localhost:8080/employees/${employeeId}`, { method: "DELETE" })
        .then(response => {
            if (response.ok) fetchEmployees();
        });
    }
}

function promoteToAdmin(employeeId, button) {
    fetch(`http://localhost:8080/employees/${employeeId}/promote`, { 
        method: "PUT", 
        headers: { "Content-Type": "application/json" }
    })
    .then(response => {
        if (response.ok) {
            console.log(`Employee ${employeeId} promoted`);

            
            const parentTd = button.parentElement;
            if (parentTd) {
                parentTd.innerHTML = "Admin";
            }
        }
    })
    .catch(error => console.error("Error promoting employee:", error));
}

function logout() {
    window.location.href = "login.html";
    sessionStorage.clear();
}


//reports
const reports = document.getElementById("reports-btn");