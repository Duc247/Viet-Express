package vn.DucBackend.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.DucBackend.Entities.Role;
import vn.DucBackend.Repositories.RoleRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Khởi tạo dữ liệu mặc định khi ứng dụng khởi động
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
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
