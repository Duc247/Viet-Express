package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.SystemConfig;
import vn.DucBackend.Services.SystemConfigService;

import java.util.*;

/**
 * Admin System Controller - Quản lý cấu hình hệ thống và system logs
 */
@Controller
@RequestMapping("/admin")
public class AdminSystemController {

    @Autowired
    private SystemConfigService systemConfigService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    /**
     * Khởi tạo các cấu hình mặc định khi ứng dụng khởi động
     */
    @PostConstruct
    public void init() {
        systemConfigService.initDefaultConfigs();
    }

    // ==========================================
    // SYSTEM CONFIG - List View
    // ==========================================
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

    // ==========================================
    // SYSTEM CONFIG - Create Form
    // ==========================================
    @GetMapping("/system-config/create")
    public String systemConfigCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("config", new SystemConfig());
        model.addAttribute("isEdit", false);
        model.addAttribute("configGroups", getConfigGroupNames());
        model.addAttribute("configTypes", getConfigTypes());
        return "admin/system-config/form";
    }

    // ==========================================
    // SYSTEM CONFIG - Edit Form
    // ==========================================
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

    // ==========================================
    // SYSTEM CONFIG - Save
    // ==========================================
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

    // ==========================================
    // SYSTEM CONFIG - Delete
    // ==========================================
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

    // ==========================================
    // SYSTEM CONFIG - Toggle Active
    // ==========================================
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

    // ==========================================
    // SYSTEM CONFIG - Init Defaults
    // ==========================================
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
    @Autowired
    private vn.DucBackend.Repositories.RoleRepository roleRepository;

    @GetMapping("/role")
    public String roleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/role/list";
    }

    @GetMapping("/role/create")
    public String roleCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("role", new vn.DucBackend.Entities.Role());
        model.addAttribute("isEdit", false);
        return "admin/role/form";
    }

    @GetMapping("/role/edit/{id}")
    public String roleEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("role", roleRepository.findById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/role/form";
    }

    @PostMapping("/role/save")
    public String roleSave(@ModelAttribute vn.DucBackend.Entities.Role role, RedirectAttributes redirectAttributes) {
        try {
            roleRepository.save(role);
            redirectAttributes.addFlashAttribute("success", "Lưu vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/role";
    }

    @GetMapping("/role/delete/{id}")
    public String roleDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            roleRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa vai trò thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/role";
    }

    @GetMapping("/role/toggle/{id}")
    public String roleToggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            vn.DucBackend.Entities.Role role = roleRepository.findById(id).orElseThrow();
            role.setIsActive(!role.getIsActive());
            roleRepository.save(role);
            redirectAttributes.addFlashAttribute("success", "Đã thay đổi trạng thái!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/role";
    }

    // ==========================================
    // ACTION TYPE MANAGEMENT
    // ==========================================
    @Autowired
    private vn.DucBackend.Repositories.ActionTypeRepository actionTypeRepository;

    @GetMapping("/actiontype")
    public String actionTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionTypes", actionTypeRepository.findAllOrderByCode());
        return "admin/actiontype/list";
    }

    @GetMapping("/actiontype/create")
    public String actionTypeCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionType", new vn.DucBackend.Entities.ActionType());
        model.addAttribute("isEdit", false);
        return "admin/actiontype/form";
    }

    @GetMapping("/actiontype/edit/{id}")
    public String actionTypeEditForm(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionType", actionTypeRepository.findById(id).orElse(null));
        model.addAttribute("isEdit", true);
        return "admin/actiontype/form";
    }

    @PostMapping("/actiontype/save")
    public String actionTypeSave(@ModelAttribute vn.DucBackend.Entities.ActionType actionType,
            RedirectAttributes redirectAttributes) {
        try {
            actionTypeRepository.save(actionType);
            redirectAttributes.addFlashAttribute("success", "Lưu loại thao tác thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/actiontype";
    }

    @GetMapping("/actiontype/delete/{id}")
    public String actionTypeDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            actionTypeRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa loại thao tác thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/actiontype";
    }

    // ==========================================
    // SYSTEM LOG MANAGEMENT
    // ==========================================
    @Autowired
    private vn.DucBackend.Repositories.SystemLogRepository systemLogRepository;

    @GetMapping("/systemlog")
    public String systemLogList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("systemLogs", systemLogRepository.findAll(
                org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC,
                        "createdAt")));
        return "admin/systemlog/list";
    }

    @GetMapping("/systemlog/view/{id}")
    public String systemLogView(@PathVariable Long id, Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("log", systemLogRepository.findById(id).orElse(null));
        return "admin/systemlog/view";
    }

    @GetMapping("/systemlog/delete/{id}")
    public String systemLogDelete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            systemLogRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Xóa log thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/systemlog";
    }

    @GetMapping("/systemlog/clear")
    public String systemLogClear(RedirectAttributes redirectAttributes) {
        try {
            systemLogRepository.deleteAll();
            redirectAttributes.addFlashAttribute("success", "Đã xóa tất cả log!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/systemlog";
    }
}
