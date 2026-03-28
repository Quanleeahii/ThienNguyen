document.addEventListener("DOMContentLoaded", function () {
    const inputAmount = document.getElementById("donationAmount");
    const amountBtns = document.querySelectorAll(".amount-btn");
    const btnOpenModal = document.getElementById("btnOpenModal");
    const paymentModal = new bootstrap.Modal(document.getElementById('paymentModal'));
    const modalDisplayAmount = document.getElementById("modalDisplayAmount");
    const qrCodeImage = document.getElementById("qrCodeImage");
    const bankAccountNumber = document.querySelector(".bank-acc-number")?.innerText.trim() || "8813273033";
    const bankAccountName = document.querySelector(".bank-acc-name")?.innerText.trim() || "LE ANH QUAN";
    const bankCode = document.querySelector(".bank-name-short")?.innerText.trim() || "BIDV";
    inputAmount.addEventListener("input", function (e) {
        let value = this.value.replace(/\./g, '').replace(/[^0-9]/g, '');
        if (value === "") {
            this.value = "";
            return;
        }
        this.value = new Intl.NumberFormat('vi-VN').format(value);
        amountBtns.forEach(b => b.classList.remove("active"));
    });
    amountBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            // Xóa active tất cả
            amountBtns.forEach(b => b.classList.remove("active"));
            this.classList.add("active");
            const value = this.getAttribute("data-value");
            inputAmount.value = new Intl.NumberFormat('vi-VN').format(value);
        });
    });
    btnOpenModal.addEventListener("click", function () {
        let currentAmountStr = inputAmount.value;
        let rawAmount = currentAmountStr.replace(/\./g, '');
        if(!rawAmount || rawAmount === "0") {
            alert("Vui lòng nhập số tiền hợp lệ!");
            inputAmount.focus();
            return;
        }
        let campaignIdElement = document.getElementById("campaignId");
        if (!campaignIdElement) {
            alert("Lỗi: Không tìm thấy ID chiến dịch!");
            return;
        }
        let campaignId = campaignIdElement.value;
        let message = document.getElementById("donationMessage")?.value || "";
        let donorName = document.getElementById("donorName")?.value || "";
        let donorEmail = document.getElementById("donorEmail")?.value || "";
        let isAnonymous = document.getElementById("anonymousCheck")?.checked || false;
        let originalBtnText = btnOpenModal.innerText;
        btnOpenModal.innerText = "Đang xử lý...";
        btnOpenModal.disabled = true;
        let formData = new URLSearchParams();
        formData.append('campaignId', campaignId);
        formData.append('amount', rawAmount);
        if(message) formData.append('message', message);
        if(donorName) formData.append('fullName', donorName);
        if(donorEmail) formData.append('email', donorEmail);
        formData.append('isAnonymous', isAnonymous);
        fetch('/api/donate', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            body: formData
        })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.error || "Lỗi mạng hoặc server") });
            }
            return response.json();
        })
        .then(data => {
            if(data.success) {
                let transactionCode = data.transactionCode;
                document.getElementById("transferMemo").innerText = transactionCode;
                modalDisplayAmount.textContent = currentAmountStr + " VND";
                let qrApiUrl = `https://img.vietqr.io/image/${bankCode}-${bankAccountNumber}-compact2.png?amount=${rawAmount}&addInfo=${encodeURIComponent(transactionCode)}&accountName=${encodeURIComponent(bankAccountName)}`;
                qrCodeImage.src = qrApiUrl;
                paymentModal.show();
            } else {
                alert(data.error || "Đã xảy ra lỗi khi tạo giao dịch từ máy chủ!");
            }
        })
        .catch(error => {
            console.error('Lỗi AJAX:', error);
            alert(error.message || "Không thể kết nối đến máy chủ! Vui lòng thử lại.");
        })
        .finally(() => {
            btnOpenModal.innerText = originalBtnText;
            btnOpenModal.disabled = false;
        });
    });
});
function copyMemo() {
    const memoText = document.getElementById("transferMemo").innerText;
    navigator.clipboard.writeText(memoText).then(() => {
        alert("Đã sao chép nội dung: " + memoText);
    }).catch(err => {
        console.error('Không thể sao chép', err);
        alert("Trình duyệt của bạn không hỗ trợ copy tự động, vui lòng copy thủ công.");
    });
}