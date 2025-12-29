package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.ActionType;

import java.util.List;
import java.util.Optional;

@Repository
public interface ActionTypeRepository extends JpaRepository<ActionType, Long> {

    Optional<ActionType> findByActionCode(String actionCode);

    @Query("SELECT a FROM ActionType a ORDER BY a.actionCode")
    List<ActionType> findAllOrderByCode();

    boolean existsByActionCode(String actionCode);
}