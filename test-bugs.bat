@echo off
REM Script kiểm tra các bug đã sửa (Windows)
REM Sử dụng: test-bugs.bat

echo ==========================================
echo KIEM TRA CAC BUG DA SUA
echo ==========================================
echo.

set BASE_URL=http://localhost:8081

echo ==========================================
echo 1. KIEM TRA FILES DA TAO/SUA
echo ==========================================
echo.

REM Bug #1: Lỗi màu text
if exist "src\main\resources\templates\public\home.html" (
    echo [PASS] Bug #1: File home.html exists
    findstr /C:"text-white" "src\main\resources\templates\public\home.html" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #1: Co class text-white
    ) else (
        echo [FAIL] Bug #1: Khong co class text-white
    )
) else (
    echo [FAIL] Bug #1: File home.html not found
)

REM Bug #3: Gửi hàng khách vãng lai
if exist "src\main\java\vn\DucBackend\Controllers\WebController.java" (
    findstr /C:"handleRequestSubmit" "src\main\java\vn\DucBackend\Controllers\WebController.java" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #3: Co method handleRequestSubmit
    ) else (
        echo [FAIL] Bug #3: Khong co method handleRequestSubmit
    )
) else (
    echo [FAIL] Bug #3: File WebController.java not found
)

REM Bug #4: Gói vận chuyển
if exist "src\main\resources\templates\customer\order\create-order.html" (
    findstr /C:"th:each" "src\main\resources\templates\customer\order\create-order.html" | findstr /C:"serviceTypes" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #4: Load service types dong
    ) else (
        echo [FAIL] Bug #4: Khong load service types dong
    )
) else (
    echo [FAIL] Bug #4: File create-order.html not found
)

REM Bug #5: Số lượng và kích thước
if exist "src\main\resources\templates\customer\order\create-order.html" (
    findstr /C:"name=\"quantity\"" "src\main\resources\templates\customer\order\create-order.html" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #5: Co field quantity
    ) else (
        echo [FAIL] Bug #5: Khong co field quantity
    )
    
    findstr /C:"name=\"lengthCm\"" "src\main\resources\templates\customer\order\create-order.html" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #5: Co field lengthCm
    ) else (
        echo [FAIL] Bug #5: Khong co field lengthCm
    )
) else (
    echo [FAIL] Bug #5: File create-order.html not found
)

REM Bug #7: Thanh toán customer
if exist "src\main\resources\templates\customer\payments.html" (
    findstr /C:"requestSearch" "src\main\resources\templates\customer\payments.html" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #7: Co field requestSearch
    ) else (
        echo [FAIL] Bug #7: Khong co field requestSearch
    )
    
    findstr /C:"tripSearch" "src\main\resources\templates\customer\payments.html" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #7: Co field tripSearch
    ) else (
        echo [FAIL] Bug #7: Khong co field tripSearch
    )
) else (
    echo [FAIL] Bug #7: File payments.html not found
)

REM Bug #8: Hồ sơ customer
if exist "src\main\java\vn\DucBackend\Controllers\Customer\CustomerProfileController.java" (
    echo [PASS] Bug #8: CustomerProfileController exists
) else (
    echo [FAIL] Bug #8: CustomerProfileController not found
)

if exist "src\main\resources\templates\customer\profile.html" (
    echo [PASS] Bug #8: customer\profile.html exists
) else (
    echo [FAIL] Bug #8: customer\profile.html not found
)

REM Bug #9: Staff location warehouse
if exist "src\main\java\vn\DucBackend\Services\Impl\StaffServiceImpl.java" (
    findstr /C:"WAREHOUSE" "src\main\java\vn\DucBackend\Services\Impl\StaffServiceImpl.java" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #9: Co validation WAREHOUSE
    ) else (
        echo [FAIL] Bug #9: Khong co validation WAREHOUSE
    )
) else (
    echo [FAIL] Bug #9: File StaffServiceImpl.java not found
)

REM Bug #11: Chi tiết chuyến xe shipper
if exist "src\main\resources\templates\shipper\trip\detail.html" (
    findstr /C:"Kien hang trong chuyen" "src\main\resources\templates\shipper\trip\detail.html" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #11: Co section kien hang
    ) else (
        echo [FAIL] Bug #11: Khong co section kien hang
    )
) else (
    echo [FAIL] Bug #11: File detail.html not found
)

REM Bug #12: Lịch sử shipper
if exist "src\main\resources\templates\shipper\history.html" (
    echo [PASS] Bug #12: shipper\history.html exists
) else (
    echo [FAIL] Bug #12: shipper\history.html not found
)

if exist "src\main\java\vn\DucBackend\Controllers\Shipper\ShipperHistoryController.java" (
    findstr /C:"totalTrips" "src\main\java\vn\DucBackend\Controllers\Shipper\ShipperHistoryController.java" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #12: Co tinh toan thong ke
    ) else (
        echo [FAIL] Bug #12: Khong co tinh toan thong ke
    )
) else (
    echo [FAIL] Bug #12: ShipperHistoryController not found
)

REM Bug #13: Hồ sơ shipper
if exist "src\main\java\vn\DucBackend\Controllers\Shipper\ShipperProfileController.java" (
    echo [PASS] Bug #13: ShipperProfileController exists
) else (
    echo [FAIL] Bug #13: ShipperProfileController not found
)

if exist "src\main\resources\templates\shipper\profile.html" (
    echo [PASS] Bug #13: shipper\profile.html exists
) else (
    echo [FAIL] Bug #13: shipper\profile.html not found
)

echo.
echo ==========================================
echo 2. KIEM TRA CODE FIXES
echo ==========================================
echo.

REM Bug #2: Lỗi 500
if exist "src\main\java\vn\DucBackend\Controllers\Customer\CustomerDashboardController.java" (
    findstr /C:"@Transactional" "src\main\java\vn\DucBackend\Controllers\Customer\CustomerDashboardController.java" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #2: Co @Transactional
    ) else (
        echo [FAIL] Bug #2: Khong co @Transactional
    )
) else (
    echo [FAIL] Bug #2: File CustomerDashboardController.java not found
)

if exist "src\main\java\vn\DucBackend\Repositories\PaymentRepository.java" (
    findstr /C:"findByRequestSenderId" "src\main\java\vn\DucBackend\Repositories\PaymentRepository.java" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #2: Co method findByRequestSenderId
    ) else (
        echo [FAIL] Bug #2: Khong co method findByRequestSenderId
    )
) else (
    echo [FAIL] Bug #2: File PaymentRepository.java not found
)

if exist "src\main\java\vn\DucBackend\Config\GlobalExceptionHandler.java" (
    findstr /C:"LazyInitializationException" "src\main\java\vn\DucBackend\Config\GlobalExceptionHandler.java" >nul
    if %errorlevel% equ 0 (
        echo [PASS] Bug #2: Co handler LazyInitializationException
    ) else (
        echo [FAIL] Bug #2: Khong co handler LazyInitializationException
    )
) else (
    echo [FAIL] Bug #2: File GlobalExceptionHandler.java not found
)

echo.
echo ==========================================
echo HOAN TAT KIEM TRA
echo ==========================================
echo.
echo Luu y: Day chi la kiem tra tu dong co ban.
echo Ban can test thu cong theo file BUG_TEST_CHECKLIST.md
echo.
pause

