package vn.DucBackend.Controllers.Shipper;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.ParcelDTO;
import vn.DucBackend.DTO.ShipperDTO;
import vn.DucBackend.DTO.TripDTO;
import vn.DucBackend.Entities.Parcel;
import vn.DucBackend.Entities.Trip;

/**
 * Shipper Trip Controller
 * Xử lý quản lý chuyến xe của tài xế
 * 
 * Endpoints:
 * - GET /shipper/trips - Danh sách chuyến xe
 * - GET /shipper/trip/{id} - Chi tiết chuyến xe
 * - POST /shipper/trip/{id}/status - Cập nhật trạng thái
 * - POST /shipper/trip/{id}/start - Bắt đầu chuyến
 * - POST /shipper/trip/{id}/complete - Hoàn thành chuyến
 * - POST /shipper/trip/{id}/update-note - Cập nhật ghi chú
 * - POST /shipper/trip/{id}/update-parcel - Cập nhật trạng thái kiện hàng
 */
@Controller
@RequestMapping("/shipper")
public class ShipperTripController extends ShipperBaseController {

    /**
     * Danh sách chuyến xe của shipper
     */
    @GetMapping("/trips")
    public String myTrips(Model model, HttpServletRequest request, Principal principal,
            @RequestParam(required = false, defaultValue = "all") String filter) {
        addCommonAttributes(model, request, principal);
        ShipperDTO shipper = getCurrentShipper(principal);

        if (shipper != null) {
            List<TripDTO> trips;
            if ("active".equals(filter)) {
                trips = tripService.findActiveTripsByShipper(shipper.getId());
            } else if ("history".equals(filter) || "completed".equals(filter)) {
                trips = tripService.findCompletedTripsByShipper(shipper.getId());
            } else {
                trips = tripService.findTripsByShipperId(shipper.getId());
            }
            model.addAttribute("trips", trips);
            model.addAttribute("filter", filter);
            
            // Thống kê nhanh
            model.addAttribute("totalTrips", trips.size());
            model.addAttribute("activeCount", trips.stream()
                .filter(t -> "IN_PROGRESS".equals(t.getStatus()) || "CREATED".equals(t.getStatus()))
                .count());
            model.addAttribute("completedCount", trips.stream()
                .filter(t -> "COMPLETED".equals(t.getStatus()))
                .count());
        }

        return "shipper/trip/list";
    }

