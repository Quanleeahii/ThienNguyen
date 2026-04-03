function openPayModal(id, title, amount) {
    // Gán dữ liệu vào các thẻ trong Modal
    document.getElementById('modalId').value = id;
    document.getElementById('modalTitle').innerText = title;

    // Định dạng tiền VNĐ cho đẹp
    const formattedAmount = new Intl.NumberFormat('vi-VN').format(amount) + ' đ';
    document.getElementById('modalAmount').innerText = formattedAmount;

    // Bật modal lên
    var myModal = new bootstrap.Modal(document.getElementById('payModal'));
    myModal.show();
}