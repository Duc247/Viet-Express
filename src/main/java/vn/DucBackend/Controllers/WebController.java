package vn.DucBackend.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import vn.DucBackend.DTO.CaseStudyDTO;
import vn.DucBackend.Services.CaseStudyService;

/**
 * Web Controller - Chỉ xử lý các trang PUBLIC (không cần đăng nhập)
 * Các route khác đã được chuyển sang:
 * - AdminController (/admin/*)
 * - CustomerController (/customer/*)
 * - ManagerController (/manager/*)
 * - ShipperController (/driver/*)
 * - AuthController (/auth/*)
 */
@Controller
public class WebController {

    @Autowired
    private CaseStudyService caseStudyService;

    // ==========================================
    // TRANG CHỦ & GIỚI THIỆU
    // ==========================================
    @GetMapping("/")
    public String homePage(Model model) {
        // Lấy case studies nổi bật (featured + published) để hiển thị trên trang chủ
        model.addAttribute("caseStudies", caseStudyService.findFeaturedCaseStudies());
        return "public/home";
    }

    @GetMapping({ "/about", "/public/about" })
    public String aboutPage() {
        return "public/about";
    }

    // ==========================================
    // CASE STUDY DETAIL (PUBLIC - KHÔNG CẦN ĐĂNG NHẬP)
    // ==========================================
    @GetMapping("/casestudy/{slug}")
    public String caseStudyDetail(@PathVariable String slug, Model model) {
        CaseStudyDTO caseStudy = caseStudyService.findBySlug(slug).orElse(null);
        if (caseStudy == null || !Boolean.TRUE.equals(caseStudy.getIsPublished())) {
            return "redirect:/";
        }
        model.addAttribute("caseStudy", caseStudy);
        // Lấy thêm các case study khác để hiển thị sidebar
        model.addAttribute("relatedCaseStudies", caseStudyService.findPublishedCaseStudies());
        return "public/casestudy-detail";
    }

    // ==========================================
    // GỬI YÊU CẦU VẬN CHUYỂN (KHÁCH VÃNG LAI)
    // ==========================================
    @GetMapping("/request")
    public String showRequestPage() {
        return "public/request";
    }

    @PostMapping("/request")
    public String handleRequestSubmit() {
        System.out.println("Đã nhận yêu cầu vận chuyển mới!");
        return "redirect:/?success=true";
    }

    // ==========================================
    // TRANG DÙNG CHUNG
    // ==========================================
    @GetMapping("/tracking")
    public String trackingPage() {
        return "common/tracking";
    }

    @GetMapping("/order-detail")
    public String orderDetailPage() {
        return "common/order-detail";
    }

    @GetMapping("/user/profile")
    public String profilePage() {
        return "user/profile";
    }
}
