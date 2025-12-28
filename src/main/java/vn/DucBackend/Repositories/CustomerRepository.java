package vn.DucBackend.Repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.Customer;

/**
 * Repository cho Customer entity - Quản lý khách hàng
 * Phục vụ: tạo vận đơn, tra cứu thông tin khách hàng
 */
@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // ==================== TÌM KIẾM THEO USER LIÊN KẾT ====================
    
    /** Tìm customer theo userId - dùng khi user đăng nhập để lấy thông tin customer */
    Optional<Customer> findByUser_Id(Long userId);
    
    /** Kiểm tra user đã có customer profile chưa */
    Boolean existsByUser_Id(Long userId);
    
    // ==================== TÌM KIẾM THEO THÔNG TIN LIÊN LẠC ====================
    
    /** Tìm customer theo email */
    Optional<Customer> findByEmail(String email);
    
    /** Kiểm tra email đã tồn tại */
    Boolean existsByEmail(String email);
    
    /** Tìm customer theo số điện thoại */
    Optional<Customer> findByPhone(String phone);
    
    /** Kiểm tra số điện thoại đã tồn tại */
    Boolean existsByPhone(String phone);
    
    // ==================== TÌM KIẾM THEO TÊN DOANH NGHIỆP ====================
    
    /** Tìm customer theo businessName (chứa keyword) - dùng cho khách hàng doanh nghiệp */
    List<Customer> findAllByBusinessNameContainingIgnoreCase(String keyword);
    
    Page<Customer> findAllByBusinessNameContainingIgnoreCase(String keyword, Pageable pageable);
    
    // ==================== LỌC THEO LOẠI KHÁCH HÀNG / TRẠNG THÁI ====================
    
    /** Lấy danh sách customer theo loại (INDIVIDUAL / BUSINESS) */
    List<Customer> findAllByCustomerType(Customer.CustomerType customerType);
    
    Page<Customer> findAllByCustomerType(Customer.CustomerType customerType, Pageable pageable);
    
    /** Lấy danh sách customer theo trạng thái */
    List<Customer> findAllByStatus(Boolean status);
    
    Page<Customer> findAllByStatus(Boolean status, Pageable pageable);
    
    /** Đếm số customer theo trạng thái */
    Long countByStatus(Boolean status);
    
    /** Đếm số customer theo loại */
    Long countByCustomerType(Customer.CustomerType customerType);
}