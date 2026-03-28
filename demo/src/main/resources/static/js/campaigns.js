function updateStatus(value) {
    let statusInput = document.getElementById('statusInput');
    let filterForm = document.getElementById('filterForm');
    if (statusInput && filterForm) {
        statusInput.value = value;
        filterForm.submit();
    } else {
        console.error("Không tìm thấy statusInput hoặc filterForm trên trang.");
    }
}