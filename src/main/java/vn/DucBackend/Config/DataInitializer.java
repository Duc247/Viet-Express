package vn.DucBackend.Config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import vn.DucBackend.Entities.Role;
import vn.DucBackend.Entities.User;
import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Entities.Staff;
import vn.DucBackend.Entities.Shipper;
import vn.DucBackend.Entities.ActionType;
import vn.DucBackend.Repositories.RoleRepository;
import vn.DucBackend.Repositories.UserRepository;
import vn.DucBackend.Repositories.CustomerRepository;
import vn.DucBackend.Repositories.StaffRepository;
import vn.DucBackend.Repositories.ShipperRepository;
import vn.DucBackend.Repositories.ActionTypeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Khởi tạo dữ liệu mặc định khi ứng dụng khởi động
 * - Roles
 * - Default users với BCrypt password
 * - Action Types cho System Log
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
    private final ActionTypeRepository actionTypeRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initRoles();
        initDefaultUsers();
        initActionTypes();
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

    /**
     * Khởi tạo các loại thao tác (Action Types) cho System Log
     */
    private void initActionTypes() {
        // Định nghĩa các loại thao tác
        String[][] actionTypes = {
                // Authentication
                { "USER_LOGIN", "Đăng nhập", "Người dùng đăng nhập vào hệ thống" },
                { "USER_LOGOUT", "Đăng xuất", "Người dùng đăng xuất khỏi hệ thống" },
                { "USER_LOGIN_FAILED", "Đăng nhập thất bại", "Đăng nhập không thành công" },
                { "PASSWORD_CHANGE", "Đổi mật khẩu", "Người dùng thay đổi mật khẩu" },
                { "PASSWORD_RESET", "Reset mật khẩu", "Yêu cầu đặt lại mật khẩu" },

                // User Management
                { "USER_CREATE", "Tạo tài khoản", "Tạo tài khoản người dùng mới" },
                { "USER_UPDATE", "Cập nhật tài khoản", "Cập nhật thông tin tài khoản" },
                { "USER_DELETE", "Xóa tài khoản", "Xóa tài khoản người dùng" },
                { "USER_ACTIVATE", "Kích hoạt tài khoản", "Kích hoạt tài khoản người dùng" },
                { "USER_DEACTIVATE", "Vô hiệu hóa tài khoản", "Vô hiệu hóa tài khoản người dùng" },

                // Customer Management
                { "CUSTOMER_CREATE", "Tạo khách hàng", "Tạo khách hàng mới" },
                { "CUSTOMER_UPDATE", "Cập nhật khách hàng", "Cập nhật thông tin khách hàng" },
                { "CUSTOMER_DELETE", "Xóa khách hàng", "Xóa khách hàng" },

                // Staff Management
                { "STAFF_CREATE", "Tạo nhân viên", "Tạo nhân viên kho mới" },
                { "STAFF_UPDATE", "Cập nhật nhân viên", "Cập nhật thông tin nhân viên" },
                { "STAFF_DELETE", "Xóa nhân viên", "Xóa nhân viên" },

                // Shipper Management
                { "SHIPPER_CREATE", "Tạo shipper", "Tạo nhân viên giao hàng mới" },
                { "SHIPPER_UPDATE", "Cập nhật shipper", "Cập nhật thông tin shipper" },
                { "SHIPPER_DELETE", "Xóa shipper", "Xóa nhân viên giao hàng" },
                { "SHIPPER_ASSIGN", "Phân công shipper", "Phân công shipper cho đơn hàng" },

                // Request Management
                { "REQUEST_CREATE", "Tạo yêu cầu", "Tạo yêu cầu vận chuyển mới" },
                { "REQUEST_UPDATE", "Cập nhật yêu cầu", "Cập nhật thông tin yêu cầu" },
                { "REQUEST_APPROVE", "Duyệt yêu cầu", "Duyệt yêu cầu vận chuyển" },
                { "REQUEST_REJECT", "Từ chối yêu cầu", "Từ chối yêu cầu vận chuyển" },
                { "REQUEST_CANCEL", "Hủy yêu cầu", "Hủy yêu cầu vận chuyển" },
                { "REQUEST_COMPLETE", "Hoàn thành yêu cầu", "Đánh dấu yêu cầu hoàn thành" },

                // Parcel Management
                { "PARCEL_CREATE", "Tạo kiện hàng", "Tạo kiện hàng mới" },
                { "PARCEL_UPDATE", "Cập nhật kiện hàng", "Cập nhật thông tin kiện hàng" },
                { "PARCEL_PICKUP", "Lấy hàng", "Nhận kiện hàng từ người gửi" },
                { "PARCEL_DELIVER", "Giao hàng", "Giao kiện hàng cho người nhận" },
                { "PARCEL_TRANSFER", "Chuyển kho", "Chuyển kiện hàng giữa các kho" },
                { "PARCEL_RETURN", "Trả hàng", "Trả lại kiện hàng cho người gửi" },

                // Tracking Status (for public tracking)
                { "CREATED", "Tạo đơn", "Đơn hàng được tạo mới" },
                { "RECEIVER_CONFIRMED", "Người nhận xác nhận", "Người nhận đã xác nhận đơn hàng" },
                { "CONFIRMED", "Chốt đơn", "Manager đã xác nhận và phân công đơn hàng" },
                { "PICKED_UP", "Đã lấy hàng", "Shipper đã lấy hàng từ người gửi" },
                { "IN_WAREHOUSE", "Nhập kho", "Hàng đã về kho" },
                { "IN_TRANSIT", "Đang vận chuyển", "Hàng đang trên đường vận chuyển" },
                { "OUT_FOR_DELIVERY", "Đang giao", "Shipper đang giao hàng" },
                { "DELIVERED", "Đã giao", "Giao hàng thành công" },
                { "FAILED", "Giao thất bại", "Giao hàng không thành công" },
                { "RETURNED", "Hoàn hàng", "Hàng đã hoàn trả về người gửi" },
                { "CANCELLED", "Đã hủy", "Đơn hàng đã bị hủy" },

                // Trip Management
                { "TRIP_CREATE", "Tạo chuyến đi", "Tạo chuyến vận chuyển mới" },
                { "TRIP_START", "Bắt đầu chuyến đi", "Bắt đầu chuyến vận chuyển" },
                { "TRIP_COMPLETE", "Hoàn thành chuyến đi", "Hoàn thành chuyến vận chuyển" },
                { "TRIP_CANCEL", "Hủy chuyến đi", "Hủy chuyến vận chuyển" },

                // Payment Management
                { "PAYMENT_CREATE", "Tạo thanh toán", "Tạo yêu cầu thanh toán" },
                { "PAYMENT_PROCESS", "Xử lý thanh toán", "Xử lý giao dịch thanh toán" },
                { "PAYMENT_COMPLETE", "Hoàn thành thanh toán", "Thanh toán thành công" },
                { "PAYMENT_REFUND", "Hoàn tiền", "Hoàn tiền cho khách hàng" },

                // Vehicle Management
                { "VEHICLE_CREATE", "Tạo phương tiện", "Thêm phương tiện mới" },
                { "VEHICLE_UPDATE", "Cập nhật phương tiện", "Cập nhật thông tin phương tiện" },
                { "VEHICLE_DELETE", "Xóa phương tiện", "Xóa phương tiện" },
                { "VEHICLE_MAINTENANCE", "Bảo trì phương tiện", "Đưa phương tiện vào bảo trì" },

                // Location Management
                { "LOCATION_CREATE", "Tạo địa điểm", "Tạo địa điểm/kho mới" },
                { "LOCATION_UPDATE", "Cập nhật địa điểm", "Cập nhật thông tin địa điểm" },
                { "LOCATION_DELETE", "Xóa địa điểm", "Xóa địa điểm" },

                // Route Management
                { "ROUTE_CREATE", "Tạo tuyến đường", "Tạo tuyến đường mới" },
                { "ROUTE_UPDATE", "Cập nhật tuyến đường", "Cập nhật thông tin tuyến đường" },
                { "ROUTE_DELETE", "Xóa tuyến đường", "Xóa tuyến đường" },

                // Service Type Management
                { "SERVICE_CREATE", "Tạo dịch vụ", "Tạo loại dịch vụ mới" },
                { "SERVICE_UPDATE", "Cập nhật dịch vụ", "Cập nhật thông tin dịch vụ" },
                { "SERVICE_DELETE", "Xóa dịch vụ", "Xóa loại dịch vụ" },

                // System Configuration
                { "CONFIG_UPDATE", "Cập nhật cấu hình", "Thay đổi cấu hình hệ thống" },
                { "SYSTEM_BACKUP", "Sao lưu hệ thống", "Tạo bản sao lưu dữ liệu" },
                { "SYSTEM_RESTORE", "Khôi phục hệ thống", "Khôi phục dữ liệu từ bản sao lưu" },

                // Report & Export
                { "REPORT_GENERATE", "Tạo báo cáo", "Tạo báo cáo thống kê" },
                { "DATA_EXPORT", "Xuất dữ liệu", "Xuất dữ liệu ra file" },
                { "DATA_IMPORT", "Nhập dữ liệu", "Nhập dữ liệu từ file" },

                // Role & Permission
                { "ROLE_CREATE", "Tạo vai trò", "Tạo vai trò mới" },
                { "ROLE_UPDATE", "Cập nhật vai trò", "Cập nhật thông tin vai trò" },
                { "ROLE_DELETE", "Xóa vai trò", "Xóa vai trò" },

                // Other Actions
                { "VIEW_DATA", "Xem dữ liệu", "Xem thông tin dữ liệu" },
                { "SEARCH_DATA", "Tìm kiếm", "Tìm kiếm dữ liệu" },
                { "PRINT_DATA", "In dữ liệu", "In thông tin" }
        };

        int count = 0;
        for (String[] actionType : actionTypes) {
            if (!actionTypeRepository.existsByActionCode(actionType[0])) {
                ActionType at = new ActionType();
                at.setActionCode(actionType[0]);
                at.setName(actionType[1]);
                at.setDescription(actionType[2]);
                actionTypeRepository.save(at);
                count++;
            }
        }

        if (count > 0) {
            log.info("Đã tạo {} loại thao tác (Action Types)", count);
        }
    }
}
