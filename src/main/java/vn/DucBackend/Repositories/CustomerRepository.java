package vn.DucBackend.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByUserId(Long userId);

    Optional<Customer> findByCustomerCode(String customerCode);

    Optional<Customer> findByPhone(String phone);

    Optional<Customer> findByEmail(String email);

    @Query("SELECT c FROM Customer c WHERE c.name LIKE %:keyword% OR c.phone LIKE %:keyword% OR c.email LIKE %:keyword%")
    List<Customer> searchByKeyword(@Param("keyword") String keyword);

    @Query("SELECT c FROM Customer c WHERE c.companyName IS NOT NULL")
    List<Customer> findBusinessCustomers();

    boolean existsByPhone(String phone);

    boolean existsByEmail(String email);

    // INSERT: JPQL không hỗ trợ INSERT, sử dụng method save() từ JpaRepository
    // Ví dụ: customerRepository.save(new Customer(...));

    // DELETE customer - sử dụng 'DELETE' thay vì 'remove'
    @Modifying
    @Query("DELETE FROM Customer c WHERE c.user = :user")
    int deleteByUser(@Param("user") User user);

    // UPDATE customer - cần @Modifying annotation
    @Modifying
    @Query("UPDATE Customer c SET c.name = :name, c.fullName = :fullName, c.phone = :phone, c.email = :email, c.address = :address, c.companyName = :companyName, c.birthday = :birthday, c.gender = :gender, c.updatedAt = :updatedAt WHERE c.user = :user")
    int updateCustomer(@Param("user") User user, @Param("name") String name, @Param("fullName") String fullName,
            @Param("phone") String phone, @Param("email") String email, @Param("address") String address,
            @Param("companyName") String companyName, @Param("birthday") LocalDateTime birthday,
            @Param("gender") String gender, @Param("updatedAt") LocalDateTime updatedAt);
}