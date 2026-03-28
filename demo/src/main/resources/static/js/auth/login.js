function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = document.getElementById('icon-' + inputId);
    if (input.type === "password") {
        input.type = "text";
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = "password";
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

const email = document.getElementById('email');
const pass = document.getElementById('password');
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

pass.addEventListener('input', function() {
    if (pass.value.trim() !== '') {
        clearError(pass, 'passError');
    }
});
document.getElementById('loginForm').addEventListener('submit', function(event) {
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

    if (!pass.value.trim()) {
        document.getElementById('passError').innerText = 'Vui lòng nhập mật khẩu';
        pass.classList.add('is-invalid');
        isValid = false;
    }

    if (!isValid) {
        event.preventDefault();
    }
});