// Code JavaScript cho các tương tác trên giao diện
document.addEventListener('DOMContentLoaded', () => {
    console.log("Website Kaleidoscope Initialized!");

    const API_BASE = 'http://localhost:8080';

    const getToken = () => localStorage.getItem('authToken');
    const setSession = (token, user) => {
        localStorage.setItem('authToken', token);
        localStorage.setItem('authUser', JSON.stringify(user || {}));
    };
    const clearSession = () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('authUser');
    };

    const parseJwt = (token) => {
        try {
            const parts = token.split('.');
            if (parts.length < 2) return null;
            const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
            const decoded = atob(payload);
            const json = decodeURIComponent(decoded.split('').map(ch => {
                return '%' + ('00' + ch.charCodeAt(0).toString(16)).slice(-2);
            }).join(''));
            return JSON.parse(json);
        } catch (err) {
            return null;
        }
    };

    const isTokenExpired = (token) => {
        const payload = parseJwt(token);
        if (!payload || !payload.exp) return false;
        return Date.now() >= payload.exp * 1000;
    };

    const redirectToLogin = () => {
        const path = window.location.pathname;
        if (path.endsWith('/admin-users.html')) {
            window.location.href = 'admin-login.html';
            return;
        }
        if (path.endsWith('/profile.html')) {
            window.location.href = 'login.html';
        }
    };

    const showMessage = (form, message, type = 'error') => {
        if (!form) return;
        let box = form.querySelector('.form-message');
        if (!box) {
            box = document.createElement('div');
            box.className = 'form-message';
            form.prepend(box);
        }
        box.textContent = message;
        box.dataset.type = type;
    };

    const apiRequest = async (path, options = {}) => {
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };
        const token = getToken();
        if (token) {
            headers.Authorization = `Bearer ${token}`;
        }

        const response = await fetch(`${API_BASE}${path}`, {
            ...options,
            headers
        });

        let payload = null;
        try {
            payload = await response.json();
        } catch (err) {
            payload = null;
        }

        if (!response.ok) {
            if (response.status === 401) {
                clearSession();
                redirectToLogin();
            }
            const message = payload?.message || 'Request failed';
            throw new Error(message);
        }

        if (payload && typeof payload.status !== 'undefined') {
            if (payload.status >= 400) {
                throw new Error(payload.message || 'Request failed');
            }
            return payload.data;
        }

        return payload;
    };

    // Toggle menu items hoặc active states
    const navLinks = document.querySelectorAll('.nav-btn');

    navLinks.forEach(link => {
        link.addEventListener('click', function (e) {
            if (this.getAttribute('href') === '#') {
                e.preventDefault();
            }
            // Xóa background/border style cũ, add class cho item hiện tại (nếu cần)
            navLinks.forEach(item => {
                item.style.border = '1px solid var(--text-color)';
                item.style.backgroundColor = 'transparent';
            });
            this.style.border = '1px solid #777';
            this.style.backgroundColor = '#fff';

            console.log('Navigated to: ' + this.textContent);
        });
    });

    // Sidebar Collapse Logic
    const sidebarHeaders = document.querySelectorAll('.sidebar-header');
    sidebarHeaders.forEach(header => {
        header.addEventListener('click', function () {
            const block = this.parentElement;
            block.classList.toggle('collapsed');
        });
    });

    // Product Gallery logic
    const thumbnails = document.querySelectorAll('.thumbnail');
    const mainImage = document.getElementById('mainImage');
    if (thumbnails.length > 0 && mainImage) {
        thumbnails.forEach(thumb => {
            thumb.addEventListener('click', function () {
                thumbnails.forEach(t => t.classList.remove('active'));
                this.classList.add('active');
                mainImage.src = this.querySelector('img').src;
            });
        });
    }

    // Quantity selector logic
    const btnMinus = document.querySelector('.qty-btn.minus');
    const btnPlus = document.querySelector('.qty-btn.plus');
    const qtyInput = document.querySelector('.qty-input');

    if (btnMinus && btnPlus && qtyInput) {
        btnMinus.addEventListener('click', () => {
            let val = parseInt(qtyInput.value) || 1;
            if (val > 1) qtyInput.value = val - 1;
        });
        btnPlus.addEventListener('click', () => {
            let val = parseInt(qtyInput.value) || 1;
            qtyInput.value = val + 1;
        });
    }

    const token = getToken();
    if (token && isTokenExpired(token)) {
        clearSession();
    }

    const path = window.location.pathname;
    const isProfilePage = path.endsWith('/profile.html');
    const isAdminUsersPage = path.endsWith('/admin-users.html');

    if (isProfilePage && !getToken()) {
        window.location.href = 'login.html';
    }
    if (isAdminUsersPage) {
        const storedUser = JSON.parse(localStorage.getItem('authUser') || '{}');
        if (!getToken() || storedUser.role !== 'ADMIN') {
            window.location.href = 'admin-login.html';
        }
    }

    const badgeMap = {
        cart: 'cartCount',
        favorite: 'favoriteCount'
    };
    window.updateBadges = async () => {
        // Sync with BE if needed
        if (getToken()) {
            try {
                const data = await apiRequest('/api/cart');
                const itemsCount = (data.items || []).reduce((acc, item) => acc + item.quantity, 0);
                localStorage.setItem('cartCount', itemsCount);
                
                const wishlistData = await apiRequest('/api/wishlist');
                localStorage.setItem('favoriteCount', (wishlistData.products || []).length);
            } catch (err) { }
        }

        document.querySelectorAll('[data-badge]').forEach(item => {
            const type = item.dataset.badge;
            const key = badgeMap[type];
            const count = parseInt(localStorage.getItem(key) || '0', 10);
            const badge = item.querySelector('.action-badge');
            if (!badge) return;
            if (count > 0) {
                badge.textContent = count;
                badge.classList.remove('hidden');
                item.classList.add('has-badge');
            } else {
                badge.textContent = '';
                badge.classList.add('hidden');
                item.classList.remove('has-badge');
            }
            
            // Add click listener for navigation if not already a link
            if (!item.querySelector('a')) {
                item.style.cursor = 'pointer';
                const link = item.dataset.link;
                if (link) {
                   item.onclick = () => window.location.href = link;
                } else {
                    item.onclick = () => {
                        const inPages = window.location.pathname.includes('/pages/');
                        const base = inPages ? '' : 'pages/';
                        if (type === 'cart') window.location.href = `${base}cart.html`;
                        if (type === 'favorite') window.location.href = `${base}wishlist.html`;
                    };
                }
            }
        });
    };
    updateBadges();

    window.addToCart = async (productId, quantity = 1) => {
        if (!getToken()) {
            const inPages = window.location.pathname.includes('/pages/');
            window.location.href = inPages ? 'login.html' : 'pages/login.html';
            return;
        }
        try {
            await apiRequest('/api/cart', {
                method: 'POST',
                body: JSON.stringify({ productId, quantity })
            });
            alert('Đã thêm sản phẩm vào giỏ hàng');
            updateBadges();
        } catch (err) {
            alert(err.message || 'Thêm vào giỏ hàng thất bại');
        }
    };

    const updateHeaderUserName = () => {
        const storedUser = JSON.parse(localStorage.getItem('authUser') || '{}');
        const name = storedUser.fullName || storedUser.username || '';
        document.querySelectorAll('.header-user-name').forEach(el => {
            el.textContent = name || 'Tài khoản';
        });
    };
    updateHeaderUserName();

    const updateHeaderUserLink = () => {
        const inPages = window.location.pathname.includes('/pages/');
        const base = inPages ? '' : 'pages/';
        const tokenValue = getToken();
        const storedUser = JSON.parse(localStorage.getItem('authUser') || '{}');
        let target = `${base}auth.html`;
        if (tokenValue && !isTokenExpired(tokenValue)) {
            if (storedUser.role === 'ADMIN') {
                target = `${base}admin-users.html`;
            } else {
                target = `${base}profile.html`;
            }
        }
        document.querySelectorAll('.header-user-link').forEach(link => {
            link.setAttribute('href', target);
        });
    };
    updateHeaderUserLink();

    const logoutLinks = document.querySelectorAll('.logout-action, .logout-link');
    logoutLinks.forEach(link => {
        link.addEventListener('click', (event) => {
            event.preventDefault();
            clearSession();
            const inPages = window.location.pathname.includes('/pages/');
            const target = inPages ? 'auth.html' : 'pages/auth.html';
            window.location.href = target;
        });
    });

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const usernameOrEmail = loginForm.querySelector('[name="usernameOrEmail"]').value.trim();
            const password = loginForm.querySelector('[name="password"]').value;
            try {
                const data = await apiRequest('/api/auth/login', {
                    method: 'POST',
                    body: JSON.stringify({ usernameOrEmail, password })
                });
                setSession(data.token, data.user);
                window.location.href = 'profile.html';
            } catch (err) {
                showMessage(loginForm, err.message || 'Đăng nhập thất bại');
            }
        });
    }

    const adminLoginForm = document.getElementById('adminLoginForm');
    if (adminLoginForm) {
        adminLoginForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const usernameOrEmail = adminLoginForm.querySelector('[name="usernameOrEmail"]').value.trim();
            const password = adminLoginForm.querySelector('[name="password"]').value;
            try {
                const data = await apiRequest('/api/auth/login', {
                    method: 'POST',
                    body: JSON.stringify({ usernameOrEmail, password })
                });
                if (data.user?.role !== 'ADMIN') {
                    throw new Error('Tài khoản không có quyền Admin');
                }
                setSession(data.token, data.user);
                window.location.href = 'admin-users.html';
            } catch (err) {
                showMessage(adminLoginForm, err.message || 'Đăng nhập thất bại');
            }
        });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const payload = {
                fullName: registerForm.querySelector('[name="fullName"]').value.trim(),
                phone: registerForm.querySelector('[name="phone"]').value.trim(),
                email: registerForm.querySelector('[name="email"]').value.trim(),
                username: registerForm.querySelector('[name="username"]').value.trim(),
                password: registerForm.querySelector('[name="password"]').value
            };
            const confirm = registerForm.querySelector('[name="confirmPassword"]').value;
            if (payload.password !== confirm) {
                showMessage(registerForm, 'Mật khẩu xác nhận không khớp');
                return;
            }
            try {
                const data = await apiRequest('/api/auth/register', {
                    method: 'POST',
                    body: JSON.stringify(payload)
                });
                setSession(data.token, data.user);
                window.location.href = 'profile.html';
            } catch (err) {
                const rawMessage = (err.message || '').toLowerCase();
                if (rawMessage.includes('email already exists')) {
                    showMessage(registerForm, 'Email này đã được đăng ký. Hãy dùng email khác hoặc đăng nhập.');
                    return;
                }
                if (rawMessage.includes('username already exists')) {
                    showMessage(registerForm, 'Tên đăng nhập đã tồn tại. Vui lòng chọn tên khác.');
                    return;
                }
                showMessage(registerForm, err.message || 'Đăng ký thất bại');
            }
        });
    }

    const profileForm = document.getElementById('profileForm');
    const profileToggle = document.getElementById('editProfileBtn');
    const profileInfo = {
        fullName: document.getElementById('profileFullName'),
        email: document.getElementById('profileEmail'),
        phone: document.getElementById('profilePhone'),
        displayName: document.getElementById('profileDisplayName'),
        headerName: document.getElementById('headerUsername'),
        avatarImg: document.getElementById('profileAvatarImg'),
        avatarIcon: document.getElementById('profileAvatarIcon')
    };

    if (profileForm || profileInfo.fullName) {
        apiRequest('/api/users/me')
            .then(user => {
                if (profileInfo.fullName) profileInfo.fullName.textContent = user.fullName || '-';
                if (profileInfo.email) profileInfo.email.textContent = user.email || '-';
                if (profileInfo.phone) profileInfo.phone.textContent = user.phone || '-';
                const displayName = user.fullName || user.username || '';
                if (profileInfo.displayName) profileInfo.displayName.textContent = displayName;
                if (profileInfo.headerName) profileInfo.headerName.textContent = displayName;

                if (profileInfo.avatarImg && profileInfo.avatarIcon) {
                    if (user.avatar) {
                        profileInfo.avatarImg.src = user.avatar;
                        profileInfo.avatarImg.classList.add('active');
                        profileInfo.avatarIcon.style.display = 'none';
                    } else {
                        profileInfo.avatarImg.classList.remove('active');
                        profileInfo.avatarIcon.style.display = 'block';
                    }
                }

                if (profileForm) {
                    profileForm.querySelector('[name="fullName"]').value = user.fullName || '';
                    profileForm.querySelector('[name="phone"]').value = user.phone || '';
                    profileForm.querySelector('[name="avatar"]').value = user.avatar || '';
                }
            })
            .catch(() => {
                if (profileForm) showMessage(profileForm, 'Không thể tải thông tin user');
            });
    }

    if (profileToggle && profileForm) {
        profileToggle.addEventListener('click', () => {
            profileForm.classList.toggle('hidden');
        });
    }

    if (profileForm) {
        profileForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const payload = {
                fullName: profileForm.querySelector('[name="fullName"]').value.trim(),
                phone: profileForm.querySelector('[name="phone"]').value.trim(),
                avatar: profileForm.querySelector('[name="avatar"]').value.trim()
            };
            try {
                const user = await apiRequest('/api/users/me', {
                    method: 'PUT',
                    body: JSON.stringify(payload)
                });
                if (profileInfo.fullName) profileInfo.fullName.textContent = user.fullName || '-';
                if (profileInfo.phone) profileInfo.phone.textContent = user.phone || '-';
                if (profileInfo.displayName) {
                    profileInfo.displayName.textContent = user.fullName || user.username || '';
                }
                if (profileInfo.avatarImg && profileInfo.avatarIcon) {
                    if (user.avatar) {
                        profileInfo.avatarImg.src = user.avatar;
                        profileInfo.avatarImg.classList.add('active');
                        profileInfo.avatarIcon.style.display = 'none';
                    } else {
                        profileInfo.avatarImg.classList.remove('active');
                        profileInfo.avatarIcon.style.display = 'block';
                    }
                }
                showMessage(profileForm, 'Cập nhật thành công', 'success');
            } catch (err) {
                showMessage(profileForm, err.message || 'Cập nhật thất bại');
            }
        });
    }

    const adminUserForm = document.getElementById('adminUserForm');
    const adminUserReset = document.getElementById('adminUserReset');
    const adminUserTable = document.getElementById('adminUserTbody');

    const renderAdminUsers = (users) => {
        if (!adminUserTable) return;
        adminUserTable.innerHTML = '';
        users.forEach(user => {
            const row = document.createElement('tr');
            row.innerHTML = `
                <td>${user.fullName || user.username}</td>
                <td>${user.role || 'USER'}</td>
                <td>${user.phone || '-'}</td>
                <td>${user.email || '-'}</td>
                <td>${user.createdAt ? new Date(user.createdAt).toLocaleDateString('vi-VN') : '-'}</td>
                <td><span class="status-pill ${user.enabled ? 'active' : 'paused'}">${user.enabled ? 'Hoạt động' : 'Tạm dừng'}</span></td>
                <td>
                    <button class="icon-btn" data-action="edit" data-id="${user.id}"><i class="fa-regular fa-pen-to-square"></i></button>
                    <button class="icon-btn danger" data-action="delete" data-id="${user.id}"><i class="fa-regular fa-trash-can"></i></button>
                </td>
            `;
            adminUserTable.appendChild(row);
        });
    };

    const loadAdminUsers = async () => {
        const users = await apiRequest('/api/admin/users');
        renderAdminUsers(users);
    };

    if (adminUserTable) {
        loadAdminUsers().catch(() => { });
        adminUserTable.addEventListener('click', async (event) => {
            const btn = event.target.closest('button');
            if (!btn) return;
            const action = btn.dataset.action;
            const id = btn.dataset.id;
            if (!action || !id) return;

            if (action === 'delete') {
                if (!confirm('Xóa user này?')) return;
                try {
                    await apiRequest(`/api/admin/users/${id}`, { method: 'DELETE' });
                    loadAdminUsers();
                } catch (err) {
                    showMessage(adminUserForm, err.message || 'Xóa thất bại');
                }
            }

            if (action === 'edit') {
                const users = await apiRequest('/api/admin/users');
                const user = users.find(item => item.id === id);
                if (!user || !adminUserForm) return;
                adminUserForm.querySelector('[name="id"]').value = user.id;
                adminUserForm.querySelector('[name="username"]').value = user.username || '';
                adminUserForm.querySelector('[name="email"]').value = user.email || '';
                adminUserForm.querySelector('[name="password"]').value = '';
                adminUserForm.querySelector('[name="fullName"]').value = user.fullName || '';
                adminUserForm.querySelector('[name="phone"]').value = user.phone || '';
                adminUserForm.querySelector('[name="avatar"]').value = user.avatar || '';
                adminUserForm.querySelector('[name="role"]').value = user.role || 'USER';
                adminUserForm.querySelector('[name="enabled"]').checked = user.enabled;
            }
        });
    }

    if (adminUserForm) {
        adminUserForm.addEventListener('submit', async (event) => {
            event.preventDefault();
            const id = adminUserForm.querySelector('[name="id"]').value;
            const payload = {
                username: adminUserForm.querySelector('[name="username"]').value.trim(),
                email: adminUserForm.querySelector('[name="email"]').value.trim(),
                password: adminUserForm.querySelector('[name="password"]').value,
                fullName: adminUserForm.querySelector('[name="fullName"]').value.trim(),
                phone: adminUserForm.querySelector('[name="phone"]').value.trim(),
                avatar: adminUserForm.querySelector('[name="avatar"]').value.trim(),
                role: adminUserForm.querySelector('[name="role"]').value,
                enabled: adminUserForm.querySelector('[name="enabled"]').checked
            };
            try {
                if (id) {
                    if (!payload.password) delete payload.password;
                    await apiRequest(`/api/admin/users/${id}`, {
                        method: 'PUT',
                        body: JSON.stringify(payload)
                    });
                } else {
                    await apiRequest('/api/admin/users', {
                        method: 'POST',
                        body: JSON.stringify(payload)
                    });
                }
                showMessage(adminUserForm, 'Lưu thành công', 'success');
                adminUserForm.reset();
                adminUserForm.querySelector('[name="id"]').value = '';
                loadAdminUsers();
            } catch (err) {
                showMessage(adminUserForm, err.message || 'Lưu thất bại');
            }
        });
    }

    if (adminUserReset && adminUserForm) {
        adminUserReset.addEventListener('click', () => {
            adminUserForm.reset();
            adminUserForm.querySelector('[name="id"]').value = '';
        });
    }

    // ─── PHẦN: CÁC HÀM GỌI API CHO CATEGORY & PRODUCTS ───

    // Lấy danh sách tất cả danh mục
    const fetchCategories = async () => {
        try {
            return await apiRequest('/api/categories');
        } catch (err) {
            console.error("Lỗi khi tải danh mục:", err);
            return [];
        }
    };

    // Lấy danh sách sản phẩm (có thể lọc theo categoryId hoặc lấy tất cả)
    const fetchProducts = async (categoryId = null) => {
        const path = categoryId ? `/api/categories/${categoryId}/products` : '/api/products';
        try {
            return await apiRequest(path);
        } catch (err) {
            console.error("Lỗi khi tải sản phẩm:", err);
            return [];
        }
    };

    // Lấy chi tiết một sản phẩm theo ID
    const fetchProductById = async (productId) => {
        try {
            const data = await apiRequest(`/api/products/${productId}`);
            return data;
        } catch (err) {
            console.error("Lỗi khi tải chi tiết sản phẩm:", err);
            return null;
        }
    };

    // ─── PHẦN: HÀM HIỂN THỊ (RENDER) DỮ LIỆU LÊN GIAO DIỆN ───

    // Render danh sách sản phẩm ra HTML
    const renderProducts = (products, containerSelector) => {
        const containers = document.querySelectorAll(containerSelector);
        if (containers.length === 0) return;

        // Kiểm tra xem có đang ở trong bản thư mục pages/ hay không
        const isInPagesFolder = window.location.pathname.includes('/pages/');
        const detailPath = isInPagesFolder ? 'product-detail.html' : 'pages/product-detail.html';

        const html = products.length === 0 
            ? '<p class="no-data">Không có sản phẩm nào.</p>'
            : products.map(product => `
                <div class="product-card" onclick="window.location.href='${detailPath}?id=${product.id}'">
                    <div class="product-image">
                        ${product.discount ? `<div class="discount-badge">-${product.discount}%</div>` : ''}
                        <img src="${product.imageUrl || 'https://placehold.co/200x250?text=No+Image'}" alt="${product.name}">
                    </div>
                    <div class="product-info">
                        <h3 class="product-title">${product.name}</h3>
                        <p class="product-author">${product.author || ''}</p>
                        <div class="product-price">
                            <span class="current-price">${(product.price || 0).toLocaleString('vi-VN')} đ</span>
                            ${product.oldPrice ? `<span class="old-price">${product.oldPrice.toLocaleString('vi-VN')} đ</span>` : ''}
                        </div>
                    </div>
                </div>
            `).join('');

        containers.forEach(container => {
            container.innerHTML = html;
        });
    };

    // Render danh mục vào Sidebar/Menu
    const renderCategories = (categories, containerSelector) => {
        const container = document.querySelector(containerSelector);
        if (!container) return;

        container.innerHTML = categories.map(cat => `
            <li><a href="#" data-category-id="${cat.id}">${cat.name}</a></li>
        `).join('');
    };

    // ─── PHẦN: TỰ ĐỘNG GỌI KHI MỞ TRANG TƯƠNG ỨNG ───
    const pagePath = window.location.pathname;

    // Trang chủ
    if (pagePath.endsWith('index.html') || pagePath === '/' || pagePath.endsWith('/')) {
        fetchProducts().then(data => {
            const grids = document.querySelectorAll('.product-grid');
            if (grids[0]) renderProducts(data.slice(0, 4), '.product-section:nth-of-type(1) .product-grid');
            if (grids[1]) renderProducts(data.slice(4, 8), '.product-section:nth-of-type(2) .product-grid');
        });
    }

    // Trang tất cả sản phẩm
    if (pagePath.endsWith('products.html')) {
        fetchProducts().then(data => renderProducts(data, '.product-grid-3'));
        fetchCategories().then(cats => renderCategories(cats, '.category-list'));
    }

    // Trang chi tiết sản phẩm
    if (pagePath.endsWith('product-detail.html')) {
        const urlParams = new URLSearchParams(window.location.search);
        const productId = urlParams.get('id');
        const pdWishlistHeart = document.querySelector('.pd-wishlist');

        if (productId) {
            fetchProductById(productId).then(product => {
                if (product) {
                    const titleEl = document.querySelector('.pd-title');
                    const priceEl = document.querySelector('.pd-current-price');
                    const imgEl = document.getElementById('mainImage');
                    const descEl = document.querySelector('.pd-description');

                    if (titleEl) titleEl.textContent = product.name;
                    if (priceEl) priceEl.textContent = (product.price || 0).toLocaleString('vi-VN') + ' đ';
                    if (imgEl) imgEl.src = product.imageUrl || '';
                    if (descEl) descEl.textContent = product.description || '';
                }
            });

            // Check if product is in wishlist
            if (getToken()) {
                apiRequest('/api/wishlist').then(data => {
                    const products = data.products || [];
                    const isInWishlist = products.some(p => p.id === productId);
                    if (isInWishlist && pdWishlistHeart) {
                        pdWishlistHeart.classList.remove('fa-regular');
                        pdWishlistHeart.classList.add('fa-solid', 'active');
                    }
                    localStorage.setItem('favoriteCount', products.length);
                    updateBadges();
                }).catch(() => { });
            }

            // Add to Cart handler
            const btnAddCart = document.querySelector('.btn-add-cart');
            const qtyInputDetail = document.querySelector('.qty-input');
            if (btnAddCart && qtyInputDetail) {
                btnAddCart.onclick = (e) => {
                    e.preventDefault();
                    const qty = parseInt(qtyInputDetail.value) || 1;
                    window.addToCart(productId, qty);
                };
            }
        }

        if (pdWishlistHeart && productId) {
            pdWishlistHeart.addEventListener('click', async () => {
                if (!getToken()) {
                    window.location.href = 'login.html';
                    return;
                }

                const isActive = pdWishlistHeart.classList.contains('active');
                try {
                    if (isActive) {
                        await apiRequest(`/api/wishlist/${productId}`, { method: 'DELETE' });
                        pdWishlistHeart.classList.remove('fa-solid', 'active');
                        pdWishlistHeart.classList.add('fa-regular');
                    } else {
                        await apiRequest(`/api/wishlist/${productId}`, { method: 'POST' });
                        pdWishlistHeart.classList.remove('fa-regular');
                        pdWishlistHeart.classList.add('fa-solid', 'active');
                    }
                    // Cập nhật badge
                    const data = await apiRequest('/api/wishlist');
                    localStorage.setItem('favoriteCount', (data.products || []).length);
                    updateBadges();
                } catch (err) {
                    alert(err.message || 'Thao tác thất bại');
                }
            });
        }
    }
});
