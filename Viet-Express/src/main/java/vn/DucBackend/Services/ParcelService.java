package vn.DucBackend.Services;

import vn.DucBackend.DTO.ParcelDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Parcel (Kiện hàng)
 * 
 * Repository sử dụng: ParcelRepository, CustomerRequestRepository,
 * ShipperRepository, TripRepository, LocationRepository
 * Controller sử dụng: AdminOperationController
 */
public interface ParcelService {

    /** Repository: parcelRepository.findAll() */
    List<ParcelDTO> findAllParcels();

    /** Repository: parcelRepository.findById() */
    Optional<ParcelDTO> findParcelById(Long id);

    /** Repository: parcelRepository.findByParcelCode() */
    Optional<ParcelDTO> findByParcelCode(String parcelCode);

    /** Repository: parcelRepository.findByRequestId() */
    List<ParcelDTO> findParcelsByRequestId(Long requestId);

    /** Repository: parcelRepository.findByStatus() */
    List<ParcelDTO> findParcelsByStatus(String status);

    /** Repository: parcelRepository.findByShipperId() */
    List<ParcelDTO> findParcelsByShipperId(Long shipperId);

    /** Repository: parcelRepository.findByTripId() */
    List<ParcelDTO> findParcelsByTripId(Long tripId);

    /** Repository: parcelRepository.findByCurrentLocationId() */
    List<ParcelDTO> findParcelsByLocationId(Long locationId);

    /** Repository: parcelRepository.findActiveParcelsByShipper() */
    List<ParcelDTO> findActiveParcelsByShipper(Long shipperId);

    /** Repository: parcelRepository.countByRequestId() */
    Long countParcelsByRequestId(Long requestId);

    /** Repository: parcelRepository.save(), customerRequestRepository.findById() */
    ParcelDTO createParcel(ParcelDTO dto);

    /** Repository: parcelRepository.findById(), parcelRepository.save() */
    ParcelDTO updateParcel(Long id, ParcelDTO dto);

    /** Repository: parcelRepository.findById(), parcelRepository.save() */
    ParcelDTO updateParcelStatus(Long id, String status);

    /**
     * Repository: parcelRepository.findById(), parcelRepository.save(),
     * shipperRepository.findById()
     */
    ParcelDTO assignShipperToParcel(Long parcelId, Long shipperId);

    /**
     * Repository: parcelRepository.findById(), parcelRepository.save(),
     * tripRepository.findById()
     */
    ParcelDTO assignTripToParcel(Long parcelId, Long tripId);

    /**
     * Repository: parcelRepository.findById(), parcelRepository.save(),
     * locationRepository.findById()
     */
    ParcelDTO updateParcelLocation(Long parcelId, Long locationId);

    /** Repository: parcelRepository.deleteById() */
    void deleteParcel(Long id);

    /** Tạo mã kiện hàng tự động */
    String generateParcelCode(Long requestId);
}
