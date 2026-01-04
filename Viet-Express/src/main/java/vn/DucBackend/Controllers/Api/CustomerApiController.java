package vn.DucBackend.Controllers.Api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import vn.DucBackend.Entities.Customer;
import vn.DucBackend.Repositories.CustomerRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * API Controller cho Customer - dùng cho AJAX calls
 */
@RestController
@RequestMapping("/api/customers")
public class CustomerApiController {

    @Autowired
    private CustomerRepository customerRepository;

    /**
     * Lookup customer by phone number
     * GET /api/customers/by-phone?phone=0901234567
     */
    @GetMapping("/by-phone")
    public ResponseEntity<?> findByPhone(@RequestParam("phone") String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Số điện thoại không được để trống"));
        }

        Optional<Customer> customerOpt = customerRepository.findByPhone(phone.trim());

        if (customerOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Customer customer = customerOpt.get();

        Map<String, Object> result = new HashMap<>();
        result.put("id", customer.getId());
        result.put("name", customer.getName());
        result.put("fullName", customer.getFullName());
        result.put("phone", customer.getPhone());
        result.put("email", customer.getEmail());
        result.put("address", customer.getAddress());
        result.put("companyName", customer.getCompanyName());

        return ResponseEntity.ok(result);
    }

    /**
     * Check if phone exists
     * GET /api/customers/check-phone?phone=0901234567
     */
    @GetMapping("/check-phone")
    public ResponseEntity<?> checkPhone(@RequestParam("phone") String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("exists", false, "error", "Số điện thoại không hợp lệ"));
        }

        boolean exists = customerRepository.existsByPhone(phone.trim());
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
