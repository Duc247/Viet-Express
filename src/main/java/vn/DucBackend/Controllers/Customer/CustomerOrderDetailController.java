package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Repositories.*;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Controller xử lý chi tiết đơn hàng cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderDetailController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TrackingCodeRepository trackingCodeRepository;

    @Autowired
    private ParcelActionRepository parcelActionRepository;

    @Autowired
    private TripRepository tripRepository;

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

    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);

        // Phải đăng nhập để xem chi tiết đơn hàng
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
        model.addAttribute("isSender", isSender);
        model.addAttribute("isReceiver", isReceiver);
        model.addAttribute("parcels", parcelRepository.findByRequestId(id));
        model.addAttribute("trackingCodes", trackingCodeRepository.findByRequestId(id));
        model.addAttribute("parcelActions", parcelActionRepository.findByRequestIdOrderByCreatedAtDesc(id));
        model.addAttribute("payments", paymentRepository.findByRequestId(id));
        model.addAttribute("trips", tripRepository.findTripsByRequestId(id));

        // Parcel summary statistics
        Long totalParcels = parcelRepository.countByRequestId(id);
        Long deliveredParcels = parcelRepository.countDeliveredByRequestId(id);
        Long inDeliveryParcels = parcelRepository.countInDeliveryByRequestId(id);
        Long pendingParcels = parcelRepository.countPendingByRequestId(id);

        model.addAttribute("totalParcels", totalParcels != null ? totalParcels : 0L);
        model.addAttribute("deliveredParcels", deliveredParcels != null ? deliveredParcels : 0L);
        model.addAttribute("inDeliveryParcels", inDeliveryParcels != null ? inDeliveryParcels : 0L);
        model.addAttribute("pendingParcels", pendingParcels != null ? pendingParcels : 0L);

        // Trip summary statistics
        Long totalTrips = tripRepository.countTripsByRequestId(id);
        Long completedTrips = tripRepository.countCompletedTripsByRequestId(id);
        Long inProgressTrips = tripRepository.countInProgressTripsByRequestId(id);
        Long pendingTrips = tripRepository.countCreatedTripsByRequestId(id);

        model.addAttribute("totalTrips", totalTrips != null ? totalTrips : 0L);
        model.addAttribute("completedTrips", completedTrips != null ? completedTrips : 0L);
        model.addAttribute("inProgressTrips", inProgressTrips != null ? inProgressTrips : 0L);
        model.addAttribute("pendingTrips", pendingTrips != null ? pendingTrips : 0L);

        // Calculate completion percentage based on delivered parcels
        int completionPercentage = 0;
        if (totalParcels != null && totalParcels > 0) {
            completionPercentage = (int) ((deliveredParcels * 100) / totalParcels);
        }
        model.addAttribute("completionPercentage", completionPercentage);

        BigDecimal totalExpected = paymentRepository.sumExpectedAmountByRequestId(id);
        BigDecimal totalPaid = paymentRepository.sumPaidAmountByRequestId(id);

        model.addAttribute("totalExpected", totalExpected != null ? totalExpected : BigDecimal.ZERO);
        model.addAttribute("totalPaid", totalPaid != null ? totalPaid : BigDecimal.ZERO);

        BigDecimal remaining = (totalExpected != null ? totalExpected : BigDecimal.ZERO)
                .subtract(totalPaid != null ? totalPaid : BigDecimal.ZERO);
        model.addAttribute("remainingAmount", remaining);

        // Payment summary statistics
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

        return "customer/order/detail";
    }

    /**
     * Receiver xác nhận đơn hàng
     */
    @PostMapping("/orders/{id}/confirm")
    public String confirmOrder(@PathVariable("id") Long id, HttpSession session,
            RedirectAttributes redirectAttributes) {
        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Chỉ receiver mới được xác nhận
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);
        if (!isReceiver) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ người nhận mới có thể xác nhận đơn hàng!");
            return "redirect:/customer/orders/" + id;
        }

        // Receiver chỉ xác nhận khi đang ở PENDING (chờ xác nhận)
        if (order.getStatus() != CustomerRequest.RequestStatus.PENDING) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng không ở trạng thái chờ bạn xác nhận!");
            return "redirect:/customer/orders/" + id;
        }

        // Receiver xác nhận → RECEIVER_CONFIRMED (chờ Manager chốt đơn)
        order.setStatus(CustomerRequest.RequestStatus.RECEIVER_CONFIRMED);
        customerRequestRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Đã xác nhận nhận hàng! Chờ quản lý chốt đơn.");
        return "redirect:/customer/orders/" + id;
    }

    /**
     * Receiver từ chối đơn hàng
     */
    @PostMapping("/orders/{id}/reject")
    public String rejectOrder(@PathVariable("id") Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Chỉ receiver mới được từ chối
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);
        if (!isReceiver) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ người nhận mới có thể từ chối đơn hàng!");
            return "redirect:/customer/orders/" + id;
        }

        // Receiver chỉ từ chối khi đang ở PENDING
        if (order.getStatus() != CustomerRequest.RequestStatus.PENDING) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đơn hàng không ở trạng thái chờ bạn xác nhận!");
            return "redirect:/customer/orders/" + id;
        }

        // Chuyển sang CANCELLED
        order.setStatus(CustomerRequest.RequestStatus.CANCELLED);
        customerRequestRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Đã từ chối đơn hàng.");
        return "redirect:/customer/orders";
    }

    // ==========================================
    // SENDER CHỈNH SỬA ĐƠN HÀNG
    // ==========================================

    /**
     * Hiển thị form chỉnh sửa đơn hàng cho Sender
     * Chỉ cho phép khi đơn chưa được xác nhận (PENDING hoặc
     * WAITING_RECEIVER_CONFIRM)
     */
    @GetMapping("/orders/{id}/edit")
    public String editOrderForm(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            HttpSession session, RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Chỉ sender mới được chỉnh sửa
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        if (!isSender) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ người gửi mới có thể chỉnh sửa đơn hàng!");
            return "redirect:/customer/orders/" + id;
        }

        // Chỉ cho phép chỉnh sửa khi chưa được cả 2 xác nhận (PENDING hoặc
        // RECEIVER_CONFIRMED)
        if (order.getStatus() != CustomerRequest.RequestStatus.PENDING
                && order.getStatus() != CustomerRequest.RequestStatus.RECEIVER_CONFIRMED) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Đơn hàng đã được xác nhận, không thể chỉnh sửa!");
            return "redirect:/customer/orders/" + id;
        }

        model.addAttribute("order", order);
        return "customer/order/edit.html";
    }

    /**
     * Xử lý cập nhật đơn hàng từ Sender
     */
    @PostMapping("/orders/{id}/update")
    public String updateOrder(@PathVariable("id") Long id,
            @org.springframework.web.bind.annotation.RequestParam(value = "parcelDescription", required = false) String parcelDescription,
            @org.springframework.web.bind.annotation.RequestParam(value = "codAmount", required = false) BigDecimal codAmount,
            @org.springframework.web.bind.annotation.RequestParam(value = "note", required = false) String note,
            @org.springframework.web.bind.annotation.RequestParam(value = "description", required = false) String description,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Chỉ sender mới được chỉnh sửa
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        if (!isSender) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ người gửi mới có thể chỉnh sửa đơn hàng!");
            return "redirect:/customer/orders/" + id;
        }

        // Chỉ cho phép chỉnh sửa khi chưa được cả 2 xác nhận
        if (order.getStatus() != CustomerRequest.RequestStatus.PENDING
                && order.getStatus() != CustomerRequest.RequestStatus.RECEIVER_CONFIRMED) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Đơn hàng đã được xác nhận, không thể chỉnh sửa!");
            return "redirect:/customer/orders/" + id;
        }

        // Cập nhật thông tin
        if (parcelDescription != null) {
            order.setParcelDescription(parcelDescription);
        }
        if (codAmount != null) {
            order.setCodAmount(codAmount);
        }
        if (note != null) {
            order.setNote(note);
        }
        if (description != null) {
            order.setDescription(description);
        }

        customerRequestRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật đơn hàng thành công!");
        return "redirect:/customer/orders/" + id;
    }

    /**
     * Hủy đơn hàng (chỉ sender, trước khi xác nhận)
     */
    @PostMapping("/orders/{id}/cancel")
    public String cancelOrder(@PathVariable("id") Long id, HttpSession session,
            RedirectAttributes redirectAttributes) {

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);
        if (orderOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Chỉ sender mới được hủy
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        if (!isSender) {
            redirectAttributes.addFlashAttribute("errorMessage", "Chỉ người gửi mới có thể hủy đơn hàng!");
            return "redirect:/customer/orders/" + id;
        }

        // Chỉ cho phép hủy khi chưa được cả 2 xác nhận
        if (order.getStatus() != CustomerRequest.RequestStatus.PENDING
                && order.getStatus() != CustomerRequest.RequestStatus.RECEIVER_CONFIRMED) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Đơn hàng đã được xác nhận, không thể hủy!");
            return "redirect:/customer/orders/" + id;
        }

        order.setStatus(CustomerRequest.RequestStatus.CANCELLED);
        customerRequestRepository.save(order);

        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy đơn hàng.");
        return "redirect:/customer/orders";
    }
}
