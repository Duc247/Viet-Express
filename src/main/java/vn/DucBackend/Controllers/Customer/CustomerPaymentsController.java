package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.Payment;
import vn.DucBackend.Entities.PaymentTransaction;
import vn.DucBackend.Repositories.PaymentTransactionRepository;
import vn.DucBackend.Services.CustomerRequestService;
import vn.DucBackend.Services.PaymentService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller xử lý chi tiết thanh toán cho Customer
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/customer")
public class CustomerPaymentsController {

    @Autowired
    private CustomerRequestService customerRequestService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getCustomerIdFromSession(HttpSession session) {
        Object customerId = session.getAttribute("customerId");
        if (customerId != null) {
            return (Long) customerId;
        }
        return null;
    }

    /**
     * Hiển thị trang tổng hợp thanh toán của customer
     * Lấy tất cả payments từ các requests mà customer là sender hoặc receiver
     */
    @GetMapping("/payments")
    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public String paymentsList(
            @RequestParam(value = "search", required = false) String search,
            Model model,
            HttpServletRequest request,
            HttpSession session) {

        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);

        // Phải đăng nhập để xem
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // Lấy tất cả payments từ customer (sender hoặc receiver)
        List<Payment> allPayments = paymentService.findPaymentsByCustomerIdEntities(customerId);

        // Pre-fetch relationships để tránh lazy loading exception
        allPayments.forEach(p -> {
            if (p.getRequest() != null) {
                p.getRequest().getRequestCode();
                if (p.getRequest().getSender() != null)
                    p.getRequest().getSender().getName();
                if (p.getRequest().getReceiver() != null)
                    p.getRequest().getReceiver().getName();
            }
        });

