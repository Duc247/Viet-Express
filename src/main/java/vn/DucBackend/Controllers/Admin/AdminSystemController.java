package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.RoleDTO;
import vn.DucBackend.Repositories.*;
import vn.DucBackend.Services.RoleService;
import vn.DucBackend.Services.SystemLogService;

/**
 * Admin System Controller - Quản lý hệ thống (Role, ActionType, SystemLog, Config)
 * 
 * @RequestMapping("/admin") - Tất cả endpoint bắt đầu bằng /admin
 */
@Controller
@RequestMapping("/admin")
public class AdminSystemController {

    @Autowired
    private RoleService roleService;
    @Autowired
    private ActionTypeRepository actionTypeRepository;
    @Autowired
    private SystemLogService systemLogService;

    /**
     * Helper method: Thêm các attribute chung cho tất cả các view
     */
    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // ROLE - Quản lý vai trò (chỉ xem, không sửa xoá)
    // ==========================================
    
    /**
     * GET /admin/role - Hiển thị danh sách vai trò
     */
    @GetMapping("/role")
    public String roleList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("roles", roleService.findAllRoles());
        return "admin/role/list";
    }

    // ==========================================
    // ACTION TYPE - Loại hành động
    // ==========================================
    @GetMapping("/actiontype")
    public String actionTypeList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("actionTypes", actionTypeRepository.findAll());
        return "admin/actiontype/list";
    }

    // ==========================================
    // SYSTEM LOG - Nhật ký hệ thống
    // ==========================================
    @GetMapping("/systemlog")
    public String systemLogList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("logs", systemLogService.findAllLogs());
        return "admin/systemlog/list";
    }

    // ==========================================
    // CONFIG - Cấu hình hệ thống
    // ==========================================
    @GetMapping("/system-config")
    public String systemConfig(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        return "admin/config/system-config";
    }
}
