package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ParcelDTO;
import vn.DucBackend.Entities.Parcel;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Entities.Location;
import vn.DucBackend.Entities.ActionType;
import vn.DucBackend.Entities.ParcelAction;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ParcelRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.TripRepository;
import vn.DucBackend.Repositories.StaffRepository;
import vn.DucBackend.Repositories.ActionTypeRepository;
import vn.DucBackend.Repositories.ParcelActionRepository;
import vn.DucBackend.Services.ParcelService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ParcelServiceImpl implements ParcelService {

    private final ParcelRepository parcelRepository;
    private final CustomerRequestRepository requestRepository;
    private final LocationRepository locationRepository;
    private final ShipperRepository shipperRepository;
    private final TripRepository tripRepository;
    private final StaffRepository staffRepository;
    private final ActionTypeRepository actionTypeRepository;
    private final ParcelActionRepository parcelActionRepository;

    @Override
    public List<ParcelDTO> findAllParcels() {
        return parcelRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<ParcelDTO> findParcelById(Long id) {
        return parcelRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<ParcelDTO> findByParcelCode(String parcelCode) {
        return parcelRepository.findByParcelCode(parcelCode).map(this::toDTO);
    }

    @Override
    public List<ParcelDTO> findParcelsByRequestId(Long requestId) {
        return parcelRepository.findByRequestId(requestId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ParcelDTO> findParcelsByStatus(String status) {
        return parcelRepository.findByStatus(Parcel.ParcelStatus.valueOf(status)).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParcelDTO> findParcelsByShipperId(Long shipperId) {
        return parcelRepository.findByCurrentShipperId(shipperId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParcelDTO> findParcelsByTripId(Long tripId) {
        return parcelRepository.findByCurrentTripId(tripId).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ParcelDTO> findParcelsByLocationId(Long locationId) {
        return parcelRepository.findByCurrentLocationId(locationId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ParcelDTO> findActiveParcelsByShipper(Long shipperId) {
        return parcelRepository.findActiveParcelsByShipper(shipperId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countParcelsByRequestId(Long requestId) {
        return parcelRepository.countByRequestId(requestId);
    }

    @Override
    public ParcelDTO createParcel(ParcelDTO dto) {
        Parcel parcel = new Parcel();
        parcel.setRequest(requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found")));
        parcel.setParcelCode(
                dto.getParcelCode() != null ? dto.getParcelCode() : generateParcelCode(dto.getRequestId()));
        parcel.setDescription(dto.getDescription());
        parcel.setCodAmount(dto.getCodAmount());
        parcel.setWeightKg(dto.getWeightKg());
        parcel.setLengthCm(dto.getLengthCm());
        parcel.setWidthCm(dto.getWidthCm());
        parcel.setHeightCm(dto.getHeightCm());
        parcel.setStatus(Parcel.ParcelStatus.CREATED);
        return toDTO(parcelRepository.save(parcel));
    }

    @Override
    public ParcelDTO updateParcel(Long id, ParcelDTO dto) {
        Parcel parcel = parcelRepository.findById(id).orElseThrow(() -> new RuntimeException("Parcel not found"));
        if (dto.getDescription() != null)
            parcel.setDescription(dto.getDescription());
        if (dto.getCodAmount() != null)
            parcel.setCodAmount(dto.getCodAmount());
        if (dto.getWeightKg() != null)
            parcel.setWeightKg(dto.getWeightKg());
        if (dto.getLengthCm() != null)
            parcel.setLengthCm(dto.getLengthCm());
        if (dto.getWidthCm() != null)
            parcel.setWidthCm(dto.getWidthCm());
        if (dto.getHeightCm() != null)
            parcel.setHeightCm(dto.getHeightCm());
        return toDTO(parcelRepository.save(parcel));
    }

    @Override
    public ParcelDTO updateParcelStatus(Long id, String status) {
        Parcel parcel = parcelRepository.findById(id).orElseThrow(() -> new RuntimeException("Parcel not found"));
        parcel.setStatus(Parcel.ParcelStatus.valueOf(status));
        return toDTO(parcelRepository.save(parcel));
    }

    @Override
    public ParcelDTO assignShipperToParcel(Long parcelId, Long shipperId) {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new RuntimeException("Parcel not found"));
        parcel.setCurrentShipper(
                shipperRepository.findById(shipperId).orElseThrow(() -> new RuntimeException("Shipper not found")));
        return toDTO(parcelRepository.save(parcel));
    }

    @Override
    public ParcelDTO assignTripToParcel(Long parcelId, Long tripId) {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new RuntimeException("Parcel not found"));
        parcel.setCurrentTrip(
                tripRepository.findById(tripId).orElseThrow(() -> new RuntimeException("Trip not found")));
        return toDTO(parcelRepository.save(parcel));
    }

    @Override
    public ParcelDTO updateParcelLocation(Long parcelId, Long locationId) {
        Parcel parcel = parcelRepository.findById(parcelId).orElseThrow(() -> new RuntimeException("Parcel not found"));
        parcel.setCurrentLocation(
                locationRepository.findById(locationId).orElseThrow(() -> new RuntimeException("Location not found")));
        return toDTO(parcelRepository.save(parcel));
    }

    @Override
    public void deleteParcel(Long id) {
        parcelRepository.deleteById(id);
    }

    @Override
    public String generateParcelCode(Long requestId) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long count = parcelRepository.countByRequestId(requestId) + 1;
        return String.format("PCL-%s-%d-%02d", dateStr, requestId, count);
    }

    private ParcelDTO toDTO(Parcel parcel) {
        ParcelDTO dto = new ParcelDTO();
        dto.setId(parcel.getId());
        dto.setRequestId(parcel.getRequest().getId());
        dto.setRequestCode(parcel.getRequest().getRequestCode());
        dto.setParcelCode(parcel.getParcelCode());
        dto.setDescription(parcel.getDescription());
        dto.setCodAmount(parcel.getCodAmount());
        dto.setWeightKg(parcel.getWeightKg());
        dto.setLengthCm(parcel.getLengthCm());
        dto.setWidthCm(parcel.getWidthCm());
        dto.setHeightCm(parcel.getHeightCm());
        if (parcel.getCurrentLocation() != null) {
            dto.setCurrentLocationId(parcel.getCurrentLocation().getId());
            dto.setCurrentLocationName(parcel.getCurrentLocation().getName());
        }
        if (parcel.getCurrentShipper() != null) {
            dto.setCurrentShipperId(parcel.getCurrentShipper().getId());
            dto.setCurrentShipperName(parcel.getCurrentShipper().getFullName());
        }
        if (parcel.getCurrentTrip() != null) {
            dto.setCurrentTripId(parcel.getCurrentTrip().getId());
        }
        dto.setStatus(parcel.getStatus().name());
        dto.setCreatedAt(parcel.getCreatedAt());
        dto.setUpdatedAt(parcel.getUpdatedAt());

        // Sender/Receiver info from CustomerRequest
        CustomerRequest request = parcel.getRequest();
        if (request != null) {
            if (request.getSender() != null) {
                dto.setSenderName(request.getSender().getFullName() != null
                        ? request.getSender().getFullName()
                        : request.getSender().getName());
                dto.setSenderPhone(request.getSender().getPhone());
            }
            if (request.getSenderLocation() != null) {
                dto.setSenderAddress(request.getSenderLocation().getAddressText());
            }
            if (request.getReceiver() != null) {
                dto.setReceiverName(request.getReceiver().getFullName() != null
                        ? request.getReceiver().getFullName()
                        : request.getReceiver().getName());
                dto.setReceiverPhone(request.getReceiver().getPhone());
            }
            if (request.getReceiverLocation() != null) {
                dto.setReceiverAddress(request.getReceiverLocation().getAddressText());
            }
        }

        return dto;
    }

    // ==========================================
    // Methods cho Staff Controllers
    // ==========================================

    @Override
    public Long countParcelsByStatus(String status) {
        return (long) parcelRepository.findByStatus(Parcel.ParcelStatus.valueOf(status)).size();
    }

    @Override
    public List<ParcelDTO> findByLocationIdAndStatus(Long locationId, String status) {
        return parcelRepository.findByCurrentLocationIdAndStatus(locationId, Parcel.ParcelStatus.valueOf(status))
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ParcelDTO> findAllExceptDelivered() {
        return parcelRepository.findAll().stream()
                .filter(p -> p.getStatus() != Parcel.ParcelStatus.DELIVERED)
                .map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public ParcelDTO checkinParcel(Long parcelId, Long staffId, String note) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new RuntimeException("Parcel not found"));
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff not found"));

        if (staff.getLocation() == null) {
            throw new RuntimeException("Staff chưa được gán kho làm việc!");
        }

        Location fromLocation = parcel.getCurrentLocation();
        parcel.setStatus(Parcel.ParcelStatus.IN_WAREHOUSE);
        parcel.setCurrentLocation(staff.getLocation());
        parcel.setCurrentShipper(null);
        parcelRepository.save(parcel);

        // Tạo parcel action
        createParcelAction(parcel, "IN_WAREHOUSE", fromLocation, staff.getLocation(),
                staff.getUser() != null ? staff.getUser().getId() : null,
                note != null ? note : "Nhập kho " + staff.getLocation().getName());

        return toDTO(parcel);
    }

    @Override
    public ParcelDTO checkoutParcel(Long parcelId, Long staffId, String note) {
        Parcel parcel = parcelRepository.findById(parcelId)
                .orElseThrow(() -> new RuntimeException("Parcel not found"));
        Staff staff = staffRepository.findById(staffId).orElse(null);

        Location fromLocation = parcel.getCurrentLocation();
        parcel.setStatus(Parcel.ParcelStatus.IN_TRANSIT);
        parcelRepository.save(parcel);

        // Tạo parcel action
        Long userId = staff != null && staff.getUser() != null ? staff.getUser().getId() : null;
        createParcelAction(parcel, "IN_TRANSIT", fromLocation, null,
                userId, note != null ? note : "Xuất kho để vận chuyển");

        return toDTO(parcel);
    }

    @Override
    public Parcel getParcelEntityById(Long id) {
        return parcelRepository.findById(id).orElse(null);
    }

    @Override
    public ParcelDTO createParcelWithLocation(ParcelDTO dto, Long locationId) {
        Parcel parcel = new Parcel();
        parcel.setRequest(requestRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new RuntimeException("Request not found")));
        parcel.setParcelCode(
                dto.getParcelCode() != null ? dto.getParcelCode() : generateParcelCode(dto.getRequestId()));
        parcel.setDescription(dto.getDescription());
        parcel.setCodAmount(dto.getCodAmount());
        parcel.setWeightKg(dto.getWeightKg());
        parcel.setLengthCm(dto.getLengthCm());
        parcel.setWidthCm(dto.getWidthCm());
        parcel.setHeightCm(dto.getHeightCm());
        parcel.setStatus(Parcel.ParcelStatus.CREATED);

        if (locationId != null) {
            Location location = locationRepository.findById(locationId).orElse(null);
            parcel.setCurrentLocation(location);
        }

        Parcel saved = parcelRepository.save(parcel);

        // Tạo parcel action - CREATED
        Location toLocation = parcel.getCurrentLocation();
        createParcelAction(saved, "CREATED", null, toLocation, null,
                "Staff tạo kiện hàng: " + dto.getDescription());

        return toDTO(saved);
    }

    private void createParcelAction(Parcel parcel, String actionCode, Location fromLocation,
            Location toLocation, Long userId, String note) {
        Optional<ActionType> actionTypeOpt = actionTypeRepository.findByActionCode(actionCode);
        if (actionTypeOpt.isPresent()) {
            ParcelAction action = new ParcelAction();
            action.setParcel(parcel);
            action.setRequest(parcel.getRequest());
            action.setActionType(actionTypeOpt.get());
            action.setFromLocation(fromLocation);
            action.setToLocation(toLocation);
            action.setNote(note);

            if (userId != null) {
                vn.DucBackend.Entities.User user = new vn.DucBackend.Entities.User();
                user.setId(userId);
                action.setActorUser(user);
            }

            parcelActionRepository.save(action);
        }
    }

    // ==========================================
    // Methods cho Manager Controllers
    // ==========================================

    @Override
    public java.util.List<Parcel> getAllParcelEntities() {
        return parcelRepository.findAll();
    }

    @Override
    public Parcel saveParcelEntity(Parcel parcel) {
        return parcelRepository.save(parcel);
    }

    @Override
    public ParcelDTO updateParcelLocation(Long parcelId, Long locationId, String newStatus) {
        Parcel parcel = parcelRepository.findById(parcelId).orElse(null);
        if (parcel == null) {
            return null;
        }

        // Update status
        if (newStatus != null && !newStatus.isEmpty()) {
            parcel.setStatus(Parcel.ParcelStatus.valueOf(newStatus));
        }

        // Update location
        if (locationId != null) {
            Location location = locationRepository.findById(locationId).orElse(null);
            if (location != null) {
                parcel.setCurrentLocation(location);
            }
        }

        Parcel saved = parcelRepository.save(parcel);
        return toDTO(saved);
    }

    @Override
    public java.util.List<Parcel> findByRequestIdEntities(Long requestId) {
        return parcelRepository.findByRequestId(requestId);
    }

    @Override
    public java.util.List<Parcel> findByTripIdEntities(Long tripId) {
        return parcelRepository.findAll().stream()
                .filter(p -> p.getCurrentTrip() != null && p.getCurrentTrip().getId().equals(tripId))
                .collect(java.util.stream.Collectors.toList());
    }

    // ==========================================
    // Methods cho Customer Controllers
    // ==========================================

    @Override
    public Long countByRequestId(Long requestId) {
        return parcelRepository.countByRequestId(requestId);
    }

    @Override
    public Long countDeliveredByRequestId(Long requestId) {
        return parcelRepository.countDeliveredByRequestId(requestId);
    }

    @Override
    public Long countInDeliveryByRequestId(Long requestId) {
        return parcelRepository.countInDeliveryByRequestId(requestId);
    }

    @Override
    public Long countPendingByRequestId(Long requestId) {
        return parcelRepository.countPendingByRequestId(requestId);
    }

    @Override
    public java.util.List<Parcel> searchByRequestIdAndKeyword(Long requestId, String keyword) {
        return parcelRepository.searchByRequestIdAndKeyword(requestId, keyword);
    }

    @Override
    public java.util.List<Parcel> findByRequestIdAndStatusEntities(Long requestId, String status) {
        Parcel.ParcelStatus parcelStatus = Parcel.ParcelStatus.valueOf(status);
        return parcelRepository.findByRequestIdAndStatus(requestId, parcelStatus);
    }

    // ==========================================
    // Bulk Parcel Creation
    // ==========================================

    @Override
    public java.util.List<ParcelDTO> createBulkParcels(Long requestId, String description,
            java.math.BigDecimal codAmount, java.math.BigDecimal weightKg,
            java.math.BigDecimal lengthCm, java.math.BigDecimal widthCm, java.math.BigDecimal heightCm,
            Integer quantity, Long locationId) {

        java.util.List<ParcelDTO> createdParcels = new java.util.ArrayList<>();

        // Lấy request entity
        var request = requestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Request not found"));

        // Lấy location nếu có
        Location location = null;
        if (locationId != null) {
            location = locationRepository.findById(locationId).orElse(null);
        }

        // Tạo N parcels
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        long currentCount = parcelRepository.countByRequestId(requestId);

        for (int i = 1; i <= quantity; i++) {
            Parcel parcel = new Parcel();
            parcel.setRequest(request);

            // Tạo parcel code unique
            String parcelCode = String.format("PCL-%s-%d-%02d", dateStr, requestId, currentCount + i);
            parcel.setParcelCode(parcelCode);

            // Description với số thứ tự
            String numberedDescription = "#" + i + " - " + description;
            parcel.setDescription(numberedDescription);

            parcel.setCodAmount(codAmount);
            parcel.setWeightKg(weightKg);
            parcel.setLengthCm(lengthCm);
            parcel.setWidthCm(widthCm);
            parcel.setHeightCm(heightCm);
            parcel.setStatus(Parcel.ParcelStatus.CREATED);
            parcel.setCurrentLocation(location);

            Parcel saved = parcelRepository.save(parcel);

            // Tạo parcel action
            createParcelAction(saved, "CREATED", null, location, null,
                    "Staff tạo kiện hàng (bulk): " + numberedDescription);

            createdParcels.add(toDTO(saved));
        }

        return createdParcels;
    }
}
