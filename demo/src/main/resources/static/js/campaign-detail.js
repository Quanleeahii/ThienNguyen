document.addEventListener("DOMContentLoaded", function () {
    const campaignIdInput = document.getElementById("campaignId");
    if (!campaignIdInput) return;

    const campaignId = campaignIdInput.value;
    let lastDonationCount = -1;
    let allDonations = [];

    function removeVietnameseTones(str) {
        return str.normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase();
    }

    function fetchCampaignData() {
        fetch(`/api/campaign/${campaignId}/stats`)
            .then(res => res.json())
            .then(data => {
                const elements = {
                    amount: document.getElementById("campaignCurrentAmount"),
                    progressText: document.getElementById("campaignProgressText"),
                    progressBar: document.getElementById("campaignProgressBar"),
                    count: document.getElementById("campaignDonationCount"),
                    tabCount: document.getElementById("tabDonationCount")
                };

                if (elements.amount) elements.amount.innerText = new Intl.NumberFormat('vi-VN').format(data.currentAmount) + ' đ';
                if (elements.progressText) elements.progressText.innerText = data.progressPercentage + '%';
                if (elements.progressBar) elements.progressBar.style.width = data.progressPercentage + '%';
                if (elements.count) elements.count.innerText = data.donationCount;
                if (elements.tabCount) elements.tabCount.innerText = data.donationCount;

                if (data.donationCount !== lastDonationCount) {
                    fetchDonorsList();
                    lastDonationCount = data.donationCount;
                }
            })
            .catch(() => console.log("Stats update pending..."));
    }

    function fetchDonorsList() {
        fetch(`/api/campaign/${campaignId}/donations`)
            .then(res => res.json())
            .then(donations => {
                allDonations = donations;
                renderTable(allDonations);
            })
            .catch(() => {});
    }

    function renderTable(dataList) {
        const tableBody = document.getElementById("donorTableBody");
        if (!tableBody) return;
        tableBody.innerHTML = "";

        if(dataList.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="3" class="text-center py-5 text-muted"><i class="fa-solid fa-box-open fs-1 d-block mb-2 opacity-50"></i>Chưa có người ủng hộ nào.</td></tr>`;
            return;
        }

        dataList.forEach(d => {
            const row = `
                <tr class="border-bottom">
                    <td class="py-3 text-dark donor-name-cell" title="${d.fullName}">${d.fullName}</td>
                    <td class="py-3 text-end fw-bold text-dark">${new Intl.NumberFormat('vi-VN').format(d.amount)} đ</td>
                    <td class="py-3 text-end text-muted small">${d.createdAt}</td>
                </tr>`;
            tableBody.innerHTML += row;
        });
    }

    const searchInput = document.getElementById("searchDonorInput");
    let debounceTimer;

    if (searchInput) {
        searchInput.addEventListener("input", function (e) {
            clearTimeout(debounceTimer);
            debounceTimer = setTimeout(() => {
                const keyword = removeVietnameseTones(e.target.value);
                const filtered = allDonations.filter(d => removeVietnameseTones(d.fullName).includes(keyword));
                renderTable(filtered);
            }, 300);
        });
    }

    fetchCampaignData();
    setInterval(fetchCampaignData, 3000);
});