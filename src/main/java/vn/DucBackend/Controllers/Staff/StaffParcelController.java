package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Staff Parcel Controller - Quản lý kiện hàng, nhập kho, xuất kho
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/staff")
public class StaffParcelController {

    // Services cho business logic
    @Autowired
    private ParcelService parcelService;

    // Repositories cho template data
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private StaffRepository staffRepository;
    @Autowired
    private ActionTypeRepository actionTypeRepository;
    @Autowired
    private ParcelActionRepository parcelActionRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getStaffIdFromSession(HttpSession session) {
        Object staffId = session.getAttribute("staffId");
        return staffId != null ? (Long) staffId : null;
    }

    private Long getUserIdFromSession(HttpSession session) {
        Object userId = session.getAttribute("userId");
        return userId != null ? (Long) userId : null;
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
        Staff staff = staffId != null ? staffRepository.findById(staffId).orElse(null) : null;

        if (staff != null && staff.getLocation() != null) {
            model.addAttribute("staffWarehouse", staff.getLocation().getName());
        }

        // Lấy TẤT CẢ kiện hàng từ tất cả dự án
        List<Parcel> parcels = parcelRepository.findAll();

        // Ẩn kiện hàng đã giao (DELIVERED)
        parcels = parcels.stream()
                .filter(p -> p.getStatus() != Parcel.ParcelStatus.DELIVERED)
                .toList();

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
                    .filter(p -> p.getRequest() != null && requestId.equals(p.getRequest().getId()))
                    .toList();
        }

        // Filter theo SĐT người gửi
        if (senderPhone != null && !senderPhone.trim().isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getRequest() != null && p.getRequest().getSender() != null
                            && p.getRequest().getSender().getPhone() != null
                            && p.getRequest().getSender().getPhone().contains(senderPhone))
                    .toList();
        }

        // Filter theo SĐT người nhận
        if (receiverPhone != null && !receiverPhone.trim().isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getRequest() != null && p.getRequest().getReceiver() != null
                            && p.getRequest().getReceiver().getPhone() != null
                            && p.getRequest().getReceiver().getPhone().contains(receiverPhone))
                    .toList();
        }

        // Filter theo status
        if (status != null && !status.trim().isEmpty()) {
            parcels = parcels.stream()
                    .filter(p -> p.getStatus() != null && p.getStatus().name().equals(status))
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
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/login";
        }

        Optional<Parcel> parcelOpt = parcelRepository.findById(parcelId);
        if (parcelOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy kiện hàng!");
            return "redirect:/staff/parcels";
        }

        Parcel parcel = parcelOpt.get();
        Staff staff = staffRepository.findById(staffId).orElse(null);

        if (staff == null || staff.getLocation() == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn chưa được gán kho làm việc!");
            return "redirect:/staff/parcels";
        }

        Location fromLocation = parcel.getCurrentLocation();

        // Sử dụng Service để update status
        parcelService.updateParcelStatus(parcelId, "IN_WAREHOUSE");

        // Cập nhật location
        parcel.setCurrentLocation(staff.getLocation());
        parcel.setCurrentShipper(null); // Hàng đã về kho, không còn với shipper
        parcelRepository.save(parcel);

        // Tạo parcel action - IN_WAREHOUSE
        createParcelAction(parcel, parcel.getRequest(), "IN_WAREHOUSE",
                fromLocation, staff.getLocation(),
                getUserIdFromSession(session),
                note != null ? note : "Nhập kho " + staff.getLocation().getName());

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã nhập kho kiện " + parcel.getParcelCode() + " thành công!");
        return "redirect:/staff/parcels";
    }

    // ==========================================
    // XUẤT KHO (CHECK-OUT)
    // ==========================================
    @PostMapping("/parcels/{id}/checkout")
    public String checkoutParcel(@PathVariable("id") Long parcelId,
            @RequestParam(value = "note", required = false) String note,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/login";
        }

        Optional<Parcel> parcelOpt = parcelRepository.findById(parcelId);
        if (parcelOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy kiện hàng!");
            return "redirect:/staff/parcels";
        }

        Parcel parcel = parcelOpt.get();
        Location fromLocation = parcel.getCurrentLocation();

        // Sử dụng Service để update status
        parcelService.updateParcelStatus(parcelId, "IN_TRANSIT");

        // Tạo parcel action - IN_TRANSIT
        createParcelAction(parcel, parcel.getRequest(), "IN_TRANSIT",
                fromLocation, null,
                getUserIdFromSession(session),
                note != null ? note : "Xuất kho để vận chuyển");

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã xuất kho kiện " + parcel.getParcelCode() + " thành công!");
        return "redirect:/staff/parcels";
    }

    // ==========================================
    // HELPER METHODS
    // ==========================================
    private void createParcelAction(Parcel parcel, CustomerRequest request, String actionCode,
            Location fromLocation, Location toLocation, Long userId, String note) {
        Optional<ActionType> actionTypeOpt = actionTypeRepository.findByActionCode(actionCode);
        if (actionTypeOpt.isPresent()) {
            ParcelAction action = new ParcelAction();
            action.setParcel(parcel);
            action.setRequest(request);
            action.setActionType(actionTypeOpt.get());
            action.setFromLocation(fromLocation);
            action.setToLocation(toLocation);
            action.setNote(note);

            if (userId != null) {
                User user = new User();
                user.setId(userId);
                action.setActorUser(user);
            }

            parcelActionRepository.save(action);
        }
    }
}
