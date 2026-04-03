document.addEventListener("DOMContentLoaded", function() {
    // --- PHẦN 1: XỬ LÝ NHẬP 6 Ô OTP ---
    const inputs = document.querySelectorAll('.otp-box');
    inputs.forEach((input, index) => {
        input.addEventListener('input', (e) => {
            if (e.target.value.length > 1) {
                e.target.value = e.target.value.slice(0, 1);
            }
            if (e.target.value && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });

        input.addEventListener('keydown', (e) => {
            if (e.key === 'Backspace' && !e.target.value && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });

    document.getElementById('otpForm').addEventListener('submit', function(e) {
        let isValid = true;
        inputs.forEach(input => {
            if (!input.value.trim()) {
                input.classList.add('is-invalid');
                isValid = false;
            } else {
                input.classList.remove('is-invalid');
            }
        });
        if (!isValid) e.preventDefault();
    });

    // --- PHẦN 2: ĐỒNG HỒ ĐẾM NGƯỢC 5 PHÚT ---
    let timeLeft = 300;
    const timeDisplay = document.getElementById("time");
    const timerContainer = document.getElementById("countdown-timer");
    const resendContainer = document.getElementById("resend-container");
    const submitBtn = document.querySelector(".login-btn");

    const countdown = setInterval(function() {
        if (timeLeft <= 0) {
            clearInterval(countdown);
            timerContainer.innerHTML = "<i class='fa-solid fa-circle-xmark text-danger me-1'></i> Mã OTP đã hết hạn!";
            timerContainer.className = "badge bg-danger text-white p-2 fs-6";

            submitBtn.disabled = true;
            submitBtn.innerHTML = "Đã hết hạn";
            submitBtn.classList.add("bg-secondary");

            inputs.forEach(input => {
                input.disabled = true;
                input.value = "";
            });

            resendContainer.style.display = "block";
        } else {
            let minutes = Math.floor(timeLeft / 60);
            let seconds = timeLeft % 60;
            timeDisplay.innerHTML = `${minutes < 10 ? '0' : ''}${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
        }
        timeLeft -= 1;
    }, 1000);
});