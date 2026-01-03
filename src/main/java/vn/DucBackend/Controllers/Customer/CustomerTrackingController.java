package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ParcelActionRepository;
import vn.DucBackend.Repositories.ParcelRepository;
import vn.DucBackend.Repositories.TripRepository;
import vn.DucBackend.Repositories.TrackingCodeRepository;
import vn.DucBackend.Entities.TrackingCode;

import java.util.Optional;

/**
 * Controller xử lý tracking (theo dõi đơn hàng) cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerTrackingController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParcelRepository parcelRepository;

    @Autowired
    private ParcelActionRepository parcelActionRepository;

    @Autowired
    private TrackingCodeRepository trackingCodeRepository;

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
     * Hiển thị trang tracking và xử lý tìm kiếm
     */
    @GetMapping("/tracking")
    @Transactional(readOnly = true)
    public String tracking(
            @RequestParam(value = "code", required = false) String requestCode,
            Model model,
            HttpServletRequest request,
            HttpSession session) {
        
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId == null) {
            return "redirect:/auth/login";
        }

        // Nếu có code, tìm kiếm (có thể là tracking code TRK-xxx hoặc request code REQ-xxx)
        if (requestCode != null && !requestCode.trim().isEmpty()) {
            String code = requestCode.trim();
            Optional<CustomerRequest> requestOpt = Optional.empty();
            
            // Kiểm tra xem là tracking code (TRK-xxx) hay request code (REQ-xxx)
            if (code.startsWith("TRK-")) {
                // Tìm theo tracking code
                Optional<TrackingCode> trackingCodeOpt = trackingCodeRepository.findByCode(code);
                if (trackingCodeOpt.isPresent()) {
                    TrackingCode trackingCode = trackingCodeOpt.get();
                    // Pre-fetch request để tránh lazy loading
                    trackingCode.getRequest().getId();
                    requestOpt = Optional.of(trackingCode.getRequest());
                }
            } else {
                // Tìm theo request code (REQ-xxx)
                requestOpt = customerRequestRepository.findByRequestCode(code);
            }
            
            if (requestOpt.isPresent()) {
                CustomerRequest order = requestOpt.get();
                
                // Kiểm tra quyền xem - phải là sender hoặc receiver
                boolean isSender = order.getSender() != null && order.getSender().getId().equals(customerId);
                boolean isReceiver = order.getReceiver() != null && order.getReceiver().getId().equals(customerId);
                
                if (isSender || isReceiver) {
                    // Fetch các relationships để tránh lazy loading exception
                    // Access các lazy-loaded properties trong transaction
                    if (order.getSender() != null) {
                        order.getSender().getName();
                        order.getSender().getFullName();
                    }
                    if (order.getReceiver() != null) {
                        order.getReceiver().getName();
                        order.getReceiver().getFullName();
                    }
                    if (order.getSenderLocation() != null) {
                        order.getSenderLocation().getName();
                        order.getSenderLocation().getAddressText();
                    }
                    if (order.getReceiverLocation() != null) {
                        order.getReceiverLocation().getName();
                        order.getReceiverLocation().getAddressText();
                    }
                    
                    model.addAttribute("order", order);
                    model.addAttribute("found", true);
                    model.addAttribute("isSender", isSender);
                    model.addAttribute("isReceiver", isReceiver);
                    
                    // Lấy các trips
                    model.addAttribute("trips", tripRepository.findTripsByRequestId(order.getId()));
                    
                    // Lấy các parcels
                    model.addAttribute("parcels", parcelRepository.findByRequestId(order.getId()));
                    
                    // Lấy lịch sử hành động (parcel actions) - fetch relationships
                    var actions = parcelActionRepository.findByRequestIdOrderByCreatedAtDesc(order.getId());
                    // Pre-fetch các relationships của parcel actions
                    actions.forEach(action -> {
                        if (action.getActionType() != null) action.getActionType().getName();
                        if (action.getToLocation() != null) action.getToLocation().getName();
                    });
                    model.addAttribute("parcelActions", actions);
                } else {
                    model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này!");
                    model.addAttribute("found", false);
                }
            } else {
                model.addAttribute("errorMessage", "Không tìm thấy đơn hàng với mã: " + code);
                model.addAttribute("found", false);
            }
            
            model.addAttribute("searchCode", code);
        }

        return "customer/tracking";
    }
}

