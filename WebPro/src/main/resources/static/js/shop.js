/* 
 * Enhanced Shop Page with Quick View, Lazy Loading, and Filters
 */

// Register Service Worker for PWA
if ('serviceWorker' in navigator) {
    window.addEventListener('load', () => {
        navigator.serviceWorker.register('/sw.js')
            .then(registration => console.log('SW registered:', registration))
            .catch(error => console.log('SW registration failed:', error));
    });
}

// Initialize features when DOM is loaded
document.addEventListener('DOMContentLoaded', function () {
    initQuickViewButtons();
    initPriceFilter();
    initSortButtons();
});

// Quick View functionality
function initQuickViewButtons() {
    const quickViewButtons = document.querySelectorAll('.btn-quick-view');
    quickViewButtons.forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const productId = btn.dataset.productId;
            showQuickView(productId);
        });
    });
}

// Price Range Filter
function initPriceFilter() {
    const priceRange = document.getElementById('priceRange');
    const priceValue = document.getElementById('priceValue');

    if (priceRange && priceValue) {
        priceRange.addEventListener('input', (e) => {
            const value = parseInt(e.target.value);
            priceValue.textContent = formatPrice(value);
            filterProductsByPrice(value);
        });
    }
}

function formatPrice(price) {
    return '₫' + price.toLocaleString('vi-VN');
}

function filterProductsByPrice(maxPrice) {
    const products = document.querySelectorAll('.product-card');
    products.forEach(product => {
        const price = parseInt(product.dataset.price);
        if (price <= maxPrice) {
            product.style.display = 'block';
        } else {
            product.style.display = 'none';
        }
    });
}

// Sort functionality
function initSortButtons() {
    const sortButtons = document.querySelectorAll('.sort-btn');
    sortButtons.forEach(btn => {
        btn.addEventListener('click', () => {
            sortButtons.forEach(b => b.classList.remove('active'));
            btn.classList.add('active');
            const sortType = btn.dataset.sort;
            sortProducts(sortType);
        });
    });
}

function sortProducts(sortType) {
    const container = document.querySelector('.product-container');
    const products = Array.from(container.children);

    products.sort((a, b) => {
        if (sortType === 'price-asc') {
            return parseInt(a.dataset.price) - parseInt(b.dataset.price);
        } else if (sortType === 'price-desc') {
            return parseInt(b.dataset.price) - parseInt(a.dataset.price);
        } else if (sortType === 'name') {
            return a.dataset.name.localeCompare(b.dataset.name);
        }
        return 0;
    });

    products.forEach(product => container.appendChild(product));
}

// Add to cart with animation
function addToCartWithAnimation(button, productId) {
    showLoading(button);

    fetch('/add-to-cart', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        body: `mahh=${productId}&quantity=1`
    })
        .then(response => {
            hideLoading(button);

            if (response.ok) {
                // Update cart badge
                const currentCount = parseInt(document.querySelector('.cart-badge').textContent);
                updateCartBadge(currentCount + 1);

                // Show success toast
                showToast('Đã thêm sản phẩm vào giỏ hàng!', 'success');

                // Cart icon animation
                const cartIcon = document.querySelector('.cart-icon');
                cartIcon.classList.add('bounce');
                setTimeout(() => cartIcon.classList.remove('bounce'), 500);
            } else {
                showToast('Có lỗi xảy ra, vui lòng thử lại', 'error');
            }
        })
        .catch(error => {
            hideLoading(button);
            showToast('Có lỗi xảy ra, vui lòng thử lại', 'error');
        });
}

// Smooth scroll to top
function scrollToTop() {
    window.scrollTo({
        top: 0,
        behavior: 'smooth'
    });
}

// Show scroll to top button when scrolling
window.addEventListener('scroll', () => {
    const scrollBtn = document.getElementById('scrollToTop');
    if (scrollBtn) {
        if (window.pageYOffset > 300) {
            scrollBtn.style.display = 'flex';
        } else {
            scrollBtn.style.display = 'none';
        }
    }
});

// Search suggestions
function initSearchSuggestions() {
    const searchInput = document.getElementById('searchInput');
    if (!searchInput) return;

    let debounceTimer;
    searchInput.addEventListener('input', (e) => {
        clearTimeout(debounceTimer);
        debounceTimer = setTimeout(() => {
            const query = e.target.value;
            if (query.length >= 2) {
                fetchSearchSuggestions(query);
            }
        }, 300);
    });
}

function fetchSearchSuggestions(query) {
    fetch(`/api/search-suggestions?q=${encodeURIComponent(query)}`)
        .then(response => response.json())
        .then(suggestions => {
            displaySearchSuggestions(suggestions);
        })
        .catch(error => console.error('Search suggestions error:', error));
}

function displaySearchSuggestions(suggestions) {
    const container = document.getElementById('searchSuggestions');
    if (!container) return;

    container.innerHTML = suggestions.map(s => `
        <div class="suggestion-item" onclick="window.location.href='/product/${s.mahh}'">
            <img src="/Images/${s.hinh}" alt="${s.tenhh}">
            <div>
                <strong>${s.tenhh}</strong>
                <span>${formatPrice(s.dongia)}</span>
            </div>
        </div>
    `).join('');

    container.style.display = suggestions.length > 0 ? 'block' : 'none';
}
