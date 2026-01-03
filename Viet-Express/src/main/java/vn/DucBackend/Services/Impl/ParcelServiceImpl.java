package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ParcelDTO;
import vn.DucBackend.Entities.Parcel;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.LocationRepository;
import vn.DucBackend.Repositories.ParcelRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.TripRepository;
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
        return dto;
    }
}
