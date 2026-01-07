-- =====================================================
-- VIET-EXPRESS - INSERT CASE STUDIES & SERVICE TYPES
-- Dựa trên hình ảnh trong thư mục static/images
-- =====================================================

-- =====================================================
-- 1. SERVICE TYPES (Loại dịch vụ vận chuyển)
-- Hình ảnh: static/images/services/
-- =====================================================

-- Xóa dữ liệu cũ nếu có (tuỳ chọn)
-- DELETE FROM service_types WHERE 1=1;

-- Insert hoặc Update Service Types với đầy đủ thông tin
INSERT INTO service_types (code, slug, name, icon, description, long_description, image_url, price_per_km, average_speed_kmh, is_active, created_at, updated_at) VALUES

-- 1. EXPRESS - Giao hàng nhanh (express.jpg)
('EXPRESS', 'giao-hang-nhanh', 'Giao hàng nhanh', 'fa-bolt', 
 'Giao hàng trong 24h nội thành, 48h liên tỉnh',
 'Dịch vụ giao hàng nhanh chóng dành cho những đơn hàng cần giao gấp. Đảm bảo hàng hóa được vận chuyển an toàn và đúng thời hạn cam kết. Thích hợp cho các mặt hàng điện tử, thời trang, mỹ phẩm cần giao nhanh. Hỗ trợ tracking realtime và thông báo SMS khi giao thành công.',
 '/images/services/express.jpg', 
 5000.00, 60.00, true, NOW(), NOW()),

-- 2. STANDARD - Tiêu chuẩn (standard.jpg)
('STANDARD', 'giao-hang-tieu-chuan', 'Giao hàng tiêu chuẩn', 'fa-truck',
 'Giao hàng trong 3-5 ngày làm việc',
 'Dịch vụ giao hàng tiêu chuẩn phù hợp với đa số nhu cầu vận chuyển. Giá cả hợp lý, thời gian giao hàng ổn định từ 3-5 ngày làm việc. Thích hợp cho các mặt hàng không cần giao gấp như đồ gia dụng, sách vở, quần áo. Bao gồm bảo hiểm hàng hóa cơ bản.',
 '/images/services/standard.jpg',
 2000.00, 40.00, true, NOW(), NOW()),

-- 3. ECONOMY - Tiết kiệm (economy.jpg)
('ECONOMY', 'giao-hang-tiet-kiem', 'Giao hàng tiết kiệm', 'fa-piggy-bank',
 'Giao hàng trong 5-7 ngày, giá ưu đãi',
 'Dịch vụ giao hàng tiết kiệm chi phí, phù hợp cho các đơn hàng không gấp. Thời gian giao từ 5-7 ngày làm việc. Thích hợp cho hàng hóa nặng, cồng kềnh, hoặc số lượng lớn. Giá cước thấp nhất trong các loại dịch vụ, phù hợp cho doanh nghiệp cần vận chuyển hàng loạt.',
 '/images/services/economy.jpg',
 1000.00, 30.00, true, NOW(), NOW()),

-- 4. SAME_DAY - Giao trong ngày (sameday.jpg)
('SAME_DAY', 'giao-hang-trong-ngay', 'Giao trong ngày', 'fa-rocket',
 'Giao hàng nội thành trong ngày đặt đơn',
 'Dịch vụ giao hàng siêu tốc, đảm bảo hàng đến tay người nhận trong ngày đặt đơn. Áp dụng cho nội thành các thành phố lớn (TP.HCM, Hà Nội, Đà Nẵng). Thích hợp cho tài liệu gấp, hợp đồng, thuốc men, hoặc quà tặng. Shipper chuyên nghiệp, tracking GPS realtime.',
 '/images/services/sameday.jpg',
 10000.00, 80.00, true, NOW(), NOW())

ON DUPLICATE KEY UPDATE
    slug = VALUES(slug),
    icon = VALUES(icon),
    long_description = VALUES(long_description),
    image_url = VALUES(image_url),
    updated_at = NOW();

-- =====================================================
-- 2. CASE STUDIES (Dự án tiêu biểu)
-- Hình ảnh: static/images/case-study/
-- =====================================================

-- Xóa dữ liệu cũ nếu có (tuỳ chọn)
-- DELETE FROM case_studies WHERE 1=1;

INSERT INTO case_studies (
    request_id, service_type_id, title, slug, client_name_display, 
    challenge, solution, result, 
    thumbnail_url, image_gallery, 
    is_featured, is_published, created_at, updated_at
) VALUES

