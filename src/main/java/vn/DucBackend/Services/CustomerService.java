package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.CustomerDTO;

import java.util.Optional;

/**
 * Service interface cho Customer - Quản lý khách hàng
 */
public interface CustomerService {

    // ==================== TẠO / CẬP NHẬT ====================

    /** Tạo customer profile mới */
    CustomerDTO createCustomer(CustomerDTO customerDTO);

    /** Cập nhật thông tin customer */
    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    /** Thay đổi trạng thái customer */
    CustomerDTO changeStatus(Long id, Boolean status);

    // ==================== TÌM KIẾM ====================

    /** Tìm customer theo ID */
    Optional<CustomerDTO> findById(Long id);

    /** Tìm customer theo user ID - dùng khi user đăng nhập */
    Optional<CustomerDTO> findByUserId(Long userId);

    /** Kiểm tra user đã có customer profile chưa */
    boolean existsByUserId(Long userId);

    // ==================== DANH SÁCH ====================

    /** Lấy tất cả customers */
    Page<CustomerDTO> findAll(Pageable pageable);

    /** Lấy customers theo loại (INDIVIDUAL/BUSINESS) */
    Page<CustomerDTO> findAllByCustomerType(String customerType, Pageable pageable);

    /** Lấy customers theo trạng thái */
    Page<CustomerDTO> findAllByStatus(Boolean status, Pageable pageable);

    /** Tìm kiếm theo tên doanh nghiệp */
    Page<CustomerDTO> searchByBusinessName(String keyword, Pageable pageable);
}
