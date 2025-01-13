
const form = document.getElementById("loginForm");
const alertBox = document.getElementById("alertBox");
const submitButton = document.getElementById("submitButton");

function showError(message, isGlobal = true, fieldId = null) {
    if (isGlobal) {
        alertBox.className = "alert alert-error";
        alertBox.textContent = message;
        alertBox.style.display = "block";
    } else if (fieldId) {
        const errorElement = document.getElementById(`${fieldId}-error`);
        const inputElement = document.getElementById(fieldId);

        if (errorElement && inputElement) {
            errorElement.textContent = message;
            errorElement.style.display = "block";
            inputElement.classList.add("field-error");
        }
    }
}

function clearErrors() {
    alertBox.style.display = "none";
    document.querySelectorAll('.error-message').forEach(elem => {
        elem.style.display = "none";
        elem.textContent = "";
    });
    document.querySelectorAll('input').forEach(input => {
        input.classList.remove("field-error");
    });
}

function setLoading(isLoading) {
    submitButton.disabled = isLoading;
    submitButton.textContent = isLoading ? "Registering..." : "Register";
}

form.addEventListener('submit', async (event) => {
    event.preventDefault();
    clearErrors();
    setLoading(true);

    const username = document.getElementById("username").value;
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;

    try {
        const response = await fetch("/register", {
            method: "POST",
            credentials: 'include',
            headers: {
                "Content-Type": "application/json",
            },
            body: JSON.stringify({
                username: username,
                email: email,
                password: password
            })
        });

        const data = await response.json();

        if (response.ok && data.code === 200) {
            alertBox.className = "alert alert-success";
            alertBox.textContent = "Registration successful! Redirecting to login...";
            alertBox.style.display = "block";
            window.location.href = "/login.html";

        } else {
            if (response.status === 409) {
                if (data.details.includes("Username")) {
                    showError("This username is already taken. Please choose another one.", false, "username");
                } else if (data.details.includes("Email")) {
                    showError("This email is already registered. Please use another email or try logging in.", false, "email");
                }
            } else if (response.status === 400) {
                if (data.details.includes("Invalid")) {
                    showError("Please check your input format:", true);
                    showError("Username must be alphanumeric", false, "username");
                    showError("Email must be valid", false, "email");
                    showError("Password must meet security requirements", false, "password");
                } else {
                    showError(data.details || "Please fill in all required fields correctly");
                }
            } else {
                showError(data.details || "Registration failed. Please try again.");
            }
        }
    } catch (error) {
        console.error("Error:", error);
        showError("Unable to connect to the server. Please check your internet connection and try again.");
    } finally {
        setLoading(false);
    }
});
