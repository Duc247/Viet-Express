package vn.DucBackend.Config;

import org.hibernate.LazyInitializationException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

/**
 * Global Exception Handler
 * - Ẩn thông tin lỗi SQL/Database khỏi user
 * - Hiển thị thông báo thân thiện
 * - Log lỗi chi tiết cho developer
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Xử lý LazyInitializationException - Lỗi lazy loading phổ biến
     */
    @ExceptionHandler(LazyInitializationException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleLazyInitializationException(LazyInitializationException ex, Model model, HttpServletRequest request) {
        // Log lỗi chi tiết cho developer
        log.error("LazyInitializationException at {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        log.error("Stack trace: ", ex);

        // Hiển thị thông báo thân thiện cho user
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Lỗi tải dữ liệu");
        model.addAttribute("errorMessage", "Không thể tải dữ liệu liên quan. Vui lòng thử lại sau.");
        model.addAttribute("errorDetail", "Lỗi này thường xảy ra lần đầu. Vui lòng refresh trang.");

        return "error/error";
    }

    /**
     * Xử lý lỗi Database/SQL - ẨN chi tiết khỏi user
     */
    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleDatabaseException(DataAccessException ex, Model model, HttpServletRequest request) {
        // Log lỗi chi tiết cho developer
        log.error("Database error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // Hiển thị thông báo thân thiện cho user
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Lỗi hệ thống");
        model.addAttribute("errorMessage", "Đã xảy ra lỗi khi xử lý dữ liệu. Vui lòng thử lại sau.");
        model.addAttribute("errorDetail", "Nếu lỗi tiếp tục xảy ra, vui lòng liên hệ quản trị viên.");

        return "error/error";
    }

    /**
     * Xử lý lỗi Access Denied (403)
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleAccessDeniedException(AccessDeniedException ex, Model model, HttpServletRequest request) {
        log.warn("Access denied at {}: {}", request.getRequestURI(), ex.getMessage());

        model.addAttribute("errorCode", "403");
        model.addAttribute("errorTitle", "Không có quyền truy cập");
        model.addAttribute("errorMessage", "Bạn không có quyền truy cập trang này.");
        model.addAttribute("errorDetail", "Vui lòng đăng nhập với tài khoản có quyền phù hợp.");

        return "error/403";
    }

    /**
     * Xử lý lỗi Authentication
     */
    @ExceptionHandler(AuthenticationException.class)
    public String handleAuthenticationException(AuthenticationException ex, RedirectAttributes redirectAttributes) {
        log.warn("Authentication error: {}", ex.getMessage());
        redirectAttributes.addFlashAttribute("errorMessage", "Đăng nhập thất bại. Vui lòng kiểm tra lại thông tin.");
        return "redirect:/auth/login";
    }

    /**
     * Xử lý lỗi 404 Not Found
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFoundException(NoHandlerFoundException ex, Model model) {
        log.warn("Page not found: {}", ex.getRequestURL());

        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Không tìm thấy trang");
        model.addAttribute("errorMessage", "Trang bạn đang tìm kiếm không tồn tại.");
        model.addAttribute("errorDetail", "Vui lòng kiểm tra lại địa chỉ URL.");

        return "error/404";
    }

    /**
     * Xử lý IllegalArgumentException
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgumentException(IllegalArgumentException ex, Model model) {
        log.warn("Bad request: {}", ex.getMessage());

        model.addAttribute("errorCode", "400");
        model.addAttribute("errorTitle", "Yêu cầu không hợp lệ");
        model.addAttribute("errorMessage", "Dữ liệu bạn gửi không hợp lệ.");
        model.addAttribute("errorDetail", ex.getMessage());

        return "error/error";
    }

    /**
     * Xử lý tất cả lỗi khác - Fallback handler
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        // Log lỗi chi tiết
        log.error("Unexpected error at {}: {}", request.getRequestURI(), ex.getMessage(), ex);

        // Hiển thị thông báo chung, KHÔNG expose chi tiết lỗi
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Lỗi không mong muốn");
        model.addAttribute("errorMessage", "Đã xảy ra lỗi trong quá trình xử lý. Vui lòng thử lại sau.");
        model.addAttribute("errorDetail", "Mã lỗi: " + System.currentTimeMillis());

        return "error/error";
    }
}
