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
    private ParcelService parcelService;
    @Autowired
    private ShipperService shipperService;

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

        // Nếu có chọn chuyến, load chi tiết chuyến đó
        if (tripId != null) {
            tripRepository.findById(tripId).ifPresent(trip -> {
                model.addAttribute("selectedTrip", trip);
                // Lấy danh sách kiện đã gán vào chuyến này
                java.util.List<Parcel> loadedParcels = parcelRepository.findAll().stream()
                        .filter(p -> p.getCurrentTrip() != null && p.getCurrentTrip().getId().equals(tripId))
                        .toList();
                model.addAttribute("loadedParcels", loadedParcels);
            });
        }

        // Danh sách kiện chưa được gán chuyến (currentTrip = null) và chưa giao
        java.util.List<Parcel> unassignedParcels = parcelRepository.findAll().stream()
                .filter(p -> p.getCurrentTrip() == null &&
                        p.getStatus() != Parcel.ParcelStatus.DELIVERED &&
                        p.getStatus() != Parcel.ParcelStatus.RETURNED)
                .toList();
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
        int loadedCount = 0;
        for (Long parcelId : parcelIds) {
            parcelRepository.findById(parcelId).ifPresent(parcel -> {
                parcel.setCurrentTrip(trip);
                parcel.setStatus(Parcel.ParcelStatus.IN_TRANSIT);
                parcelRepository.save(parcel);
            });
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

        parcelRepository.findById(parcelId).ifPresent(parcel -> {
            parcel.setCurrentTrip(null);
            parcel.setStatus(Parcel.ParcelStatus.IN_WAREHOUSE);
            parcelRepository.save(parcel);
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
            @RequestParam("shipperId") Long shipperId,
            RedirectAttributes redirectAttributes) {

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
