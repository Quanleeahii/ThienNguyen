document.addEventListener("DOMContentLoaded", function() {

    // Xử lý Validation cho form Xóa tài khoản
    const deleteForm = document.getElementById('deleteAccountForm');
    const deletePassword = document.getElementById('deletePassword');
    const btnConfirmDelete = document.getElementById('btnConfirmDelete');

    if (deleteForm) {
        deleteForm.addEventListener('submit', function(e) {
            // Nếu chưa nhập mật khẩu
            if (deletePassword.value.trim() === '') {
                e.preventDefault(); // Chặn gửi đi
                deletePassword.classList.add('is-invalid');
            } else {
                deletePassword.classList.remove('is-invalid');

                // UX: Đổi trạng thái nút bấm khi đang gửi request để tránh click 2 lần
                btnConfirmDelete.innerHTML = '<span class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>Đang xử lý...';
                btnConfirmDelete.classList.add('disabled');
            }
        });

        // Xóa thông báo lỗi khi người dùng bắt đầu gõ
        deletePassword.addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    }
});