package vn.DucBackend.Controllers.Admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import vn.DucBackend.Entities.*;
import vn.DucBackend.Repositories.*;

/**
 * Admin Marketing Controller - Quản lý marketing (CaseStudy)
 */
@Controller
@RequestMapping("/admin")
public class AdminMarketingController {

    @Autowired
    private CaseStudyRepository caseStudyRepository;
    @Autowired
    private ServiceTypeRepository serviceTypeRepository;
    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    // ==========================================
    // CASE STUDY
    // ==========================================
    @GetMapping("/casestudy")
    public String caseStudyList(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudies", caseStudyRepository.findAll());
        return "admin/casestudy/list";
    }

    @GetMapping("/casestudy/create")
    public String caseStudyForm(Model model, HttpServletRequest request) {
        addCommonAttributes(model, request);
        model.addAttribute("caseStudy", new CaseStudy());
        model.addAttribute("serviceTypes", serviceTypeRepository.findAll());
        model.addAttribute("requests", customerRequestRepository.findAll());
        return "admin/casestudy/form";
    }
}
