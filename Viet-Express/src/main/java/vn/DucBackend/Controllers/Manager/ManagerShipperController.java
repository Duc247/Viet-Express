package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.PaginationUtil;

/**
 * Manager Shipper Controller - Quản lý tài xế
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerShipperController {

    // Services cho business logic
    @Autowired
    private ShipperService shipperService;

    // Repositories cho template data
    @Autowired
    private ShipperRepository shipperRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // QUẢN LÝ TÀI XẾ
    // ==========================================
    @GetMapping("/shippers")
    public String shipperList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        java.util.List<Shipper> shippers = shipperRepository.findAll();

        // Lọc theo keyword
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            shippers = shippers.stream()
                    .filter(s -> (s.getFullName() != null && s.getFullName().toLowerCase().contains(kw)) ||
                            (s.getPhone() != null && s.getPhone().contains(kw)) ||
                            (s.getWorkingArea() != null && s.getWorkingArea().toLowerCase().contains(kw)))
                    .toList();
        }

        model.addAttribute("shippersPage", PaginationUtil.paginate(shippers, page, 10));
        model.addAttribute("keyword", keyword);
        return "manager/shipper/shippers";
    }
}
