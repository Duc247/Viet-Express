package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.ActionTypeDTO;
import vn.DucBackend.Entities.ActionType;
import vn.DucBackend.Repositories.ActionTypeRepository;
import vn.DucBackend.Services.ActionTypeService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ ActionType (Loại thao tác)
 * 
 * Admin Controller sử dụng:
 * - AdminSystemController: CRUD ActionType
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ActionTypeServiceImpl implements ActionTypeService {

    private final ActionTypeRepository actionTypeRepository;

    @Override
    public List<ActionTypeDTO> findAll() {
        return actionTypeRepository.findAllOrderByCode().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ActionTypeDTO> findById(Long id) {
        return actionTypeRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<ActionTypeDTO> findByActionCode(String actionCode) {
        return actionTypeRepository.findByActionCode(actionCode).map(this::toDTO);
    }

    @Override
    public ActionTypeDTO create(ActionTypeDTO dto) {
        ActionType actionType = new ActionType();
        actionType.setActionCode(dto.getActionCode());
        actionType.setName(dto.getName());
        actionType.setDescription(dto.getDescription());
        return toDTO(actionTypeRepository.save(actionType));
    }

    @Override
    public ActionTypeDTO update(Long id, ActionTypeDTO dto) {
        ActionType actionType = actionTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ActionType not found"));

        if (dto.getActionCode() != null) {
            actionType.setActionCode(dto.getActionCode());
        }
        if (dto.getName() != null) {
            actionType.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            actionType.setDescription(dto.getDescription());
        }

        return toDTO(actionTypeRepository.save(actionType));
    }

    @Override
    public void delete(Long id) {
        actionTypeRepository.deleteById(id);
    }

    private ActionTypeDTO toDTO(ActionType actionType) {
        ActionTypeDTO dto = new ActionTypeDTO();
        dto.setId(actionType.getId());
        dto.setActionCode(actionType.getActionCode());
        dto.setName(actionType.getName());
        dto.setDescription(actionType.getDescription());
        dto.setCreatedAt(actionType.getCreatedAt());
        dto.setUpdatedAt(actionType.getUpdatedAt());
        return dto;
    }
}
