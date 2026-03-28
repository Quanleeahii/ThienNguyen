const email = document.getElementById('email');
const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

function clearError(inputElement, errorId) {
    inputElement.classList.remove('is-invalid');
    document.getElementById(errorId).innerText = '';
}

email.addEventListener('input', function() {
    if (emailRegex.test(email.value.trim())) {
        clearError(email, 'emailError');
    }
});

document.getElementById('forgotForm').addEventListener('submit', function(event) {
    let isValid = true;

    document.querySelectorAll('.error-msg').forEach(el => el.innerText = '');
    document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));

    if (!email.value.trim()) {
        document.getElementById('emailError').innerText = 'Vui lòng nhập Email';
        email.classList.add('is-invalid');
        isValid = false;
    } else if (!emailRegex.test(email.value.trim())) {
        document.getElementById('emailError').innerText = 'Email không hợp lệ';
        email.classList.add('is-invalid');
        isValid = false;
    }

    if (!isValid) {
        event.preventDefault();
    }
});