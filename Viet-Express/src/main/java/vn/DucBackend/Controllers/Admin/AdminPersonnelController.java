package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.*;
import vn.DucBackend.Services.*;

/**
 * Admin Personnel Controller - Quản lý nhân sự (User, Customer, Shipper, Staff)
 * Kết nối Controller + Service + View
 * 
 * Services sử dụng:
 * - UserService: CRUD và quản lý tài khoản User
 * - CustomerService: CRUD và quản lý khách hàng
 * - ShipperService: CRUD và quản lý shipper
 * - StaffService: CRUD và quản lý nhân viên
 * - RoleService: Lấy danh sách vai trò
 * - LocationService: Lấy danh sách địa điểm
 */
@Controller
@RequestMapping("/admin")
public class AdminPersonnelController {

    @Autowired
    private UserService userService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private ShipperService shipperService;
    @Autowired
    private StaffService staffService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private LocationService locationService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // USER - CRUD
    // ==========================================

    /**
     * Danh sách User
     * Service: userService.findAllUsers(), roleService.findAllRoles()
     */
    @GetMapping("/user")
    public String userList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("roles", roleService.findAllRoles());
        return "admin/user/list";
    }

    /**
     * Tìm kiếm User
     * Service: userService.findUsersByRole(), userService.searchUsers(),
     * roleService.findAllRoles()
     */
    @GetMapping("/user/search")
    public String searchUser(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) String roleName,
            Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        if (roleName != null && !roleName.isEmpty()) {
            model.addAttribute("users", userService.findUsersByRole(roleName));
            model.addAttribute("selectedRole", roleName);
        } else if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("users", userService.searchUsers(keyword));
            model.addAttribute("keyword", keyword);
        } else {
            model.addAttribute("users", userService.findAllUsers());
        }

        model.addAttribute("roles", roleService.findAllRoles());
        return "admin/user/list";
    }

    /**
     * Form tạo mới User
     * Service: roleService.findAllRoles(), locationService.findAllLocations()
     */
    @GetMapping("/user/create")
    public String userCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("user", new UserDTO());
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", false);
        return "admin/user/form";
    }

    /**
     * Xử lý tạo mới User
     * Service: userService.createUser()
     */
    @PostMapping("/user/create")
    public String createUser(@ModelAttribute UserDTO dto, RedirectAttributes redirectAttributes) {
        try {
            userService.createUser(dto);

            redirectAttributes.addFlashAttribute("success", "Tạo user thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    /**
     * Form chỉnh sửa User
     * Service: userService.findUserById(), roleService.findAllRoles(),
     * locationService.findAllLocations()
     */
    @GetMapping("/user/edit/{id}")
    public String userEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("user", userService.findUserById(id).orElse(null));
        model.addAttribute("roles", roleService.findAllRoles());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", true);
        return "admin/user/form";
    }

    /**
     * Xử lý cập nhật User
     * Service: userService.updateUser()
     */
    @PostMapping("/user/edit/{id}")
    public String updateUser(@PathVariable Long id, @ModelAttribute UserDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            userService.updateUser(id, dto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật user thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    /**
     * Xoá User
     * Service: userService.deleteUser()
     */
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "Xoá user thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    /**
     * Toggle trạng thái User
     * Service: userService.toggleUserStatus()
     */
    @GetMapping("/user/toggle/{id}")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái user!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/user";
    }

    // ==========================================
    // CUSTOMER - CRUD
    // ==========================================

    /**
     * Danh sách Customer
     * Service: customerService.findAllCustomers()
     */
    @GetMapping("/customer")
    public String customerList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customers", customerService.findAllCustomers());
        return "admin/customer/list";
    }

    /**
     * Tìm kiếm Customer
     * Service: customerService.searchCustomers()
     */
    @GetMapping("/customer/search")
    public String searchCustomer(@RequestParam String keyword, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customers", customerService.searchCustomers(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/customer/list";
    }

    /**
     * Form tạo mới Customer
     * Service: userService.findAllUsers()
     */
    @GetMapping("/customer/create")
    public String customerCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customer", new CustomerDTO());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("isEdit", false);
        return "admin/customer/form";
    }

    /**
     * Xử lý tạo mới Customer
     * Service: customerService.createCustomer()
     */
    @PostMapping("/customer/create")
    public String createCustomer(@ModelAttribute CustomerDTO dto, RedirectAttributes redirectAttributes) {
        try {
            customerService.createCustomer(dto);
            redirectAttributes.addFlashAttribute("success", "Tạo khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/customer";
    }

    /**
     * Form chỉnh sửa Customer
     * Service: customerService.findCustomerById(), userService.findAllUsers()
     */
    @GetMapping("/customer/edit/{id}")
    public String customerEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customer", customerService.findCustomerById(id).orElse(null));
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("isEdit", true);
        return "admin/customer/form";
    }

    /**
     * Xử lý cập nhật Customer
     * Service: customerService.updateCustomer()
     */
    @PostMapping("/customer/edit/{id}")
    public String updateCustomer(@PathVariable Long id, @ModelAttribute CustomerDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            customerService.updateCustomer(id, dto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/customer";
    }

    /**
     * Xoá Customer
     * Service: customerService.deleteCustomer()
     */
    @GetMapping("/customer/delete/{id}")
    public String deleteCustomer(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            customerService.deleteCustomer(id);
            redirectAttributes.addFlashAttribute("success", "Xoá khách hàng thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/customer";
    }

    // ==========================================
    // SHIPPER - CRUD
    // ==========================================

    /**
     * Danh sách Shipper
     * Service: shipperService.findAllShippers()
     */
    @GetMapping("/shipper")
    public String shipperList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shippers", shipperService.findAllShippers());
        return "admin/shipper/list";
    }

    /**
     * Tìm kiếm Shipper
     * Service: shipperService.searchShippers()
     */
    @GetMapping("/shipper/search")
    public String searchShipper(@RequestParam String keyword, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shippers", shipperService.searchShippers(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/shipper/list";
    }

    /**
     * Form tạo mới Shipper
     * Service: userService.findAllUsers(), locationService.findAllLocations()
     */
    @GetMapping("/shipper/create")
    public String shipperCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shipper", new ShipperDTO());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", false);
        return "admin/shipper/form";
    }

    /**
     * Xử lý tạo mới Shipper
     * Service: shipperService.createShipper()
     */
    @PostMapping("/shipper/create")
    public String createShipper(@ModelAttribute ShipperDTO dto, RedirectAttributes redirectAttributes) {
        try {
            shipperService.createShipper(dto);
            redirectAttributes.addFlashAttribute("success", "Tạo shipper thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/shipper";
    }

    /**
     * Form chỉnh sửa Shipper
     * Service: shipperService.findShipperById(), userService.findAllUsers(),
     * locationService.findAllLocations()
     */
    @GetMapping("/shipper/edit/{id}")
    public String shipperEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shipper", shipperService.findShipperById(id).orElse(null));
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", true);
        return "admin/shipper/form";
    }

    /**
     * Xử lý cập nhật Shipper
     * Service: shipperService.updateShipper()
     */
    @PostMapping("/shipper/edit/{id}")
    public String updateShipper(@PathVariable Long id, @ModelAttribute ShipperDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            shipperService.updateShipper(id, dto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật shipper thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/shipper";
    }

    /**
     * Xoá Shipper
     * Service: shipperService.deleteShipper()
     */
    @GetMapping("/shipper/delete/{id}")
    public String deleteShipper(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            shipperService.deleteShipper(id);
            redirectAttributes.addFlashAttribute("success", "Xoá shipper thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/shipper";
    }

    /**
     * Toggle trạng thái Shipper
     * Service: shipperService.toggleShipperStatus()
     */
    @GetMapping("/shipper/toggle/{id}")
    public String toggleShipperStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            shipperService.toggleShipperStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái shipper!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/shipper";
    }

    // ==========================================
    // STAFF - CRUD
    // ==========================================

    /**
     * Danh sách Staff
     * Service: staffService.findAllStaff()
     */
    @GetMapping("/staff")
    public String staffList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staffList", staffService.findAllStaff());
        return "admin/staff/list";
    }

    /**
     * Tìm kiếm Staff
     * Service: staffService.searchStaff()
     */
    @GetMapping("/staff/search")
    public String searchStaff(@RequestParam String keyword, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staffList", staffService.searchStaff(keyword));
        model.addAttribute("keyword", keyword);
        return "admin/staff/list";
    }

    /**
     * Form tạo mới Staff
     * Service: userService.findAllUsers(), locationService.findAllLocations()
     */
    @GetMapping("/staff/create")
    public String staffCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staff", new StaffDTO());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", false);
        return "admin/staff/form";
    }

    /**
     * Xử lý tạo mới Staff
     * Service: staffService.createStaff()
     */
    @PostMapping("/staff/create")
    public String createStaff(@ModelAttribute StaffDTO dto, RedirectAttributes redirectAttributes) {
        try {
            staffService.createStaff(dto);
            redirectAttributes.addFlashAttribute("success", "Tạo nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    /**
     * Form chỉnh sửa Staff
     * Service: staffService.findStaffById(), userService.findAllUsers(),
     * locationService.findAllLocations()
     */
    @GetMapping("/staff/edit/{id}")
    public String staffEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staff", staffService.findStaffById(id).orElse(null));
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("locations", locationService.findAllLocations());
        model.addAttribute("isEdit", true);
        return "admin/staff/form";
    }

    /**
     * Xử lý cập nhật Staff
     * Service: staffService.updateStaff()
     */
    @PostMapping("/staff/edit/{id}")
    public String updateStaff(@PathVariable Long id, @ModelAttribute StaffDTO dto,
            RedirectAttributes redirectAttributes) {
        try {
            staffService.updateStaff(id, dto);
            redirectAttributes.addFlashAttribute("success", "Cập nhật nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    /**
     * Xoá Staff
     * Service: staffService.deleteStaff()
     */
    @GetMapping("/staff/delete/{id}")
    public String deleteStaff(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            staffService.deleteStaff(id);
            redirectAttributes.addFlashAttribute("success", "Xoá nhân viên thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    /**
     * Toggle trạng thái Staff
     * Service: staffService.toggleStaffStatus()
     */
    @GetMapping("/staff/toggle/{id}")
    public String toggleStaffStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            staffService.toggleStaffStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái nhân viên!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/staff";
    }

    // ==========================================
    // PROFILE DETAIL PAGES
    // ==========================================

    /**
     * Chi tiết Customer
     * Service: customerService.findCustomerById()
     */
    @GetMapping("/customer/detail/{id}")
    public String customerDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("customer", customerService.findCustomerById(id).orElse(null));
        return "admin/customer/profile";
    }

    /**
     * Chi tiết Staff
     * Service: staffService.findStaffById()
     */
    @GetMapping("/staff/detail/{id}")
    public String staffDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("staff", staffService.findStaffById(id).orElse(null));
        return "admin/staff/profile";
    }

    /**
     * Chi tiết Shipper
     * Service: shipperService.findShipperById()
     */
    @GetMapping("/shipper/detail/{id}")
    public String shipperDetail(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("shipper", shipperService.findShipperById(id).orElse(null));
        return "admin/shipper/profile";
    }
}
