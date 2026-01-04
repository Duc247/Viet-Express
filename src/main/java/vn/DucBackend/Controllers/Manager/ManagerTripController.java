package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.LoggingHelper;
import vn.DucBackend.Utils.PaginationUtil;

/**
 * Manager Trip Controller - Quản lý chuyến vận chuyển & xếp hàng lên xe
 * Sử dụng Service layer cho business logic
 */
@Controller
@RequestMapping("/manager")
public class ManagerTripController {

    // Services cho business logic
    @Autowired
    private TripService tripService;
    @Autowired
    private ParcelService parcelService;
    @Autowired
    private ShipperService shipperService;
    @Autowired
    private CustomerRequestService customerRequestService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private LoggingHelper loggingHelper;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("currentPath", request.getRequestURI());
    }

    // ==========================================
    // LẬP KẾ HOẠCH - XẾP HÀNG LÊN XE
    // ==========================================
    @GetMapping("/trip-planning")
    public String tripPlanning(
            @RequestParam(value = "tripId", required = false) Long tripId,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        // Danh sách chuyến đang tạo (CREATED status)
        java.util.List<Trip> availableTrips = tripService.getAllTripEntities().stream()
                .filter(t -> t.getStatus() == Trip.TripStatus.CREATED)
                .toList();
        model.addAttribute("availableTrips", availableTrips);

        // Nếu có chọn chuyến, load chi tiết chuyến đó
        if (tripId != null) {
            Trip trip = tripService.getTripEntityById(tripId);
            if (trip != null) {
                model.addAttribute("selectedTrip", trip);
                // Lấy danh sách kiện đã gán vào chuyến này
                java.util.List<Parcel> loadedParcels = parcelService.findByTripIdEntities(tripId);
                model.addAttribute("loadedParcels", loadedParcels);
            }
        }

        // Danh sách kiện chưa được gán chuyến (currentTrip = null) và chưa giao
        java.util.List<Parcel> unassignedParcels = parcelService.getAllParcelEntities().stream()
                .filter(p -> p.getCurrentTrip() == null &&
                        p.getStatus() != Parcel.ParcelStatus.DELIVERED &&
                        p.getStatus() != Parcel.ParcelStatus.RETURNED)
                .toList();
        model.addAttribute("unassignedParcels", unassignedParcels);

        model.addAttribute("locations", locationService.getAllLocationEntities());
        model.addAttribute("shippers", shipperService.getAllShipperEntities());
        return "manager/planning/trip-planning";
    }

    @PostMapping("/trip-planning/load-parcels")
    public String loadParcelsToTrip(
            @RequestParam("tripId") Long tripId,
            @RequestParam("parcelIds") java.util.List<Long> parcelIds,
            RedirectAttributes redirectAttributes) {

        Trip trip = tripService.getTripEntityById(tripId);
        if (trip == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến!");
            return "redirect:/manager/trip-planning";
        }

        int loadedCount = 0;
        for (Long parcelId : parcelIds) {
            Parcel parcel = parcelService.getParcelEntityById(parcelId);
            if (parcel != null) {
                parcel.setCurrentTrip(trip);
                parcel.setStatus(Parcel.ParcelStatus.IN_TRANSIT);
                parcelService.saveParcelEntity(parcel);
                loadedCount++;
            }
        }

        // Cập nhật trạng thái xe nếu còn trống
        if (trip.getCapacityStatus() == Trip.CapacityStatus.EMPTY) {
            trip.setCapacityStatus(Trip.CapacityStatus.AVAILABLE);
            tripService.saveTripEntity(trip);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã xếp " + loadedCount + " kiện lên chuyến #" + tripId);
        return "redirect:/manager/trip-planning?tripId=" + tripId;
    }

    @PostMapping("/trips/{id}/update-capacity")
    public String updateTripCapacity(
            @PathVariable("id") Long id,
            @RequestParam("capacityStatus") String capacityStatus,
            RedirectAttributes redirectAttributes) {

        Trip trip = tripService.getTripEntityById(id);
        if (trip != null) {
            trip.setCapacityStatus(Trip.CapacityStatus.valueOf(capacityStatus));
            tripService.saveTripEntity(trip);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái xe!");
        return "redirect:/manager/trip-planning?tripId=" + id;
    }

    @PostMapping("/trip-planning/unload-parcel")
    public String unloadParcelFromTrip(
            @RequestParam("parcelId") Long parcelId,
            @RequestParam("tripId") Long tripId,
            RedirectAttributes redirectAttributes) {

        Parcel parcel = parcelService.getParcelEntityById(parcelId);
        if (parcel != null) {
            parcel.setCurrentTrip(null);
            parcel.setStatus(Parcel.ParcelStatus.IN_WAREHOUSE);
            parcelService.saveParcelEntity(parcel);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã dỡ kiện ra khỏi chuyến!");
        return "redirect:/manager/trip-planning?tripId=" + tripId;
    }

    // ==========================================
    // QUẢN LÝ CHUYẾN VẬN CHUYỂN (TRIPS)
    // ==========================================
    @GetMapping("/trips")
    public String tripList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "page", defaultValue = "1") int page,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        java.util.List<Trip> trips = tripService.getAllTripEntities();

        // Lọc
        if (keyword != null && !keyword.trim().isEmpty()) {
            String kw = keyword.toLowerCase().trim();
            trips = trips.stream()
                    .filter(t -> (t.getShipper() != null && t.getShipper().getFullName().toLowerCase().contains(kw)) ||
                            (t.getStartLocation() != null
                                    && t.getStartLocation().getName().toLowerCase().contains(kw))
                            ||
                            (t.getEndLocation() != null && t.getEndLocation().getName().toLowerCase().contains(kw)))
                    .toList();
        }
        if (status != null && !status.isEmpty()) {
            trips = trips.stream().filter(t -> t.getStatus().name().equals(status)).toList();
        }
        if (type != null && !type.isEmpty()) {
            trips = trips.stream().filter(t -> t.getTripType().name().equals(type)).toList();
        }

        model.addAttribute("tripsPage", PaginationUtil.paginate(trips, page, 10));
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("type", type);
        return "manager/trip/trips";
    }

    @GetMapping("/trips/{id}")
    public String tripDetail(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        Trip trip = tripService.getTripEntityById(id);
        if (trip == null) {
            return "redirect:/manager/trips";
        }

        model.addAttribute("trip", trip);
        model.addAttribute("shippers", shipperService.getAllShipperEntities());
        return "manager/trip/detail";
    }

    @PostMapping("/trips/{id}/status")
    public String updateTripStatus(
            @PathVariable("id") Long id,
            @RequestParam("newStatus") String newStatus,
            RedirectAttributes redirectAttributes) {

        // Sử dụng Service cho update status
        tripService.updateTripStatus(id, newStatus);

        Trip trip = tripService.getTripEntityById(id);
        if (trip == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến!");
            return "redirect:/manager/trips";
        }

        if (newStatus.equals("IN_PROGRESS") && trip.getStartedAt() == null) {
            trip.setStartedAt(java.time.LocalDateTime.now());
            tripService.saveTripEntity(trip);

            // AUTO-CREATE COD PAYMENT: Tính tổng COD theo từng request
            createCodPaymentsForTrip(trip);
        }
        if (newStatus.equals("COMPLETED") && trip.getEndedAt() == null) {
            trip.setEndedAt(java.time.LocalDateTime.now());
            tripService.saveTripEntity(trip);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái chuyến!");
        return "redirect:/manager/trips/" + id;
    }

    // GÁN SHIPPER VÀO TRIP
    @PostMapping("/trips/{id}/assign-shipper")
    public String assignShipperToTrip(
            @PathVariable("id") Long id,
            @RequestParam("shipperId") Long shipperId,
            RedirectAttributes redirectAttributes) {

        Trip trip = tripService.getTripEntityById(id);
        if (trip == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến!");
            return "redirect:/manager/trips";
        }

        Shipper shipper = shipperService.getShipperEntityById(shipperId);
        if (shipper == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài xế!");
            return "redirect:/manager/trips/" + id;
        }

        trip.setShipper(shipper);
        tripService.saveTripEntity(trip);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã gán tài xế " + shipper.getFullName() + " vào chuyến!");
        return "redirect:/manager/trips/" + id;
    }

    // TẠO TRIP
    @PostMapping("/trips/create")
    public String createTrip(
            @RequestParam("requestId") Long requestId,
            @RequestParam("tripType") String tripType,
            @RequestParam(value = "shipperId", required = false) Long shipperId,
            @RequestParam("startLocationId") Long startLocationId,
            @RequestParam("endLocationId") Long endLocationId,
            @RequestParam(value = "note", required = false) String note,
            HttpServletRequest httpRequest,
            RedirectAttributes redirectAttributes) {

        CustomerRequest customerRequest = customerRequestService.getRequestEntityById(requestId);
        if (customerRequest == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        Location startLocation = locationService.getLocationEntityById(startLocationId);
        Location endLocation = locationService.getLocationEntityById(endLocationId);

        if (startLocation == null || endLocation == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa điểm không hợp lệ!");
            return "redirect:/manager/requests/" + requestId + "/trips";
        }

        Trip trip = new Trip();
        trip.setRequest(customerRequest);
        trip.setTripType(Trip.TripType.valueOf(tripType));
        trip.setStartLocation(startLocation);
        trip.setEndLocation(endLocation);
        trip.setNote(note);

        // Gán shipper nếu có
        if (shipperId != null) {
            Shipper shipper = shipperService.getShipperEntityById(shipperId);
            if (shipper != null) {
                trip.setShipper(shipper);
            }
        }

        Trip savedTrip = tripService.saveTripEntity(trip);

        // Ghi log tạo trip
        loggingHelper.logTripCreated(null, savedTrip.getId(), tripType, httpRequest);

        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo chuyến thành công!");
        return "redirect:/manager/requests/" + requestId + "/trips";
    }

    // ==========================================
    // AUTO-CREATE COD PAYMENT KHI TRIP KHỞI HÀNH
    // ==========================================
    private void createCodPaymentsForTrip(Trip trip) {
        // Lấy tất cả parcels trong trip này
        java.util.List<Parcel> parcelsInTrip = parcelService.findByTripIdEntities(trip.getId());

        if (parcelsInTrip.isEmpty()) {
            return;
        }

        // Nhóm parcels theo request
        java.util.Map<Long, java.math.BigDecimal> codByRequest = new java.util.HashMap<>();
        java.util.Map<Long, CustomerRequest> requestMap = new java.util.HashMap<>();

        for (Parcel parcel : parcelsInTrip) {
            CustomerRequest request = parcel.getRequest();
            if (request != null && parcel.getCodAmount() != null
                    && parcel.getCodAmount().compareTo(java.math.BigDecimal.ZERO) > 0) {
                Long requestId = request.getId();
                codByRequest.merge(requestId, parcel.getCodAmount(), java.math.BigDecimal::add);
                requestMap.put(requestId, request);
            }
        }

        // Tạo Payment COD cho mỗi request
        for (java.util.Map.Entry<Long, java.math.BigDecimal> entry : codByRequest.entrySet()) {
            Long requestId = entry.getKey();
            java.math.BigDecimal totalCod = entry.getValue();
            CustomerRequest request = requestMap.get(requestId);

            // Kiểm tra xem đã có COD payment cho trip này và request này chưa
            java.util.List<Payment> existingPayments = paymentService.findPaymentsByRequestIdEntities(requestId);
            boolean existsCodPayment = existingPayments.stream()
                    .anyMatch(p -> p.getPaymentType() == Payment.PaymentType.COD
                            && p.getTrip() != null
                            && p.getTrip().getId().equals(trip.getId()));

            if (!existsCodPayment && totalCod.compareTo(java.math.BigDecimal.ZERO) > 0) {
                Payment codPayment = new Payment();
                codPayment.setRequest(request);
                codPayment.setTrip(trip);
                codPayment.setPaymentType(Payment.PaymentType.COD);
                codPayment.setExpectedAmount(totalCod);
                codPayment.setStatus(Payment.PaymentStatus.UNPAID);
                codPayment.setPaymentScope(Payment.PaymentScope.PER_TRIP);
                codPayment.setDescription("Tự động tạo khi chuyến #" + trip.getId() + " khởi hành");
                paymentService.savePaymentEntity(codPayment);
            }
        }
    }
}
