// Service Worker for PWA
// Bump version so browsers fetch the updated SW + new cache contents.
const CACHE_NAME = 'handcratz-v4';
const urlsToCache = [
    '/css/global.css',
    '/js/global.js',
    // NOTE: Không cache trang HTML (/, /shop, /cart...) vì nội dung phụ thuộc session/auth.
];

// Install event - cache resources
self.addEventListener('install', event => {
    event.waitUntil(
        (async () => {
            // Kích hoạt SW mới ngay khi cài xong (không chờ tab đóng).
            self.skipWaiting();
            const cache = await caches.open(CACHE_NAME);
            console.log('Opened cache');
            await cache.addAll(urlsToCache);
        })()
    );
});

// Fetch event - serve from cache when offline
self.addEventListener('fetch', event => {
    // Skip caching for POST requests - let them go directly to server
    if (event.request.method !== 'GET') {
        return;
    }

    event.respondWith(
        (async () => {
            const accept = event.request.headers.get('accept') || '';
            const destination = event.request.destination;
            let pathname = '';
            try {
                pathname = new URL(event.request.url).pathname || '';
            } catch (e) {
                // ignore
            }

            // Bypass cache/logic cho các trang cart vì phụ thuộc session (cartItems render server-side)
            if (pathname === '/cart' || pathname.startsWith('/cart/')) {
                // Không cache, luôn lấy từ network (để tránh render sai do SW cũ/cache cũ).
                return fetch(event.request);
            }

            const isAssetJsCss =
                pathname.startsWith('/js/') ||
                pathname.startsWith('/css/');
            const isNavigation =
                event.request.mode === 'navigate' ||
                accept.includes('text/html') ||
                destination === 'document';

            // Tuyệt đối không “đóng băng” trang HTML theo cache,
            // vì nó phụ thuộc vào trạng thái đăng nhập (session/cookie).
            if (isNavigation) {
                try {
                    return await fetch(event.request);
                } catch (err) {
                    const cache = await caches.open(CACHE_NAME);
                    return (await cache.match(event.request)) || new Response('Offline', { status: 503 });
                }
            }

            // Với JS/CSS: ưu tiên network-first để lấy bản mới (đặc biệt khi dev hot-reload file).
            if (isAssetJsCss) {
                try {
                    const networkResponse = await fetch(event.request);
                    if (networkResponse && networkResponse.ok) {
                        const cache = await caches.open(CACHE_NAME);
                        cache.put(event.request, networkResponse.clone());
                    }
                    return networkResponse;
                } catch (err) {
                    const cachedResponse = await caches.match(event.request);
                    if (cachedResponse) return cachedResponse;
                    throw err;
                }
            }

            // Với tài nguyên khác: cache-first.
            const cachedResponse = await caches.match(event.request);
            if (cachedResponse) return cachedResponse;

            const networkResponse = await fetch(event.request);
            if (networkResponse && networkResponse.ok) {
                // Không cache document/HTML dù lỡ phân loại sai.
                if ((event.request.destination === 'document') || (accept && accept.includes('text/html'))) {
                    return networkResponse;
                }
                const cache = await caches.open(CACHE_NAME);
                cache.put(event.request, networkResponse.clone());
            }
            return networkResponse;
        })()
    );
});

// Activate event - clean up old caches
self.addEventListener('activate', event => {
    const cacheWhitelist = [CACHE_NAME];
    event.waitUntil(
        caches.keys().then(cacheNames => {
            return Promise.all(
                cacheNames.map(cacheName => {
                    if (cacheWhitelist.indexOf(cacheName) === -1) {
                        return caches.delete(cacheName);
                    }
                })
            );
        })
            .then(() => self.clients.claim())
    );
});
