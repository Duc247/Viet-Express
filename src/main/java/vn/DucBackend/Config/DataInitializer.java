package vn.DucBackend.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.DucBackend.Entities.Role;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Entities.Shipper;
import vn.DucBackend.Repositories.RoleRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.StaffRepository;
import vn.DucBackend.Repositories.ShipperRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Khởi tạo dữ liệu mặc định khi ứng dụng khởi động
 * - Roles
 * - Default users với BCrypt password
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CustomerRepository customerRepository;
    private final StaffRepository staffRepository;
    private final ShipperRepository shipperRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initDefaultUsers();
    }

    /**
     * Khởi tạo các role mặc định nếu chưa tồn tại
     */
    private void initRoles() {
        String[] defaultRoles = { "ADMIN", "MANAGER", "STAFF", "CUSTOMER", "SHIPPER" };

        for (String roleName : defaultRoles) {
            if (!roleRepository.existsByRoleName(roleName)) {
                Role role = new Role();
                role.setRoleName(roleName);
                role.setDescription(getDescriptionForRole(roleName));
                role.setIsActive(true);
                roleRepository.save(role);
                log.info("Đã tạo role: {}", roleName);
            }
        }
    }

    /**
     * Khởi tạo users mặc định với password đã hash
     */
    private void initDefaultUsers() {
        // Tạo Admin nếu chưa tồn tại
        if (!userRepository.existsByUsername("admin")) {
            Role adminRole = roleRepository.findByRoleName("ADMIN").orElse(null);
            if (adminRole != null) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setEmail("admin@vietexpress.com");
                admin.setPhone("0900000001");
                admin.setPassword(passwordEncoder.encode("admin123")); // Password: admin123
                admin.setRole(adminRole);
                admin.setIsActive(true);
                userRepository.save(admin);
                log.info("Đã tạo user admin (password: admin123)");
            }
        }

        // Tạo Manager nếu chưa tồn tại
        if (!userRepository.existsByUsername("manager")) {
            Role managerRole = roleRepository.findByRoleName("MANAGER").orElse(null);
            if (managerRole != null) {
                User manager = new User();
                manager.setUsername("manager");
                manager.setEmail("manager@vietexpress.com");
                manager.setPhone("0900000002");
                manager.setPassword(passwordEncoder.encode("manager123")); // Password: manager123
                manager.setRole(managerRole);
                manager.setIsActive(true);
                userRepository.save(manager);
                log.info("Đã tạo user manager (password: manager123)");
            }
        }

        // Tạo Staff nếu chưa tồn tại
        if (!userRepository.existsByUsername("staff")) {
            Role staffRole = roleRepository.findByRoleName("STAFF").orElse(null);
            if (staffRole != null) {
                User staffUser = new User();
                staffUser.setUsername("staff");
                staffUser.setEmail("staff@vietexpress.com");
                staffUser.setPhone("0900000003");
                staffUser.setPassword(passwordEncoder.encode("staff123")); // Password: staff123
                staffUser.setRole(staffRole);
                staffUser.setIsActive(true);
                userRepository.save(staffUser);

                // Tạo Staff profile
                Staff staff = new Staff();
                staff.setUser(staffUser);
                staff.setFullName("Nhân viên Kho");
                staff.setPhone("0901234567");
                staff.setIsActive(true);
                staffRepository.save(staff);
                log.info("Đã tạo user staff (password: staff123)");
            }
        }

        // Tạo Customer nếu chưa tồn tại
        if (!userRepository.existsByUsername("customer")) {
            Role customerRole = roleRepository.findByRoleName("CUSTOMER").orElse(null);
            if (customerRole != null) {
                User customerUser = new User();
                customerUser.setUsername("customer");
                customerUser.setEmail("customer@gmail.com");
                customerUser.setPhone("0900000004");
                customerUser.setPassword(passwordEncoder.encode("customer123")); // Password: customer123
                customerUser.setRole(customerRole);
                customerUser.setIsActive(true);
                userRepository.save(customerUser);

                // Tạo Customer profile
                Customer customer = new Customer();
                customer.setUser(customerUser);
                customer.setName("Khách hàng Demo");
                customer.setFullName("Khách hàng Demo");
                customer.setPhone("0909123456");
                customer.setAddress("123 Đường ABC, Quận 1, TP.HCM");
                customerRepository.save(customer);
                log.info("Đã tạo user customer (password: customer123)");
            }
        }

        // Tạo Shipper nếu chưa tồn tại
        if (!userRepository.existsByUsername("shipper")) {
            Role shipperRole = roleRepository.findByRoleName("SHIPPER").orElse(null);
            if (shipperRole != null) {
                User shipperUser = new User();
                shipperUser.setUsername("shipper");
                shipperUser.setEmail("shipper@vietexpress.com");
                shipperUser.setPhone("0900000005");
                shipperUser.setPassword(passwordEncoder.encode("shipper123")); // Password: shipper123
                shipperUser.setRole(shipperRole);
                shipperUser.setIsActive(true);
                userRepository.save(shipperUser);

                // Tạo Shipper profile
                Shipper shipper = new Shipper();
                shipper.setUser(shipperUser);
                shipper.setFullName("Tài xế Demo");
                shipper.setPhone("0912345678");
                shipper.setIsAvailable(true);
                shipper.setIsActive(true);
                shipperRepository.save(shipper);
                log.info("Đã tạo user shipper (password: shipper123)");
            }
        }
    }

    private String getDescriptionForRole(String roleName) {
        switch (roleName) {
            case "ADMIN":
                return "Quản trị viên hệ thống";
            case "MANAGER":
                return "Quản lý";
            case "STAFF":
                return "Nhân viên kho";
            case "CUSTOMER":
                return "Khách hàng";
            case "SHIPPER":
                return "Nhân viên giao hàng";
            default:
                return roleName;
        }
    }
}
