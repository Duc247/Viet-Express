package vn.DucBackend.Services;

import vn.DucBackend.DTO.VehicleDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Vehicle (Phương tiện)
 * 
 * Repository sử dụng: VehicleRepository, LocationRepository
 * Controller sử dụng: AdminResourceController
 */
public interface VehicleService {

    /**
     * Lấy tất cả phương tiện
     * Repository: vehicleRepository.findAll()
     */
    List<VehicleDTO> findAll();

    /**
     * Lấy phương tiện theo ID
     * Repository: vehicleRepository.findById()
     */
    Optional<VehicleDTO> findById(Long id);

    /**
     * Lấy phương tiện theo trạng thái
     * Repository: vehicleRepository.findByStatus()
     */
    List<VehicleDTO> findByStatus(String status);

    /**
     * Lấy phương tiện khả dụng
     * Repository: vehicleRepository.findByStatus(AVAILABLE)
     */
    List<VehicleDTO> findAvailable();

    /**
     * Tạo mới phương tiện
     * Repository: vehicleRepository.save(), locationRepository.findById()
     */
    VehicleDTO create(VehicleDTO dto);

    /**
     * Cập nhật phương tiện
     * Repository: vehicleRepository.findById(), vehicleRepository.save(),
     * locationRepository.findById()
     */
    VehicleDTO update(Long id, VehicleDTO dto);

    /**
     * Xóa phương tiện
     * Repository: vehicleRepository.deleteById()
     */
    void delete(Long id);

    /**
     * Bật/tắt trạng thái phương tiện (AVAILABLE <-> INACTIVE)
     * Repository: vehicleRepository.findById(), vehicleRepository.save()
     */
    void toggleStatus(Long id);
}
