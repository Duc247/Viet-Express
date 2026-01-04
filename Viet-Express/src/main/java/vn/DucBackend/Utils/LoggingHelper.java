package vn.DucBackend.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Services.SystemLogService;

/**
 * Helper class để ghi log hệ thống
 * Sử dụng ActionType từ bảng action_types
 */
@Component
public class LoggingHelper {

    @Autowired
    private SystemLogService systemLogService;

    // =====================================================
    // CONSTANTS - ACTION TYPES
    // =====================================================

    // AUTH
    public static final String USER_LOGIN = "USER_LOGIN";
    public static final String USER_LOGOUT = "USER_LOGOUT";
    public static final String USER_REGISTER = "USER_REGISTER";
    public static final String PASSWORD_CHANGE = "PASSWORD_CHANGE";

    // ORDER
    public static final String CREATE_ORDER = "CREATE_ORDER";
    public static final String UPDATE_ORDER = "UPDATE_ORDER";
    public static final String CONFIRM_ORDER = "CONFIRM_ORDER";
    public static final String CANCEL_ORDER = "CANCEL_ORDER";
    public static final String RECEIVER_CONFIRM = "RECEIVER_CONFIRM";

    // PARCEL
    public static final String CREATE_PARCEL = "CREATE_PARCEL";
    public static final String UPDATE_PARCEL = "UPDATE_PARCEL";
    public static final String SCAN_PARCEL = "SCAN_PARCEL";

    // PICKUP
    public static final String ASSIGN_PICKUP = "ASSIGN_PICKUP";
    public static final String PICKUP_SUCCESS = "PICKUP_SUCCESS";
    public static final String PICKUP_FAIL = "PICKUP_FAIL";

    // WAREHOUSE
    public static final String RECEIVE_WAREHOUSE = "RECEIVE_WAREHOUSE";
    public static final String DISPATCH_WAREHOUSE = "DISPATCH_WAREHOUSE";

    // TRIP
    public static final String CREATE_TRIP = "CREATE_TRIP";
    public static final String ASSIGN_TRIP = "ASSIGN_TRIP";
    public static final String START_TRIP = "START_TRIP";
    public static final String END_TRIP = "END_TRIP";
    public static final String CANCEL_TRIP = "CANCEL_TRIP";

    // DELIVERY
    public static final String DELIVER_SUCCESS = "DELIVER_SUCCESS";
    public static final String DELIVER_FAIL = "DELIVER_FAIL";

    // PAYMENT
    public static final String CREATE_PAYMENT = "CREATE_PAYMENT";
    public static final String COD_COLLECTED = "COD_COLLECTED";
    public static final String PAYMENT_RECEIVED = "PAYMENT_RECEIVED";

    // TRACKING
    public static final String UPDATE_LOCATION = "UPDATE_LOCATION";
    public static final String CHECK_IN = "CHECK_IN";

    // SYSTEM
    public static final String SYSTEM_CONFIG = "SYSTEM_CONFIG";
    public static final String DATA_EXPORT = "DATA_EXPORT";

    // =====================================================
    // MODULES
    // =====================================================
    public static final String MODULE_AUTH = "AUTH";
    public static final String MODULE_ORDER = "ORDER";
    public static final String MODULE_PARCEL = "PARCEL";
    public static final String MODULE_PICKUP = "PICKUP";
    public static final String MODULE_WAREHOUSE = "WAREHOUSE";
    public static final String MODULE_TRIP = "TRIP";
    public static final String MODULE_DELIVERY = "DELIVERY";
    public static final String MODULE_PAYMENT = "PAYMENT";
    public static final String MODULE_TRACKING = "TRACKING";
    public static final String MODULE_SYSTEM = "SYSTEM";

    // =====================================================
    // LOGGING METHODS
    // =====================================================

    /**
     * Ghi log INFO
     */
    public void logInfo(String module, String actionType, Long actorId, String targetId,
            String details, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        systemLogService.logInfo(module, actionType, actorId, targetId, details, ip, userAgent);
    }

    /**
     * Ghi log WARNING
     */
    public void logWarning(String module, String actionType, Long actorId, String targetId,
            String details, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        systemLogService.logWarning(module, actionType, actorId, targetId, details, ip, userAgent);
    }

    /**
     * Ghi log ERROR
     */
    public void logError(String module, String actionType, Long actorId, String targetId,
            String details, HttpServletRequest request) {
        String ip = getClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        systemLogService.logError(module, actionType, actorId, targetId, details, ip, userAgent);
    }

    // =====================================================
    // HELPER METHODS
    // =====================================================

