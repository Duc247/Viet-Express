package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Customer;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:keyword% OR c.phone LIKE %:keyword% OR c.email LIKE %:keyword%")
    List<Customer> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM Customer c WHERE c.companyName IS NOT NULL")
    List<Customer> findBusinessCustomers();

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);
}