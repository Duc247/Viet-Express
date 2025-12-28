package vn.DucBackend.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO cho TripItem entity
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripItemDTO {
    private Long id;
    private Long tripId;
    private Long requestId;
    private String trackingCode;
    private String status; // TripItemStatus: LOADED, IN_TRANSIT, UNLOADED
}
