package vn.DucBackend.Services.Impl;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.DucBackend.DTO.CustomerDTO;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Services.CustomerService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Customer
 * Được sử dụng bởi: AdminPersonnelController (CRUD Customer)
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    /**
     * Lấy danh sách tất cả Customer
     * Sử dụng bởi: AdminPersonnelController.customerList() - GET /admin/customer
     */
    @Override
    public List<CustomerDTO> findAllCustomers() {
        return customerRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Tìm Customer theo ID
     * Sử dụng bởi: AdminPersonnelController.customerEditForm() - GET
     * /admin/customer/edit/{id}
     */
    @Override
    public Optional<CustomerDTO> findCustomerById(Long id) {
        return customerRepository.findById(id).map(this::toDTO);
    }

    @Override
    public Optional<CustomerDTO> findByUserId(Long userId) {
        return customerRepository.findByUserId(userId).map(this::toDTO);
    }

    @Override
    public Optional<CustomerDTO> findByPhone(String phone) {
        return customerRepository.findByPhone(phone).map(this::toDTO);
    }

    @Override
    public Optional<CustomerDTO> findByEmail(String email) {
        return customerRepository.findByEmail(email).map(this::toDTO);
    }

    @Override
    public List<CustomerDTO> findBusinessCustomers() {
        return customerRepository.findBusinessCustomers().stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Tìm kiếm Customer theo keyword (name, phone, email)
     * Sử dụng bởi: AdminPersonnelController.customerSearch() - GET
     * /admin/customer/search
     */
    @Override
    public List<CustomerDTO> searchCustomers(String keyword) {
        return customerRepository.searchByKeyword(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Tạo Customer mới
     * Sử dụng bởi: AdminPersonnelController.customerCreate() - POST
     * /admin/customer/create
     */
    @Override
    public CustomerDTO createCustomer(CustomerDTO dto) {
        Customer customer = new Customer();
        if (dto.getUserId() != null) {
            customer.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        }
        customer.setName(dto.getName());
        customer.setPhone(dto.getPhone());
        customer.setEmail(dto.getEmail());
        customer.setCompanyName(dto.getCompanyName());
        customer.setGender(dto.getGender());
        customer.setAddress(dto.getAddress());
        customer.setBirthday(dto.getBirthday());
        customer.setCreatedAt(dto.getCreatedAt());
        customer.setUpdatedAt(dto.getUpdatedAt());
        return toDTO(customerRepository.save(customer));
    }

    /**
     * Cập nhật thông tin Customer
     * Sử dụng bởi: AdminPersonnelController.customerUpdate() - POST
     * /admin/customer/edit/{id}
     */
    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        if (dto.getName() != null)
            customer.setName(dto.getName());
        if (dto.getPhone() != null)
            customer.setPhone(dto.getPhone());
        if (dto.getEmail() != null)
            customer.setEmail(dto.getEmail());
        if (dto.getCompanyName() != null)
            customer.setCompanyName(dto.getCompanyName());
        if (dto.getUserId() != null) {
            customer.setUser(userRepository.findById(dto.getUserId()).orElse(null));
        }
        if (dto.getGender() != null) {
            customer.setGender(dto.getGender());
        }
        if (dto.getAddress() != null) {
            customer.setAddress(dto.getAddress());
        }
        if (dto.getBirthday() != null) {
            customer.setBirthday(dto.getBirthday());
        }
        customer.setUpdatedAt(dto.getUpdatedAt());
        return toDTO(customerRepository.save(customer));
    }

    /**
     * Xóa Customer theo ID
     * Sử dụng bởi: AdminPersonnelController.customerDelete() - POST
     * /admin/customer/delete/{id}
     */
    @Override
    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    private CustomerDTO toDTO(Customer customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setId(customer.getId());
        if (customer.getUser() != null) {
            dto.setUserId(customer.getUser().getId());
            dto.setUsername(customer.getUser().getUsername());
        }
        dto.setName(customer.getName());
        dto.setPhone(customer.getPhone());
        dto.setEmail(customer.getEmail());
        dto.setCompanyName(customer.getCompanyName());
        dto.setCreatedAt(customer.getCreatedAt());
        dto.setUpdatedAt(customer.getUpdatedAt());
        return dto;
    }
}
