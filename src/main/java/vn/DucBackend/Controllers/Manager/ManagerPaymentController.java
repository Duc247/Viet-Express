package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.PaginationUtil;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Manager Payment Controller - Quản lý thanh toán
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerPaymentController {

    // Services cho business logic
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private TripService tripService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // TẠO PAYMENT
    // ==========================================
    @PostMapping("/payments/create")
    public String createPayment(
            @RequestParam("requestId") Long requestId,
            @RequestParam("paymentType") String paymentType,
            @RequestParam("expectedAmount") BigDecimal expectedAmount,
            @RequestParam("paymentScope") String paymentScope,
            @RequestParam("payerType") String payerType,
            @RequestParam("receiverType") String receiverType,
            @RequestParam(value = "tripId", required = false) Long tripId,
            @RequestParam(value = "description", required = false) String description,
            RedirectAttributes redirectAttributes) {

        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(requestId);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        // ==========================================
        // VALIDATION: Không được tạo payment vượt quá số tiền còn lại
        // ==========================================
        // Tính tổng phí đơn hàng (shippingFee + codAmount)
        BigDecimal shippingFee = customerRequest.getShippingFee() != null ? customerRequest.getShippingFee()
                : BigDecimal.ZERO;
        BigDecimal codAmount = customerRequest.getCodAmount() != null ? customerRequest.getCodAmount()
                : BigDecimal.ZERO;
        BigDecimal totalOrderFees = shippingFee.add(codAmount);

        // Tính tổng số tiền đã tạo payment trước đó
        java.util.List<Payment> existingPayments = paymentService.findPaymentsByRequestIdEntities(requestId);
        BigDecimal totalExistingPayments = existingPayments.stream()
                .map(p -> p.getExpectedAmount() != null ? p.getExpectedAmount() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Số tiền còn lại khả dụng
        BigDecimal availableAmount = totalOrderFees.subtract(totalExistingPayments);

        // Kiểm tra
        if (expectedAmount.compareTo(availableAmount) > 0) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    String.format("Số tiền thanh toán vượt quá số tiền còn lại! " +
                            "Tổng phí đơn: %s₫, Đã tạo: %s₫, Còn lại: %s₫",
                            totalOrderFees.toString(), totalExistingPayments.toString(), availableAmount.toString()));
            return "redirect:/manager/requests/" + requestId + "/payments";
        }

        if (expectedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số tiền thanh toán phải lớn hơn 0!");
            return "redirect:/manager/requests/" + requestId + "/payments";
        }
        // ==========================================

        Payment payment = new Payment();
        payment.setRequest(customerRequest);
        payment.setPaymentType(Payment.PaymentType.valueOf(paymentType));
        payment.setExpectedAmount(expectedAmount);
        payment.setPaidAmount(BigDecimal.ZERO);
        payment.setPaymentCode("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        payment.setDescription(description);
        payment.setPaymentScope(Payment.PaymentScope.valueOf(paymentScope));
        payment.setPayerType(Payment.PayerType.valueOf(payerType));
        payment.setReceiverType(Payment.ReceiverType.valueOf(receiverType));

        // Nếu scope là PER_TRIP và có tripId thì gắn trip
        if (paymentScope.equals("PER_TRIP") && tripId != null) {
            Trip trip = tripService.getTripEntityById(tripId);
            if (trip != null) {
                payment.setTrip(trip);
            }
        }

        paymentService.savePaymentEntity(payment);
        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo thanh toán thành công!");
        return "redirect:/manager/requests/" + requestId + "/payments";
    }

    // ==========================================
    // QUẢN LÝ THANH TOÁN (PAYMENTS)
    // ==========================================
    @GetMapping("/payments")
    public String paymentList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        java.util.List<Payment> payments = paymentService.getAllPaymentEntities();

        // Lọc
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            payments = payments.stream()
                    .filter(p -> (p.getPaymentCode() != null && p.getPaymentCode().toLowerCase().contains(kw)) ||
                            (p.getRequest() != null && p.getRequest().getRequestCode() != null
                                    && p.getRequest().getRequestCode().toLowerCase().contains(kw)))
                    .toList();
        }
        if (status != null && !status.isEmpty()) {
            payments = payments.stream().filter(p -> p.getStatus().name().equals(status)).toList();
        }
        if (type != null && !type.isEmpty()) {
            payments = payments.stream().filter(p -> p.getPaymentType().name().equals(type)).toList();
        }

        model.addAttribute("paymentsPage", PaginationUtil.paginate(payments, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        return "manager/payment/payments";
    }

    @GetMapping("/payments/{id}")
    public String paymentDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Payment payment = paymentService.getPaymentEntityById(id);
        if (payment == null) {
            return "redirect:/manager/payments";
        }

        model.addAttribute("payment", payment);
        return "manager/payment/detail";
    }

    @PostMapping("/payments/{id}/update")
    public String updatePayment(
            @PathVariable("id") Long id,
            @RequestParam("paidAmount") BigDecimal paidAmount,
            @RequestParam("newStatus") String newStatus,
            RedirectAttributes redirectAttributes) {

        // Sử dụng Service cho update status
        paymentService.updatePaymentStatus(id, newStatus);

        Payment payment = paymentService.getPaymentEntityById(id);
        if (payment == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy thanh toán!");
            return "redirect:/manager/payments";
        }

        payment.setPaidAmount(paidAmount);
        paymentService.savePaymentEntity(payment);

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật thanh toán!");
        return "redirect:/manager/payments/" + id;
    }
}
