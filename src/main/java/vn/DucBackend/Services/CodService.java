package vn.DucBackend.Services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.DucBackend.DTO.CodTransactionDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface cho COD - Quản lý thu hộ
 */
public interface CodService {

    // ==================== COD TRANSACTION ====================

    /** Shipper thu COD */
    CodTransactionDTO collectCod(CodTransactionDTO codDTO);

    /** Thanh toán COD về shop (đánh dấu đã settle) */
    CodTransactionDTO settleCod(Long codTransactionId);

    /** Tìm COD transaction theo ID */
    Optional<CodTransactionDTO> findById(Long id);

    /** Lấy COD transactions của vận đơn */
    List<CodTransactionDTO> findAllByRequestId(Long requestId);

    /** Lấy COD transactions của shipper theo trạng thái */
    Page<CodTransactionDTO> findAllByShipperIdAndStatus(Long shipperId, String status, Pageable pageable);

    /** Lấy COD transactions của shipper */
    Page<CodTransactionDTO> findAllByShipperId(Long shipperId, Pageable pageable);

    /** Lấy COD transactions theo trạng thái */
    Page<CodTransactionDTO> findAllByStatus(String status, Pageable pageable);

    /** Lấy COD transactions theo thời gian thu */
    Page<CodTransactionDTO> findAllByStatusAndCollectedAtBetween(String status, LocalDateTime from, LocalDateTime to,
            Pageable pageable);

    // ==================== THỐNG KÊ ====================

    /** Tính tổng COD shipper đang giữ theo trạng thái */
    BigDecimal getTotalCodByShipperAndStatus(Long shipperId, String status);

    /** Tính tổng COD của vận đơn */
    BigDecimal getTotalCodByRequestId(Long requestId);

    /** Đếm COD transactions của shipper theo trạng thái */
    Long countByShipperIdAndStatus(Long shipperId, String status);
}
