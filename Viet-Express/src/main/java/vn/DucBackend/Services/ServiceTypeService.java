package vn.DucBackend.Services;

import vn.DucBackend.DTO.ServiceTypeDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý ServiceType (Loại dịch vụ)
 * 
 * Repository sử dụng: ServiceTypeRepository
 * Controller sử dụng: AdminResourceController
 */
public interface ServiceTypeService {

    /**
     * Lấy tất cả loại dịch vụ
     * Repository: serviceTypeRepository.findAll()
     */
    List<ServiceTypeDTO> findAll();

    /**
     * Lấy loại dịch vụ đang hoạt động
     * Repository: serviceTypeRepository.findByIsActiveTrue()
     */
    List<ServiceTypeDTO> findActive();

    /**
     * Lấy loại dịch vụ theo ID
     * Repository: serviceTypeRepository.findById()
     */
    Optional<ServiceTypeDTO> findById(Long id);

    /**
     * Lấy loại dịch vụ theo mã code
     * Repository: serviceTypeRepository.findByCode()
     */
    Optional<ServiceTypeDTO> findByCode(String code);

    /**
     * Tạo mới loại dịch vụ
     * Repository: serviceTypeRepository.save()
     */
    ServiceTypeDTO create(ServiceTypeDTO dto);

    /**
     * Cập nhật loại dịch vụ
     * Repository: serviceTypeRepository.findById(), serviceTypeRepository.save()
     */
    ServiceTypeDTO update(Long id, ServiceTypeDTO dto);

    /**
     * Xóa loại dịch vụ
     * Repository: serviceTypeRepository.deleteById()
     */
    void delete(Long id);

    /**
     * Bật/tắt trạng thái loại dịch vụ
     * Repository: serviceTypeRepository.findById(), serviceTypeRepository.save()
     */
    void toggleStatus(Long id);
}
