package com.example.demo.service.impl;

import com.example.demo.dto.request.CheckoutRequest;
import com.example.demo.dto.request.CouponPreviewRequest;
import com.example.demo.dto.response.CheckoutResponse;
import com.example.demo.dto.response.CouponPreviewResponse;
import com.example.demo.dto.response.PaymentCallbackResult;
import com.example.demo.entity.*;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.service.CheckoutService;
import com.example.demo.service.CouponService;
import com.example.demo.util.AppUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    @Value("${payment.momo.endpoint:https://test-payment.momo.vn/v2/gateway/api/create}")
    private String momoEndpoint;
    @Value("${payment.momo.partner-code:}")
    private String momoPartnerCode;
    @Value("${payment.momo.access-key:}")
    private String momoAccessKey;
    @Value("${payment.momo.secret-key:}")
    private String momoSecretKey;
    @Value("${payment.momo.redirect-url:http://localhost:8080/api/payments/momo/return}")
    private String momoRedirectUrl;
    @Value("${payment.momo.ipn-url:http://localhost:8080/api/payments/momo/ipn}")
    private String momoIpnUrl;

    @Value("${payment.vnpay.pay-url:https://sandbox.vnpayment.vn/paymentv2/vpcpay.html}")
    private String vnpayPayUrl;
    @Value("${payment.vnpay.tmn-code:}")
    private String vnpayTmnCode;
    @Value("${payment.vnpay.hash-secret:}")
    private String vnpayHashSecret;
    @Value("${payment.vnpay.return-url:http://localhost:8080/api/payments/vnpay/return}")
    private String vnpayReturnUrl;

    private static final ZoneId VIETNAM_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter VNPAY_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CouponService couponService;

    @Override
    public CheckoutResponse checkout(String userId, CheckoutRequest request, String clientIp) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống");
        }

        List<Order.OrderItem> orderItems = buildOrderItems(cart);
        double subtotal = orderItems.stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();

        double shippingFee = request.getShippingFee() == null ? 0d : Math.max(0d, request.getShippingFee());
        Coupon coupon = couponService.validateCouponForOrder(request.getCouponCode(), subtotal);
        double discount = couponService.calculateDiscountAmount(coupon, subtotal);
        double totalAmount = Math.max(0d, subtotal + shippingFee - discount);

        PaymentMethod paymentMethod = request.getPaymentMethod() == null ? PaymentMethod.COD : request.getPaymentMethod();
        String txRef = paymentMethod == PaymentMethod.COD ? null : generateTxnRef(paymentMethod);
        LocalDateTime now = LocalDateTime.now();

        Order order = Order.builder()
            .userId(userId)
            .items(orderItems)
            .subtotalAmount(subtotal)
            .shippingFee(shippingFee)
            .shippingMethod(request.getShippingMethod())
            .totalAmount(totalAmount)
            .couponCode(coupon == null ? null : coupon.getCode())
            .discount(discount)
            .receiverName(request.getReceiverName())
            .receiverPhone(request.getReceiverPhone())
            .receiverEmail(request.getReceiverEmail())
            .shippingAddress(request.getShippingAddress())
            .paymentMethod(paymentMethod)
            .paymentProvider(paymentMethod.name())
            .paymentStatus(PaymentStatus.PENDING)
            .paymentTxnRef(txRef)
            .status(OrderStatus.PENDING)
            .createdAt(now)
            .updatedAt(now)
            .build();
        order = orderRepository.save(order);

        if (paymentMethod == PaymentMethod.COD) {
            if (!AppUtil.isNullOrEmpty(order.getCouponCode()) && order.getDiscount() != null && order.getDiscount() > 0) {
                couponService.increaseUsage(order.getCouponCode());
            }
            clearCart(userId);
            return CheckoutResponse.builder()
                .orderId(order.getId())
                .subtotalAmount(subtotal)
                .shippingFee(shippingFee)
                .discount(discount)
                .totalAmount(totalAmount)
                .couponCode(order.getCouponCode())
                .orderStatus(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .message("Đặt hàng thành công. Vui lòng thanh toán khi nhận hàng.")
                .build();
        }

        try {
            String paymentUrl = switch (paymentMethod) {
                case MOMO -> createMomoPaymentUrl(order);
                case VNPAY -> createVnpayPaymentUrl(order, clientIp);
                default -> throw new RuntimeException("Phương thức thanh toán không được hỗ trợ");
            };

            return CheckoutResponse.builder()
                .orderId(order.getId())
                .subtotalAmount(subtotal)
                .shippingFee(shippingFee)
                .discount(discount)
                .totalAmount(totalAmount)
                .couponCode(order.getCouponCode())
                .orderStatus(order.getStatus())
                .paymentMethod(order.getPaymentMethod())
                .paymentStatus(order.getPaymentStatus())
                .paymentUrl(paymentUrl)
                .message("Tạo giao dịch thành công")
                .build();
        } catch (Exception ex) {
            markFailed(order, ex.getMessage());
            throw ex;
        }
    }

    @Override
    public CouponPreviewResponse previewCoupon(String userId, CouponPreviewRequest request) {
        Cart cart = cartRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Giỏ hàng không tồn tại"));
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new RuntimeException("Giỏ hàng đang trống");
        }

        double subtotal = buildOrderItems(cart).stream()
            .mapToDouble(item -> item.getPrice() * item.getQuantity())
            .sum();

        Coupon coupon = couponService.validateCouponForOrder(request.getCouponCode(), subtotal);
        double discount = couponService.calculateDiscountAmount(coupon, subtotal);
        double shippingFee = request.getShippingFee() == null ? 0d : Math.max(0d, request.getShippingFee());
        double totalAmount = Math.max(0d, subtotal + shippingFee - discount);

        return CouponPreviewResponse.builder()
            .couponCode(coupon.getCode())
            .subtotalAmount(subtotal)
            .shippingFee(shippingFee)
            .discount(discount)
            .totalAmount(totalAmount)
            .build();
    }

    @Override
    public PaymentCallbackResult processMomoCallback(Map<String, String> payload) {
        verifyMomoSignature(payload);

        String txRef = payload.get("orderId");
        Order order = orderRepository.findByPaymentTxnRef(txRef)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng cho giao dịch MoMo"));

        String resultCode = payload.getOrDefault("resultCode", "-1");
        String message = payload.getOrDefault("message", "");
        String transactionId = payload.get("transId");

        if ("0".equals(resultCode)) {
            markPaid(order, transactionId, message);
            return PaymentCallbackResult.builder()
                .orderId(order.getId())
                .gateway("MOMO")
                .success(true)
                .message("Thanh toán MoMo thành công")
                .build();
        }

        markFailed(order, message);
        return PaymentCallbackResult.builder()
            .orderId(order.getId())
            .gateway("MOMO")
            .success(false)
            .message(AppUtil.isNullOrEmpty(message) ? "Thanh toán MoMo thất bại" : message)
            .build();
    }

    @Override
    public PaymentCallbackResult processVnpayCallback(Map<String, String> payload) {
        verifyVnpaySignature(payload);

        String txRef = payload.get("vnp_TxnRef");
        Order order = orderRepository.findByPaymentTxnRef(txRef)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng cho giao dịch VNPay"));

        String responseCode = payload.getOrDefault("vnp_ResponseCode", "");
        String transactionStatus = payload.getOrDefault("vnp_TransactionStatus", "");
        String transactionNo = payload.getOrDefault("vnp_TransactionNo", "");
        boolean success = "00".equals(responseCode) && "00".equals(transactionStatus);

        if (success) {
            markPaid(order, transactionNo, "Thanh toán VNPay thành công");
            return PaymentCallbackResult.builder()
                .orderId(order.getId())
                .gateway("VNPAY")
                .success(true)
                .message("Thanh toán VNPay thành công")
                .build();
        }

        String failMessage = "Thanh toán VNPay thất bại (mã " + responseCode + ")";
        markFailed(order, failMessage);
        return PaymentCallbackResult.builder()
            .orderId(order.getId())
            .gateway("VNPAY")
            .success(false)
            .message(failMessage)
            .build();
    }

    private List<Order.OrderItem> buildOrderItems(Cart cart) {
        return cart.getItems().stream().map(item -> {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + item.getProductId()));
            if (item.getQuantity() <= 0) {
                throw new RuntimeException("Số lượng sản phẩm không hợp lệ");
            }

            return Order.OrderItem.builder()
                .productId(product.getId())
                .productName(product.getName())
                .quantity(item.getQuantity())
                .price(product.getPrice())
                .build();
        }).collect(Collectors.toList());
    }

    private String createMomoPaymentUrl(Order order) {
        requireMomoConfig();

        String requestId = order.getPaymentTxnRef();
        String orderId = order.getPaymentTxnRef();
        String orderInfo = "Thanh toan don hang " + order.getId();
        String requestType = "captureWallet";
        String extraData = Base64.getEncoder().encodeToString(("orderId=" + order.getId()).getBytes(StandardCharsets.UTF_8));
        String amount = String.valueOf(Math.round(order.getTotalAmount()));

        String rawSignature = "accessKey=" + momoAccessKey
            + "&amount=" + amount
            + "&extraData=" + extraData
            + "&ipnUrl=" + momoIpnUrl
            + "&orderId=" + orderId
            + "&orderInfo=" + orderInfo
            + "&partnerCode=" + momoPartnerCode
            + "&redirectUrl=" + momoRedirectUrl
            + "&requestId=" + requestId
            + "&requestType=" + requestType;

        String signature = hmacSHA256(momoSecretKey, rawSignature);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("partnerCode", momoPartnerCode);
        body.put("accessKey", momoAccessKey);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", orderInfo);
        body.put("redirectUrl", momoRedirectUrl);
        body.put("ipnUrl", momoIpnUrl);
        body.put("extraData", extraData);
        body.put("requestType", requestType);
        body.put("lang", "vi");
        body.put("signature", signature);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(momoEndpoint, httpEntity, Map.class);
        Map<String, Object> responseBody = response.getBody();
        if (responseBody == null) {
            throw new RuntimeException("MoMo trả về dữ liệu rỗng");
        }

        Object resultCode = responseBody.get("resultCode");
        Object payUrl = responseBody.get("payUrl");
        if (!"0".equals(String.valueOf(resultCode)) || payUrl == null) {
            Object message = responseBody.getOrDefault("message", "Không tạo được link thanh toán MoMo");
            throw new RuntimeException(String.valueOf(message));
        }

        return String.valueOf(payUrl);
    }

    private String createVnpayPaymentUrl(Order order, String clientIp) {
        requireVnpayConfig();

        Map<String, String> params = new TreeMap<>();
        params.put("vnp_Version", "2.1.0");
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpayTmnCode);
        params.put("vnp_Amount", String.valueOf(Math.round(order.getTotalAmount() * 100)));
        params.put("vnp_CurrCode", "VND");
        params.put("vnp_TxnRef", order.getPaymentTxnRef());
        params.put("vnp_OrderInfo", "Thanh toan don hang " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", "vn");
        params.put("vnp_ReturnUrl", vnpayReturnUrl);
        params.put("vnp_IpAddr", AppUtil.isNullOrEmpty(clientIp) ? "127.0.0.1" : clientIp);

        LocalDateTime now = LocalDateTime.now(VIETNAM_ZONE);
        LocalDateTime expired = now.plusMinutes(15);
        params.put("vnp_CreateDate", now.format(VNPAY_DATE_FORMAT));
        params.put("vnp_ExpireDate", expired.format(VNPAY_DATE_FORMAT));

        String queryString = buildVnpQuery(params);
        String secureHash = hmacSHA512(vnpayHashSecret, queryString);
        return vnpayPayUrl + "?" + queryString + "&vnp_SecureHash=" + secureHash;
    }

    private void verifyMomoSignature(Map<String, String> payload) {
        if (AppUtil.isNullOrEmpty(momoSecretKey) || AppUtil.isNullOrEmpty(momoAccessKey)) {
            return;
        }

        String signature = payload.get("signature");
        if (AppUtil.isNullOrEmpty(signature)) {
            throw new RuntimeException("MoMo callback thiếu chữ ký");
        }

        String accessKey = safe(payload.get("accessKey"));
        if (AppUtil.isNullOrEmpty(accessKey)) {
            accessKey = momoAccessKey;
        }

        String partnerCode = safe(payload.get("partnerCode"));
        if (AppUtil.isNullOrEmpty(partnerCode)) {
            partnerCode = momoPartnerCode;
        }

        String rawSignature = "accessKey=" + accessKey
            + "&amount=" + safe(payload.get("amount"))
            + "&extraData=" + safe(payload.get("extraData"))
            + "&message=" + safe(payload.get("message"))
            + "&orderId=" + safe(payload.get("orderId"))
            + "&orderInfo=" + safe(payload.get("orderInfo"))
            + "&orderType=" + safe(payload.get("orderType"))
            + "&partnerCode=" + partnerCode
            + "&payType=" + safe(payload.get("payType"))
            + "&requestId=" + safe(payload.get("requestId"))
            + "&responseTime=" + safe(payload.get("responseTime"))
            + "&resultCode=" + safe(payload.get("resultCode"))
            + "&transId=" + safe(payload.get("transId"));

        String expected = hmacSHA256(momoSecretKey, rawSignature);
        if (!expected.equalsIgnoreCase(signature)) {
            throw new RuntimeException("Chữ ký MoMo không hợp lệ");
        }
    }

    private void verifyVnpaySignature(Map<String, String> payload) {
        if (AppUtil.isNullOrEmpty(vnpayHashSecret)) {
            return;
        }

        String secureHash = payload.get("vnp_SecureHash");
        if (AppUtil.isNullOrEmpty(secureHash)) {
            throw new RuntimeException("VNPay callback thiếu chữ ký");
        }

        Map<String, String> filtered = payload.entrySet().stream()
            .filter(entry -> !AppUtil.isNullOrEmpty(entry.getValue()))
            .filter(entry -> !"vnp_SecureHash".equals(entry.getKey()))
            .filter(entry -> !"vnp_SecureHashType".equals(entry.getKey()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, TreeMap::new));

        String hashData = buildVnpQuery(filtered);
        String expectedHash = hmacSHA512(vnpayHashSecret, hashData);
        if (!expectedHash.equalsIgnoreCase(secureHash)) {
            throw new RuntimeException("Chữ ký VNPay không hợp lệ");
        }
    }

    private void markPaid(Order order, String transactionId, String message) {
        boolean alreadyPaid = order.getPaymentStatus() == PaymentStatus.PAID;
        order.setPaymentStatus(PaymentStatus.PAID);
        order.setPaymentTransactionId(transactionId);
        order.setPaymentMessage(message);
        order.setPaidAt(LocalDateTime.now());
        order.setStatus(OrderStatus.PROCESSING);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);

        if (!alreadyPaid) {
            if (!AppUtil.isNullOrEmpty(order.getCouponCode()) && order.getDiscount() != null && order.getDiscount() > 0) {
                couponService.increaseUsage(order.getCouponCode());
            }
            clearCart(order.getUserId());
        }
    }

    private void markFailed(Order order, String message) {
        if (order.getPaymentStatus() == PaymentStatus.PAID) {
            return;
        }
        order.setPaymentStatus(PaymentStatus.FAILED);
        order.setPaymentMessage(message);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.save(order);
    }

    private void clearCart(String userId) {
        cartRepository.findByUserId(userId).ifPresent(cart -> {
            cart.setItems(new ArrayList<>());
            cartRepository.save(cart);
        });
    }

    private String generateTxnRef(PaymentMethod method) {
        return method.name() + System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(100, 999);
    }

    private void requireMomoConfig() {
        if (AppUtil.isNullOrEmpty(momoPartnerCode)
            || AppUtil.isNullOrEmpty(momoAccessKey)
            || AppUtil.isNullOrEmpty(momoSecretKey)) {
            throw new RuntimeException("Chưa cấu hình MoMo (partnerCode/accessKey/secretKey)");
        }
    }

    private void requireVnpayConfig() {
        if (AppUtil.isNullOrEmpty(vnpayTmnCode) || AppUtil.isNullOrEmpty(vnpayHashSecret)) {
            throw new RuntimeException("Chưa cấu hình VNPay (tmnCode/hashSecret)");
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8).replace("+", "%20");
    }

    private String buildVnpQuery(Map<String, String> params) {
        return params.entrySet().stream()
            .map(entry -> encode(entry.getKey()) + "=" + encode(entry.getValue()))
            .collect(Collectors.joining("&"));
    }

    private String hmacSHA256(String key, String data) {
        return hmac("HmacSHA256", key, data);
    }

    private String hmacSHA512(String key, String data) {
        return hmac("HmacSHA512", key, data);
    }

    private String hmac(String algorithm, String key, String data) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), algorithm);
            mac.init(secretKeySpec);
            byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Không thể tạo chữ ký " + algorithm, ex);
        }
    }
}
