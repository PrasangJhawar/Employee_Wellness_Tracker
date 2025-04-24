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
            `;
        
            questionsContainer.appendChild(questionItem);
        });
    } catch (error) {
        console.error("Error fetching survey questions:", error);
    }
}



fetchSurveyDetails();
