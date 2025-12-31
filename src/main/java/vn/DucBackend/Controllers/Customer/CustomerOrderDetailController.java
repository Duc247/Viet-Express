package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

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
        Optional<CustomerRequest> orderOpt = customerRequestRepository.findById(id);

        if (orderOpt.isEmpty()) {
            model.addAttribute("errorMessage", "Không tìm thấy đơn hàng!");
            return "redirect:/customer/orders";
        }

        CustomerRequest order = orderOpt.get();

        // Kiểm tra quyền xem
        boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
        boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);

        if (customerId != null && !isSender && !isReceiver) {
            model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
            return "redirect:/customer/orders";
        }

        model.addAttribute("order", order);
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
}
