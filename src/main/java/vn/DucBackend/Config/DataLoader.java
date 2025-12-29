package vn.DucBackend.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import vn.DucBackend.Entities.Role;
import vn.DucBackend.Repositories.RoleRepository;

/**
 * DataLoader - Tự động tạo 4 vai trò cố định khi ứng dụng khởi động
 * 
 * CommandLineRunner: Interface của Spring Boot, method run() sẽ được gọi
 * ngay sau khi ứng dụng khởi động hoàn tất.
 */
@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Chỉ tạo roles nếu chưa có trong database
        if (roleRepository.count() == 0) {
            createRole("ADMIN", "Quản trị viên hệ thống - Toàn quyền quản lý");
            createRole("MANAGER", "Quản lý - Quản lý vận hành và nhân sự");
            createRole("STAFF", "Nhân viên - Xử lý đơn hàng, hỗ trợ khách hàng");
            createRole("CUSTOMER", "Khách hàng - Tạo và theo dõi đơn hàng");
            System.out.println("======================================");
            System.out.println("✅ Đã tạo 4 roles mặc định:");
            System.out.println("   - ADMIN");
            System.out.println("   - MANAGER");
            System.out.println("   - STAFF");
            System.out.println("   - CUSTOMER");
            System.out.println("======================================");
        }
    }

    private void createRole(String roleName, String description) {
        Role role = new Role();
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setIsActive(true);
        roleRepository.save(role);
    }
}
