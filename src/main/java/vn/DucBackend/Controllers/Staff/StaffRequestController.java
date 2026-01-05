package vn.DucBackend.Controllers.Staff;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.DTO.CustomerRequestDTO;
import vn.DucBackend.DTO.ParcelDTO;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Services.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Staff Request Controller - Tiếp nhận yêu cầu và tạo kiện hàng
 * Chỉ sử dụng Service layer - không gọi Repository trực tiếp
 */
@Controller
@RequestMapping("/staff")
public class StaffRequestController {

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
    // TIẾP NHẬN YÊU CẦU - Hiển thị requests được giao cho staff
    // ==========================================
    @GetMapping("/requests")
    public String requestList(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        // Lấy requests được giao cho staff này - Sử dụng Service
        List<CustomerRequestDTO> assignedRequests = customerRequestService.findByAssignedStaff(staffId);
        model.addAttribute("requests", assignedRequests);
        return "staff/request/requests";
    }

    // ==========================================
    // CHI TIẾT REQUEST - Form tạo kiện hàng
    // ==========================================
    @GetMapping("/requests/{id}")
    public String requestDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, request);

        // Sử dụng Service để lấy request entity (cần cho template)
        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(id);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/staff/requests";
        }

        model.addAttribute("customerRequest", customerRequest);

        // Lấy danh sách kiện hàng đã tạo cho request này - Sử dụng Service
        List<ParcelDTO> existingParcels = parcelService.findParcelsByRequestId(id);
        model.addAttribute("existingParcels", existingParcels);

        return "staff/request/detail";
    }

    // ==========================================
    // TẠO NHIỀU KIỆN HÀNG TỪ REQUEST
    // ==========================================
    @PostMapping("/requests/{id}/create-parcels")
    public String createParcels(@PathVariable("id") Long requestId,
            @RequestParam("descriptions") List<String> descriptions,
            @RequestParam(value = "codAmounts", required = false) List<BigDecimal> codAmounts,
            @RequestParam(value = "weightKgs", required = false) List<BigDecimal> weightKgs,
            @RequestParam(value = "lengthCms", required = false) List<BigDecimal> lengthCms,
            @RequestParam(value = "widthCms", required = false) List<BigDecimal> widthCms,
            @RequestParam(value = "heightCms", required = false) List<BigDecimal> heightCms,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        Optional<CustomerRequestDTO> reqOpt = customerRequestService.findRequestById(requestId);
        if (reqOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/staff/requests";
        }

        Staff staff = staffService.getStaffEntityById(staffId);
        Long locationId = (staff != null && staff.getLocation() != null) ? staff.getLocation().getId() : null;

        // Tạo nhiều parcels
        int createdCount = 0;
        StringBuilder parcelCodes = new StringBuilder();

        for (int i = 0; i < descriptions.size(); i++) {
            String description = descriptions.get(i);
            if (description == null || description.trim().isEmpty()) {
                continue;
            }

            ParcelDTO parcelDTO = new ParcelDTO();
            parcelDTO.setRequestId(requestId);
            parcelDTO.setDescription(description.trim());
            parcelDTO.setCodAmount(getValueOrDefault(codAmounts, i, BigDecimal.ZERO));
            parcelDTO.setWeightKg(getValueOrNull(weightKgs, i));
            parcelDTO.setLengthCm(getValueOrNull(lengthCms, i));
            parcelDTO.setWidthCm(getValueOrNull(widthCms, i));
            parcelDTO.setHeightCm(getValueOrNull(heightCms, i));

            ParcelDTO created = parcelService.createParcelWithLocation(parcelDTO, locationId);
            createdCount++;
            if (parcelCodes.length() > 0) {
                parcelCodes.append(", ");
            }
            parcelCodes.append(created.getParcelCode());
        }

        if (createdCount > 0) {
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã tạo " + createdCount + " kiện hàng thành công: " + parcelCodes);
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Không có kiện hàng nào được tạo!");
        }

        return "redirect:/staff/requests/" + requestId;
    }

    // Helper methods để lấy giá trị từ list an toàn
    private BigDecimal getValueOrDefault(List<BigDecimal> list, int index, BigDecimal defaultValue) {
        if (list == null || index >= list.size() || list.get(index) == null) {
            return defaultValue;
        }
        return list.get(index);
    }

    private BigDecimal getValueOrNull(List<BigDecimal> list, int index) {
        if (list == null || index >= list.size()) {
            return null;
        }
        return list.get(index);
    }

    // ==========================================
    // TẠO KIỆN HÀNG ĐƠN TỪ REQUEST (giữ lại để tương thích)
    // ==========================================
    @PostMapping("/requests/{id}/create-parcel")
    public String createParcel(@PathVariable("id") Long requestId,
            @RequestParam("description") String description,
            @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
            @RequestParam(value = "weightKg", required = false) BigDecimal weightKg,
            @RequestParam(value = "lengthCm", required = false) BigDecimal lengthCm,
            @RequestParam(value = "widthCm", required = false) BigDecimal widthCm,
            @RequestParam(value = "heightCm", required = false) BigDecimal heightCm,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        Optional<CustomerRequestDTO> reqOpt = customerRequestService.findRequestById(requestId);
        if (reqOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/staff/requests";
        }

        Staff staff = staffService.getStaffEntityById(staffId);
        Long locationId = (staff != null && staff.getLocation() != null) ? staff.getLocation().getId() : null;

        // Tạo parcel DTO
        ParcelDTO parcelDTO = new ParcelDTO();
        parcelDTO.setRequestId(requestId);
        parcelDTO.setDescription(description);
        parcelDTO.setCodAmount(codAmount);
        parcelDTO.setWeightKg(weightKg);
        parcelDTO.setLengthCm(lengthCm);
        parcelDTO.setWidthCm(widthCm);
        parcelDTO.setHeightCm(heightCm);

        // Sử dụng Service để tạo parcel
        ParcelDTO created = parcelService.createParcelWithLocation(parcelDTO, locationId);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã tạo kiện hàng " + created.getParcelCode() + " thành công!");
        return "redirect:/staff/requests/" + requestId;
    }

    // ==========================================
    // TẠO NHIỀU KIỆN HÀNG GIỐNG NHAU (BULK)
    // ==========================================
    @PostMapping("/requests/{id}/create-bulk-parcels")
    public String createBulkParcels(@PathVariable("id") Long requestId,
            @RequestParam("description") String description,
            @RequestParam(value = "codAmount", defaultValue = "0") BigDecimal codAmount,
            @RequestParam(value = "weightKg", required = false) BigDecimal weightKg,
            @RequestParam(value = "lengthCm", required = false) BigDecimal lengthCm,
            @RequestParam(value = "widthCm", required = false) BigDecimal widthCm,
            @RequestParam(value = "heightCm", required = false) BigDecimal heightCm,
            @RequestParam("quantity") Integer quantity,
            HttpSession session, RedirectAttributes redirectAttributes) {

        Long staffId = getStaffIdFromSession(session);
        if (staffId == null) {
            return "redirect:/auth/login";
        }

        Optional<CustomerRequestDTO> reqOpt = customerRequestService.findRequestById(requestId);
        if (reqOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/staff/requests";
        }

        // Validate quantity
        if (quantity == null || quantity < 2) {
            redirectAttributes.addFlashAttribute("errorMessage", "Số lượng phải từ 2 trở lên!");
            return "redirect:/staff/requests/" + requestId;
        }

        // Lấy location của staff
        vn.DucBackend.Entities.Staff staff = staffService.getStaffEntityById(staffId);
        Long locationId = (staff != null && staff.getLocation() != null) ? staff.getLocation().getId() : null;

        // Gọi service để tạo bulk parcels
        List<vn.DucBackend.DTO.ParcelDTO> createdParcels = parcelService.createBulkParcels(
                requestId, description, codAmount, weightKg, lengthCm, widthCm, heightCm, quantity, locationId);

        // Lấy danh sách mã parcel đã tạo
        StringBuilder parcelCodes = new StringBuilder();
        for (vn.DucBackend.DTO.ParcelDTO p : createdParcels) {
            if (parcelCodes.length() > 0) {
                parcelCodes.append(", ");
            }
            parcelCodes.append(p.getParcelCode());
        }

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã tạo " + quantity + " kiện hàng thành công: " + parcelCodes);
        return "redirect:/staff/requests/" + requestId;
    }
}
