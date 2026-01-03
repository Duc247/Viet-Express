package vn.DucBackend.Services;

import vn.DucBackend.DTO.SystemLogDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SystemLogService {

    List<SystemLogDTO> findAllLogs();

    Optional<SystemLogDTO> findLogById(Long id);

    List<SystemLogDTO> findLogsByLevel(String logLevel);

    List<SystemLogDTO> findLogsByModule(String moduleName);

    List<SystemLogDTO> findLogsByActor(Long actorId);

    List<SystemLogDTO> findLogsByActionType(String actionType);

    List<SystemLogDTO> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    List<SystemLogDTO> findLogsByTargetId(String targetId);

    List<SystemLogDTO> findErrorLogs();

    List<SystemLogDTO> findWarningLogs();

    List<SystemLogDTO> findLogsByIpAddress(String ipAddress);

    SystemLogDTO logInfo(String moduleName, String actionType, Long actorId, String targetId, String details,
            String ipAddress, String userAgent);

    SystemLogDTO logWarning(String moduleName, String actionType, Long actorId, String targetId, String details,
            String ipAddress, String userAgent);

    SystemLogDTO logError(String moduleName, String actionType, Long actorId, String targetId, String details,
            String ipAddress, String userAgent);

    void deleteOldLogs(int daysToKeep);
}
