package vn.DucBackend.Controllers.Shipper;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.DTO.TripDTO;

/**
 * Shipper History Controller
 * Xử lý lịch sử giao hàng của tài xế
 * 
 * Endpoints:
 * - GET /shipper/history - Lịch sử chuyến xe
 */
@Controller
@RequestMapping("/shipper")
public class ShipperHistoryController extends ShipperBaseController {

    /**
     * Xem lịch sử chuyến xe đã hoàn thành
     */
    @GetMapping("/history")
    public String history(Model model, HttpServletRequest request, Principal principal,
            @RequestParam(value = "status", required = false) String status) {
        addCommonAttributes(model, request, principal);

        ShipperDTO shipper = getCurrentShipper(principal);
        if (shipper != null) {
            // Lấy tất cả trips và lọc theo status nếu có
            List<TripDTO> allTrips = tripService.findTripsByShipperId(shipper.getId());
            
            // Mặc định chỉ hiển thị completed và cancelled
            if (status == null || status.isEmpty()) {
                allTrips = allTrips.stream()
                    .filter(t -> "COMPLETED".equals(t.getStatus()) || "CANCELLED".equals(t.getStatus()))
                    .toList();
            } else if (!"all".equals(status)) {
                allTrips = allTrips.stream()
                    .filter(t -> status.equals(t.getStatus()))
                    .toList();
            }
            
            model.addAttribute("trips", allTrips);
            model.addAttribute("filter", "history");
            model.addAttribute("status", status);
            
            // Tính toán thống kê
            long totalTrips = allTrips.size();
            long completedTrips = allTrips.stream().filter(t -> "COMPLETED".equals(t.getStatus())).count();
            BigDecimal totalCod = allTrips.stream()
                .filter(t -> t.getCodAmount() != null)
                .map(TripDTO::getCodAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            model.addAttribute("totalTrips", totalTrips);
            model.addAttribute("completedTrips", completedTrips);
            model.addAttribute("totalCod", totalCod);
        }

        return "shipper/history";
    }
}
