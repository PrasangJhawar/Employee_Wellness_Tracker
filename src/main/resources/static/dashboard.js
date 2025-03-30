async function fetchSurveys(){
    try{
        const response = await fetch("http://localhost:8080/surveys");
        const surveys = await response.json();

        const employeeId = sessionStorage.getItem("employeeId");
        if(!employeeId){
            console.error("No employee ID found");
            window.location.href = "login.html";
            return;
        }

        const submittedResponse = await fetch(`http://localhost:8080/responses/employee/${employeeId}`);
        const submittedSurveys = await submittedResponse.json();

        //extracting surveys IDs
        const submittedSurveyIds = [...new Set(submittedSurveys.map(response => response.surveyId))];

        const surveyList = document.getElementById("survey-list");
        surveyList.innerHTML = "";

        if(surveys.length === 0){
            surveyList.innerHTML = "<p>No surveys available!</p>";
            return;
        }

        //getting each survey object one by one from the above surveys variable(line 4)
        surveys.forEach(survey => {
            const surveyItem = document.createElement("div");
            surveyItem.classList.add("survey-item");

            const isSubmitted = submittedSurveyIds.includes(survey.id);

            
            const button = document.createElement("button");
            button.innerText = isSubmitted ? "Already Submitted" : "Participate";
            button.onclick = () => viewSurvey(survey.id);

            if(isSubmitted){
                button.disabled = true;
                button.style.backgroundColor = "#ccc";
                button.style.color = "#666";
                button.style.cursor = "not-allowed";
            }

            //populating (inserting name and desc dynamically)
            surveyItem.innerHTML = `
                <h3>${survey.title}</h3>
                <p>${survey.description}</p>
            `;

            //we add the button to the survey item
            surveyItem.appendChild(button);

            //we add the survey entity(whole done till now) to survey list and now it gets displayed
            surveyList.appendChild(surveyItem);
        });
    }catch(error){
        console.error("Error fetching surveys:", error);
    }
}

async function fetchSubmittedSurveys(){
    const employeeId = sessionStorage.getItem("employeeId");
    if(!employeeId){
        console.error("Employee ID not found, redirecting to login");
        window.location.href = "login.html";
        return;
    }

    try{
        const response = await fetch(`http://localhost:8080/responses/employee/${employeeId}`);
        const submittedResponses = await response.json();

        const submittedSurveyIds = [...new Set(submittedResponses.map(res => res.surveyId))];

        const responsesContainer = document.getElementById("submitted-surveys");
        responsesContainer.innerHTML = "";

        if(submittedSurveyIds.length === 0){
            responsesContainer.innerHTML = "<p>No surveys submitted yet.</p>";
            return;
        }

        submittedSurveyIds.forEach(surveyId => {
            const survey = submittedResponses.find(res => res.surveyId === surveyId);
            if(survey){
                const surveyItem = document.createElement("div");
                surveyItem.classList.add("survey-item");
                surveyItem.innerHTML = `
                    <h3>${survey.surveyTitle}</h3>
                    <p>${survey.surveyDescription}</p>
                    <button onclick="viewSubmittedResponses('${surveyId}')">View Responses</button>
                `;
                responsesContainer.appendChild(surveyItem);
            }
        });

    }catch(error){
        console.error("Error fetching submitted surveys:", error);
    }
}

function viewSurvey(surveyId){
    window.location.href = `survey-details.html?id=${surveyId}`;
}

function viewSubmittedResponses(surveyId){
    window.location.href = `submitted-responses.html?id=${surveyId}`;
}

document.addEventListener("DOMContentLoaded", () => {
    // Check if survey was updated, then refresh data
    if(sessionStorage.getItem("surveyUpdated") === "true"){
        console.log("refreshing dashboard");
        fetchSurveys();
        fetchSubmittedSurveys();
        sessionStorage.removeItem("surveyUpdated"); // Clear flag
    }

    // Load Surveys
    fetchSurveys();
    fetchSubmittedSurveys();

    //updating navbar with employee name
    const employeeName = sessionStorage.getItem("employeeName");
    const nameElement = document.getElementById("employee-name");
    if(nameElement){
        nameElement.textContent = `Hi, ${employeeName || "User"}`;
    }
});

document.getElementById("edit-profile-button").addEventListener("click", function(){
    window.location.href = "profile.html";
});

function logout(){
    window.location.href="login.html";
    sessionStorage.clear();
}
