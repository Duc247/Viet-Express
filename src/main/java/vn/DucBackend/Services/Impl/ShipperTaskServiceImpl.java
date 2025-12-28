package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ShipperTaskDTO;
import vn.DucBackend.Entities.ShipperTask;
import vn.DucBackend.Entities.ShipperTask.TaskStatus;
import vn.DucBackend.Entities.ShipperTask.TaskType;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.ShipperTaskRepository;
import vn.DucBackend.Repositories.TrackingCodeRepository;
import vn.DucBackend.Services.ShipperTaskService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation của ShipperTaskService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ShipperTaskServiceImpl implements ShipperTaskService {

    private final ShipperTaskRepository shipperTaskRepository;
    private final ShipperRepository shipperRepository;
    private final CustomerRequestRepository customerRequestRepository;
    private final TrackingCodeRepository trackingCodeRepository;

    // ==================== CONVERTER ====================

    private ShipperTaskDTO toDTO(ShipperTask task) {
        ShipperTaskDTO dto = ShipperTaskDTO.builder()
                .id(task.getId())
                .shipperId(task.getShipper() != null ? task.getShipper().getId() : null)
                .shipperName(task.getShipper() != null && task.getShipper().getUser() != null
                        ? task.getShipper().getUser().getFullName()
                        : null)
                .requestId(task.getRequest() != null ? task.getRequest().getId() : null)
                .taskType(task.getTaskType() != null ? task.getTaskType().name() : null)
                .assignedAt(task.getAssignedAt())
                .completedAt(task.getCompletedAt())
                .taskStatus(task.getTaskStatus() != null ? task.getTaskStatus().name() : null)
                .resultNote(task.getResultNote())
                .build();

        // Get tracking code
        if (task.getRequest() != null) {
            trackingCodeRepository.findByRequest_Id(task.getRequest().getId())
                    .ifPresent(tc -> dto.setTrackingCode(tc.getCode()));
        }

        return dto;
    }

    private ShipperTask toEntity(ShipperTaskDTO dto) {
        ShipperTask task = new ShipperTask();

        if (dto.getShipperId() != null) {
            shipperRepository.findById(dto.getShipperId()).ifPresent(task::setShipper);
        }
        if (dto.getRequestId() != null) {
            customerRequestRepository.findById(dto.getRequestId()).ifPresent(task::setRequest);
        }
        if (dto.getTaskType() != null) {
            task.setTaskType(TaskType.valueOf(dto.getTaskType()));
        }

        task.setTaskStatus(TaskStatus.ASSIGNED);
        task.setAssignedAt(LocalDateTime.now());

        return task;
    }

    // ==================== PHÂN CÔNG TASK ====================

    @Override
    public ShipperTaskDTO assignTask(ShipperTaskDTO taskDTO) {
        ShipperTask task = toEntity(taskDTO);
        task = shipperTaskRepository.save(task);
        return toDTO(task);
    }

    @Override
    public ShipperTaskDTO updateTaskStatus(Long id, String taskStatus, String resultNote) {
        ShipperTask task = shipperTaskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ShipperTask not found: " + id));

        task.setTaskStatus(TaskStatus.valueOf(taskStatus));
        if (resultNote != null)
            task.setResultNote(resultNote);

        if (TaskStatus.valueOf(taskStatus) == TaskStatus.DONE ||
                TaskStatus.valueOf(taskStatus) == TaskStatus.FAILED) {
            task.setCompletedAt(LocalDateTime.now());
        }

        task = shipperTaskRepository.save(task);
        return toDTO(task);
    }

    @Override
    public ShipperTaskDTO completeTask(Long id, String resultNote) {
        return updateTaskStatus(id, TaskStatus.DONE.name(), resultNote);
    }

    @Override
    public ShipperTaskDTO failTask(Long id, String resultNote) {
        return updateTaskStatus(id, TaskStatus.FAILED.name(), resultNote);
    }

    // ==================== TÌM KIẾM ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipperTaskDTO> findById(Long id) {
        return shipperTaskRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ShipperTaskDTO> findLatestByRequestIdAndTaskType(Long requestId, String taskType) {
        TaskType type = TaskType.valueOf(taskType);
        return shipperTaskRepository.findTop1ByRequest_IdAndTaskTypeOrderByAssignedAtDesc(requestId, type)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByRequestIdAndTaskTypeAndStatus(Long requestId, String taskType, String taskStatus) {
        TaskType type = TaskType.valueOf(taskType);
        TaskStatus status = TaskStatus.valueOf(taskStatus);
        return shipperTaskRepository.existsByRequest_IdAndTaskTypeAndTaskStatus(requestId, type, status);
    }

    // ==================== DANH SÁCH ====================

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperTaskDTO> findAll(Pageable pageable) {
        return shipperTaskRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperTaskDTO> findAllByShipperIdAndStatus(Long shipperId, String taskStatus, Pageable pageable) {
        TaskStatus status = TaskStatus.valueOf(taskStatus);
        return shipperTaskRepository.findAllByShipper_IdAndTaskStatus(shipperId, status, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperTaskDTO> findAllByShipperId(Long shipperId, Pageable pageable) {
        return shipperTaskRepository.findAllByShipper_Id(shipperId, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShipperTaskDTO> findAllByRequestId(Long requestId) {
        return shipperTaskRepository.findAllByRequest_Id(requestId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShipperTaskDTO> findAllByTaskStatus(String taskStatus, Pageable pageable) {
        TaskStatus status = TaskStatus.valueOf(taskStatus);
        return shipperTaskRepository.findAllByTaskStatus(status, pageable).map(this::toDTO);
    }

    // ==================== THỐNG KÊ ====================

    @Override
    @Transactional(readOnly = true)
    public Long countByShipperIdAndStatus(Long shipperId, String taskStatus) {
        TaskStatus status = TaskStatus.valueOf(taskStatus);
        return shipperTaskRepository.countByShipper_IdAndTaskStatus(shipperId, status);
    }
}
