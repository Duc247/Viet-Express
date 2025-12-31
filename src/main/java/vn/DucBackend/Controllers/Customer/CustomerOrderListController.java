package vn.DucBackend.Controllers.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Repositories.CustomerRequestRepository;

import java.util.List;

/**
 * Controller xử lý danh sách và tìm kiếm đơn hàng cho Customer
 */
@Controller
@RequestMapping("/customer")
public class CustomerOrderListController {

    @Autowired
    private CustomerRequestRepository customerRequestRepository;

    private void addCommonAttributes(Model model, HttpServletRequest request) {
        model.addAttribute("requestURI", request.getRequestURI());
    }

    private Long getCustomerIdFromSession(HttpSession session) {
        Object customerId = session.getAttribute("customerId");
        if (customerId != null) {
            return (Long) customerId;
        }
        return null;
    }

    @GetMapping("/orders")
    public String orderList(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) String status,
            Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId != null) {
            List<CustomerRequest> orders = customerRequestRepository.findByCustomerId(customerId);

            // Lọc theo keyword
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchKey = keyword.trim().toLowerCase();
                orders = orders.stream()
                        .filter(o -> (o.getRequestCode() != null
                                && o.getRequestCode().toLowerCase().contains(searchKey))
                                || (o.getParcelDescription() != null
                                        && o.getParcelDescription().toLowerCase().contains(searchKey)))
                        .toList();
            }

            // Lọc theo trạng thái
            if (status != null && !status.trim().isEmpty()) {
                orders = orders.stream()
                        .filter(o -> o.getStatus().name().equals(status))
                        .toList();
            }

            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("orders", List.of());
        }

        model.addAttribute("searchKeyword", keyword);
        model.addAttribute("searchStatus", status);

        return "customer/order/list";
    }

    @GetMapping("/orders/history")
    public String orderHistory(Model model, HttpServletRequest request, HttpSession session) {
        addCommonAttributes(model, request);

        Long customerId = getCustomerIdFromSession(session);
        if (customerId != null) {
            List<CustomerRequest> orders = customerRequestRepository.findByCustomerId(customerId);
            model.addAttribute("orders", orders);
        } else {
            model.addAttribute("orders", List.of());
        }

        return "customer/order/history";
    }
}
