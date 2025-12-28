package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho ActionType entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionTypeDTO {
    private Long id;
    private String actionCode;
    private String actionName;
    private String description;
}
