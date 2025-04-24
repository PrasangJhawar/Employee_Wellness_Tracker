document.addEventListener("DOMContentLoaded", function (){
    fetchEmployees();
    fetchLiveSurveys();

    document.getElementById("reports-btn").addEventListener("click", function(){
        window.location.href = "report.html"; // Redirect to reports.html
    });

    
    document.getElementById("survey-form").addEventListener("submit", createSurvey);
});

function fetchEmployees(){
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
                        <button onclick="editEmployee('${employee.id}')" style="margin: 8px;">Edit</button>
                    </td>
                `;
            }

            employeeTableBody.appendChild(row);
        });
    })
    .catch(error => {
        console.error("Error fetching employees:", error.message);
        alert("Failed to fetch employees");
    });
}


function fetchLiveSurveys() {
    fetch("http://localhost:8080/surveys")
    .then(response => response.json())
    .then(data => {
        const liveSurveysTableBody = document.getElementById("live-surveys-table-body");
        const viewSurveyButton = document.createElement("button");
        viewSurveyButton.textContent = "Take Survey";
        liveSurveysTableBody.innerHTML = "";
        
        
        data.forEach(survey => {
            if (survey.active) {
                const row = document.createElement("tr");
                row.innerHTML = `
                    <td>${survey.id}</td>
                    <td>${survey.title}</td>
                    <td>${survey.description}</td>
                    <td>${survey.active ? "Live" : "Inactive"}</td>
                    <td>
                        <button class="view-survey-btn">View Survey</button>
                        <!--<button class="participate-in-survey-btn">Participate</button>-->
                        <!--<button class="view-responses-btn">View Responses</button>-->
                    </td>
                `;

                const viewSurveyButton = row.querySelector(".view-survey-btn");
                const participateButton = row.querySelector(".participate-in-survey-btn");
                const viewResponsesButton = row.querySelector(".view-responses-btn");


                // viewResponsesButton.addEventListener("click", () => {
                //     window.location.href=`submitted-responses.html?id=${survey.id}`;
                // })

                // participateButton.addEventListener("click", ()=>{
                //     window.location.href=`survey-details.html?id=${survey.id}`;
                // })


                viewSurveyButton.addEventListener("click", () => {
                    //redirecting to view-survey.html with the survey ID as a query parameter
                    window.location.href = `view-survey.html?id=${survey.id}`;
                });
                

                liveSurveysTableBody.appendChild(row);
                
            }
        });
    })
    .catch(error => console.error("Error fetching surveys:", error));
}



function createSurvey(event){
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

function addQuestion(){
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
        fetch(`http://localhost:8080/employees/${employeeId}`, 
            { 
                method: "DELETE" 
            })
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

const isAdmin = sessionStorage.getItem("isAdmin");
if(!isAdmin){
    window.location.href="login.html";
}

//reports
const reports = document.getElementById("reports-btn");






//employee details editing 
async function editEmployee(employeeId) {
    const modal = document.getElementById("editEmployeeModal");
    modal.style.display = "block";

    try{
        //fetching the employees
        const response = await fetch(`http://localhost:8080/employees/${employeeId}`);
        if(!response.ok){
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        const employee = await response.json();

        //populating the modal
        document.getElementById("employee-name").value = employee.name;
        document.getElementById("employee-email").value = employee.email;
        document.getElementById("employee-department").value = employee.department;

        const form = document.getElementById("editEmployeeForm");
        form.onsubmit = function(event){
            event.preventDefault();

            const updatedData = {
                name: document.getElementById("employee-name").value,
                email: document.getElementById("employee-email").value,
                department: document.getElementById("employee-department").value
            };

            //PUT api request to update the employee
            fetch(`http://localhost:8080/auth/employees/${employeeId}`, {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(updatedData)
            })
            .then(response => {
                if(response.ok){
                    alert("Employee details updated successfully!");
                    fetchEmployees();
                    closeModal();
                }else{
                    alert("Failed to update employee details");
                }
            })
            .catch(error => {
                console.error("Error updating employee:", error);
                alert("Failed to update employee details");
            });
        };
    }catch(error){
        console.error("Error fetching employee data:", error);
        alert("Failed to fetch employee details");
    }
}

function closeModal(){
    const modal = document.getElementById("editEmployeeModal");
    modal.style.display = "none";
}

document.getElementById("closeModalBtn").addEventListener("click", closeModal);

window.addEventListener("click", function (event){
    const modal = document.getElementById("editEmployeeModal");
    if(event.target === modal){
        closeModal();
    }
});
