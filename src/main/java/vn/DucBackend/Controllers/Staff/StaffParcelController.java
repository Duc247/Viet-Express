package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.DTO.ParcelDTO;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.LoggingHelper;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.List;

/**
 * Staff Parcel Controller - Quản lý kiện hàng, nhập kho, xuất kho
 * Chỉ sử dụng Service layer - không gọi Repository trực tiếp
 */
@Controller
@RequestMapping("/staff")
public class StaffParcelController {

    @Autowired
    private ParcelService parcelService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private LoggingHelper loggingHelper;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getStaffIdFromSession(HttpSession session) {
        Object staffId = session.getAttribute("staffId");
        return staffId != null ? (Long) staffId : null;
    }

    // ==========================================
    // QUẢN LÝ KIỆN HÀNG - Hiển thị TẤT CẢ kiện hàng
    // ==========================================
    @GetMapping("/parcels")
    public String parcelList(Model model, HttpServletRequest request, HttpSession session,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "requestId", required = false) Long requestId,
            @RequestParam(value = "senderPhone", required = false) String senderPhone,
            @RequestParam(value = "receiverPhone", required = false) String receiverPhone,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page", defaultValue = "0") int page) {

        addCommonAttributes(model, request);

        Long staffId = getStaffIdFromSession(session);
        Staff staff = staffId != null ? staffService.getStaffEntityById(staffId) : null;

        if (staff != null && staff.getLocation() != null) {
            model.addAttribute("staffWarehouse", staff.getLocation().getName());
        }

        // Lấy TẤT CẢ kiện hàng ngoại trừ DELIVERED - Sử dụng Service
        List<ParcelDTO> parcels = parcelService.findAllExceptDelivered();

        // Áp dụng filter theo mô tả/mã kiện
        if (search != null && !search.trim().isEmpty()) {
            String searchLower = search.toLowerCase();
            parcels = parcels.stream()
                    .filter(p -> (p.getDescription() != null && p.getDescription().toLowerCase().contains(searchLower))
                            || (p.getParcelCode() != null && p.getParcelCode().toLowerCase().contains(searchLower)))
                    .toList();
        }

        // Filter theo request ID
        if (requestId != null) {
            parcels = parcels.stream()
                    .filter(p -> requestId.equals(p.getRequestId()))
                    .toList();
        }

        // Filter theo status
        if (status != null && !status.trim().isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus().equals(status))
                    .toList();
        }

        // Pagination
        var parcelsPage = PaginationUtil.paginate(parcels, page, PaginationUtil.DEFAULT_PAGE_SIZE);

        model.addAttribute("parcels", parcelsPage.getContent());
        model.addAttribute("totalCount", parcelsPage.getTotalItems());
        model.addAttribute("currentPage", parcelsPage.getCurrentPage());
        model.addAttribute("totalPages", parcelsPage.getTotalPages());
        model.addAttribute("pageSize", PaginationUtil.DEFAULT_PAGE_SIZE);

        // Trả lại các giá trị filter đã nhập
        model.addAttribute("search", search);
        model.addAttribute("requestId", requestId);
        model.addAttribute("senderPhone", senderPhone);
        model.addAttribute("receiverPhone", receiverPhone);
        model.addAttribute("selectedStatus", status);

        return "staff/parcel/parcels";
    }

    // ==========================================
    // NHẬP KHO (CHECK-IN)
    // ==========================================
    @PostMapping("/parcels/{id}/checkin")
    public String checkinParcel(@PathVariable("id") Long parcelId,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        Staff staff = staffService.getStaffEntityById(staffId);
        if (staff == null || staff.getLocation() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn chưa được gán kho làm việc!");
            return "redirect:/staff/parcels";
        }

        try {
            // Sử dụng Service để xử lý checkin
            ParcelDTO parcel = parcelService.checkinParcel(parcelId, staffId, note);

            // Ghi log nhập kho
            loggingHelper.logWarehouseReceive(staffId, parcel.getParcelCode(), staff.getLocation().getName(), request);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã nhập kho kiện " + parcel.getParcelCode() + " thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/staff/parcels";
    }

    // ==========================================
    // XUẤT KHO (CHECK-OUT)
    // ==========================================
    @PostMapping("/parcels/{id}/checkout")
    public String checkoutParcel(@PathVariable("id") Long parcelId,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request, HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        try {
            // Sử dụng Service để xử lý checkout
            ParcelDTO parcel = parcelService.checkoutParcel(parcelId, staffId, note);

            // Ghi log xuất kho
            loggingHelper.logWarehouseDispatch(staffId, parcel.getParcelCode(), request);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xuất kho kiện " + parcel.getParcelCode() + " thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/staff/parcels";
    }
}
