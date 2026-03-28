
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

const pass = document.getElementById('password');
const confirmPass = document.getElementById('confirmPassword');

function clearError(inputElement, errorId) {
    inputElement.classList.remove('is-invalid');
    document.getElementById(errorId).innerText = '';
}

pass.addEventListener('input', function() {
    if (pass.value.length >= 6) {
        clearError(pass, 'passError');
    }
    if (confirmPass.value !== '' && pass.value === confirmPass.value) {
        clearError(confirmPass, 'confirmPassError');
    }
});

confirmPass.addEventListener('input', function() {
    if (confirmPass.value !== '' && confirmPass.value === pass.value) {
        clearError(confirmPass, 'confirmPassError');
    }
});

document.getElementById('resetForm').addEventListener('submit', function(event) {
    let isValid = true;

    document.querySelectorAll('.error-msg').forEach(el => el.innerText = '');
    document.querySelectorAll('.form-control').forEach(el => el.classList.remove('is-invalid'));

    if (pass.value.length < 6) {
        document.getElementById('passError').innerText = 'Mật khẩu tối thiểu 6 ký tự';
        pass.classList.add('is-invalid');
        isValid = false;
    }

    if (!confirmPass.value.trim()) {
        document.getElementById('confirmPassError').innerText = 'Vui lòng nhập lại mật khẩu';
        confirmPass.classList.add('is-invalid');
        isValid = false;
    } else if (confirmPass.value !== pass.value) {
        document.getElementById('confirmPassError').innerText = 'Mật khẩu không khớp';
        confirmPass.classList.add('is-invalid');
        isValid = false;
    }

    if (!isValid) {
        event.preventDefault();
    }
});