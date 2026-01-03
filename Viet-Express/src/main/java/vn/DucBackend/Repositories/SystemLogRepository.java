package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.SystemLog;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SystemLogRepository extends JpaRepository<SystemLog, Long> {

        List<SystemLog> findByLogLevel(SystemLog.LogLevel logLevel);

        List<SystemLog> findByModuleName(String moduleName);

        List<SystemLog> findByActorId(Long actorId);

        List<SystemLog> findByActionType(String actionType);

        @Query("SELECT sl FROM SystemLog sl WHERE sl.createdAt BETWEEN :startDate AND :endDate ORDER BY sl.createdAt DESC")
        List<SystemLog> findByDateRange(@Param("startDate") LocalDateTime startDate,
                        @Param("endDate") LocalDateTime endDate);

        @Query("SELECT sl FROM SystemLog sl WHERE sl.actor.id = :actorId ORDER BY sl.createdAt DESC")
        List<SystemLog> findByActorIdOrderByCreatedAtDesc(@Param("actorId") Long actorId);

        @Query("SELECT sl FROM SystemLog sl WHERE sl.targetId = :targetId ORDER BY sl.createdAt DESC")
        List<SystemLog> findByTargetIdOrderByCreatedAtDesc(@Param("targetId") String targetId);

        @Query("SELECT sl FROM SystemLog sl WHERE sl.logLevel = 'ERROR' ORDER BY sl.createdAt DESC")
        List<SystemLog> findErrorLogs();

        @Query("SELECT sl FROM SystemLog sl WHERE sl.logLevel = 'WARN' ORDER BY sl.createdAt DESC")
        List<SystemLog> findWarningLogs();

        @Query("SELECT sl FROM SystemLog sl WHERE sl.ipAddress = :ip ORDER BY sl.createdAt DESC")
        List<SystemLog> findByIpAddress(@Param("ip") String ipAddress);
}
