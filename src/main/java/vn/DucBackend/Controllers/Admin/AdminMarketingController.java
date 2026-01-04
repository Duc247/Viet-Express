package vn.DucBackend.Controllers.Admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.DTO.*;
import vn.DucBackend.Services.*;

/**
 * Admin Marketing Controller - Quản lý marketing (CaseStudy)
 * 
 * Services sử dụng:
 * - CaseStudyService: Quản lý case study
 * - ServiceTypeService: Lấy danh sách loại dịch vụ (cho form)
 * - CustomerRequestService: Lấy danh sách yêu cầu (cho form)
 */
@Controller
@RequestMapping("/admin")
public class AdminMarketingController {

    private static final Logger logger = LoggerFactory.getLogger(AdminMarketingController.class);

    @Autowired
    private CaseStudyService caseStudyService;
    @Autowired
    private ServiceTypeService serviceTypeService;
    @Autowired
    private CustomerRequestService customerRequestService;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // CASE STUDY - GET ENDPOINTS
    // ==========================================

    /**
     * Danh sách Case Study
     */
    @GetMapping("/casestudy")
    public String caseStudyList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudies", caseStudyService.findAllCaseStudies());
        return "admin/casestudy/list";
    }

    /**
     * Form tạo Case Study mới
     */
    @GetMapping("/casestudy/create")
    public String caseStudyCreateForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudy", new CaseStudyDTO());
        model.addAttribute("serviceTypes", serviceTypeService.findAll());
        // Chỉ lấy đơn đã hoàn thành (DELIVERED)
        model.addAttribute("requests", customerRequestService.findRequestsByStatus("DELIVERED"));
        return "admin/casestudy/form";
    }

    /**
     * Form sửa Case Study
     */
    @GetMapping("/casestudy/edit/{id}")
    public String caseStudyEditForm(@PathVariable Long id, Model model,
            HttpServletRequest request, RedirectAttributes redirectAttributes) {
        addCommonAttributes(model, request);
        CaseStudyDTO dto = caseStudyService.findCaseStudyById(id).orElse(null);
        if (dto == null) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy Case Study!");
            return "redirect:/admin/casestudy";
        }
        model.addAttribute("caseStudy", dto);
        model.addAttribute("serviceTypes", serviceTypeService.findAll());
        model.addAttribute("requests", customerRequestService.findAllRequests());
        return "admin/casestudy/form";
    }

    // ==========================================
    // CASE STUDY - POST ENDPOINTS
    // ==========================================

    /**
     * Lưu Case Study (tạo mới hoặc cập nhật)
     */
    @PostMapping("/casestudy/save")
    public String saveCaseStudy(@ModelAttribute CaseStudyDTO caseStudy,
            RedirectAttributes redirectAttributes) {
        logger.info("=== SAVE CASESTUDY REQUEST ===");
        logger.info("ID: {}", caseStudy.getId());
        logger.info("Title: {}", caseStudy.getTitle());
        logger.info("Slug: {}", caseStudy.getSlug());
        logger.info("RequestId: {}", caseStudy.getRequestId());
        try {
            if (caseStudy.getId() != null && caseStudy.getId() > 0) {
                // Update
                logger.info("Updating CaseStudy ID: {}", caseStudy.getId());
                caseStudyService.updateCaseStudy(caseStudy.getId(), caseStudy);
                redirectAttributes.addFlashAttribute("success", "Đã cập nhật Case Study thành công!");
            } else {
                // Create
                logger.info("Creating new CaseStudy");
                caseStudyService.createCaseStudy(caseStudy);
                redirectAttributes.addFlashAttribute("success", "Đã tạo Case Study mới thành công!");
            }
        } catch (Exception e) {
            logger.error("Error saving CaseStudy: ", e);
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/casestudy";
    }

    /**
     * Toggle trạng thái Xuất bản
     */
    @PostMapping("/casestudy/{id}/toggle-publish")
    public String togglePublish(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            CaseStudyDTO dto = caseStudyService.togglePublished(id);
            String status = dto.getIsPublished() ? "đã xuất bản" : "đã chuyển về nháp";
            redirectAttributes.addFlashAttribute("success", "Case Study " + status + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/casestudy";
    }

    /**
     * Toggle trạng thái Nổi bật
     */
    @PostMapping("/casestudy/{id}/toggle-featured")
    public String toggleFeatured(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            CaseStudyDTO dto = caseStudyService.toggleFeatured(id);
            String status = dto.getIsFeatured() ? "đã đánh dấu nổi bật" : "đã bỏ nổi bật";
            redirectAttributes.addFlashAttribute("success", "Case Study " + status + "!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/casestudy";
    }

    /**
     * Xóa Case Study
     */
    @PostMapping("/casestudy/{id}/delete")
    public String deleteCaseStudy(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            caseStudyService.deleteCaseStudy(id);
            redirectAttributes.addFlashAttribute("success", "Đã xóa Case Study thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Lỗi: " + e.getMessage());
        }
        return "redirect:/admin/casestudy";
    }
}
