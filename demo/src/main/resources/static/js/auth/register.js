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
const nameInput = document.getElementById('name');
const pass = document.getElementById('password1');
const pass2 = document.getElementById('password2');
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

nameInput.addEventListener('input', function() {
    if (nameInput.value.trim() !== '') {
        clearError(nameInput, 'nameError');
    }
});

pass.addEventListener('input', function() {
    if (pass.value.length >= 6) {
        clearError(pass, 'passError');
    }
    if (pass2.value !== '' && pass.value === pass2.value) {
        clearError(pass2, 'pass2Error');
    }
});

pass2.addEventListener('input', function() {
    if (pass2.value !== '' && pass2.value === pass.value) {
        clearError(pass2, 'pass2Error');
    }
});

document.getElementById('registerForm').addEventListener('submit', function(event) {
    let isValid = true;
    document.querySelectorAll('.error-msg').forEach(el => el.innerText = '');
    document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));

    if (!email.value.trim()) {
        document.getElementById('emailError').innerText = 'Trường bắt buộc nhập';
        email.classList.add('is-invalid');
        isValid = false;
    } else if (!emailRegex.test(email.value.trim())) {
        document.getElementById('emailError').innerText = 'Email không hợp lệ (ví dụ: abc@gmail.com)';
        email.classList.add('is-invalid');
        isValid = false;
    }

    if (!nameInput.value.trim()) {
        document.getElementById('nameError').innerText = 'Trường bắt buộc nhập';
        nameInput.classList.add('is-invalid');
        isValid = false;
    }

    if (pass.value.length < 6) {
        document.getElementById('passError').innerText = 'Mật khẩu tối thiểu 6 ký tự';
        pass.classList.add('is-invalid');
        isValid = false;
    }

    if (!pass2.value.trim()) {
        document.getElementById('pass2Error').innerText = 'Trường bắt buộc nhập';
        pass2.classList.add('is-invalid');
        isValid = false;
    } else if (pass2.value !== pass.value) {
        document.getElementById('pass2Error').innerText = 'Mật khẩu không khớp';
        pass2.classList.add('is-invalid');
        isValid = false;
    }

    if (!isValid) {
        event.preventDefault();
    }
});