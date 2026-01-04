package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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
    // CASE STUDY
    // ==========================================

    /**
     * Danh sách Case Study
     * Service: caseStudyService.findAllCaseStudies()
     */
    @GetMapping("/casestudy")
    public String caseStudyList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudies", caseStudyService.findAllCaseStudies());
        return "admin/casestudy/list";
    }

    /**
     * Form tạo Case Study mới
     * Service: serviceTypeService.findAll(),
     * customerRequestService.findAllRequests()
     */
    @GetMapping("/casestudy/create")
    public String caseStudyForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudy", new CaseStudyDTO());
        model.addAttribute("serviceTypes", serviceTypeService.findAll());
        model.addAttribute("requests", customerRequestService.findAllRequests());
        return "admin/casestudy/form";
    }
}
