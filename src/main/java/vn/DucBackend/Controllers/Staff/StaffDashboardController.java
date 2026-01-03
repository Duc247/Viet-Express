package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;

/**
 * Staff Dashboard Controller - Trang chủ nhân viên kho
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    // Services cho business logic
    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private StaffService staffService;

    // Repositories cho template data
    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StaffRepository staffRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getStaffIdFromSession(HttpSession session) {
        Object staffId = session.getAttribute("staffId");
        return staffId != null ? (Long) staffId : null;
    }

    // ==========================================
    // DASHBOARD
    // ==========================================
    @GetMapping({ "", "/", "/dashboard" })
    public String dashboard(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long staffId = getStaffIdFromSession(session);

        // Thống kê cho staff
        long confirmedRequests = customerRequestRepository.findAll().stream()
                .filter(r -> r.getStatus() == CustomerRequest.RequestStatus.CONFIRMED)
                .count();

        long inWarehouseCount = parcelRepository.findAll().stream()
                .filter(p -> p.getStatus() == Parcel.ParcelStatus.IN_WAREHOUSE)
                .count();

        long waitingPickupCount = parcelRepository.findAll().stream()
                .filter(p -> p.getStatus() == Parcel.ParcelStatus.CREATED ||
                        p.getStatus() == Parcel.ParcelStatus.PICKED_UP)
                .count();

        model.addAttribute("confirmedRequestCount", confirmedRequests);
        model.addAttribute("inWarehouseCount", inWarehouseCount);
        model.addAttribute("waitingPickupCount", waitingPickupCount);

        // Thông tin kho của staff
        if (staffId != null) {
            staffRepository.findById(staffId).ifPresent(staff -> {
                model.addAttribute("staff", staff);
                model.addAttribute("currentWarehouse", staff.getLocation());
            });
        }

        return "staff/dashboard";
    }
}