    /**
     * Chi tiết một chuyến xe
     */
    @GetMapping("/trip/{id}")
    public String tripDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request,
            Principal principal) {
        addCommonAttributes(model, request, principal);
        ShipperDTO shipper = getCurrentShipper(principal);
        
        Optional<TripDTO> tripOpt = tripService.findTripById(id);
        if (tripOpt.isPresent()) {
            TripDTO trip = tripOpt.get();

            // Kiểm tra quyền truy cập - chỉ shipper được gán mới xem được
            if (shipper != null && trip.getShipperId() != null && !shipper.getId().equals(trip.getShipperId())) {
                return "redirect:/shipper/trips?error=unauthorized";
            }

            model.addAttribute("trip", trip);
            // Thêm thông tin parcels trong chuyến
            if (trip.getId() != null) {
                List<ParcelDTO> parcels = parcelService.findParcelsByTripId(trip.getId());
                model.addAttribute("parcels", parcels);
                
                // Thống kê kiện hàng
                model.addAttribute("totalParcels", parcels.size());
                model.addAttribute("deliveredCount", parcels.stream()
                    .filter(p -> "DELIVERED".equals(p.getStatus()))
                    .count());
                model.addAttribute("pendingCount", parcels.stream()
                    .filter(p -> !"DELIVERED".equals(p.getStatus()) && !"FAILED".equals(p.getStatus()))
                    .count());
            }

            return "shipper/trip/detail";
        } else {
            return "redirect:/shipper/trips";
        }
    }

    /**
     * Bắt đầu chuyến xe
     */
    @PostMapping("/trip/{id}/start")
    public String startTrip(@PathVariable("id") Long id, HttpServletRequest request, 
            Principal principal, RedirectAttributes redirectAttributes) {
        ShipperDTO shipper = getCurrentShipper(principal);

        if (shipper == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập tài xế để thực hiện thao tác này!");
            return "redirect:/auth/login";
        }

        try {
            Trip trip = tripService.getTripEntityById(id);
            if (trip != null) {
                // Kiểm tra quyền
                if (shipper != null && trip.getShipper() != null 
                    && !shipper.getId().equals(trip.getShipper().getId())) {
                    redirectAttributes.addFlashAttribute("error", "Bạn không có quyền với chuyến này!");
                    return "redirect:/shipper/trips";
                }
                
                // Cập nhật trạng thái
                trip.setStatus(Trip.TripStatus.IN_PROGRESS);
                trip.setStartedAt(LocalDateTime.now());
                tripService.saveTripEntity(trip);
                
                // Cập nhật trạng thái các kiện hàng thành IN_TRANSIT
                updateParcelsStatus(id, "IN_TRANSIT");
                
                // Ghi log
                loggingHelper.logTripStarted(shipper.getId(), id, request);
                
                redirectAttributes.addFlashAttribute("success", "Đã bắt đầu chuyến xe!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/shipper/trip/" + id;
    }

    /**
     * Hoàn thành chuyến xe
     */
    @PostMapping("/trip/{id}/complete")
    public String completeTrip(@PathVariable("id") Long id, 
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        ShipperDTO shipper = getCurrentShipper(principal);

        if (shipper == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập tài xế để thực hiện thao tác này!");
            return "redirect:/auth/login";
        }

        try {
            Trip trip = tripService.getTripEntityById(id);
            if (trip != null) {
                // Kiểm tra quyền
                if (shipper != null && trip.getShipper() != null 
                    && !shipper.getId().equals(trip.getShipper().getId())) {
                    redirectAttributes.addFlashAttribute("error", "Bạn không có quyền với chuyến này!");
                    return "redirect:/shipper/trips";
                }
                
                // Cập nhật trạng thái
                trip.setStatus(Trip.TripStatus.COMPLETED);
                trip.setEndedAt(LocalDateTime.now());
                if (note != null && !note.isEmpty()) {
                    trip.setNote(note);
                }
                tripService.saveTripEntity(trip);
                
                // Cập nhật trạng thái các kiện hàng (theo loại chuyến)
                if (trip.getTripType() == Trip.TripType.DELIVERY) {
                    updateParcelsStatus(id, "DELIVERED");
                } else if (trip.getTripType() == Trip.TripType.PICKUP) {
                    updateParcelsStatus(id, "PICKED_UP");
                } else {
                    updateParcelsStatus(id, "IN_WAREHOUSE");
                }
                
                // Ghi log
                loggingHelper.logTripEnded(shipper.getId(), id, request);
                
                redirectAttributes.addFlashAttribute("success", "Đã hoàn thành chuyến xe!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/shipper/trip/" + id;
    }

    /**
     * Cập nhật trạng thái chuyến xe (legacy - giữ cho tương thích)
     */
    @PostMapping("/trip/{id}/status")
    public String updateTripStatus(@PathVariable("id") Long id, @RequestParam("status") String status,
            HttpServletRequest request, Principal principal, RedirectAttributes redirectAttributes) {
        ShipperDTO shipper = getCurrentShipper(principal);

        try {
            tripService.updateTripStatus(id, status);

            // Ghi log cập nhật trạng thái
            if (shipper != null) {
                if ("IN_PROGRESS".equals(status)) {
                    loggingHelper.logTripStarted(shipper.getId(), id, request);
                } else if ("COMPLETED".equals(status)) {
                    loggingHelper.logTripEnded(shipper.getId(), id, request);
                }
            }
            
            redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái chuyến xe!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }

        return "redirect:/shipper/trip/" + id;
    }

    /**
     * Cập nhật ghi chú chuyến xe
     */
    @PostMapping("/trip/{id}/update-note")
    public String updateTripNote(@PathVariable("id") Long id, 
            @RequestParam("note") String note,
            Principal principal, RedirectAttributes redirectAttributes) {
        ShipperDTO shipper = getCurrentShipper(principal);

        if (shipper == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập tài xế để thực hiện thao tác này!");
            return "redirect:/auth/login";
        }

        try {
            Trip trip = tripService.getTripEntityById(id);
            if (trip != null) {
                trip.setNote(note);
                tripService.saveTripEntity(trip);
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật ghi chú!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/shipper/trip/" + id;
    }

    /**
     * Cập nhật trạng thái một kiện hàng trong chuyến
     */
    @PostMapping("/trip/{tripId}/parcel/{parcelId}/status")
    public String updateParcelStatus(@PathVariable("tripId") Long tripId,
            @PathVariable("parcelId") Long parcelId,
            @RequestParam("status") String status,
            Principal principal, RedirectAttributes redirectAttributes) {
        ShipperDTO shipper = getCurrentShipper(principal);

        if (shipper == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập tài xế để thực hiện thao tác này!");
            return "redirect:/auth/login";
        }

        try {
            Parcel parcel = parcelService.getParcelEntityById(parcelId);
            if (parcel != null) {
                parcel.setStatus(Parcel.ParcelStatus.valueOf(status));
                parcelService.saveParcelEntity(parcel);
                redirectAttributes.addFlashAttribute("success", 
                    "Đã cập nhật trạng thái kiện " + parcel.getParcelCode() + "!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/shipper/trip/" + tripId;
    }

    /**
     * Helper: Cập nhật trạng thái tất cả kiện hàng trong chuyến
     */
    private void updateParcelsStatus(Long tripId, String status) {
        try {
            List<ParcelDTO> parcels = parcelService.findParcelsByTripId(tripId);
            for (ParcelDTO parcelDTO : parcels) {
                Parcel parcel = parcelService.getParcelEntityById(parcelDTO.getId());
                if (parcel != null) {
                    parcel.setStatus(Parcel.ParcelStatus.valueOf(status));
                    parcelService.saveParcelEntity(parcel);
                }
            }
        } catch (Exception e) {
            // Log error but don't throw
            e.printStackTrace();
        }
    }
}
