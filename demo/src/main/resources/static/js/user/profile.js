document.addEventListener("DOMContentLoaded", function() {
    const profileForm = document.getElementById('profileForm');
    const fullNameInput = document.getElementById('fullName');
    const nameError = document.getElementById('nameError');
    const avatarPreview = document.getElementById('avatar-preview');
    const fileInput = document.getElementById('file-input');

    // --- 1. Xem trước ảnh khi chọn file (Giữ nguyên) ---
    if (fileInput && avatarPreview) {
        fileInput.addEventListener('change', function() {
            const file = this.files[0];
            if (file) {
                avatarPreview.src = URL.createObjectURL(file);
            }
        });
    }

    // --- 2. Xử lý AJAX nộp form ---
    if (profileForm) {
        profileForm.addEventListener('submit', function(e) {
            e.preventDefault(); // Chặn hành động load lại trang mặc định

            // Kiểm tra Validation nhanh
            if (fullNameInput.value.trim() === '') {
                nameError.classList.remove('d-none');
                fullNameInput.style.borderColor = '#dc3545';
                return;
            }

            // Đóng gói tất cả dữ liệu form (bao gồm cả file ảnh)
            const formData = new FormData(profileForm);

            // Gửi dữ liệu "ngầm" đến Server
            fetch('/profile/update', {
                method: 'POST',
                body: formData
            })
            .then(response => response.json()) // Chờ Server trả về JSON
            .then(data => {
                if (data.status === 'success') {
                    // 1. Hiện thông báo thành công (In xanh)
                    showNotification('success', data.message);

                    // 2. Cập nhật ảnh đại diện trên Header ngay lập tức
                    if (data.newAvatar) {
                        const headerAvatar = document.querySelector('.dropdown img.rounded-circle');
                        if (headerAvatar) {
                            headerAvatar.src = '/upload_img/avatars/' + data.newAvatar;
                        }
                    }
                } else {
                    showNotification('error', data.message);
                }
            })
            .catch(err => {
                showNotification('error', 'Có lỗi xảy ra khi kết nối máy chủ!');
            });
        });
    }

    // Hàm phụ để tạo dòng chữ thông báo đỏ/xanh mà không cần load lại trang
    function showNotification(type, message) {
        const container = document.querySelector('.profile-message-container');
        container.innerHTML = ''; // Xóa thông báo cũ

        const div = document.createElement('div');
        div.className = (type === 'success') ? 'txt-msg-success' : 'txt-msg-error';
        div.innerHTML = `<i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-triangle'} me-2"></i><span>${message}</span>`;

        container.appendChild(div);

        // Tự động ẩn thông báo sau 5 giây cho chuyên nghiệp
        setTimeout(() => { div.style.opacity = '0'; div.style.transition = '1s'; }, 4000);
        setTimeout(() => { div.remove(); }, 5000);
    }
});