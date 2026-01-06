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

                // Cập nhật trạng thái các kiện hàng thành IN_TRANSIT và ghi tracking
                Long endLocationId = trip.getEndLocation() != null ? trip.getEndLocation().getId() : null;
                String startLocationName = trip.getStartLocation() != null ? trip.getStartLocation().getName() : "N/A";
                String endLocationName = trip.getEndLocation() != null ? trip.getEndLocation().getName() : "N/A";
                Long shipperUserId = trip.getShipper() != null && trip.getShipper().getUser() != null
                        ? trip.getShipper().getUser().getId()
                        : null;

                for (Parcel parcel : parcelService.findByTripIdEntities(id)) {
                    Long fromLocationId = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getId()
                            : null;
                    String fromLocationName = parcel.getCurrentLocation() != null
                            ? parcel.getCurrentLocation().getName()
                            : startLocationName;

                    parcel.setStatus(Parcel.ParcelStatus.IN_TRANSIT);
                    parcel.setCurrentLocation(null); // Đang trên đường
                    parcel.setCurrentShipper(trip.getShipper());
                    parcelService.saveParcelEntity(parcel);

                    // Ghi tracking action IN_TRANSIT với tên địa điểm
                    Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
                    String trackingNote = "Đang vận chuyển từ " + fromLocationName + " đến " + endLocationName;
                    trackingService.logAction(parcel.getId(), requestId, "IN_TRANSIT",
                            fromLocationId, endLocationId, shipperUserId, trackingNote);
                }

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

                // Cập nhật parcels: status + currentLocation=endLocation + currentTrip=null
                Parcel.ParcelStatus newStatus;
                String actionCode;
                if (trip.getTripType() == Trip.TripType.DELIVERY) {
                    newStatus = Parcel.ParcelStatus.DELIVERED;
                    actionCode = "DELIVERED";
                } else if (trip.getTripType() == Trip.TripType.PICKUP) {
                    newStatus = Parcel.ParcelStatus.PICKED_UP;
                    actionCode = "PICKED_UP";
                } else {
                    newStatus = Parcel.ParcelStatus.IN_WAREHOUSE;
                    actionCode = "IN_WAREHOUSE";
                }

                Long endLocationId = trip.getEndLocation() != null ? trip.getEndLocation().getId() : null;
                String endLocationName = trip.getEndLocation() != null ? trip.getEndLocation().getName() : "N/A";
                Long shipperUserId = trip.getShipper() != null && trip.getShipper().getUser() != null
                        ? trip.getShipper().getUser().getId()
                        : null;

                for (Parcel parcel : parcelService.findByTripIdEntities(id)) {
                    Long fromLocationId = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getId()
                            : null;
                    String fromLocationName = parcel.getCurrentLocation() != null
                            ? parcel.getCurrentLocation().getName()
                            : "Đang vận chuyển";

                    parcel.setStatus(newStatus);
                    parcel.setCurrentLocation(trip.getEndLocation());
                    parcel.setCurrentTrip(null);
                    parcelService.saveParcelEntity(parcel);

                    // Ghi tracking action cho parcel với tên địa điểm
                    Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
                    String trackingNote = switch (actionCode) {
                        case "DELIVERED" -> "Đã giao hàng tại " + endLocationName;
                        case "PICKED_UP" -> "Đã lấy hàng từ " + fromLocationName + ", đến " + endLocationName;
                        case "IN_WAREHOUSE" -> "Đã nhập kho " + endLocationName;
                        default -> "Hoàn thành chuyến tại " + endLocationName;
                    };
                    trackingService.logAction(parcel.getId(), requestId, actionCode,
                            fromLocationId, endLocationId, shipperUserId, trackingNote);
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
            Trip trip = tripService.getTripEntityById(id);
            if (trip == null) {
                redirectAttributes.addFlashAttribute("error", "Không tìm thấy chuyến xe!");
                return "redirect:/shipper/trips";
            }

            // Kiểm tra quyền (chỉ shipper được gán)
            if (shipper != null && trip.getShipper() != null && !shipper.getId().equals(trip.getShipper().getId())) {
                redirectAttributes.addFlashAttribute("error", "Bạn không có quyền với chuyến này!");
                return "redirect:/shipper/trips";
            }

            if ("IN_PROGRESS".equals(status)) {
                trip.setStatus(Trip.TripStatus.IN_PROGRESS);
                if (trip.getStartedAt() == null) {
                    trip.setStartedAt(LocalDateTime.now());
                }
                tripService.saveTripEntity(trip);

                Long endLocationId = trip.getEndLocation() != null ? trip.getEndLocation().getId() : null;
                Long shipperUserId = trip.getShipper() != null && trip.getShipper().getUser() != null
                        ? trip.getShipper().getUser().getId()
                        : null;

                for (Parcel parcel : parcelService.findByTripIdEntities(id)) {
                    Long fromLocationId = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getId()
                            : null;

                    parcel.setStatus(Parcel.ParcelStatus.IN_TRANSIT);
                    parcel.setCurrentLocation(null);
                    parcel.setCurrentShipper(trip.getShipper());
                    parcelService.saveParcelEntity(parcel);

                    // Ghi tracking
                    Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
                    trackingService.logAction(parcel.getId(), requestId, "IN_TRANSIT",
                            fromLocationId, endLocationId, shipperUserId, "Bắt đầu vận chuyển");
                }

                if (shipper != null) {
                    loggingHelper.logTripStarted(shipper.getId(), id, request);
                }
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái chuyến xe!");
            } else if ("COMPLETED".equals(status)) {
                trip.setStatus(Trip.TripStatus.COMPLETED);
                if (trip.getEndedAt() == null) {
                    trip.setEndedAt(LocalDateTime.now());
                }
                tripService.saveTripEntity(trip);

                Parcel.ParcelStatus newStatus;
                String actionCode;
                if (trip.getTripType() == Trip.TripType.DELIVERY) {
                    newStatus = Parcel.ParcelStatus.DELIVERED;
                    actionCode = "DELIVERED";
                } else if (trip.getTripType() == Trip.TripType.PICKUP) {
                    newStatus = Parcel.ParcelStatus.PICKED_UP;
                    actionCode = "PICKED_UP";
                } else {
                    newStatus = Parcel.ParcelStatus.IN_WAREHOUSE;
                    actionCode = "IN_WAREHOUSE";
                }

                Long endLocationId = trip.getEndLocation() != null ? trip.getEndLocation().getId() : null;
                Long shipperUserId = trip.getShipper() != null && trip.getShipper().getUser() != null
                        ? trip.getShipper().getUser().getId()
                        : null;

                for (Parcel parcel : parcelService.findByTripIdEntities(id)) {
                    Long fromLocationId = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getId()
                            : null;

                    parcel.setStatus(newStatus);
                    // Ưu tiên endLocation của trip; nếu null, fallback theo loại chuyến
                    Long toLocationId = endLocationId;
                    if (trip.getEndLocation() != null) {
                        parcel.setCurrentLocation(trip.getEndLocation());
                    } else if (trip.getTripType() == Trip.TripType.DELIVERY
                            && parcel.getRequest() != null
                            && parcel.getRequest().getReceiverLocation() != null) {
                        parcel.setCurrentLocation(parcel.getRequest().getReceiverLocation());
                        toLocationId = parcel.getRequest().getReceiverLocation().getId();
                    }
                    parcel.setCurrentTrip(null);
                    parcelService.saveParcelEntity(parcel);

                    // Ghi tracking
                    Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
                    trackingService.logAction(parcel.getId(), requestId, actionCode,
                            fromLocationId, toLocationId, shipperUserId, "Hoàn thành chuyến");
                }

                if (shipper != null) {
                    loggingHelper.logTripEnded(shipper.getId(), id, request);
                }
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái chuyến xe!");
            } else if ("CANCELLED".equals(status)) {
                trip.setStatus(Trip.TripStatus.CANCELLED);
                tripService.saveTripEntity(trip);
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật trạng thái chuyến xe!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Trạng thái không hợp lệ!");
            }
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
                Parcel.ParcelStatus newStatus = Parcel.ParcelStatus.valueOf(status);
                Trip trip = parcel.getCurrentTrip();
                if (trip == null || trip.getId() == null || !trip.getId().equals(tripId)) {
                    redirectAttributes.addFlashAttribute("error", "Kiện hàng không thuộc chuyến này!");
                    return "redirect:/shipper/trip/" + tripId;
                }

                Long fromLocationId = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getId() : null;

                parcel.setStatus(newStatus);
                parcel.setCurrentShipper(trip.getShipper());

                Long toLocationId = null;

                // Đồng bộ location/trip theo status (tránh UI bị lệch)
                if (newStatus == Parcel.ParcelStatus.IN_TRANSIT || newStatus == Parcel.ParcelStatus.PICKED_UP) {
                    parcel.setCurrentLocation(null);
                }
                if (newStatus == Parcel.ParcelStatus.DELIVERED) {
                    // Giao xong: set đến điểm nhận và tách khỏi chuyến
                    if (parcel.getRequest() != null && parcel.getRequest().getReceiverLocation() != null) {
                        parcel.setCurrentLocation(parcel.getRequest().getReceiverLocation());
                        toLocationId = parcel.getRequest().getReceiverLocation().getId();
                    } else if (trip.getEndLocation() != null) {
                        parcel.setCurrentLocation(trip.getEndLocation());
                        toLocationId = trip.getEndLocation().getId();
                    }
                    parcel.setCurrentTrip(null);
                }
                if (newStatus == Parcel.ParcelStatus.RETURNED) {
                    if (trip.getEndLocation() != null) {
                        parcel.setCurrentLocation(trip.getEndLocation());
                        toLocationId = trip.getEndLocation().getId();
                    }
                    parcel.setCurrentTrip(null);
                }
                if (newStatus == Parcel.ParcelStatus.FAILED) {
                    // Giao thất bại - giữ nguyên vị trí
                }

                parcelService.saveParcelEntity(parcel);

                // Ghi tracking action
                Long shipperUserId = trip.getShipper() != null && trip.getShipper().getUser() != null
                        ? trip.getShipper().getUser().getId()
                        : null;
                Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
                trackingService.logAction(parcel.getId(), requestId, status,
                        fromLocationId, toLocationId, shipperUserId,
                        "Shipper cập nhật trạng thái: " + status);

                redirectAttributes.addFlashAttribute("success",
                        "Đã cập nhật trạng thái kiện " + parcel.getParcelCode() + "!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/shipper/trip/" + tripId;
    }
}