    /**
     * Lấy IP thực của client (hỗ trợ proxy/load balancer)
     */
    public String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty()) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }

    // =====================================================
    // SHORTCUT METHODS - AUTH
    // =====================================================

    public void logLoginSuccess(Long userId, String username, String role, HttpServletRequest request) {
        logInfo(MODULE_AUTH, USER_LOGIN, userId, userId.toString(),
                "Đăng nhập thành công: " + username + " (" + role + ")", request);
    }

    public void logLoginFailed(Long userId, String username, String reason, HttpServletRequest request) {
        logWarning(MODULE_AUTH, USER_LOGIN, userId, username,
                "Đăng nhập thất bại: " + reason + " - " + username, request);
    }

    public void logLogout(Long userId, String username, HttpServletRequest request) {
        logInfo(MODULE_AUTH, USER_LOGOUT, userId, userId.toString(),
                "Đăng xuất: " + username, request);
    }

    // =====================================================
    // SHORTCUT METHODS - ORDER
    // =====================================================

    public void logOrderCreated(Long customerId, String requestCode, HttpServletRequest request) {
        logInfo(MODULE_ORDER, CREATE_ORDER, customerId, requestCode,
                "Tạo đơn hàng mới: " + requestCode, request);
    }

    public void logOrderConfirmed(Long managerId, String requestCode, HttpServletRequest request) {
        logInfo(MODULE_ORDER, CONFIRM_ORDER, managerId, requestCode,
                "Duyệt đơn hàng: " + requestCode, request);
    }

    public void logOrderCancelled(Long userId, String requestCode, String reason, HttpServletRequest request) {
        logInfo(MODULE_ORDER, CANCEL_ORDER, userId, requestCode,
                "Hủy đơn hàng: " + requestCode + " - Lý do: " + reason, request);
    }

    // =====================================================
    // SHORTCUT METHODS - PICKUP
    // =====================================================

    public void logPickupAssigned(Long managerId, Long tripId, String shipperName, HttpServletRequest request) {
        logInfo(MODULE_PICKUP, ASSIGN_PICKUP, managerId, tripId.toString(),
                "Gán shipper lấy hàng: Trip #" + tripId + " - " + shipperName, request);
    }

    public void logPickupSuccess(Long shipperId, String parcelCode, HttpServletRequest request) {
        logInfo(MODULE_PICKUP, PICKUP_SUCCESS, shipperId, parcelCode,
                "Lấy hàng thành công: " + parcelCode, request);
    }

    public void logPickupFailed(Long shipperId, String parcelCode, String reason, HttpServletRequest request) {
        logWarning(MODULE_PICKUP, PICKUP_FAIL, shipperId, parcelCode,
                "Lấy hàng thất bại: " + parcelCode + " - " + reason, request);
    }

    // =====================================================
    // SHORTCUT METHODS - WAREHOUSE
    // =====================================================

    public void logWarehouseReceive(Long staffId, String parcelCode, String warehouseName, HttpServletRequest request) {
        logInfo(MODULE_WAREHOUSE, RECEIVE_WAREHOUSE, staffId, parcelCode,
                "Nhập kho: " + parcelCode + " tại " + warehouseName, request);
    }

    public void logWarehouseDispatch(Long staffId, String parcelCode, HttpServletRequest request) {
        logInfo(MODULE_WAREHOUSE, DISPATCH_WAREHOUSE, staffId, parcelCode,
                "Xuất kho: " + parcelCode, request);
    }

    // =====================================================
    // SHORTCUT METHODS - TRIP
    // =====================================================

    public void logTripCreated(Long managerId, Long tripId, String tripType, HttpServletRequest request) {
        logInfo(MODULE_TRIP, CREATE_TRIP, managerId, tripId.toString(),
                "Tạo chuyến mới: " + tripType + " - Trip #" + tripId, request);
    }

    public void logTripStarted(Long shipperId, Long tripId, HttpServletRequest request) {
        logInfo(MODULE_TRIP, START_TRIP, shipperId, tripId.toString(),
                "Bắt đầu chuyến: Trip #" + tripId, request);
    }

    public void logTripEnded(Long shipperId, Long tripId, HttpServletRequest request) {
        logInfo(MODULE_TRIP, END_TRIP, shipperId, tripId.toString(),
                "Hoàn thành chuyến: Trip #" + tripId, request);
    }

    // =====================================================
    // SHORTCUT METHODS - DELIVERY
    // =====================================================

    public void logDeliverySuccess(Long shipperId, String parcelCode, String receiverName, HttpServletRequest request) {
        logInfo(MODULE_DELIVERY, DELIVER_SUCCESS, shipperId, parcelCode,
                "Giao hàng thành công: " + parcelCode + " cho " + receiverName, request);
    }

    public void logDeliveryFailed(Long shipperId, String parcelCode, String reason, HttpServletRequest request) {
        logWarning(MODULE_DELIVERY, DELIVER_FAIL, shipperId, parcelCode,
                "Giao hàng thất bại: " + parcelCode + " - " + reason, request);
    }

    // =====================================================
    // SHORTCUT METHODS - PAYMENT
    // =====================================================

    public void logCodCollected(Long shipperId, String parcelCode, Double amount, HttpServletRequest request) {
        logInfo(MODULE_PAYMENT, COD_COLLECTED, shipperId, parcelCode,
                "Thu COD: " + parcelCode + " - " + amount + "đ", request);
    }

    // =====================================================
    // SHORTCUT METHODS - LOCATION
    // =====================================================

    public void logLocationUpdate(Long shipperId, Double lat, Double lng, HttpServletRequest request) {
        logInfo(MODULE_TRACKING, UPDATE_LOCATION, shipperId, shipperId.toString(),
                "Cập nhật vị trí: Lat=" + lat + ", Lng=" + lng, request);
    }

    public void logCheckIn(Long shipperId, String locationName, HttpServletRequest request) {
        logInfo(MODULE_TRACKING, CHECK_IN, shipperId, shipperId.toString(),
                "Check-in: " + locationName, request);
    }
}
