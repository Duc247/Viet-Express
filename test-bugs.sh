#!/bin/bash

# Script kiểm tra các bug đã sửa
# Sử dụng: ./test-bugs.sh

echo "=========================================="
echo "KIỂM TRA CÁC BUG ĐÃ SỬA"
echo "=========================================="
echo ""

BASE_URL="http://localhost:8081"

# Màu sắc
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Hàm kiểm tra HTTP status
check_url() {
    local url=$1
    local expected=$2
    local description=$3
    
    echo -n "Kiểm tra: $description... "
    
    status=$(curl -s -o /dev/null -w "%{http_code}" "$url")
    
    if [ "$status" == "$expected" ]; then
        echo -e "${GREEN}✓ PASS${NC} (HTTP $status)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (HTTP $status, expected $expected)"
        return 1
    fi
}

# Hàm kiểm tra file tồn tại
check_file() {
    local file=$1
    local description=$2
    
    echo -n "Kiểm tra: $description... "
    
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓ PASS${NC} (File exists)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (File not found)"
        return 1
    fi
}

# Hàm kiểm tra code có chứa string
check_code() {
    local file=$1
    local pattern=$2
    local description=$3
    
    echo -n "Kiểm tra: $description... "
    
    if [ -f "$file" ] && grep -q "$pattern" "$file"; then
        echo -e "${GREEN}✓ PASS${NC} (Code found)"
        return 0
    else
        echo -e "${RED}✗ FAIL${NC} (Code not found)"
        return 1
    fi
}

echo "=========================================="
echo "1. KIỂM TRA FILES ĐÃ TẠO/SỬA"
echo "=========================================="
echo ""

# Bug #1: Lỗi màu text
check_file "src/main/resources/templates/public/home.html" "Bug #1: File home.html"
check_code "src/main/resources/templates/public/home.html" "text-white" "Bug #1: Có class text-white"

# Bug #3: Gửi hàng khách vãng lai
check_code "src/main/java/vn/DucBackend/Controllers/WebController.java" "handleRequestSubmit" "Bug #3: Có method handleRequestSubmit"

# Bug #4: Gói vận chuyển
check_code "src/main/resources/templates/customer/order/create-order.html" "th:each.*serviceTypes" "Bug #4: Load service types động"

# Bug #5: Số lượng và kích thước
check_code "src/main/resources/templates/customer/order/create-order.html" "name=\"quantity\"" "Bug #5: Có field quantity"
check_code "src/main/resources/templates/customer/order/create-order.html" "name=\"lengthCm\"" "Bug #5: Có field lengthCm"
check_code "src/main/resources/templates/customer/order/create-order.html" "name=\"widthCm\"" "Bug #5: Có field widthCm"
check_code "src/main/resources/templates/customer/order/create-order.html" "name=\"heightCm\"" "Bug #5: Có field heightCm"

# Bug #7: Thanh toán customer
check_code "src/main/resources/templates/customer/payments.html" "requestSearch" "Bug #7: Có field requestSearch"
check_code "src/main/resources/templates/customer/payments.html" "tripSearch" "Bug #7: Có field tripSearch"

# Bug #8: Hồ sơ customer
check_file "src/main/java/vn/DucBackend/Controllers/Customer/CustomerProfileController.java" "Bug #8: CustomerProfileController"
check_file "src/main/resources/templates/customer/profile.html" "Bug #8: customer/profile.html"

# Bug #9: Staff location warehouse
check_code "src/main/java/vn/DucBackend/Services/Impl/StaffServiceImpl.java" "WAREHOUSE" "Bug #9: Có validation WAREHOUSE"

# Bug #11: Chi tiết chuyến xe shipper
check_code "src/main/resources/templates/shipper/trip/detail.html" "Kiện hàng trong chuyến" "Bug #11: Có section kiện hàng"

# Bug #12: Lịch sử shipper
check_file "src/main/resources/templates/shipper/history.html" "Bug #12: shipper/history.html"
check_code "src/main/java/vn/DucBackend/Controllers/Shipper/ShipperHistoryController.java" "totalTrips" "Bug #12: Có tính toán thống kê"

# Bug #13: Hồ sơ shipper
check_file "src/main/java/vn/DucBackend/Controllers/Shipper/ShipperProfileController.java" "Bug #13: ShipperProfileController"
check_file "src/main/resources/templates/shipper/profile.html" "Bug #13: shipper/profile.html"

echo ""
echo "=========================================="
echo "2. KIỂM TRA CODE FIXES"
echo "=========================================="
echo ""

# Bug #2: Lỗi 500
check_code "src/main/java/vn/DucBackend/Controllers/Customer/CustomerDashboardController.java" "@Transactional" "Bug #2: Có @Transactional"
check_code "src/main/java/vn/DucBackend/Repositories/PaymentRepository.java" "findByRequestSenderId" "Bug #2: Có method findByRequestSenderId"

# Bug #2: Exception handler
check_code "src/main/java/vn/DucBackend/Config/GlobalExceptionHandler.java" "LazyInitializationException" "Bug #2: Có handler LazyInitializationException"

echo ""
echo "=========================================="
echo "3. KIỂM TRA URLS (Cần server đang chạy)"
echo "=========================================="
echo ""

# Kiểm tra server có đang chạy không
if curl -s -o /dev/null -w "%{http_code}" "$BASE_URL" | grep -q "200\|302"; then
    echo -e "${GREEN}Server đang chạy${NC}"
    echo ""
    
    # Test các URLs
    check_url "$BASE_URL/" "200" "Bug #1: Trang chủ"
    check_url "$BASE_URL/request" "200" "Bug #3: Trang gửi yêu cầu"
    check_url "$BASE_URL/customer/create-order" "200\|302" "Bug #4, #5: Trang tạo đơn"
    check_url "$BASE_URL/customer/payments" "200\|302" "Bug #7: Trang thanh toán"
    check_url "$BASE_URL/customer/profile" "200\|302" "Bug #8: Trang hồ sơ customer"
    check_url "$BASE_URL/shipper/profile" "200\|302" "Bug #13: Trang hồ sơ shipper"
    check_url "$BASE_URL/shipper/history" "200\|302" "Bug #12: Trang lịch sử shipper"
else
    echo -e "${YELLOW}Server chưa chạy. Bỏ qua kiểm tra URLs.${NC}"
    echo "Chạy: mvn spring-boot:run"
fi

echo ""
echo "=========================================="
echo "HOÀN TẤT KIỂM TRA"
echo "=========================================="
echo ""
echo "Lưu ý: Đây chỉ là kiểm tra tự động cơ bản."
echo "Bạn cần test thủ công theo file BUG_TEST_CHECKLIST.md"

