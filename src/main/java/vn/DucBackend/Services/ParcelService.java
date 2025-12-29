package vn.DucBackend.Services;

import vn.DucBackend.DTO.ParcelDTO;

import java.util.List;
import java.util.Optional;

public interface ParcelService {

    List<ParcelDTO> findAllParcels();

    Optional<ParcelDTO> findParcelById(Long id);

    Optional<ParcelDTO> findByParcelCode(String parcelCode);

    List<ParcelDTO> findParcelsByRequestId(Long requestId);

    List<ParcelDTO> findParcelsByStatus(String status);

    List<ParcelDTO> findParcelsByShipperId(Long shipperId);

    List<ParcelDTO> findParcelsByTripId(Long tripId);

    List<ParcelDTO> findParcelsByLocationId(Long locationId);

    List<ParcelDTO> findActiveParcelsByShipper(Long shipperId);

    Long countParcelsByRequestId(Long requestId);

    ParcelDTO createParcel(ParcelDTO dto);

    ParcelDTO updateParcel(Long id, ParcelDTO dto);

    ParcelDTO updateParcelStatus(Long id, String status);

    ParcelDTO assignShipperToParcel(Long parcelId, Long shipperId);

    ParcelDTO assignTripToParcel(Long parcelId, Long tripId);

    ParcelDTO updateParcelLocation(Long parcelId, Long locationId);

    void deleteParcel(Long id);

    String generateParcelCode(Long requestId);
}
