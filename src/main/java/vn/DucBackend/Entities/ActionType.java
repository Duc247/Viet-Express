 package vn.DucBackend.Entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "action_types")
@Getter
@Setter
public class ActionType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_type_id")
    private Long id;

    @Column(name = "action_code", unique = true, nullable = false)
    private String actionCode;

    @Column(name = "action_name", nullable = false)
    private String actionName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
}
