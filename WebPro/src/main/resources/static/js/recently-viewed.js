// Recently Viewed Products - localStorage implementation
// Add this script to index.html, shop.html, and product-detail.html

(function () {
    const STORAGE_KEY = 'pctech_recently_viewed';
    const MAX_ITEMS = 8;

    // Get recently viewed products
    function getRecentlyViewed() {
        const data = localStorage.getItem(STORAGE_KEY);
        return data ? JSON.parse(data) : [];
    }

    // Add product to recently viewed
    function addToRecentlyViewed(product) {
        let products = getRecentlyViewed();

        // Remove if already exists
        products = products.filter(p => p.id !== product.id);

        // Add to beginning
        products.unshift(product);

        // Keep only MAX_ITEMS
        products = products.slice(0, MAX_ITEMS);

        localStorage.setItem(STORAGE_KEY, JSON.stringify(products));
    }

    // Track current product (on product detail page)
    window.trackProductView = function (productId, productName, productImage, productPrice) {
        addToRecentlyViewed({
            id: productId,
            name: productName,
            image: productImage,
            price: productPrice,
            viewedAt: new Date().toISOString()
        });
    };

    // Display recently viewed products
    window.displayRecentlyViewed = function (containerId) {
        const container = document.getElementById(containerId);
        if (!container) return;

        const products = getRecentlyViewed();

        if (products.length === 0) {
            container.innerHTML = '<p style="color:#999; text-align:center; padding:20px;">Chưa có sản phẩm đã xem</p>';
            return;
        }

        container.innerHTML = `
            <h3 style="margin-bottom:20px; font-size:1.3em;">
                <i class="fas fa-history"></i> Sản phẩm đã xem
            </h3>
            <div style="display:grid; grid-template-columns:repeat(auto-fill, minmax(180px, 1fr)); gap:15px;">
                ${products.map(p => `
                    <a href="/product/${p.id}" style="text-decoration:none; color:inherit;">
                        <div style="background:white; border-radius:8px; overflow:hidden; box-shadow:0 2px 8px rgba(0,0,0,0.1); transition:transform 0.3s;"
                             onmouseover="this.style.transform='translateY(-5px)'" 
                             onmouseout="this.style.transform='translateY(0)'">
                            <img src="/Images/${p.image}" 
                                 style="width:100%; height:150px; object-fit:cover;"
                                 onerror="this.src='/images/placeholder.jpg'">
                            <div style="padding:12px;">
                                <div style="font-size:0.9em; font-weight:600; margin-bottom:8px; 
                                           overflow:hidden; text-overflow:ellipsis; white-space:nowrap;">
                                    ${p.name}
                                </div>
                                <div style="color:#667eea; font-weight:700; font-size:1em;">
                                    ${p.price}
                                </div>
                            </div>
                        </div>
                    </a>
                `).join('')}
            </div>
        `;
    };

    // Clear recently viewed
    window.clearRecentlyViewed = function () {
        localStorage.removeItem(STORAGE_KEY);
        console.log('Recently viewed cleared');
    };

    console.log('Recently Viewed tracker loaded');
})();

/* 
USAGE INSTRUCTIONS:

1. Add this script to your HTML pages:
   <script src="/js/recently-viewed.js"></script>

2. On product detail page, track the view:
   <script th:inline="javascript">
       window.trackProductView(
           /*[[${product.mahh}]]*/ '',
           /*[[${product.tenhh}]]*/ '',
           /*[[${product.hinh}]]*/ '',
           /*[[${#numbers.formatDecimal(product.giaHienTai, 0, 'COMMA', 0, 'POINT')}]]*/ '' + ' ₫'
       );
   </script >

    3. Display recently viewed anywhere:
   <div id="recently-viewed-container"></div>
   <script>
       window.displayRecentlyViewed('recently-viewed-container');
   </script>
    */
