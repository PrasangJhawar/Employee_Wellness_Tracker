document.addEventListener("DOMContentLoaded", function (){
    const profileForm = document.getElementById("profile-form");

    if(profileForm){
        profileForm.addEventListener("submit", async function (event){
            //doesn't reload the page
            event.preventDefault();
            await updateProfile();
        });
    }
});

async function updateProfile(){
    const employeeId = sessionStorage.getItem("employeeId");

    if(!employeeId){
        alert("No employee ID found. Please log in again.");
        return;
    }

    const updatedName = document.getElementById("name").value;
    const updatedPassword = document.getElementById("password").value;

    const updateData = {
        name: updatedName,
        password: updatedPassword || null
    };

    try{
        const response = await fetch(`http://localhost:8080/auth/employees/${employeeId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(updateData)
        });

        if(response.ok){
            alert("Profile updated successfully!");
        }else{
            alert("Failed to update profile, try again");
        }
    }catch(error){
        console.error("Error updating profile:", error);
        alert("An error occurred. Please try again.");
    }
}

function goBackToDashboard(){
    const updatedName = document.getElementById("name").value;
    sessionStorage.setItem("employeeName", updatedName);
    window.location.href = "dashboard.html";
}
