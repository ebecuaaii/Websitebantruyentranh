document.addEventListener('DOMContentLoaded', () => {
    const API_BASE = 'http://localhost:8080';
    const getToken = () => localStorage.getItem('authToken');

    const state = {
        subtotal: 0,
        shippingFee: 20000,
        discount: 0,
        couponCode: null,
        hasItems: false
    };

    const elements = {
        checkoutForm: document.getElementById('checkoutForm'),
        receiverName: document.getElementById('receiverName'),
        receiverPhone: document.getElementById('receiverPhone'),
        receiverEmail: document.getElementById('receiverEmail'),
        city: document.getElementById('city'),
        district: document.getElementById('district'),
        ward: document.getElementById('ward'),
        addressDetail: document.getElementById('addressDetail'),
        orderItems: document.getElementById('orderItems'),
        subtotalAmount: document.getElementById('subtotalAmount'),
        shippingFee: document.getElementById('shippingFee'),
        discountAmount: document.getElementById('discountAmount'),
        totalAmount: document.getElementById('totalAmount'),
        couponCode: document.getElementById('couponCode'),
        applyCouponBtn: document.getElementById('applyCouponBtn'),
        couponMessage: document.getElementById('couponMessage'),
        placeOrderBtn: document.getElementById('placeOrderBtn'),
        checkoutMessage: document.getElementById('checkoutMessage')
    };

    const locationMap = {
        hcm: {
            districts: ['Quận 1', 'Quận 7', 'TP. Thủ Đức'],
            wards: ['Phường Bến Nghé', 'Phường Tân Phú', 'Phường Linh Trung']
        },
        hanoi: {
            districts: ['Ba Đình', 'Đống Đa', 'Cầu Giấy'],
            wards: ['Phường Kim Mã', 'Phường Láng Hạ', 'Phường Dịch Vọng']
        },
        danang: {
            districts: ['Hải Châu', 'Sơn Trà', 'Liên Chiểu'],
            wards: ['Phường Thạch Thang', 'Phường An Hải Bắc', 'Phường Hòa Khánh Bắc']
        }
    };

    const formatMoney = (value) => `${Number(value || 0).toLocaleString('vi-VN')} đ`;

    const showCheckoutMessage = (message, type = 'error') => {
        if (!elements.checkoutMessage) return;
        elements.checkoutMessage.textContent = message || '';
        elements.checkoutMessage.classList.remove('hidden', 'success', 'error');
        elements.checkoutMessage.classList.add(type === 'success' ? 'success' : 'error');
    };

    const clearCheckoutMessage = () => {
        if (!elements.checkoutMessage) return;
        elements.checkoutMessage.classList.add('hidden');
        elements.checkoutMessage.textContent = '';
        elements.checkoutMessage.classList.remove('success', 'error');
    };

    const showCouponMessage = (message, type = '') => {
        if (!elements.couponMessage) return;
        elements.couponMessage.textContent = message || '';
        elements.couponMessage.classList.remove('success', 'error');
        if (type) elements.couponMessage.classList.add(type);
    };

    const apiRequest = async (path, options = {}) => {
        const token = getToken();
        const headers = {
            'Content-Type': 'application/json',
            ...(options.headers || {})
        };
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
        } catch (error) {
            payload = null;
        }

        if (!response.ok || (payload && payload.status >= 400)) {
            if (response.status === 401 || response.status === 403) {
                localStorage.removeItem('authToken');
                localStorage.removeItem('authUser');
                window.location.href = 'login.html';
                return null;
            }
            throw new Error(payload?.message || 'Yeu cau that bai');
        }

        return payload?.data ?? payload;
    };

    const getSelectedShippingFee = () => {
        const selected = document.querySelector('input[name="shipping"]:checked');
        return Number(selected?.dataset?.fee || 0);
    };

    const renderTotals = () => {
        const total = Math.max(0, state.subtotal + state.shippingFee - state.discount);
        if (elements.subtotalAmount) elements.subtotalAmount.textContent = formatMoney(state.subtotal);
        if (elements.shippingFee) elements.shippingFee.textContent = formatMoney(state.shippingFee);
        if (elements.discountAmount) elements.discountAmount.textContent = `-${formatMoney(state.discount)}`;
        if (elements.totalAmount) elements.totalAmount.textContent = formatMoney(total);
    };

    const renderOrderItems = (items) => {
        if (!elements.orderItems) return;
        if (!items || items.length === 0) {
            elements.orderItems.innerHTML = '<p class="loading-line">Gio hang dang trong.</p>';
            state.hasItems = false;
            if (elements.placeOrderBtn) elements.placeOrderBtn.disabled = true;
            return;
        }

        state.hasItems = true;
        if (elements.placeOrderBtn) elements.placeOrderBtn.disabled = false;
        elements.orderItems.innerHTML = items.map(item => `
            <div class="item-row">
                <span>${item.productName} x${item.quantity}</span>
                <span>${formatMoney(item.subtotal)}</span>
            </div>
        `).join('');
    };

    const loadCart = async () => {
        const cart = await apiRequest('/api/cart');
        const items = cart?.items || [];
        state.subtotal = Number(cart?.totalAmount || 0);
        state.shippingFee = getSelectedShippingFee();
        state.discount = 0;
        state.couponCode = null;
        if (elements.couponCode) elements.couponCode.value = '';
        showCouponMessage('');
        renderOrderItems(items);
        renderTotals();
    };

    const loadCurrentUser = async () => {
        try {
            const user = await apiRequest('/api/users/me');
            if (!user) return;
            if (elements.receiverName && !elements.receiverName.value) {
                elements.receiverName.value = user.fullName || '';
            }
            if (elements.receiverPhone && !elements.receiverPhone.value) {
                elements.receiverPhone.value = user.phone || '';
            }
            if (elements.receiverEmail && !elements.receiverEmail.value) {
                elements.receiverEmail.value = user.email || '';
            }
        } catch (error) {
            // Skip profile preload if request fails.
        }
    };

    const populateLocationSelect = () => {
        if (!elements.city || !elements.district || !elements.ward) return;
        const cityKey = elements.city.value;
        const config = locationMap[cityKey];

        const districtOptions = config?.districts || [];
        elements.district.innerHTML = '<option value="">Chon quan/huyen</option>' +
            districtOptions.map(item => `<option value="${item}">${item}</option>`).join('');

        const wardOptions = config?.wards || [];
        elements.ward.innerHTML = '<option value="">Chon xa/phuong</option>' +
            wardOptions.map(item => `<option value="${item}">${item}</option>`).join('');
    };

    const applyCoupon = async () => {
        clearCheckoutMessage();
        const code = elements.couponCode?.value?.trim();
        if (!code) {
            showCouponMessage('Vui long nhap ma giam gia', 'error');
            return;
        }

        const preview = await apiRequest('/api/checkout/preview-coupon', {
            method: 'POST',
            body: JSON.stringify({
                couponCode: code,
                shippingFee: state.shippingFee
            })
        });

        state.couponCode = preview.couponCode;
        state.discount = Number(preview.discount || 0);
        showCouponMessage(`Ap dung ma ${preview.couponCode} thanh cong`, 'success');
        renderTotals();
    };

    const selectedPaymentMethod = () => {
        const selected = document.querySelector('input[name="payment"]:checked');
        return selected?.value || 'COD';
    };

    const selectedShippingMethod = () => {
        const selected = document.querySelector('input[name="shipping"]:checked');
        return selected?.value || 'normal';
    };

    const buildShippingAddress = () => {
        const detail = elements.addressDetail?.value?.trim();
        const ward = elements.ward?.value?.trim();
        const district = elements.district?.value?.trim();
        const cityLabel = elements.city?.options?.[elements.city.selectedIndex]?.text?.trim();
        return [detail, ward, district, cityLabel].filter(Boolean).join(', ');
    };

    const placeOrder = async () => {
        clearCheckoutMessage();
        if (!state.hasItems) {
            showCheckoutMessage('Gio hang dang trong, khong the dat hang.');
            return;
        }

        if (!elements.checkoutForm?.checkValidity()) {
            elements.checkoutForm.reportValidity();
            return;
        }

        const payload = {
            receiverName: elements.receiverName.value.trim(),
            receiverPhone: elements.receiverPhone.value.trim(),
            receiverEmail: elements.receiverEmail.value.trim(),
            shippingAddress: buildShippingAddress(),
            shippingMethod: selectedShippingMethod(),
            shippingFee: state.shippingFee,
            couponCode: state.couponCode || elements.couponCode.value.trim() || null,
            paymentMethod: selectedPaymentMethod()
        };

        elements.placeOrderBtn.disabled = true;
        elements.placeOrderBtn.textContent = 'Dang xu ly...';
        const result = await apiRequest('/api/checkout', {
            method: 'POST',
            body: JSON.stringify(payload)
        });

        if (result.paymentUrl) {
            showCheckoutMessage('Dang chuyen ban den cong thanh toan...', 'success');
            window.location.href = result.paymentUrl;
            return;
        }

        showCheckoutMessage('Dat hang thanh cong! Dang chuyen den don hang cua ban...', 'success');
        setTimeout(() => {
            window.location.href = 'my-orders.html';
        }, 1000);
    };

    const handlePaymentReturnMessage = () => {
        const params = new URLSearchParams(window.location.search);
        const paymentStatus = params.get('paymentStatus');
        const message = params.get('message');
        const orderId = params.get('orderId');
        const gateway = params.get('gateway');
        if (!paymentStatus) return;

        if (paymentStatus === 'success') {
            const title = gateway ? `Thanh toan ${gateway} thanh cong.` : 'Thanh toan thanh cong.';
            const suffix = orderId ? ` Ma don: ${orderId}.` : '';
            showCheckoutMessage(`${title}${suffix}`, 'success');
        } else {
            const fallback = gateway ? `Thanh toan ${gateway} that bai.` : 'Thanh toan that bai.';
            showCheckoutMessage(message || fallback, 'error');
        }
    };

    const registerEvents = () => {
        if (elements.city) {
            elements.city.addEventListener('change', populateLocationSelect);
        }

        document.querySelectorAll('input[name="shipping"]').forEach(input => {
            input.addEventListener('change', () => {
                state.shippingFee = getSelectedShippingFee();
                renderTotals();
            });
        });

        if (elements.applyCouponBtn) {
            elements.applyCouponBtn.addEventListener('click', async () => {
                try {
                    await applyCoupon();
                } catch (error) {
                    state.discount = 0;
                    state.couponCode = null;
                    renderTotals();
                    showCouponMessage(error.message || 'Ap dung coupon that bai', 'error');
                }
            });
        }

        if (elements.placeOrderBtn) {
            elements.placeOrderBtn.addEventListener('click', async () => {
                try {
                    await placeOrder();
                } catch (error) {
                    showCheckoutMessage(error.message || 'Dat hang that bai', 'error');
                } finally {
                    elements.placeOrderBtn.disabled = false;
                    elements.placeOrderBtn.textContent = 'DAT HANG';
                }
            });
        }
    };

    const init = async () => {
        if (!getToken()) {
            window.location.href = 'login.html';
            return;
        }

        registerEvents();
        populateLocationSelect();
        handlePaymentReturnMessage();

        try {
            await Promise.all([loadCurrentUser(), loadCart()]);
        } catch (error) {
            showCheckoutMessage(error.message || 'Khong the tai du lieu checkout');
        }
    };

    init();
});
