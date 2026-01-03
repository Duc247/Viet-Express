package vn.DucBackend.Services;

import vn.DucBackend.DTO.ActionTypeDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý ActionType (Loại thao tác)
 * 
 * Repository sử dụng: ActionTypeRepository
 * Controller sử dụng: AdminSystemController
 */
public interface ActionTypeService {

    /** Repository: actionTypeRepository.findAllOrderByCode() */
    List<ActionTypeDTO> findAll();

    /** Repository: actionTypeRepository.findById() */
    Optional<ActionTypeDTO> findById(Long id);

    /** Repository: actionTypeRepository.findByActionCode() */
    Optional<ActionTypeDTO> findByActionCode(String actionCode);

    /** Repository: actionTypeRepository.save() */
    ActionTypeDTO create(ActionTypeDTO dto);

    /** Repository: actionTypeRepository.findById(), actionTypeRepository.save() */
    ActionTypeDTO update(Long id, ActionTypeDTO dto);

    /** Repository: actionTypeRepository.deleteById() */
    void delete(Long id);
}
