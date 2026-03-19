// function.js - Global functions for admin panel
(function ($) {

	// Check all functionality
	$(document).on('click', 'input#check-all', function (event) {
		var check = $(this).is(':checked');
		if (check) {
			$('input.item-check').prop({
				checked: true
			});
		} else {
			$('input.item-check').prop({
				checked: false
			});
		}
	});

}(jQuery));

// GLOBAL loadContent function - QUAN TRỌNG!
function loadContent(url, title) {
	// Nếu không có title, dùng text từ button
	if (!title) {
		title = 'Nội dung';
	}

	// Cập nhật tiêu đề
	$('.content-header h1').html(title);
	$('.breadcrumb li.active').text(title);

	// Hiển thị loading
	$('#dynamic-content').html(`
        <div style="text-align:center;padding:50px;">
            <i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>
            <p>Đang tải...</p>
        </div>
    `);

	// Load nội dung từ URL
	$.get(url)
		.done(function (data) {
			$('#dynamic-content').html(data);
			console.log('Loaded content from: ' + url);
		})
		.fail(function (xhr, status, error) {
			console.error('Error loading content:', error);
			$('#dynamic-content').html(`
                <div class="alert alert-danger">
                    <strong>Lỗi!</strong> Không thể tải nội dung. Vui lòng thử lại.
                    <br><small>URL: ${url}</small>
                    <br><small>Error: ${error}</small>
                </div>
            `);
		});
}

// TOAST NOTIFICATION FUNCTION - Thay thế alert()
function showToast(message, type) {
	// type: success, error, warning, info
	var bgColor = '#28a745'; // success green
	var icon = 'fa-check-circle';

	if (type === 'error') {
		bgColor = '#dc3545'; // danger red
		icon = 'fa-times-circle';
	} else if (type === 'warning') {
		bgColor = '#ffc107'; // warning yellow
		icon = 'fa-exclamation-triangle';
	} else if (type === 'info') {
		bgColor = '#17a2b8'; // info blue
		icon = 'fa-info-circle';
	}

	// Tạo toast element
	var toastId = 'toast-' + Date.now();
	var toastHtml = `
        <div id="${toastId}" class="custom-toast" style="
            position: fixed;
            top: 80px;
            right: 20px;
            min-width: 300px;
            max-width: 500px;
            background: ${bgColor};
            color: white;
            padding: 15px 20px;
            border-radius: 4px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 9999;
            display: flex;
            align-items: center;
            animation: slideInRight 0.3s ease-out;
        ">
            <i class="fa ${icon}" style="font-size: 24px; margin-right: 15px;"></i>
            <div style="flex: 1;">${message}</div>
            <button onclick="$('#${toastId}').fadeOut(300, function(){ $(this).remove(); })" 
                    style="background: none; border: none; color: white; font-size: 20px; cursor: pointer; margin-left: 10px; opacity: 0.8;">
                ×
            </button>
        </div>
    `;

	// Thêm CSS animation nếu chưa có
	if (!$('#toast-animation-style').length) {
		$('head').append(`
            <style id="toast-animation-style">
                @keyframes slideInRight {
                    from {
                        transform: translateX(100%);
                        opacity: 0;
                    }
                    to {
                        transform: translateX(0);
                        opacity: 1;
                    }
                }
            </style>
        `);
	}

	// Thêm toast vào body
	$('body').append(toastHtml);

	// Tự động ẩn sau 4 giây
	setTimeout(function () {
		$('#' + toastId).fadeOut(300, function () {
			$(this).remove();
		});
	}, 4000);
}