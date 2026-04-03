document.addEventListener("DOMContentLoaded", function () {
    const inputAmount = document.getElementById("donationAmount");
    const amountBtns = document.querySelectorAll(".amount-btn");
    const btnOpenModal = document.getElementById("btnOpenModal");
    const paymentModalElement = document.getElementById('paymentModal');
    const paymentModal = new bootstrap.Modal(paymentModalElement, { backdrop: 'static' });
    const qrCodeImage = document.getElementById("qrCodeImage");

    const anonymousCheck = document.getElementById("anonymousCheck");
    const donorNameInput = document.getElementById("donorName");
    const donorEmailInput = document.getElementById("donorEmail");

    let currentDonationId = null;
    let checkInterval = null;

    inputAmount.addEventListener("input", function () {
        let value = this.value.replace(/\./g, '').replace(/[^0-9]/g, '');
        if (value === "") { this.value = ""; return; }
        this.value = new Intl.NumberFormat('vi-VN').format(value);
        amountBtns.forEach(b => b.classList.remove("active"));
    });

    amountBtns.forEach(btn => {
        btn.addEventListener("click", function () {
            amountBtns.forEach(b => b.classList.remove("active"));
            this.classList.add("active");
            const value = this.getAttribute("data-value");
            inputAmount.value = new Intl.NumberFormat('vi-VN').format(value);
        });
    });

    anonymousCheck.addEventListener("change", function() {
        donorNameInput.disabled = this.checked;
        donorEmailInput.disabled = this.checked;
        if(this.checked) { donorNameInput.value = ""; donorEmailInput.value = ""; }
    });

    btnOpenModal.addEventListener("click", function () {
        let rawAmount = inputAmount.value.replace(/\D/g, '');
        if(!rawAmount || parseInt(rawAmount) < 2000) {
            alert("Tối thiểu 2.000đ ông ơi!");
            return;
        }

        let campaignId = document.getElementById("campaignId").value;
        btnOpenModal.disabled = true;

        let formData = new URLSearchParams();
        formData.append('campaignId', campaignId);
        formData.append('amount', rawAmount);
        formData.append('message', document.getElementById("donationMessage")?.value || "");
        formData.append('fullName', anonymousCheck.checked ? "Nhà Hảo Tâm Ẩn Danh" : (donorNameInput.value.trim() || "Mạnh Thường Quân"));
        formData.append('email', donorEmailInput.value || "");
        formData.append('isAnonymous', anonymousCheck.checked);

        fetch('/api/donate', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: formData
        })
        .then(res => res.json())
        .then(data => {
            if(data.success) {
                currentDonationId = data.donationId;
                document.getElementById("modalDisplayAmount").textContent = inputAmount.value + " VND";

                document.getElementById("transferMemo").innerText = data.description;
                document.getElementById("modalAccountNumber").innerText = data.accountNumber;
                document.getElementById("modalAccountName").innerText = data.accountName;

                qrCodeImage.src = `https://api.qrserver.com/v1/create-qr-code/?size=300x300&data=${encodeURIComponent(data.qrCode)}`;

                let payosLinkBtn = document.getElementById("linkCheckoutPayOS");
                if(payosLinkBtn) payosLinkBtn.href = data.checkoutUrl;

                paymentModal.show();
                startCheckingThisDonation();
            } else {
                Swal.fire('Thông báo từ Server', data.error || data.message || 'Lỗi không xác định', 'warning');
            }
        })
        .catch(err => {
            console.error("Lỗi:", err);
            Swal.fire('Lỗi', 'Không kết nối được với Server!', 'error');
        })
        .finally(() => {
            btnOpenModal.disabled = false;
        });
    });

    function startCheckingThisDonation() {
        if (checkInterval) clearInterval(checkInterval);
        checkInterval = setInterval(() => {
            fetch(`/api/donation/check-status/${currentDonationId}`)
                .then(res => res.json())
                .then(data => {
                    if (data.isPaid) {
                        clearInterval(checkInterval);
                        paymentModal.hide();
                        updateCampaignUI();
                        setTimeout(() => {
                            Swal.fire({ title: 'Thành công! 🎉', text: 'Cảm ơn bạn!', icon: 'success', confirmButtonColor: '#f26b21' });
                        }, 500);
                    }
                });
        }, 3000);
    }

    function updateCampaignUI() {
        let campaignId = document.getElementById("campaignId").value;
        fetch(`/api/campaign/${campaignId}/stats`)
            .then(res => res.json())
            .then(data => {
                document.getElementById("campaignCurrentAmount").innerText = new Intl.NumberFormat('vi-VN').format(data.currentAmount) + ' VND';
                document.getElementById("campaignProgressText").innerText = data.progressPercentage + '%';
                document.getElementById("campaignProgressBar").style.width = data.progressPercentage + '%';
                document.getElementById("campaignDonationCount").innerText = data.donationCount;
            });
    }

    paymentModalElement.addEventListener('hidden.bs.modal', () => { if (checkInterval) clearInterval(checkInterval); });
});

function copyMemo() {
    navigator.clipboard.writeText(document.getElementById("transferMemo").innerText).then(() => {
        let btn = document.querySelector('.copy-badge');
        btn.innerHTML = 'Đã chép';
        setTimeout(() => { btn.innerHTML = 'Copy'; }, 2000);
    });
}