-- Case Study 1: Vận chuyển hàng công nghệ (case-study1.jpeg)
(NULL, 
 (SELECT service_type_id FROM service_types WHERE code = 'EXPRESS' LIMIT 1),
 'Vận chuyển thiết bị công nghệ cho doanh nghiệp',
 'van-chuyen-thiet-bi-cong-nghe-doanh-nghiep',
 'Công ty Công nghệ TechViet',
 
 -- Challenge (Thách thức)
 'TechViet cần vận chuyển 500 bộ laptop và thiết bị văn phòng từ TP.HCM đến 3 chi nhánh tại Hà Nội, Đà Nẵng và Cần Thơ trong vòng 48 giờ để kịp sự kiện ra mắt văn phòng mới. Hàng hóa có giá trị cao (hơn 5 tỷ đồng), dễ vỡ và cần bảo quản cẩn thận.',
 
 -- Solution (Giải pháp)
 'Viet-Express triển khai đội ngũ chuyên biệt với:
• 3 xe tải chuyên dụng có điều hòa và hệ thống chống sốc
• Đóng gói riêng từng thiết bị với vật liệu chống va đập
• GPS tracking realtime để khách hàng theo dõi
• Bảo hiểm toàn bộ lô hàng với mức bồi thường 120% giá trị
• Đội ngũ shipper được đào tạo xử lý hàng điện tử',
 
 -- Result (Kết quả)
 'Giao thành công 100% hàng hóa đúng thời hạn:
• Hà Nội: Giao trong 42 giờ (trước deadline 6 tiếng)
• Đà Nẵng: Giao trong 38 giờ 
• Cần Thơ: Giao trong 24 giờ
• KHÔNG có thiết bị nào bị hư hỏng
• Khách hàng đánh giá 5 sao và ký hợp đồng dài hạn',
 
 '/images/case-study/case-study1.jpeg',
 '/images/case-study/case-study1.jpeg',
 true, true, NOW(), NOW()),

-- Case Study 2: Vận chuyển hàng thực phẩm (case-study2.jpg)  
(NULL,
 (SELECT service_type_id FROM service_types WHERE code = 'SAME_DAY' LIMIT 1),
 'Vận chuyển thực phẩm tươi sống cho chuỗi nhà hàng',
 'van-chuyen-thuc-pham-tuoi-song-nha-hang',
 'Chuỗi nhà hàng GoldSea Seafood',
 
 -- Challenge (Thách thức)
 'GoldSea Seafood cần vận chuyển hải sản tươi sống từ cảng cá Vũng Tàu đến 5 chi nhánh nhà hàng tại TP.HCM mỗi sáng sớm. Yêu cầu khắt khe:
• Thời gian vận chuyển tối đa 4 tiếng
• Duy trì nhiệt độ lạnh 2-5°C
• Hải sản phải "còn sống" khi đến nơi
• Giao hàng trước 8h sáng để kịp chuẩn bị bữa trưa',
 
 -- Solution (Giải pháp)
 'Viet-Express thiết kế giải pháp chuyên biệt:
• Xe tải đông lạnh chuyên dụng với thùng giữ lạnh đạt chuẩn HACCP
• Lịch trình cố định 4h sáng mỗi ngày
• Thiết bị theo dõi nhiệt độ IoT realtime
• Đội shipper được đào tạo về vệ sinh an toàn thực phẩm
• Quy trình bàn giao nhanh gọn, có biên bản kiểm tra chất lượng',
 
 -- Result (Kết quả)
 'Sau 6 tháng hợp tác:
• Tỷ lệ giao đúng giờ: 99.2%
• Tỷ lệ hàng đạt chuẩn chất lượng: 99.8%
• Giảm 30% chi phí logistics so với tự vận chuyển
• 0 vụ khiếu nại về chất lượng hải sản
• GoldSea mở thêm 3 chi nhánh mới và mở rộng hợp đồng',
 
 '/images/case-study/case-study2.jpg',
 '/images/case-study/case-study2.jpg',
 true, true, NOW(), NOW());

-- =====================================================
-- 3. CẬP NHẬT SERVICE TYPES VỚI THÔNG TIN BỔ SUNG
-- (Nếu data đã tồn tại, update thêm)
-- =====================================================

UPDATE service_types SET
    slug = 'giao-hang-nhanh',
    icon = 'fa-bolt',
    long_description = 'Dịch vụ giao hàng nhanh chóng dành cho những đơn hàng cần giao gấp. Đảm bảo hàng hóa được vận chuyển an toàn và đúng thời hạn cam kết. Thích hợp cho các mặt hàng điện tử, thời trang, mỹ phẩm cần giao nhanh.',
    image_url = '/images/services/express.jpg',
    updated_at = NOW()
WHERE code = 'EXPRESS';

UPDATE service_types SET
    slug = 'giao-hang-tieu-chuan',
    icon = 'fa-truck',
    long_description = 'Dịch vụ giao hàng tiêu chuẩn phù hợp với đa số nhu cầu vận chuyển. Giá cả hợp lý, thời gian giao hàng ổn định từ 3-5 ngày làm việc.',
    image_url = '/images/services/standard.jpg',
    updated_at = NOW()
WHERE code = 'STANDARD';

UPDATE service_types SET
    slug = 'giao-hang-tiet-kiem',
    icon = 'fa-piggy-bank',
    long_description = 'Dịch vụ giao hàng tiết kiệm chi phí, phù hợp cho các đơn hàng không gấp. Thời gian giao từ 5-7 ngày làm việc.',
    image_url = '/images/services/economy.jpg',
    updated_at = NOW()
WHERE code = 'ECONOMY';

UPDATE service_types SET
    slug = 'giao-hang-trong-ngay',
    icon = 'fa-rocket',
    long_description = 'Dịch vụ giao hàng siêu tốc, đảm bảo hàng đến tay người nhận trong ngày đặt đơn. Áp dụng cho nội thành các thành phố lớn.',
    image_url = '/images/services/sameday.jpg',
    updated_at = NOW()
WHERE code = 'SAME_DAY';

-- =====================================================
-- HOÀN THÀNH
-- File này có thể chạy độc lập hoặc sau data.sql
-- =====================================================
