function getSurveyIdFromURL() {
    const urlParams = new URLSearchParams(window.location.search);
    return urlParams.get("id");
}

async function fetchSurveyDetails() {
    const surveyId = getSurveyIdFromURL();
    if (!surveyId) {
        console.error("Survey ID not found in URL");
        return;
    }

    try {
        const surveyResponse = await fetch(`http://localhost:8080/surveys/${surveyId}`);
        const survey = await surveyResponse.json();

        const surveyDetailsDiv = document.getElementById("survey-details");
        surveyDetailsDiv.innerHTML = `
            <h1>${survey.title}</h1>
            <p>${survey.description}</p>
        `;

        fetchSurveyQuestions(surveyId);
    } catch (error) {
        console.error("Error fetching survey details:", error);
    }
}

async function fetchSurveyQuestions(surveyId) {
    try {
        const questionsResponse = await fetch(`http://localhost:8080/surveys/${surveyId}/questions`);
        const questions = await questionsResponse.json();

        const questionsContainer = document.getElementById("questions-container");
        questionsContainer.innerHTML = "<h2>Questions:</h2>";

        questionsContainer.style.marginTop = "20px";

        if (questions.length === 0) {
            questionsContainer.innerHTML += "<p>No questions available for this survey.</p>";
            return;
        }

        questions.forEach((question, index) => {
            const questionItem = document.createElement("div");
            questionItem.classList.add("question-item");
            
            //incrementing question number
            const questionNumber = index + 1;

            // text input rendering
            const inputField = ` 
                <input type="text" name="response-${question.id}" required placeholder="Enter your response"
                    style="width: 100%; padding: 10px; margin-bottom: 40px; margin-top:20px; border: 1px solid #ccc; border-radius: 5px; font-size: 16px;">
            `;
        
            questionItem.innerHTML = `
                <p><strong>Question ${questionNumber}: ${question.text}</strong></p>
                <form id="response-form-${question.id}">
                    ${inputField}
                </form>
            `;
        
            questionsContainer.appendChild(questionItem);
        });
        
        const submitButton = document.createElement("button");
        submitButton.innerText = "Submit Responses";
        submitButton.onclick = submitResponses;
        questionsContainer.appendChild(submitButton);
    } catch (error) {
        console.error("Error fetching survey questions:", error);
    }
}


async function submitResponses() {
    const surveyId = getSurveyIdFromURL();
    const employeeId = sessionStorage.getItem("employeeId");

    if (!surveyId || !employeeId) {
        console.error("Survey ID or Employee ID missing");
        return;
    }

    const responses = [];
    let allQuestionsAnswered = true;

    document.querySelectorAll(".question-item").forEach(questionItem => {
        const questionId = questionItem.querySelector("form").id.replace("response-form-", "");
        const inputField = questionItem.querySelector(`input[name="response-${questionId}"]`);

        
        if (!inputField || !inputField.value.trim()) {
            allQuestionsAnswered = false;
        } else {
            responses.push({
                employeeId: employeeId,
                surveyId: surveyId,
                questionId: questionId,
                responseText: inputField.value.trim()
            });
        }
    });

    if (!allQuestionsAnswered) {
        alert("Please answer all questions before submitting.");
        return;
    }

    try {
        await Promise.all(responses.map(response =>
            fetch("http://localhost:8080/responses/submit", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify(response),
            })
        ));

        alert("Responses submitted successfully!");
        sessionStorage.setItem("surveyUpdated", "true");
        window.location.href = "dashboard.html";

    } catch (error) {
        console.error("Error submitting responses:", error);
    }
}



fetchSurveyDetails();
