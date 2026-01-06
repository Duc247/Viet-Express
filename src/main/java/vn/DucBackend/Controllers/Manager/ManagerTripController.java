package vn.DucBackend.Controllers.Manager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.*;
import vn.DucBackend.Utils.LoggingHelper;
import vn.DucBackend.Utils.PaginationUtil;

import java.util.Optional;

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
    private TrackingService trackingService;

    // Repositories cho template data
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private CustomerRequestRepository customerRequestRepository;
    @Autowired
    private ParcelRepository parcelRepository;
    @Autowired
    private LocationRepository locationRepository;
    @Autowired
    private ShipperRepository shipperRepository;
    @Autowired
    private PaymentRepository paymentRepository;

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
        java.util.List<Trip> availableTrips = tripRepository.findAll().stream()
                .filter(t -> t.getStatus() == Trip.TripStatus.CREATED)
                .toList();
        model.addAttribute("availableTrips", availableTrips);

        Trip selectedTrip = null;
        if (tripId != null) {
            Optional<Trip> selectedTripOpt = tripRepository.findById(tripId);
            if (selectedTripOpt.isPresent()) {
                selectedTrip = selectedTripOpt.get();
                model.addAttribute("selectedTrip", selectedTrip);
                model.addAttribute("loadedParcels", parcelRepository.findByCurrentTripId(tripId));
            }
        }

        // Danh sách kiện chưa được gán chuyến và phù hợp với điểm đi của chuyến
        java.util.List<Parcel> unassignedParcels;

        if (tripId != null) {
            // Nếu đã chọn chuyến, chỉ hiển thị kiện:
            // - Đang ở kho xuất phát (startLocation)
            // - Trạng thái IN_WAREHOUSE (đang trong kho, sẵn sàng xếp)
            // - Chưa được gán vào chuyến nào (currentTrip == null)
            if (selectedTrip != null && selectedTrip.getStartLocation() != null) {
                Long startLocationId = selectedTrip.getStartLocation().getId();
                unassignedParcels = parcelRepository
                        .findByCurrentLocationIdAndStatus(startLocationId, Parcel.ParcelStatus.IN_WAREHOUSE).stream()
                        .filter(p -> p.getCurrentTrip() == null)
                        .toList();
            } else {
                unassignedParcels = java.util.List.of();
            }
        } else {
            // Chưa chọn chuyến - hiển thị tất cả kiện chưa gán
            unassignedParcels = parcelRepository.findAll().stream()
                    .filter(p -> p.getCurrentTrip() == null &&
                            p.getStatus() != Parcel.ParcelStatus.DELIVERED &&
                            p.getStatus() != Parcel.ParcelStatus.RETURNED)
                    .toList();
        }
        model.addAttribute("unassignedParcels", unassignedParcels);

        model.addAttribute("locations", locationRepository.findAll());
        model.addAttribute("shippers", shipperRepository.findAll());
        return "manager/planning/trip-planning";
    }

    @PostMapping("/trip-planning/load-parcels")
    public String loadParcelsToTrip(
            @RequestParam("tripId") Long tripId,
            @RequestParam("parcelIds") java.util.List<Long> parcelIds,
            RedirectAttributes redirectAttributes) {

        Optional<Trip> tripOpt = tripRepository.findById(tripId);
        if (tripOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến!");
            return "redirect:/manager/trip-planning";
        }

        Trip trip = tripOpt.get();
        Long endLocationId = trip.getEndLocation() != null ? trip.getEndLocation().getId() : null;
        String startLocationName = trip.getStartLocation() != null ? trip.getStartLocation().getName() : "N/A";
        String endLocationName = trip.getEndLocation() != null ? trip.getEndLocation().getName() : "N/A";

        int loadedCount = 0;
        for (Long parcelId : parcelIds) {
            Parcel parcel = parcelRepository.findById(parcelId).orElse(null);
            if (parcel == null)
                continue;

            if (parcel.getCurrentTrip() != null) {
                continue;
            }
            Long fromLocationId = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getId() : null;
            String fromLocationName = parcel.getCurrentLocation() != null ? parcel.getCurrentLocation().getName()
                    : startLocationName;

            if (trip.getStartLocation() != null) {
                if (parcel.getCurrentLocation() == null
                        || !parcel.getCurrentLocation().getId().equals(trip.getStartLocation().getId())) {
                    continue;
                }
            }

            parcel.setCurrentTrip(trip);
            parcel.setCurrentLocation(null);
            parcel.setStatus(Parcel.ParcelStatus.IN_TRANSIT);
            parcelRepository.save(parcel);

            // Ghi tracking: Xếp hàng lên chuyến với tên địa điểm
            Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
            String trackingNote = "Xuất từ " + fromLocationName + ", đang chuyển đến " + endLocationName;
            trackingService.logAction(parcel.getId(), requestId, "IN_TRANSIT",
                    fromLocationId, endLocationId, null, trackingNote);

            loadedCount++;
        }

        // Cập nhật trạng thái xe nếu còn trống
        if (trip.getCapacityStatus() == Trip.CapacityStatus.EMPTY) {
            trip.setCapacityStatus(Trip.CapacityStatus.AVAILABLE);
            tripRepository.save(trip);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã xếp " + loadedCount + " kiện lên chuyến #" + tripId);
        return "redirect:/manager/trip-planning?tripId=" + tripId;
    }

    @PostMapping("/trips/{id}/update-capacity")
    public String updateTripCapacity(
            @PathVariable("id") Long id,
            @RequestParam("capacityStatus") String capacityStatus,
            RedirectAttributes redirectAttributes) {

        tripRepository.findById(id).ifPresent(trip -> {
            trip.setCapacityStatus(Trip.CapacityStatus.valueOf(capacityStatus));
            tripRepository.save(trip);
        });

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái xe!");
        return "redirect:/manager/trip-planning?tripId=" + id;
    }

    @PostMapping("/trip-planning/unload-parcel")
    public String unloadParcelFromTrip(
            @RequestParam("parcelId") Long parcelId,
            @RequestParam("tripId") Long tripId,
            RedirectAttributes redirectAttributes) {

        Optional<Trip> tripOpt = tripRepository.findById(tripId);
        parcelRepository.findById(parcelId).ifPresent(parcel -> {
            Long toLocationId = null;
            String toLocationName = "N/A";
            parcel.setCurrentTrip(null);
            if (tripOpt.isPresent() && tripOpt.get().getStartLocation() != null) {
                parcel.setCurrentLocation(tripOpt.get().getStartLocation());
                toLocationId = tripOpt.get().getStartLocation().getId();
                toLocationName = tripOpt.get().getStartLocation().getName();
            }
            parcel.setStatus(Parcel.ParcelStatus.IN_WAREHOUSE);
            parcelRepository.save(parcel);

            // Ghi tracking: Dỡ hàng khỏi chuyến với tên địa điểm
            Long requestId = parcel.getRequest() != null ? parcel.getRequest().getId() : null;
            String trackingNote = "Đã nhập kho " + toLocationName;
            trackingService.logAction(parcel.getId(), requestId, "IN_WAREHOUSE",
                    null, toLocationId, null, trackingNote);
        });

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

        java.util.List<Trip> trips = tripRepository.findAll();

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

        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            return "redirect:/manager/trips";
        }

        model.addAttribute("trip", tripOpt.get());
        model.addAttribute("shippers", shipperRepository.findAll());
        return "manager/trip/detail";
    }

    @PostMapping("/trips/{id}/status")
    public String updateTripStatus(
            @PathVariable("id") Long id,
            @RequestParam("newStatus") String newStatus,
            RedirectAttributes redirectAttributes) {

        // Sử dụng Service cho update status
        tripService.updateTripStatus(id, newStatus);

        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến!");
            return "redirect:/manager/trips";
        }

        Trip trip = tripOpt.get();
        if (newStatus.equals("IN_PROGRESS") && trip.getStartedAt() == null) {
            trip.setStartedAt(java.time.LocalDateTime.now());
            tripRepository.save(trip);

            // AUTO-CREATE COD PAYMENT: Tính tổng COD theo từng request
            createCodPaymentsForTrip(trip);
        }
        if (newStatus.equals("COMPLETED") && trip.getEndedAt() == null) {
            trip.setEndedAt(java.time.LocalDateTime.now());
            tripRepository.save(trip);
        }

        redirectAttributes.addFlashAttribute("successMessage", "Đã cập nhật trạng thái chuyến!");
        return "redirect:/manager/trips/" + id;
    }

    // GÁN SHIPPER VÀO TRIP
    @PostMapping("/trips/{id}/assign-shipper")
    public String assignShipperToTrip(
            @PathVariable("id") Long id,
            @RequestParam(value = "shipperId", required = false) Long shipperId,
            RedirectAttributes redirectAttributes) {

        if (shipperId == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Vui lòng chọn tài xế trước khi gán!");
            return "redirect:/manager/trips/" + id;
        }

        Optional<Trip> tripOpt = tripRepository.findById(id);
        if (tripOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến!");
            return "redirect:/manager/trips";
        }

        Optional<Shipper> shipperOpt = shipperRepository.findById(shipperId);
        if (shipperOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy tài xế!");
            return "redirect:/manager/trips/" + id;
        }

        Trip trip = tripOpt.get();
        trip.setShipper(shipperOpt.get());
        tripRepository.save(trip);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đã gán tài xế " + shipperOpt.get().getFullName() + " vào chuyến!");
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

        Optional<CustomerRequest> requestOpt = customerRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy yêu cầu!");
            return "redirect:/manager/requests";
        }

        CustomerRequest customerRequest = requestOpt.get();
        Optional<Location> startOpt = locationRepository.findById(startLocationId);
        Optional<Location> endOpt = locationRepository.findById(endLocationId);

        if (startOpt.isEmpty() || endOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Địa điểm không hợp lệ!");
            return "redirect:/manager/requests/" + requestId + "/trips";
        }

        Trip trip = new Trip();
        trip.setRequest(customerRequest);
        trip.setTripType(Trip.TripType.valueOf(tripType));
        trip.setStartLocation(startOpt.get());
        trip.setEndLocation(endOpt.get());
        trip.setNote(note);

        // Gán shipper nếu có
        if (shipperId != null) {
            shipperRepository.findById(shipperId).ifPresent(trip::setShipper);
        }

        tripRepository.save(trip);

        // Ghi log tạo trip
        loggingHelper.logTripCreated(null, trip.getId(), tripType, httpRequest);

        redirectAttributes.addFlashAttribute("successMessage", "Đã tạo chuyến thành công!");
        return "redirect:/manager/requests/" + requestId + "/trips";
    }

    // ==========================================
    // AUTO-CREATE COD PAYMENT KHI TRIP KHỞI HÀNH
    // ==========================================
    private void createCodPaymentsForTrip(Trip trip) {
        // Lấy tất cả parcels trong trip này
        java.util.List<Parcel> parcelsInTrip = parcelRepository.findAll().stream()
                .filter(p -> p.getCurrentTrip() != null && p.getCurrentTrip().getId().equals(trip.getId()))
                .toList();

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
            boolean existsCodPayment = paymentRepository.findByRequestId(requestId).stream()
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
                paymentRepository.save(codPayment);
            }
        }
    }
}
