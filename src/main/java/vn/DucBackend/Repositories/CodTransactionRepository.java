package vn.DucBackend.Repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.DucBackend.Entities.CodTransaction;

/**
 * Repository cho CodTransaction entity - Quản lý giao dịch thu hộ (COD)
 * Phục vụ: shipper thu tiền COD, đối soát COD, thanh toán COD về cho shop
 */
@Repository
public interface CodTransactionRepository extends JpaRepository<CodTransaction, Long> {

    // ==================== LỌC THEO VẬN ĐƠN ====================

    /** Lấy tất cả COD transaction của một vận đơn */
    List<CodTransaction> findAllByRequest_Id(Long requestId);

    // ==================== LỌC THEO SHIPPER ====================

    /** Lấy COD transaction của shipper theo trạng thái */
    Page<CodTransaction> findAllByShipper_IdAndStatus(Long shipperId, String status, Pageable pageable);

    List<CodTransaction> findAllByShipper_IdAndStatus(Long shipperId, String status);

    /** Lấy tất cả COD transaction của shipper */
    Page<CodTransaction> findAllByShipper_Id(Long shipperId, Pageable pageable);

    List<CodTransaction> findAllByShipper_Id(Long shipperId);

    /** Đếm số COD transaction của shipper theo trạng thái */
    Long countByShipper_IdAndStatus(Long shipperId, String status);

    // ==================== LỌC THEO TRẠNG THÁI ====================

    /** Lấy danh sách COD transaction theo trạng thái */
    Page<CodTransaction> findAllByStatus(String status, Pageable pageable);

    List<CodTransaction> findAllByStatus(String status);

    /** Đếm số COD transaction theo trạng thái */
    Long countByStatus(String status);

    // ==================== LỌC THEO THỜI GIAN VÀ TRẠNG THÁI ====================

    /** Lấy COD transaction theo trạng thái và thời gian thu */
    Page<CodTransaction> findAllByStatusAndCollectedAtBetween(String status, LocalDateTime from, LocalDateTime to,
            Pageable pageable);

    List<CodTransaction> findAllByStatusAndCollectedAtBetween(String status, LocalDateTime from, LocalDateTime to);

    /** Lấy COD transaction theo thời gian thanh toán */
    Page<CodTransaction> findAllBySettledAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    // ==================== TÍNH TỔNG COD (CUSTOM QUERY) ====================

    /** Tính tổng số tiền COD mà shipper đang giữ theo trạng thái */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CodTransaction c WHERE c.shipper.id = :shipperId AND c.status = :status")
    BigDecimal sumAmountByShipper_IdAndStatus(@Param("shipperId") Long shipperId, @Param("status") String status);

    /** Tính tổng COD đã thu của một vận đơn */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CodTransaction c WHERE c.request.id = :requestId")
    BigDecimal sumAmountByRequest_Id(@Param("requestId") Long requestId);

    /** Tính tổng COD theo trạng thái trong khoảng thời gian */
    @Query("SELECT COALESCE(SUM(c.amount), 0) FROM CodTransaction c WHERE c.status = :status AND c.collectedAt BETWEEN :from AND :to")
    BigDecimal sumAmountByStatusAndCollectedAtBetween(@Param("status") String status, @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to);
}
