// ==================== CART FUNCTIONS ====================

/**
 * Simple, clean add to cart function
 */
function addToCart(productId, quantity) {
    if (!productId) {
        console.error('Product ID is required');
        return;
    }

    quantity = quantity || 1;

    // Show loading
    console.log(`Adding ${quantity}x ${productId} to cart...`);

    fetch('/add-to-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `mahh=${encodeURIComponent(productId)}&quantity=${quantity}`
    })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                // Update ALL cart badges on page
                updateCartBadge(data.cartSize);

                // Animate cart icon
                animateCartIcon();

                // Show success message
                showToast(data.message || 'Đã thêm vào giỏ hàng!', 'success');

                console.log('Cart updated! Size:', data.cartSize);
            } else {
                const type = data.errorType === 'stock' ? 'warning' : 'error';
                showToast(data.message || 'Không thể thêm vào giỏ hàng!', type);
            }
        })
        .catch(error => {
            console.error('Add to cart error:', error);
            showToast('Lỗi kết nối! Vui lòng thử lại.', 'error');
        });
}

/**
 * Update all cart badge elements
 */
function updateCartBadge(count) {
    const badges = document.querySelectorAll('.cart-badge');
    badges.forEach(badge => {
        badge.textContent = count;
        if (count > 0) {
            badge.style.display = 'flex';
        } else {
            badge.style.display = 'none';
        }
    });
}

/**
 * Animate cart icon when item added
 */
function animateCartIcon() {
    const cartIcons = document.querySelectorAll('.cart-icon, .fa-shopping-cart');
    cartIcons.forEach(icon => {
        icon.classList.add('bounce');
        setTimeout(() => {
            icon.classList.remove('bounce');
        }, 600);
    });
}

/**
 * Show toast notification
 */
function showToast(message, type = 'info') {
    // Remove existing toasts
    const existingToasts = document.querySelectorAll('.toast-notification');
    existingToasts.forEach(toast => toast.remove());

    // Create toast
    const toast = document.createElement('div');
    toast.className = `toast-notification toast-${type}`;
    toast.innerHTML = `
        <i class="fas ${type === 'success' ? 'fa-check-circle' : 'fa-exclamation-circle'}"></i>
        <span>${message}</span>
    `;

    document.body.appendChild(toast);

    // Show toast
    setTimeout(() => toast.classList.add('show'), 10);

    // Hide and remove after 3 seconds
    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Initialize cart badge on page load
document.addEventListener('DOMContentLoaded', function () {
    // Fetch current cart size from server on page load
    fetch('/cart-size')
        .then(r => r.json())
        .then(data => {
            if (data.cartSize !== undefined) {
                updateCartBadge(data.cartSize);
            }
        })
        .catch(e => console.log('Could not load cart size:', e));
});

// Export to window for global access
window.addToCart = addToCart;
window.updateCartBadge = updateCartBadge;
window.showToast = showToast;

// Backward compatibility: một số trang gọi addToCartAjax(productId, quantity, button)
// Trong codebase hiện tại, logic thực tế nằm ở addToCart(productId, quantity).
window.addToCartAjax = function (productId, quantity, button) {
    // button (tham số thứ 3) không cần dùng cho logic thêm giỏ hiện tại.
    return addToCart(productId, quantity);
};

// ==================== FORCE RELOAD ON NAVIGATION ====================
// Giải quyết triệt để vấn đề browser cache bằng cách thêm timestamp vào URL

// 1. Force reload khi user nhấn nút Back/Forward (bfcache)
// ĐANG TẮT để tránh nhấp nháy trang (đặc biệt trang /cart)
// Khi cần, có thể bật lại sau khi xử lý triệt để cache/PWA.
// window.addEventListener('pageshow', function (event) {
//     if (event.persisted) {
//         window.location.reload();
//     }
// });

// 2. Intercept tất cả click vào link để thêm timestamp
document.addEventListener('DOMContentLoaded', function () {
    // Không áp dụng cho trang admin
    if (window.location.pathname.startsWith('/admin')) {
        return;
    }

    document.body.addEventListener('click', function (e) {
        // Tìm thẻ a gần nhất (xử lý trường hợp click vào icon bên trong link)
        const link = e.target.closest('a');

        // Kiểm tra link hợp lệ
        if (link && link.href) {
            // Bỏ qua các protocol đặc biệt và tab mới
            if (link.href.startsWith('javascript:') ||
                link.href.startsWith('mailto:') ||
                link.href.startsWith('tel:') ||
                link.getAttribute('target') === '_blank') {
                return;
            }

            // Chỉ xử lý link nội bộ (cùng domain)
            if (link.hostname === window.location.hostname) {
                // Bỏ qua link trỏ tới admin
                if (link.pathname.startsWith('/admin')) {
                    return;
                }

                // Xử lý anchor link (#)
                const currentUrlNoHash = window.location.href.split('#')[0].split('?')[0];
                const targetUrlNoHash = link.href.split('#')[0].split('?')[0];

                // Nếu là cùng trang chỉ khác hash -> để mặc định cho browser scroll
                if (currentUrlNoHash === targetUrlNoHash && link.hash) {
                    return;
                }

                // Ngăn hành vi mặc định
                e.preventDefault();

                try {
                    const url = new URL(link.href);
                    // Thêm tham số _t = timestamp hiện tại để bypass cache
                    url.searchParams.set('_t', Date.now());

                    // Chuyển trang
                    window.location.assign(url.toString());
                } catch (err) {
                    // Fallback nếu có lỗi parse URL
                    window.location.href = link.href;
                }
            }
        }
    });
});

// 3. Tự động làm sạch URL (ẩn tham số _t) sau khi load xong
(function () {
    // Chỉ chạy nếu có tham số _t
    if (window.location.search.includes('_t=')) {
        try {
            const url = new URL(window.location.href);
            // Xóa tham số _t
            url.searchParams.delete('_t');
            // Thay thế URL trên thanh địa chỉ mà không reload lại trang
            window.history.replaceState({}, document.title, url.toString());
        } catch (e) {
            console.error('Could not clean URL', e);
        }
    }
})();

// ==================== DARK MODE ====================
// Toggle bằng nút #darkModeToggle (các trang đều dùng /js/global.js).
document.addEventListener('DOMContentLoaded', function () {
    const toggleBtn = document.getElementById('darkModeToggle');
    if (!toggleBtn) return;

    const storedTheme = localStorage.getItem('theme');
    if (storedTheme === 'dark') {
        document.body.classList.add('dark-mode');
    }

    toggleBtn.addEventListener('click', function () {
        const enabled = document.body.classList.toggle('dark-mode');
        localStorage.setItem('theme', enabled ? 'dark' : 'light');
    });
});
