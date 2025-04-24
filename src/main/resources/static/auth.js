function showMessage(message, isSuccess){
    const messageDiv = document.getElementById("message");
    messageDiv.textContent = message;
    messageDiv.style.color = isSuccess ? "green" : "red";
    messageDiv.style.display = "block";
}

async function register(){
    //DOM elements
    const name = document.getElementById("register-name").value;
    const email = document.getElementById("register-email").value;
    const department = document.getElementById("register-department").value;
    const password = document.getElementById("register-password").value;

    if(!name || !email || !department || !password){
        showMessage("Please fill in all the fields.", false);
        return;
    }

    const response = await fetch("http://localhost:8080/auth/register", {
        method: "POST",
        headers:{
            "Content-Type": "application/json"
        },
        body: JSON.stringify({ name, email, department, password })
    });

    //checking if response is ok
    if(!response.ok){
        let result;
        try{
            result = await response.json();  //parsing the JSON response
        }catch(e){
            showMessage("An unexpected error occurred, Try again", false);
            return;
        }

        //showing the error message returned by the backend
        //showMessage(result.message || "An error occurred. Please try again later.", false);
        return;
    }

    //parsing successful response(Registration successful)
    const result = await response.json();
    showMessage("Registration successful! Redirecting to login...", true);
    setTimeout(() => window.location.href = "login.html", 2000);
}



async function login(){

    //dom elements
    const email = document.getElementById("login-email").value;
    const password = document.getElementById("login-password").value;

    try{
        const response = await fetch("http://localhost:8080/auth/login", {
            method: "POST",
            headers:{ 
                "Content-Type": "application/json" 
            },
            body: JSON.stringify({email, password})  
        });

        if(!response.ok){
            alert("Invalid email or password");
            return;
        }

        const employee = await response.json();
        

        if(!employee.employeeId){
            return;
        }

        sessionStorage.setItem("employeeId", employee.employeeId);
        sessionStorage.setItem("isAdmin", employee.admin);
        sessionStorage.setItem("employeeName", employee.name);
        // console.log("Emp ID:", sessionStorage.getItem("employeeId"));

        window.location.href = employee.admin ? "admin-dashboard.html" : "dashboard.html";
        
    }catch(error) {
        console.error("Error during login:", error);
        alert("An error occurred, try again");
    }
}


