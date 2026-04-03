document.addEventListener("DOMContentLoaded", function () {
    function updateAllCampaignStats() {
        const cards = document.querySelectorAll('article[data-campaign-id]');
        const ids = Array.from(cards).map(card => card.getAttribute('data-campaign-id'));

        if (ids.length === 0) return;

        fetch(`/api/campaigns/stats?ids=${ids.join(',')}`)
            .then(res => res.json())
            .then(dataList => {
                dataList.forEach(data => {
                    const card = document.querySelector(`article[data-campaign-id="${data.id}"]`);
                    if (!card) return;

                    const amountEl = card.querySelector('.live-amount');
                    if (amountEl) amountEl.innerText = new Intl.NumberFormat('vi-VN').format(data.currentAmount) + ' đ';

                    const progressTextEl = card.querySelector('.live-progress-text');
                    const progressBarEl = card.querySelector('.live-progress-bar');
                    if (progressTextEl) progressTextEl.innerText = data.progressPercentage + '%';
                    if (progressBarEl) progressBarEl.style.width = data.progressPercentage + '%';

                    const countEl = card.querySelector('.live-count');
                    if (countEl) countEl.innerText = data.donationCount;
                });
            })
            .catch(() => {});
    }

    updateAllCampaignStats();
    setInterval(updateAllCampaignStats, 3000);
});

// Giữ lại hàm updateStatus cũ của ông ở dưới này
function updateStatus(value) {
    let statusInput = document.getElementById('statusInput');
    let filterForm = document.getElementById('filterForm');
    if (statusInput && filterForm) {
        statusInput.value = value;
        filterForm.submit();
    }
}