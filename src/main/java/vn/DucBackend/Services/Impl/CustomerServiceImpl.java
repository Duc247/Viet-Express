package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.CustomerDTO;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.CustomerService;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Implementation của CustomerService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    // ==================== CONVERTER ====================

    private CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .userId(customer.getUser() != null ? customer.getUser().getId() : null)
                .customerType(customer.getCustomerType() != null ? customer.getCustomerType().name() : null)
                .businessName(customer.getBusinessName())
                .taxCode(customer.getTaxCode())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .build();
    }

    private Customer toEntity(CustomerDTO dto) {
        Customer customer = new Customer();
        if (dto.getUserId() != null) {
            userRepository.findById(dto.getUserId()).ifPresent(customer::setUser);
        }
        if (dto.getCustomerType() != null) {
            customer.setCustomerType(Customer.CustomerType.valueOf(dto.getCustomerType()));
        }
        customer.setBusinessName(dto.getBusinessName());
        customer.setTaxCode(dto.getTaxCode());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        customer.setCreatedAt(LocalDateTime.now());
        return customer;
    }

    // ==================== TẠO / CẬP NHẬT ====================

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = toEntity(customerDTO);
        customer = customerRepository.save(customer);
        return toDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));

        if (customerDTO.getCustomerType() != null) {
            customer.setCustomerType(Customer.CustomerType.valueOf(customerDTO.getCustomerType()));
        }
        if (customerDTO.getBusinessName() != null)
            customer.setBusinessName(customerDTO.getBusinessName());
        if (customerDTO.getTaxCode() != null)
            customer.setTaxCode(customerDTO.getTaxCode());
        if (customerDTO.getEmail() != null)
            customer.setEmail(customerDTO.getEmail());
        if (customerDTO.getPhone() != null)
            customer.setPhone(customerDTO.getPhone());

        customer = customerRepository.save(customer);
        return toDTO(customer);
    }

    @Override
    public CustomerDTO changeStatus(Long id, Boolean status) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + id));
        customer.setStatus(status);
        customer = customerRepository.save(customer);
        return toDTO(customer);
    }

    // ==================== TÌM KIẾM ====================

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findById(Long id) {
        return customerRepository.findById(id).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CustomerDTO> findByUserId(Long userId) {
        return customerRepository.findByUser_Id(userId).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(Long userId) {
        return customerRepository.existsByUser_Id(userId);
    }

    // ==================== DANH SÁCH ====================

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAll(Pageable pageable) {
        return customerRepository.findAll(pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllByCustomerType(String customerType, Pageable pageable) {
        Customer.CustomerType type = Customer.CustomerType.valueOf(customerType);
        return customerRepository.findAllByCustomerType(type, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> findAllByStatus(Boolean status, Pageable pageable) {
        return customerRepository.findAllByStatus(status, pageable).map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CustomerDTO> searchByBusinessName(String keyword, Pageable pageable) {
        return customerRepository.findAllByBusinessNameContainingIgnoreCase(keyword, pageable).map(this::toDTO);
    }
}
