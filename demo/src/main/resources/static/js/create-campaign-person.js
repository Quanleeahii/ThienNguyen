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
                $uploadZone.html(`
                    <img src="${event.target.result}" style="max-width: 100%; max-height: 250px; border-radius: 8px; object-fit: contain; box-shadow: 0 4px 10px rgba(0,0,0,0.1);">
                    <p style="color: var(--primary-color); margin-top: 15px; margin-bottom: 0; font-size: 0.9rem; font-style: italic;">
                        Nhấp vào đây để đổi ảnh khác
                    </p>
                `);
            };

            reader.readAsDataURL(file);
        } else {
            $uploadZone.html(`
                <i class="fa-solid fa-cloud-arrow-up"></i>
                <p class="mb-0">Nhấp để tải lên ảnh đại diện chiến dịch</p>
            `);
        }
    });

    $('form').on('submit', function(e) {
        if ($('#fileInput').val() === '') {
            e.preventDefault();
            alert('Vui lòng tải lên ảnh đại diện cho chiến dịch!');
            $('html, body').animate({
                scrollTop: $('.image-upload-zone').offset().top - 100
            }, 500);
        }
    });

    $('body').on('mouseleave', '.select2-dropdown', function() {
        $('select').select2('close');
    });

});