package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.SystemLogDTO;
import vn.DucBackend.Entities.SystemLog;
import vn.DucBackend.Repositories.SystemLogRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.SystemLogService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation của SystemLogService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class SystemLogServiceImpl implements SystemLogService {

    private final SystemLogRepository systemLogRepository;
    private final UserRepository userRepository;

    // ==================== CONVERTER ====================

    private SystemLogDTO toDTO(SystemLog log) {
        return SystemLogDTO.builder()
                .id(log.getId())
                .userId(log.getUser() != null ? log.getUser().getId() : null)
                .userName(log.getUser() != null ? log.getUser().getFullName() : null)
                .action(log.getAction())
                .objectType(log.getObjectType())
                .objectId(log.getObjectId())
                .logTime(log.getLogTime())
                .build();
    }

    // ==================== GHI LOG ====================

    @Override
    public SystemLogDTO log(Long userId, String action, String objectType, Long objectId) {
        SystemLog log = new SystemLog();

        if (userId != null) {
            userRepository.findById(userId).ifPresent(log::setUser);
        }
        log.setAction(action);
        log.setObjectType(objectType);
        log.setObjectId(objectId);
        log.setLogTime(LocalDateTime.now());

        log = systemLogRepository.save(log);
        return toDTO(log);
    }

    // ==================== TRA CỨU ====================

    @Override
    @Transactional(readOnly = true)
    public Page<SystemLogDTO> findAllByUserId(Long userId, Pageable pageable) {
        return systemLogRepository.findAllByUser_Id(userId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemLogDTO> findAllByObjectTypeAndObjectId(String objectType, Long objectId, Pageable pageable) {
        return systemLogRepository.findAllByObjectTypeAndObjectId(objectType, objectId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemLogDTO> findAllByObjectType(String objectType, Pageable pageable) {
        return systemLogRepository.findAllByObjectType(objectType, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemLogDTO> findAllByAction(String action, Pageable pageable) {
        return systemLogRepository.findAllByAction(action, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SystemLogDTO> findAllByLogTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return systemLogRepository.findAllByLogTimeBetween(from, to, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SystemLogDTO> findAllByUserIdAndLogTimeBetween(Long userId, LocalDateTime from, LocalDateTime to) {
        return systemLogRepository.findAllByUser_IdAndLogTimeBetween(userId, from, to)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // ==================== THỐNG KÊ ====================

    @Override
    @Transactional(readOnly = true)
    public Long countByUserId(Long userId) {
        return systemLogRepository.countByUser_Id(userId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByObjectType(String objectType) {
        return systemLogRepository.countByObjectType(objectType);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countByLogTimeBetween(LocalDateTime from, LocalDateTime to) {
        return systemLogRepository.countByLogTimeBetween(from, to);
    }
}
