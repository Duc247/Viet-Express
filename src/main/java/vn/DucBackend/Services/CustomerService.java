package vn.DucBackend.Services;

import vn.DucBackend.DTO.CustomerDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<CustomerDTO> findAllCustomers();

    Optional<CustomerDTO> findCustomerById(Long id);

    Optional<CustomerDTO> findByUserId(Long userId);

    Optional<CustomerDTO> findByPhone(String phone);

    Optional<CustomerDTO> findByEmail(String email);

    List<CustomerDTO> findBusinessCustomers();

    List<CustomerDTO> searchCustomers(String keyword);

    CustomerDTO createCustomer(CustomerDTO dto);

    CustomerDTO updateCustomer(Long id, CustomerDTO dto);

    void deleteCustomer(Long id);
}
