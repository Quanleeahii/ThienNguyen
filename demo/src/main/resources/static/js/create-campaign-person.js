$(document).ready(function() {
    $('.select2-bank').select2({
        placeholder: "Gõ để tìm ngân hàng...",
        allowClear: false,
        width: '100%'
    });
    $('.select2-category').select2({
        placeholder: "-- Chọn lĩnh vực --",
        width: '100%',
        minimumResultsForSearch: 10
    });
    const $bankAccName = $('input[name="bankAccountName"]');
    if ($bankAccName.length > 0) {
        $bankAccName.attr('autocomplete', 'off');
        $bankAccName.on('input', function() {
            const start = this.selectionStart;
            const end = this.selectionEnd;
            this.value = this.value.toUpperCase();
            this.setSelectionRange(start, end);
        });
    }
    $('#categorySelect').on('change', function() {
        const selectedText = $(this).find("option:selected").text().trim().toLowerCase();
        const $otherDiv = $('#otherCategoryDiv');
        const $otherInput = $otherDiv.find('input');
        if (selectedText.includes('khác')) {
            $otherDiv.slideDown(300);
            $otherInput.prop('required', true);
        } else {
            $otherDiv.slideUp(300);
            $otherInput.val('').prop('required', false);
        }
    });
    $('#fileInput').on('change', function(e) {
        const file = e.target.files[0];
        const $uploadZone = $('.image-upload-zone');
        if (file) {
            if (!file.type.match('image.*')) {
                alert("Vui lòng chỉ chọn file hình ảnh!");
                $(this).val('');
                return;
            }
            const reader = new FileReader();
            reader.onload = function(event) {
                $uploadZone.css('border-color', 'var(--primary-color)');
                $uploadZone.html(`
                    <div style="position: relative; width: 100%;">
                        <img src="${event.target.result}" style="max-width: 100%; max-height: 280px; border-radius: 8px; object-fit: contain; box-shadow: 0 4px 15px rgba(0,0,0,0.1);">
                        <div style="margin-top: 10px; color: #ff6a00; font-weight: 500;">
                            <i class="fa-solid fa-rotate"></i> Nhấp để thay đổi ảnh khác
                        </div>
                    </div>
                `);
            };
            reader.readAsDataURL(file);
        }
    });
    $('input[type="datetime-local"]').on('change', function() {
        const selectedDate = new Date($(this).val());
        const now = new Date();
        if (selectedDate < now) {
            alert("Ngày kết thúc không được nằm trong quá khứ!");
            $(this).val('');
        }
    });
    $('form').on('submit', function(e) {
        if ($('#fileInput').val() === '') {
            e.preventDefault();
            alert('Vui lòng tải lên ảnh đại diện cho chiến dịch!');
            $('html, body').animate({
                scrollTop: $('.image-upload-zone').offset().top - 100
            }, 500);
            return;
        }
        const $btn = $(this).find('.btn-submit');
        $btn.html('<span class="spinner-border spinner-border-sm me-2"></span> Đang xử lý...').prop('disabled', true);
    });

});