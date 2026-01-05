package vn.DucBackend.Controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import vn.DucBackend.Entities.CustomerRequest;
import vn.DucBackend.Entities.Parcel;
import vn.DucBackend.Entities.ParcelAction;
import vn.DucBackend.Repositories.CustomerRequestRepository;
import vn.DucBackend.Repositories.ParcelRepository;
import vn.DucBackend.Services.ParcelActionService;

import java.util.List;
import java.util.Optional;

/**
 * Controller tra cứu công khai - không cần đăng nhập
 * Cho phép tra cứu theo mã đơn (REQ-xxx) hoặc mã kiện (PCL-xxx)
 */
@Controller
@RequiredArgsConstructor
public class PublicTrackingController {

    private final CustomerRequestRepository requestRepository;
    private final ParcelRepository parcelRepository;
    private final ParcelActionService parcelActionService;

    @GetMapping("/tracking")
    public String tracking(@RequestParam(value = "code", required = false) String code, Model model) {

        if (code == null || code.trim().isEmpty()) {
            // Chưa có mã tra cứu, hiển thị form
            return "public/tracking";
        }

        String searchCode = code.trim();
        model.addAttribute("searchCode", searchCode);

        // Thử tìm theo RequestCode
        Optional<CustomerRequest> requestOpt = requestRepository.findByRequestCode(searchCode);
        if (requestOpt.isPresent()) {
            CustomerRequest request = requestOpt.get();
            model.addAttribute("order", request);
            model.addAttribute("parcels", parcelRepository.findByRequestId(request.getId()));

            // Lấy lịch sử actions
            List<ParcelAction> actions = parcelActionService.getActionsByRequestId(request.getId());
            model.addAttribute("actions", actions);
            model.addAttribute("found", true);
            return "public/tracking";
        }

        // Thử tìm theo ParcelCode
        Optional<Parcel> parcelOpt = parcelRepository.findByParcelCode(searchCode);
        if (parcelOpt.isPresent()) {
            Parcel parcel = parcelOpt.get();
            model.addAttribute("parcel", parcel);
            model.addAttribute("order", parcel.getRequest());

            // Lấy lịch sử actions của parcel
            List<ParcelAction> actions = parcelActionService.getActionsByParcelId(parcel.getId());
            model.addAttribute("actions", actions);
            model.addAttribute("found", true);
            return "public/tracking";
        }

        // Không tìm thấy
        model.addAttribute("errorMessage", "Không tìm thấy đơn hàng/kiện hàng với mã: " + searchCode);
        return "public/tracking";
    }
}
