package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.*;
import vn.DucBackend.Entities.SystemConfig;
import vn.DucBackend.Services.*;

import java.util.*;

/**
 * Admin System Controller - Quản lý cấu hình hệ thống, vai trò, loại thao tác
 * và system logs
 * 
 * Services sử dụng:
 * - SystemConfigService: CRUD cấu hình hệ thống
 * - RoleService: CRUD vai trò
 * - ActionTypeService: CRUD loại thao tác
 * - SystemLogService: Quản lý log hệ thống
 */
@Controller
@RequestMapping("/admin")
public class AdminSystemController {

    @Autowired
    private SystemConfigService systemConfigService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private ActionTypeService actionTypeService;
    @Autowired
    private SystemLogService systemLogService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    /**
     * Khởi tạo các cấu hình mặc định khi ứng dụng khởi động
     * Service: systemConfigService.initDefaultConfigs()
     */
    @PostConstruct
    public void init() {
        systemConfigService.initDefaultConfigs();
    }

    // ==========================================
    // SYSTEM CONFIG - List View
    // ==========================================

    /**
     * Danh sách cấu hình hệ thống (nhóm theo group)
     * Service: systemConfigService.findAll()
     */
    @GetMapping("/system-config")
    public String systemConfigList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);

        // Lấy tất cả cấu hình và nhóm theo group
        List<SystemConfig> allConfigs = systemConfigService.findAll();
        Map<String, List<SystemConfig>> groupedConfigs = new LinkedHashMap<>();

        // Định nghĩa thứ tự các group
        List<String> groupOrder = Arrays.asList("GENERAL", "EMAIL", "PAYMENT", "API", "SOCIAL", "NOTIFICATION");

        for (String group : groupOrder) {
            groupedConfigs.put(group, new ArrayList<>());
        }

        for (SystemConfig config : allConfigs) {
            String group = config.getConfigGroup() != null ? config.getConfigGroup() : "GENERAL";
            if (!groupedConfigs.containsKey(group)) {
                groupedConfigs.put(group, new ArrayList<>());
            }
            groupedConfigs.get(group).add(config);
        }

        // Xóa các group trống
        groupedConfigs.entrySet().removeIf(entry -> entry.getValue().isEmpty());

        model.addAttribute("groupedConfigs", groupedConfigs);
        model.addAttribute("allConfigs", allConfigs);

        // Thêm thông tin cho các tab
        model.addAttribute("configGroups", getConfigGroupInfo());

        return "admin/system-config/list";
    }

    /**
     * Form tạo cấu hình mới
     * Service: (khởi tạo entity SystemConfig mới)
     */
    @GetMapping("/system-config/create")
    public String systemConfigCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("config", new SystemConfig());
        model.addAttribute("isEdit", false);
        model.addAttribute("configGroups", getConfigGroupNames());
        model.addAttribute("configTypes", getConfigTypes());
        return "admin/system-config/form";
    }

    /**
     * Form chỉnh sửa cấu hình
     * Service: systemConfigService.findById()
     */
    @GetMapping("/system-config/edit/{id}")
    public String systemConfigEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        SystemConfig config = systemConfigService.findById(id).orElse(null);
        if (config == null) {
            return "redirect:/admin/system-config";
        }
        model.addAttribute("config", config);
        model.addAttribute("isEdit", true);
        model.addAttribute("configGroups", getConfigGroupNames());
        model.addAttribute("configTypes", getConfigTypes());
        return "admin/system-config/form";
    }

    /**
     * Lưu cấu hình
     * Service: systemConfigService.save()
     */
    @PostMapping("/system-config/save")
    public String systemConfigSave(@ModelAttribute SystemConfig config, RedirectAttributes redirectAttributes) {
        try {
            systemConfigService.save(config);
            redirectAttributes.addFlashAttribute("success", "Lưu cấu hình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/system-config";
    }

    /**
     * Xóa cấu hình
     * Service: systemConfigService.delete()
     */
    @GetMapping("/system-config/delete/{id}")
    public String systemConfigDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            systemConfigService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa cấu hình thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/system-config";
    }

    /**
     * Bật/tắt trạng thái cấu hình
     * Service: systemConfigService.toggleActive()
     */
    @GetMapping("/system-config/toggle/{id}")
    public String systemConfigToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            systemConfigService.toggleActive(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/system-config";
    }

    /**
     * Khởi tạo lại các cấu hình mặc định
     * Service: systemConfigService.initDefaultConfigs()
     */
    @GetMapping("/system-config/init-defaults")
    public String initDefaultConfigs(RedirectAttributes redirectAttributes) {
        try {
            systemConfigService.initDefaultConfigs();
            redirectAttributes.addFlashAttribute("success", "Đã khởi tạo các cấu hình mặc định!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/system-config";
    }

    // ==========================================
    // Helper Methods
    // ==========================================
    private List<String> getConfigGroupNames() {
        return Arrays.asList("GENERAL", "EMAIL", "PAYMENT", "API", "SOCIAL", "NOTIFICATION");
    }

    private List<String> getConfigTypes() {
        return Arrays.asList("STRING", "NUMBER", "BOOLEAN", "JSON", "EMAIL", "URL");
    }

    private List<Map<String, String>> getConfigGroupInfo() {
        List<Map<String, String>> groups = new ArrayList<>();

        groups.add(Map.of(
                "key", "GENERAL",
                "name", "Cài đặt chung",
                "icon", "fas fa-cog",
                "description", "Thông tin cơ bản của website"));

        groups.add(Map.of(
                "key", "EMAIL",
                "name", "Email / SMTP",
                "icon", "fas fa-envelope",
                "description", "Cấu hình gửi email"));

        groups.add(Map.of(
                "key", "PAYMENT",
                "name", "Thanh toán",
                "icon", "fas fa-credit-card",
                "description", "Cấu hình thanh toán và ngân hàng"));

        groups.add(Map.of(
                "key", "API",
                "name", "API Keys",
                "icon", "fas fa-key",
                "description", "Khóa API cho các dịch vụ bên ngoài"));

        groups.add(Map.of(
                "key", "SOCIAL",
                "name", "Mạng xã hội",
                "icon", "fas fa-share-alt",
                "description", "Kết nối Facebook, Zalo..."));

        groups.add(Map.of(
                "key", "NOTIFICATION",
                "name", "Thông báo",
                "icon", "fas fa-bell",
                "description", "Cấu hình gửi thông báo"));

        return groups;
    }

    // ==========================================
    // ROLE MANAGEMENT
    // ==========================================

    /**
     * Danh sách vai trò
     * Service: roleService.findAllRoles()
     */
    @GetMapping("/role")
    public String roleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("roles", roleService.findAllRoles());
        return "admin/role/list";
    }

    /**
     * Form tạo vai trò mới
     * Service: (khởi tạo DTO RoleDTO mới)
     */
    @GetMapping("/role/create")
    public String roleCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("role", new RoleDTO());
        model.addAttribute("isEdit", false);
        return "admin/role/form";
    }

    /**
     * Form chỉnh sửa vai trò
     * Service: roleService.findRoleById()
     */
    @GetMapping("/role/edit/{id}")
    public String roleEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("role", roleService.findRoleById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/role/form";
    }

    /**
     * Lưu vai trò
     * Service: roleService.createRole() hoặc roleService.updateRole()
     */
    @PostMapping("/role/save")
    public String roleSave(@ModelAttribute RoleDTO roleDTO, RedirectAttributes redirectAttributes) {
        try {
            if (roleDTO.getId() != null) {
                roleService.updateRole(roleDTO.getId(), roleDTO);
            } else {
                roleService.createRole(roleDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Lưu vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/role";
    }

    /**
     * Xóa vai trò
     * Service: roleService.deleteRole()
     */
    @GetMapping("/role/delete/{id}")
    public String roleDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.deleteRole(id);
            redirectAttributes.addFlashAttribute("success", "Xóa vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/role";
    }

    /**
     * Bật/tắt trạng thái vai trò
     * Service: roleService.toggleRoleStatus()
     */
    @GetMapping("/role/toggle/{id}")
    public String roleToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleService.toggleRoleStatus(id);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/role";
    }

    // ==========================================
    // ACTION TYPE MANAGEMENT
    // ==========================================

    /**
     * Danh sách loại thao tác
     * Service: actionTypeService.findAll()
     */
    @GetMapping("/actiontype")
    public String actionTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionTypes", actionTypeService.findAll());
        return "admin/actiontype/list";
    }

    /**
     * Form tạo loại thao tác mới
     * Service: (khởi tạo DTO ActionTypeDTO mới)
     */
    @GetMapping("/actiontype/create")
    public String actionTypeCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionType", new ActionTypeDTO());
        model.addAttribute("isEdit", false);
        return "admin/actiontype/form";
    }

    /**
     * Form chỉnh sửa loại thao tác
     * Service: actionTypeService.findById()
     */
    @GetMapping("/actiontype/edit/{id}")
    public String actionTypeEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionType", actionTypeService.findById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/actiontype/form";
    }

    /**
     * Lưu loại thao tác
     * Service: actionTypeService.create() hoặc actionTypeService.update()
     */
    @PostMapping("/actiontype/save")
    public String actionTypeSave(@ModelAttribute ActionTypeDTO actionTypeDTO,
            RedirectAttributes redirectAttributes) {
        try {
            if (actionTypeDTO.getId() != null) {
                actionTypeService.update(actionTypeDTO.getId(), actionTypeDTO);
            } else {
                actionTypeService.create(actionTypeDTO);
            }
            redirectAttributes.addFlashAttribute("success", "Lưu loại thao tác thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/actiontype";
    }

    /**
     * Xóa loại thao tác
     * Service: actionTypeService.delete()
     */
    @GetMapping("/actiontype/delete/{id}")
    public String actionTypeDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            actionTypeService.delete(id);
            redirectAttributes.addFlashAttribute("success", "Xóa loại thao tác thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/actiontype";
    }

    // ==========================================
    // SYSTEM LOG MANAGEMENT
    // ==========================================

    /**
     * Danh sách log hệ thống (sắp xếp mới nhất trước)
     * Service: systemLogService.findAllLogs()
     */
    @GetMapping("/systemlog")
    public String systemLogList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("systemLogs", systemLogService.findAllLogs());
        return "admin/systemlog/list";
    }

    /**
     * Xem chi tiết log
     * Service: systemLogService.findLogById()
     */
    @GetMapping("/systemlog/view/{id}")
    public String systemLogView(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("log", systemLogService.findLogById(id).orElse(null));
        return "admin/systemlog/view";
    }

    /**
     * Xóa một log
     * Service: systemLogService.deleteLog()
     */
    @GetMapping("/systemlog/delete/{id}")
    public String systemLogDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            systemLogService.deleteLog(id);
            redirectAttributes.addFlashAttribute("success", "Xóa log thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/systemlog";
    }

    /**
     * Xóa tất cả log
     * Service: systemLogService.clearAllLogs()
     */
    @GetMapping("/systemlog/clear")
    public String systemLogClear(RedirectAttributes redirectAttributes) {
        try {
            systemLogService.clearAllLogs();
            redirectAttributes.addFlashAttribute("success", "Đã xóa tất cả log!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/systemlog";
    }
}
