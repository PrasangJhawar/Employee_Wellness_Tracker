async function fetchSubmittedResponses() {
    const urlParams = new URLSearchParams(window.location.search);
    const surveyId = urlParams.get("id");
    const employeeId = sessionStorage.getItem("employeeId"); //fetching employee id

    if (!surveyId || !employeeId) {
        console.error("Survey ID or Employee ID missing");
        return;
    }

    try{
        const response = await fetch(`http://localhost:8080/responses/survey/${surveyId}/employee/${employeeId}`);
        if(!response.ok){
            throw new Error(`HTTP error! Status: ${response.status}`);
        }
        
        const responses = await response.json();
        const responsesContainer = document.getElementById("responses-container");

        if(!responsesContainer){
            console.error("Element with ID 'responses-container' not found");
            return;
        }

        responsesContainer.innerHTML = "";

        if(responses.length === 0){
            responsesContainer.innerHTML = "<p>No responses found for this survey.</p>";
            return;
        }

        responses.forEach(response => {
            const responseItem = document.createElement("div");
            responseItem.classList.add("response-item");
            responseItem.innerHTML = `
                <p><strong>Question:</strong> ${response.questionText}</p>
                <textarea id="response-${response.id}" rows="3" style="width: 100%;">${response.responseText}</textarea>
                <button onclick="updateResponse('${response.id}')">Update</button>
            `;
            responsesContainer.appendChild(responseItem);
        });
        

    }catch(error){
        console.error("Error fetching submitted responses:", error);
    }
}

//calling function once the page gets loaded
document.addEventListener("DOMContentLoaded", fetchSubmittedResponses);

function goBack(){
    window.location.href = "dashboard.html";
}



async function updateResponse(responseId){
    const textarea = document.getElementById(`response-${responseId}`);
    const updatedText = textarea.value;

    try{
        const res = await fetch(`http://localhost:8080/responses/${responseId}`, {
            method: "PUT",
            headers:{
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ text: updatedText })
        });

        if(!res.ok){
            throw new Error(`Failed to update response. Status: ${res.status}`);
        }

        alert("Response updated successfully!");
    }catch(error){
        console.error("Error updating response:", error);
        alert("Error updating response.");
    }
}


