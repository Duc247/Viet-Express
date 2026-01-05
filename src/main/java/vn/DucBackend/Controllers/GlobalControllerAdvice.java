package vn.DucBackend.Controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import vn.DucBackend.Services.SystemConfigService;

@ControllerAdvice
public class GlobalControllerAdvice {

    @Autowired
    private SystemConfigService systemConfigService;

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * Thêm seasonal theme vào tất cả các trang
     * Lấy theme theo role của user đang đăng nhập
     * Trả về class cho body tag: theme-spring, theme-summer, theme-autumn, theme-winter, theme-none
     */
    @ModelAttribute("seasonalTheme")
    public String seasonalTheme() {
        try {
            // Lấy role của user hiện tại
            String role = getCurrentUserRole();
            String themeKey = "SEASONAL_THEME"; // Default key
            
            // Xác định key theo role
            if (role != null) {
                switch (role) {
                    case "ROLE_ADMIN":
                        themeKey = "SEASONAL_THEME_ADMIN";
                        break;
                    case "ROLE_MANAGER":
                        themeKey = "SEASONAL_THEME_MANAGER";
                        break;
                    case "ROLE_STAFF":
                        themeKey = "SEASONAL_THEME_STAFF";
                        break;
                    case "ROLE_SHIPPER":
                        themeKey = "SEASONAL_THEME_SHIPPER";
                        break;
                    case "ROLE_CUSTOMER":
                        themeKey = "SEASONAL_THEME_CUSTOMER";
                        break;
                }
            }
            
            // Lấy theme theo role, nếu không có thì dùng theme mặc định
            String theme = systemConfigService.getValue(themeKey, null);
            if (theme == null || theme.isEmpty() || "none".equals(theme)) {
                // Fallback to default theme
                theme = systemConfigService.getValue("SEASONAL_THEME", "none");
            }
            
            return "theme-" + theme;
        } catch (Exception e) {
            return "theme-none";
        }
    }
    
    /**
     * Lấy role của user hiện tại
     */
    private String getCurrentUserRole() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            for (GrantedAuthority authority : auth.getAuthorities()) {
                return authority.getAuthority();
            }
        }
        return null;
    }
}
