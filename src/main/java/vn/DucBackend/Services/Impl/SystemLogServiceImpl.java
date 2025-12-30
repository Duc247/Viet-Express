package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.SystemLogDTO;
import vn.DucBackend.Entities.SystemLog;
import vn.DucBackend.Repositories.SystemLogRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.SystemLogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ SystemLog (Nhật ký hệ thống)
 * Được sử dụng bởi: AdminSystemController (Xem log hệ thống)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository logRepository;
    private final UserRepository userRepository;

    @Override
    public List<SystemLogDTO> findAllLogs() {
        return logRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public Optional<SystemLogDTO> findLogById(Long id) {
        return logRepository.findById(id).map(this::toDTO);
    }

    @Override
    public List<SystemLogDTO> findLogsByLevel(String logLevel) {
        return logRepository.findByLogLevel(SystemLog.LogLevel.valueOf(logLevel)).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findLogsByModule(String moduleName) {
        return logRepository.findByModuleName(moduleName).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findLogsByActor(Long actorId) {
        return logRepository.findByActorIdOrderByCreatedAtDesc(actorId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findLogsByActionType(String actionType) {
        return logRepository.findByActionType(actionType).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return logRepository.findByDateRange(startDate, endDate).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findLogsByTargetId(String targetId) {
        return logRepository.findByTargetIdOrderByCreatedAtDesc(targetId).stream().map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findErrorLogs() {
        return logRepository.findErrorLogs().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findWarningLogs() {
        return logRepository.findWarningLogs().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<SystemLogDTO> findLogsByIpAddress(String ipAddress) {
        return logRepository.findByIpAddress(ipAddress).stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public SystemLogDTO logInfo(String moduleName, String actionType, Long actorId, String targetId, String details,
            String ipAddress, String userAgent) {
        return createLog(SystemLog.LogLevel.INFO, moduleName, actionType, actorId, targetId, details, ipAddress,
                userAgent);
    }

    @Override
    public SystemLogDTO logWarning(String moduleName, String actionType, Long actorId, String targetId, String details,
            String ipAddress, String userAgent) {
        return createLog(SystemLog.LogLevel.WARN, moduleName, actionType, actorId, targetId, details, ipAddress,
                userAgent);
    }

    @Override
    public SystemLogDTO logError(String moduleName, String actionType, Long actorId, String targetId, String details,
            String ipAddress, String userAgent) {
        return createLog(SystemLog.LogLevel.ERROR, moduleName, actionType, actorId, targetId, details, ipAddress,
                userAgent);
    }

    @Override
    public void deleteOldLogs(int daysToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysToKeep);
        List<SystemLog> oldLogs = logRepository.findByDateRange(LocalDateTime.of(2000, 1, 1, 0, 0), cutoffDate);
        logRepository.deleteAll(oldLogs);
    }

    private SystemLogDTO createLog(SystemLog.LogLevel level, String moduleName, String actionType, Long actorId,
            String targetId, String details, String ipAddress, String userAgent) {
        SystemLog log = new SystemLog();
        log.setLogLevel(level);
        log.setModuleName(moduleName);
        log.setActionType(actionType);
        if (actorId != null) {
            log.setActor(userRepository.findById(actorId).orElse(null));
        }
        log.setTargetId(targetId);
        log.setLogDetails(details);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        return toDTO(logRepository.save(log));
    }

    private SystemLogDTO toDTO(SystemLog log) {
        SystemLogDTO dto = new SystemLogDTO();
        dto.setId(log.getId());
        dto.setLogLevel(log.getLogLevel().name());
        dto.setModuleName(log.getModuleName());
        dto.setActionType(log.getActionType());
        if (log.getActor() != null) {
            dto.setActorId(log.getActor().getId());
            dto.setActorName(log.getActor().getUsername());
        }
        dto.setTargetId(log.getTargetId());
        dto.setLogDetails(log.getLogDetails());
        dto.setIpAddress(log.getIpAddress());
        dto.setUserAgent(log.getUserAgent());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
