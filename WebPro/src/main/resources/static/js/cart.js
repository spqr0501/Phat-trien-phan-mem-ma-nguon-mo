// PWA Service Worker Registration
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/sw.js')
            .then(reg => console.log('✅ Service Worker registered'))
            .catch(err => console.log('❌ SW registration failed:', err));
    });
}

// Scroll to top button
window.addEventListener('scroll', () => {
    const scrollBtn = document.getElementById('scrollToTop');
    if (window.pageYOffset > 300) {
        scrollBtn.style.display = 'flex';
    } else {
        scrollBtn.style.display = 'none';
    }
});

// Add to cart via AJAX
function addToCartQuick(button) {
    const mahh = button.getAttribute('data-mahh');
    const formData = new FormData();
    formData.append('mahh', mahh);
    formData.append('quantity', '1');

    // Get CSRF token
    const csrfToken = document.querySelector('meta[name="_csrf"]');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]');

    console.log('🔐 CSRF Token:', csrfToken ? csrfToken.content : 'NOT FOUND');
    console.log('🔐 CSRF Header:', csrfHeader ? csrfHeader.content : 'NOT FOUND');

    const headers = {};
    if (csrfToken && csrfHeader) {
        headers[csrfHeader.content] = csrfToken.content;
    }

    console.log('📤 Sending request with headers:', headers);

    fetch('/add-to-cart', {
        method: 'POST',
        headers: headers,
        body: formData
    })
        .then(response => {
            console.log('📥 Response status:', response.status);
            if (response.redirected) {
                window.location.href = response.url;
            } else if (response.ok) {
                alert('✓ Đã thêm vào giỏ hàng!');
                location.reload();
            } else {
                return response.text().then(text => {
                    console.error('❌ Error response:', text);
                    alert('Có lỗi khi thêm vào giỏ hàng!');
                });
            }
        })
        .catch(error => {
            console.error('❌ Error:', error);
            alert('Có lỗi xảy ra khi thêm vào giỏ hàng!');
        });
}

console.log('✅ cart.js loaded successfully');
