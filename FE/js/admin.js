// FE/js/admin.js

const API_BASE = 'http://localhost:8080';

// Helper lấy Token
const getToken = () => localStorage.getItem('authToken');

// Hàm gọi API chung cho Admin với xử lý Error chuẩn hóa
const adminRequest = async (path, options = {}) => {
    const headers = {
        'Content-Type': 'application/json',
        ...(options.headers || {})
    };
    const token = getToken();
    if (token) headers.Authorization = `Bearer ${token}`;

    const response = await fetch(`${API_BASE}${path}`, { ...options, headers });
    
    let payload = null;
    try {
        payload = await response.json();
    } catch (err) {
        payload = null;
    }

    if (!response.ok || (payload && payload.status >= 400)) {
        throw new Error(payload?.message || 'Yêu cầu thất bại từ server');
    }
    return payload?.data || payload;
};

document.addEventListener('DOMContentLoaded', () => {
    console.log("Admin Dashboard Logic Initialized!");

    const currentPath = window.location.pathname;

    // ─── LOGIC SIDEBAR ACTIVE (Giữ nguyên) ───
    document.querySelectorAll('.admin-sidebar .nav-item').forEach(item => {
        const href = item.getAttribute('href');
        if (href && currentPath.includes(href)) {
            item.classList.add('active');
        } else {
            item.classList.remove('active');
        }
    });

    // ─── XỬ LÝ ĐĂNG XUẤT ───
    const logoutBtn = document.querySelector('.logout');
    if (logoutBtn) {
        logoutBtn.addEventListener('click', (e) => {
            e.preventDefault();
            if (confirm("Xác nhận đăng xuất khỏi hệ thống quản trị?")) {
                localStorage.removeItem('authToken');
                localStorage.removeItem('authUser');
                window.location.href = 'admin-login.html';
            }
        });
    }

    // ─── PHỤ TRỢ: LOAD DANH MỤC CHO SELECT ───
    const loadCategorySelect = async () => {
        const selects = document.querySelectorAll('#categorySelect, [name="categoryId"]');
        if (selects.length === 0) return;
        try {
            const categories = await adminRequest('/api/categories');
            selects.forEach(select => {
                const currentVal = select.getAttribute('data-value');
                select.innerHTML = '<option value="">-- Chọn danh mục --</option>' + 
                    categories.map(c => `
                        <option value="${c.id}" ${currentVal === c.id ? 'selected' : ''}>${c.name}</option>
                    `).join('');
            });
        } catch (err) { console.error("Không nạp được danh mục:", err); }
    };

    // ─── PHỤ TRỢ: XỬ LÝ XEM TRƯỚC VÀ CHUYỂN ĐỔI ẢNH FILE ───
    const setupImageUpload = () => {
        const fileInputs = document.querySelectorAll('input[type="file"].image-picker');
        fileInputs.forEach(input => {
            input.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (!file) return;

                const reader = new FileReader();
                reader.onload = function(event) {
                    const base64String = event.target.result;
                    const container = input.closest('.form-group') || input.closest('.image-upload');
                    
                    // Linh hoạt tìm cả text input hoặc hidden input có tên imageUrl/thumbnailUrl
                    const urlInput = container.querySelector('input[type="text"], input[type="hidden"]');
                    const preview = container.querySelector('.preview-image');

                    if (urlInput) urlInput.value = base64String;
                    if (preview) preview.src = base64String;
                };
                reader.readAsDataURL(file);
            });
        });
    };

    // ─── PHỤ TRỢ: XỬ LÝ CHỌN NHIỀU ẢNH (GALLERY PREVIEW) ───
    const setupMultipleImageUpload = () => {
        const multiPickers = document.querySelectorAll('.multiple-picker');
        multiPickers.forEach(picker => {
            picker.addEventListener('change', function(e) {
                const gallery = picker.closest('.form-group').querySelector('.image-gallery-grid');
                if (!gallery) return;
                
                Array.from(e.target.files).forEach(file => {
                    const reader = new FileReader();
                    reader.onload = (event) => {
                        const div = document.createElement('div');
                        div.className = 'gallery-item';
                        div.style = 'position: relative; width: 100px; height: 100px;';
                        div.innerHTML = `
                            <img src="${event.target.result}" style="width: 100%; height: 100%; object-fit: cover; border-radius: 4px; border: 1px solid #ddd;" />
                            <span style="position: absolute; top: -8px; right: -8px; background: #9f2425; color: white; border-radius: 50%; width: 22px; height: 22px; cursor: pointer; display: flex; align-items: center; justify-content: center; font-size: 12px; pointer-events: auto;" onclick="this.parentElement.remove()">×</span>
                        `;
                        gallery.appendChild(div);
                    };
                    reader.readAsDataURL(file);
                });
            });
        });
    };

    if (currentPath.includes('product')) loadCategorySelect();
    setupImageUpload();
    setupMultipleImageUpload();

    // ─── QUẢN LÝ SẢN PHẨM (admin-products.html) ───
    if (currentPath.includes('admin-products.html')) {
        const loadProducts = async () => {
            try {
                const products = await adminRequest('/api/products');
                const tbody = document.querySelector('.admin-table tbody');
                if (!tbody) return;

                tbody.innerHTML = products.map(p => `
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>${p.name}</td>
                        <td>${p.categoryId || 'N/A'}</td>
                        <td>${(p.price || 0).toLocaleString('vi-VN')} đ</td>
                        <td>${p.quantity || 0}</td>
                        <td><span class="status-badge ${p.status === 'ACTIVE' ? 'active' : 'inactive'}">${p.status || ''}</span></td>
                        <td>
                            <div class="action-buttons">
                                <a href="admin-product-edit.html?id=${p.id}" class="btn-icon" title="Sửa"><i class="fas fa-edit"></i></a>
                                <button class="btn-icon danger btn-del-p" data-id="${p.id}" title="Xóa"><i class="fas fa-trash"></i></button>
                            </div>
                        </td>
                    </tr>
                `).join('');

                document.querySelectorAll('.btn-del-p').forEach(btn => {
                    btn.onclick = async () => {
                        if (confirm(`Xóa sản phẩm ID: ${btn.dataset.id}?`)) {
                            try {
                                await adminRequest(`/api/admin/products/${btn.dataset.id}`, { method: 'DELETE' });
                                alert('Đã xóa!');
                                loadProducts();
                            } catch (err) { alert(err.message); }
                        }
                    };
                });
            } catch (err) { console.error("Lỗi danh sách SP:", err); }
        };
        loadProducts();
    }

    // ─── QUẢN LÝ DANH MỤC (admin-categories.html) ───
    if (currentPath.includes('admin-categories.html')) {
        const loadCategories = async () => {
            try {
                const categories = await adminRequest('/api/categories');
                const tbody = document.querySelector('.admin-table tbody');
                if (!tbody) return;

                tbody.innerHTML = categories.map(c => `
                    <tr>
                        <td><input type="checkbox" /></td>
                        <td>${c.name}</td>
                        <td>${c.description || ''}</td>
                        <td>--</td>
                        <td><span class="status-badge active">Hiển thị</span></td>
                        <td>
                            <div class="action-buttons">
                                <a href="admin-category-edit.html?id=${c.id}" class="btn-icon" title="Sửa"><i class="fas fa-edit"></i></a>
                                <button class="btn-icon danger btn-del-c" data-id="${c.id}" title="Xóa"><i class="fas fa-trash"></i></button>
                            </div>
                        </td>
                    </tr>
                `).join('');

                document.querySelectorAll('.btn-del-c').forEach(btn => {
                    btn.onclick = async () => {
                        if (confirm(`Xóa danh mục ID: ${btn.dataset.id}?`)) {
                            try {
                                await adminRequest(`/api/admin/categories/${btn.dataset.id}`, { method: 'DELETE' });
                                alert('Đã xóa!');
                                loadCategories();
                            } catch (err) { alert(err.message); }
                        }
                    };
                });
            } catch (err) { console.error("Lỗi danh mục:", err); }
        };
        loadCategories();
    }

    // ─── FORM XỬ LÝ (ADD/EDIT) ───
    const adminForm = document.querySelector('.admin-form');
    if (adminForm) {
        const urlParams = new URLSearchParams(window.location.search);
        const editId = urlParams.get('id');
        const isProduct = currentPath.includes('product');

        // Điền dữ liệu cho Form Edit
        if (editId) {
            const path = isProduct ? `/api/products/${editId}` : `/api/categories/${editId}`;
            adminRequest(path).then(data => {
                if (!data) return;
                Object.keys(data).forEach(key => {
                    const input = adminForm.querySelector(`[name="${key}"]`);
                    if (input) {
                        if (input.type === 'checkbox') input.checked = data[key];
                        else input.value = data[key];
                        
                        // Chạy preview cho ảnh nếu là URL field
                        const preview = input.closest('.form-group')?.querySelector('.preview-image');
                        if (preview && (key.toLowerCase().includes('url') || key === 'imageUrl' || key === 'thumbnailUrl')) {
                            preview.src = data[key];
                        }
                    }
                });
            }).catch(err => console.error("Nạp dữ liệu cũ lỗi:", err));
        }

        adminForm.addEventListener('submit', async (e) => {
            e.preventDefault();
            const formData = new FormData(adminForm);
            const payload = Object.fromEntries(formData.entries());

            // Chuyển kiểu số cho các trường Backend mong đợi kiểu double/int
            if (isProduct) {
                payload.price = parseFloat(payload.price || 0);
                payload.quantity = parseInt(payload.quantity || 0);
            }

            const baseUrl = isProduct ? '/api/admin/products' : '/api/admin/categories';
            const url = editId ? `${baseUrl}/${editId}` : baseUrl;
            const method = editId ? 'PUT' : 'POST';

            try {
                await adminRequest(url, {
                    method: method,
                    body: JSON.stringify(payload)
                });
                alert('Dữ liệu đã được lưu thành công!');
                window.location.href = isProduct ? 'admin-products.html' : 'admin-categories.html';
            } catch (err) {
                alert('Lỗi khi lưu: ' + err.message);
            }
        });
    }
});
