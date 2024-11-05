
const form = document.getElementById('loginForm');
const alertBox = document.getElementById('alertBox');
const submitButton = document.getElementById('submitButton');
const inputs = document.querySelectorAll('input');

function showAlert(message, type = 'error') {
    alertBox.className = `alert alert-${type}`;
    alertBox.textContent = message;
    alertBox.style.display = 'block';
}

function clearAlerts() {
    alertBox.style.display = 'none';
    inputs.forEach(input => input.classList.remove('error'));
}

function setLoading(isLoading) {
    submitButton.disabled = isLoading;
    submitButton.textContent = isLoading ? 'Logging in...' : 'Login';
}
form.addEventListener('submit', async (e) => {
    e.preventDefault();
    clearAlerts();
    setLoading(true);

    const formData = {
        username: document.getElementById('username').value,
        password: document.getElementById('password').value
    };

    try {
        const response = await fetch("/login", {
            method: "POST",
            credentials: 'include',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        });

        const data = await response.json();

        if (response.status === 400) {
            showAlert('Please fill in all required fields');
            inputs.forEach(input => {
                if (!input.value) {
                    input.classList.add('error');
                }
            });
        } else if (response.status === 401) {
            showAlert('Invalid username or password');
            inputs.forEach(input => input.classList.add('error'));
        } else if (response.status === 500) {
            showAlert('Server error. Please try again later.');
        } else if (data.code === 200) {
            showAlert('Login successful! Redirecting...', 'success');
            const apiKey = data.details;
            sessionStorage.setItem("apiKey", apiKey);
            setTimeout(() => {
                window.location.href = '/index.html';
            }, 1000);
        } else {
            showAlert(data.message || 'Login failed. Please try again.');
        }
    } catch (error) {
        console.error('Login error:', error);
        showAlert('Network error. Please check your connection and try again.');
    } finally {
        setLoading(false);
    }
});


inputs.forEach(input => {
    input.addEventListener('input', () => {
        input.classList.remove('error');
        if (!inputs[0].classList.contains('error') && !inputs[1].classList.contains('error')) {
            clearAlerts();
        }
    });
});
