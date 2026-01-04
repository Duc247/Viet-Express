package vn.DucBackend.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParcelActionDTO {
    private Long id;
    private Long parcelId;
    private String parcelCode;
    private Long requestId;
    private String requestCode;
    private Long actionTypeId;
    private String actionTypeName;
    private Long fromLocationId;
    private String fromLocationName;
    private Long toLocationId;
    private String toLocationName;
    private Long actorUserId;
    private String actorUserName;
    private String note;
    private LocalDateTime createdAt;
}
