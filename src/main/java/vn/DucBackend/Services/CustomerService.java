package vn.DucBackend.Services;

import vn.DucBackend.DTO.CustomerDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service interface quản lý Customer (Khách hàng)
 * 
 * Repository sử dụng: CustomerRepository, UserRepository
 * Controller sử dụng: AdminPersonnelController
 */
public interface CustomerService {

    /** Repository: customerRepository.findAll() */
    List<CustomerDTO> findAllCustomers();

    /** Repository: customerRepository.findById() */
    Optional<CustomerDTO> findCustomerById(Long id);

    /** Repository: customerRepository.findByUserId() */
    Optional<CustomerDTO> findByUserId(Long userId);

    /** Repository: customerRepository.findByPhone() */
    Optional<CustomerDTO> findByPhone(String phone);

    /** Repository: customerRepository.findByEmail() */
    Optional<CustomerDTO> findByEmail(String email);

    /** Repository: customerRepository.findBusinessCustomers() */
    List<CustomerDTO> findBusinessCustomers();

    /** Repository: customerRepository.searchByKeyword() */
    List<CustomerDTO> searchCustomers(String keyword);

    /** Repository: customerRepository.save(), userRepository.findById() */
    CustomerDTO createCustomer(CustomerDTO dto);

    /**
     * Repository: customerRepository.findById(), customerRepository.save(),
     * userRepository.findById()
     */
    CustomerDTO updateCustomer(Long id, CustomerDTO dto);

    /** Repository: customerRepository.deleteById() */
    void deleteCustomer(Long id);
}
