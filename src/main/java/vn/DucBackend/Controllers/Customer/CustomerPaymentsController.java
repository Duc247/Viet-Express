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
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.PaymentRepository;
import vn.DucBackend.Repositories.PaymentTransactionRepository;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Controller xử lý chi tiết thanh toán cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerPaymentsController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private PaymentRepository paymentRepository;

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
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);

        if (orderOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

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
            payments = paymentRepository.searchByRequestIdAndKeyword(id, search.trim());
            model.addAttribute("search", search);
        } else if (status != null && !status.isEmpty()) {
            // Filter by status
            try {
                Payment.PaymentStatus paymentStatus = Payment.PaymentStatus.valueOf(status);
                payments = paymentRepository.findByRequestIdAndStatus(id, paymentStatus);
            } catch (IllegalArgumentException e) {
                payments = paymentRepository.findByRequestId(id);
            }
            model.addAttribute("status", status);
        } else if (type != null && !type.isEmpty()) {
            // Filter by payment type
            try {
                Payment.PaymentType paymentType = Payment.PaymentType.valueOf(type);
                payments = paymentRepository.findByRequestIdAndPaymentType(id, paymentType);
            } catch (IllegalArgumentException e) {
                payments = paymentRepository.findByRequestId(id);
            }
            model.addAttribute("type", type);
        } else if (scope != null && !scope.isEmpty()) {
            // Filter by payment scope
            try {
                Payment.PaymentScope paymentScope = Payment.PaymentScope.valueOf(scope);
                payments = paymentRepository.findByRequestIdAndScope(id, paymentScope);
            } catch (IllegalArgumentException e) {
                payments = paymentRepository.findByRequestId(id);
            }
            model.addAttribute("scope", scope);
        } else {
            // Get all payments
            payments = paymentRepository.findByRequestId(id);
        }

        model.addAttribute("payments", payments);

        // Summary statistics
        Long totalPayments = paymentRepository.countByRequestId(id);
        Long paidPayments = paymentRepository.countPaidByRequestId(id);
        Long unpaidPayments = paymentRepository.countUnpaidByRequestId(id);
        Long partiallyPaidPayments = paymentRepository.countPartiallyPaidByRequestId(id);
        Long shippingFeeCount = paymentRepository.countShippingFeeByRequestId(id);
        Long codCount = paymentRepository.countCodByRequestId(id);

        model.addAttribute("totalPayments", totalPayments != null ? totalPayments : 0L);
        model.addAttribute("paidPayments", paidPayments != null ? paidPayments : 0L);
        model.addAttribute("unpaidPayments", unpaidPayments != null ? unpaidPayments : 0L);
        model.addAttribute("partiallyPaidPayments", partiallyPaidPayments != null ? partiallyPaidPayments : 0L);
        model.addAttribute("shippingFeeCount", shippingFeeCount != null ? shippingFeeCount : 0L);
        model.addAttribute("codCount", codCount != null ? codCount : 0L);

        // Amount statistics
        BigDecimal totalExpected = paymentRepository.sumExpectedAmountByRequestId(id);
        BigDecimal totalPaid = paymentRepository.sumPaidAmountByRequestId(id);
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

        Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);

        if (paymentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Payment payment = paymentOpt.get();
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
            Optional<Payment> paymentOpt = paymentRepository.findById(paymentId);

            if (paymentOpt.isEmpty()) {
                response.put("success", false);
                response.put("message", "Không tìm thấy khoản thanh toán!");
                return ResponseEntity.badRequest().body(response);
            }

            Payment payment = paymentOpt.get();

            // Kiểm tra nếu đã thanh toán rồi
            if (payment.getStatus() == Payment.PaymentStatus.PAID) {
                response.put("success", false);
                response.put("message", "Khoản thanh toán này đã được thanh toán!");
                return ResponseEntity.badRequest().body(response);
            }

            // Cập nhật payment
            payment.setPaidAmount(payment.getExpectedAmount());
            payment.setStatus(Payment.PaymentStatus.PAID);

            paymentRepository.save(payment);

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
}