        // Filter by search keyword if provided
        if (search != null && !search.trim().isEmpty()) {
            String keyword = search.trim().toLowerCase();
            allPayments = allPayments.stream()
                    .filter(p -> (p.getPaymentCode() != null && p.getPaymentCode().toLowerCase().contains(keyword)) ||
                            (p.getRequest() != null && p.getRequest().getRequestCode() != null &&
                                    p.getRequest().getRequestCode().toLowerCase().contains(keyword))
                            ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(keyword)))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("search", search);
        }

        model.addAttribute("payments", allPayments);

        // Calculate summary statistics
        BigDecimal totalPaid = allPayments.stream()
                .filter(p -> p.getPaidAmount() != null)
                .map(Payment::getPaidAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnpaid = allPayments.stream()
                .filter(p -> p.getStatus() == Payment.PaymentStatus.UNPAID ||
                        p.getStatus() == Payment.PaymentStatus.PARTIALLY_PAID)
                .filter(p -> p.getExpectedAmount() != null)
                .map(p -> p.getExpectedAmount()
                        .subtract(p.getPaidAmount() != null ? p.getPaidAmount() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCod = allPayments.stream()
                .filter(p -> p.getPaymentType() == Payment.PaymentType.COD)
                .filter(p -> p.getStatus() != Payment.PaymentStatus.COLLECTED_FROM_RECEIVER &&
                        p.getStatus() != Payment.PaymentStatus.PAID_TO_SENDER)
                .filter(p -> p.getExpectedAmount() != null)
                .map(p -> p.getExpectedAmount()
                        .subtract(p.getPaidAmount() != null ? p.getPaidAmount() : BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("totalPaid", totalPaid);
        model.addAttribute("totalUnpaid", totalUnpaid);
        model.addAttribute("totalCod", totalCod);

        return "customer/payments";
    }

    @GetMapping("/orders/{id}/payments")
    public String paymentsDetail(
            @PathVariable("id") Long id,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "scope", required = false) String scope,
            Model model,
            HttpServletRequest request,
            HttpSession session) {

        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);

        // Phải đăng nhập để xem chi tiết
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        CustomerRequest order = customerRequestService.getRequestEntityById(id);

        if (order == null) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        // Kiểm tra quyền xem - phải là sender hoặc receiver
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

        if (!isSender && !isReceiver) {
            model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/customer/orders";
        }

        model.addAttribute("order", order);

        // Get payments based on filters
        List<Payment> payments;

        if (search != null && !search.trim().isEmpty()) {
            // Search by keyword (code, description)
            payments = paymentService.searchByRequestIdAndKeyword(id, search.trim());
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                payments = paymentService.findByRequestIdAndStatusEntities(id, status);
            } catch (IllegalArgumentException e) {
                payments = paymentService.findPaymentsByRequestIdEntities(id);
            }
            model.addAttribute("status", status);
        } else if (type != null && !type.isEmpty()) {
            // Filter by payment type
            try {
                payments = paymentService.findByRequestIdAndTypeEntities(id, type);
            } catch (IllegalArgumentException e) {
                payments = paymentService.findPaymentsByRequestIdEntities(id);
            }
            model.addAttribute("type", type);
        } else if (scope != null && !scope.isEmpty()) {
            // Filter by payment scope
            try {
                payments = paymentService.findByRequestIdAndScopeEntities(id, scope);
            } catch (IllegalArgumentException e) {
                payments = paymentService.findPaymentsByRequestIdEntities(id);
            }
            model.addAttribute("scope", scope);
        } else {
            // Get all payments
            payments = paymentService.findPaymentsByRequestIdEntities(id);
        }

        model.addAttribute("payments", payments);

        // Summary statistics
        Long totalPayments = paymentService.countByRequestId(id);
        Long paidPayments = paymentService.countPaidByRequestId(id);
        Long unpaidPayments = paymentService.countUnpaidByRequestId(id);
        Long partiallyPaidPayments = paymentService.countPartiallyPaidByRequestId(id);
        Long shippingFeeCount = paymentService.countShippingFeeByRequestId(id);
        Long codCount = paymentService.countCodByRequestId(id);

        model.addAttribute("totalPayments", totalPayments != null ? totalPayments : 0L);
        model.addAttribute("paidPayments", paidPayments != null ? paidPayments : 0L);
        model.addAttribute("unpaidPayments", unpaidPayments != null ? unpaidPayments : 0L);
        model.addAttribute("partiallyPaidPayments", partiallyPaidPayments != null ? partiallyPaidPayments : 0L);
        model.addAttribute("shippingFeeCount", shippingFeeCount != null ? shippingFeeCount : 0L);
        model.addAttribute("codCount", codCount != null ? codCount : 0L);

        // Amount statistics
        BigDecimal totalExpected = paymentService.sumExpectedAmountByRequestId(id);
        BigDecimal totalPaid = paymentService.sumPaidAmountByRequestId(id);
        BigDecimal remaining = (totalExpected != null ? totalExpected : BigDecimal.ZERO)
                .subtract(totalPaid != null ? totalPaid : BigDecimal.ZERO);

        model.addAttribute("totalExpected", totalExpected != null ? totalExpected : BigDecimal.ZERO);
        model.addAttribute("totalPaid", totalPaid != null ? totalPaid : BigDecimal.ZERO);
        model.addAttribute("remainingAmount", remaining);

        // Payment statuses and types for filter dropdowns
        model.addAttribute("paymentStatuses", Payment.PaymentStatus.values());
        model.addAttribute("paymentTypes", Payment.PaymentType.values());

        return "customer/order/payments-detail";
    }

    /**
     * API lấy lịch sử giao dịch của một khoản thanh toán
     */
    @GetMapping("/payments/{paymentId}/transactions")
    @ResponseBody
    public ResponseEntity<?> getPaymentTransactions(
            @PathVariable Long paymentId,
            @RequestParam(value = "search", required = false) String search,
            HttpSession session) {

        Long customerId = getCustomerIdFromSession(session);

        // Phải đăng nhập để xem
        if (customerId == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập");
        }

        Payment payment = paymentService.getPaymentEntityById(paymentId);

        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        CustomerRequest order = payment.getRequest();

        // Kiểm tra quyền - phải là sender hoặc receiver
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

        if (!isSender && !isReceiver) {
            return ResponseEntity.status(403).body("Bạn không có quyền xem thông tin này");
        }

        List<PaymentTransaction> transactions;
        if (search != null && !search.trim().isEmpty()) {
            transactions = paymentTransactionRepository.findByPaymentIdAndKeyword(paymentId, search.trim());
        } else {
            transactions = paymentTransactionRepository.findByPaymentIdOrderByTransactionAtDesc(paymentId);
        }

        // Chuyển sang Map để tránh đơn tuần hoàn và lazy loading khi chuyển JSON
        List<Map<String, Object>> result = transactions.stream().map(pt -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", pt.getId());
            map.put("amount", pt.getAmount());
            map.put("type", pt.getTransactionType().name());
            map.put("method", pt.getPaymentMethod() != null ? pt.getPaymentMethod().name() : "N/A");
            map.put("ref", pt.getTransactionRef());
            map.put("status", pt.getStatus().name());
            map.put("time", pt.getTransactionAt() != null ? pt.getTransactionAt().toString() : "");

            // Thêm các fields cho STATUS_CHANGE
            if (pt.getTransactionType() == PaymentTransaction.TransactionType.STATUS_CHANGE) {
                map.put("oldStatus", pt.getOldPaymentStatus() != null ? pt.getOldPaymentStatus().name() : null);
                map.put("newStatus", pt.getNewPaymentStatus() != null ? pt.getNewPaymentStatus().name() : null);
                map.put("actorType", pt.getActorType());
                map.put("note", pt.getGatewayResponse()); // note được lưu trong gatewayResponse
            }
            map.put("performedBy", pt.getPerformedBy() != null ? pt.getPerformedBy().getUsername() : null);

            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }

    /**
     * API endpoint để mô phỏng thanh toán thành công qua VNPay
     * Cập nhật trạng thái payment sang PAID
     */
    @PostMapping("/api/payments/{paymentId}/simulate-pay")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> simulatePayment(
            @PathVariable("paymentId") Long paymentId,
            HttpSession session) {

        Map<String, Object> response = new HashMap<>();

        try {
            Payment payment = paymentService.getPaymentEntityById(paymentId);

            if (payment == null) {
                response.put("success", false);
                response.put("message", "Không tìm thấy khoản thanh toán!");
                return ResponseEntity.badRequest().body(response);
            }

            // Kiểm tra nếu đã thanh toán rồi
            if (payment.getStatus() == Payment.PaymentStatus.PAID) {
                response.put("success", false);
                response.put("message", "Khoản thanh toán này đã được thanh toán!");
                return ResponseEntity.badRequest().body(response);
            }

            // Cập nhật paidAmount
            payment.setPaidAmount(payment.getExpectedAmount());
            paymentService.savePaymentEntity(payment);

            // Gọi changePaymentStatus để ghi lịch sử (actor = SYSTEM vì là thanh toán
            // online)
            paymentService.changePaymentStatus(paymentId, Payment.PaymentStatus.PAID,
                    null, "SYSTEM", "Thanh toán qua VNPay");

            response.put("success", true);
            response.put("message", "Thanh toán thành công!");
            response.put("paymentCode", payment.getPaymentCode());
            response.put("amount", payment.getPaidAmount());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Lỗi xử lý thanh toán: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }

    /**
     * API endpoint để lấy lịch sử thay đổi status của một payment
     */
    @GetMapping("/payments/{paymentId}/status-history")
    @ResponseBody
    public ResponseEntity<?> getPaymentStatusHistory(
            @PathVariable Long paymentId,
            @RequestParam(value = "sort", defaultValue = "desc") String sort,
            HttpSession session) {

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return ResponseEntity.status(401).body("Vui lòng đăng nhập");
        }

        Payment payment = paymentService.getPaymentEntityById(paymentId);
        if (payment == null) {
            return ResponseEntity.notFound().build();
        }

        // Kiểm tra quyền - phải là sender hoặc receiver
        CustomerRequest order = payment.getRequest();
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

        if (!isSender && !isReceiver) {
            return ResponseEntity.status(403).body("Bạn không có quyền xem thông tin này");
        }

        // Lấy lịch sử
        java.util.List<PaymentTransaction> history = "asc".equalsIgnoreCase(sort)
                ? paymentService.getStatusHistoryAsc(paymentId)
                : paymentService.getStatusHistory(paymentId);

        // Chuyển sang Map để tránh lazy loading khi JSON
        java.util.List<Map<String, Object>> result = history.stream().map(txn -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", txn.getId());
            map.put("oldStatus", txn.getOldPaymentStatus() != null ? txn.getOldPaymentStatus().name() : null);
            map.put("newStatus", txn.getNewPaymentStatus() != null ? txn.getNewPaymentStatus().name() : null);
            map.put("actorType", txn.getActorType());
            map.put("performedBy", txn.getPerformedBy() != null ? txn.getPerformedBy().getUsername() : null);
            map.put("note", txn.getGatewayResponse()); // note được lưu trong gatewayResponse
            map.put("time", txn.getTransactionAt() != null ? txn.getTransactionAt().toString() : "");
            return map;
        }).toList();

        return ResponseEntity.ok(result);
    }
}
