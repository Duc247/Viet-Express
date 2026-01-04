package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Services.*;

/**
 * Staff Dashboard Controller - Trang chủ nhân viên kho
 * Chỉ sử dụng Service layer - không gọi Repository trực tiếp
 */
@Controller
@RequestMapping("/staff")
public class StaffDashboardController {

    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private StaffService staffService;

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

        // Thống kê cho staff - Sử dụng Service
        long confirmedRequests = customerRequestService.countRequestsByStatus("CONFIRMED");
        long inWarehouseCount = parcelService.countParcelsByStatus("IN_WAREHOUSE");
        
        // Đếm CREATED + PICKED_UP
        long createdCount = parcelService.countParcelsByStatus("CREATED");
        long pickedUpCount = parcelService.countParcelsByStatus("PICKED_UP");
        long waitingPickupCount = createdCount + pickedUpCount;

        model.addAttribute("confirmedRequestCount", confirmedRequests);
        model.addAttribute("inWarehouseCount", inWarehouseCount);
        model.addAttribute("waitingPickupCount", waitingPickupCount);

        // Thông tin kho của staff - Sử dụng Service
        if (staffId != null) {
            Staff staff = staffService.getStaffEntityById(staffId);
            if (staff != null) {
                model.addAttribute("staff", staff);
                model.addAttribute("currentWarehouse", staff.getLocation());
            }
        }

        return "staff/dashboard";
    }
}
