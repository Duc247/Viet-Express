<div align="center">

# 🚚 LOGISTICS MANAGEMENT SYSTEM

### Hệ thống quản lý vận chuyển & logistics toàn diện

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.1-brightgreen?style=for-the-badge&logo=springboot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.1-green?style=for-the-badge&logo=thymeleaf)](https://www.thymeleaf.org/)
[![License](https://img.shields.io/badge/License-MIT-purple?style=for-the-badge)](LICENSE)

<img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png" alt="line" width="100%">

**Xây dựng bởi:** Viet Express Team  
**Version:** 1.0.0

</div>

---

## 📖 Giới thiệu

**Logistics Management System** là một hệ thống web được xây dựng nhằm quản lý toàn bộ quy trình vận chuyển hàng hóa, từ thời điểm khách hàng tạo yêu cầu gửi hàng cho đến khi đơn hàng được giao thành công hoặc hoàn trả.

> 🎯 **Mục tiêu:** Tự động hóa quy trình logistics, theo dõi vận chuyển minh bạch, quản lý COD chính xác

---

## ✨ Tính năng chính

<table>
<tr>
<td width="50%">

### 📦 Quản lý đơn hàng
- Tạo và xử lý yêu cầu gửi hàng
- Quản lý chi tiết kiện hàng (Parcel)
- Tracking theo thời gian thực

</td>
<td width="50%">

### 🚛 Quản lý vận chuyển
- Phân tuyến và lập kế hoạch Trip
- Quản lý Shipper và phương tiện
- Theo dõi trạng thái giao hàng

</td>
</tr>
<tr>
<td width="50%">

### 💰 Thanh toán & COD
- Quản lý phí vận chuyển
- Thu tiền COD từ người nhận
- Đối soát giao dịch

</td>
<td width="50%">

### 📊 Báo cáo & Thống kê
- Dashboard tổng quan
- Nhật ký hệ thống (System Log)
- Case Study quảng bá

</td>
</tr>
</table>

---

## 👥 Vai trò người dùng

```
┌─────────────────────────────────────────────────────────────────┐
│                         ADMIN                                   │
│            Quản trị hệ thống - Toàn quyền                       │
├─────────────────────────────────────────────────────────────────┤
│                                                                 │
│     ┌──────────┐    ┌──────────┐    ┌──────────┐               │
│     │ MANAGER  │    │  STAFF   │    │ SHIPPER  │               │
│     │ Quản lý  │    │Nhân viên │    │Giao hàng │               │
│     └──────────┘    └──────────┘    └──────────┘               │
│                                                                 │
│                      ┌──────────┐                               │
│                      │ CUSTOMER │                               │
│                      │Khách hàng│                               │
│                      └──────────┘                               │
└─────────────────────────────────────────────────────────────────┘
```

| Vai trò | Mô tả | Chức năng chính |
|---------|-------|-----------------|
| 🔴 **ADMIN** | Quản trị viên hệ thống | Quản lý users, phân quyền, cấu hình hệ thống |
| 🟠 **MANAGER** | Quản lý điều phối | Phân tuyến, phân công shipper, giám sát |
| 🟡 **STAFF** | Nhân viên kho | Xử lý đơn hàng, nhập/xuất kho, tạo Trip |
| 🟢 **SHIPPER** | Người giao hàng | Lấy hàng, giao hàng, thu COD |
| 🔵 **CUSTOMER** | Khách hàng | Tạo đơn, theo dõi tracking |

---

## 🏗️ Kiến trúc hệ thống

```
┌─────────────────────────────────────────────────────────────┐
│                        BROWSER                              │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      CONTROLLER                             │
│    AdminPersonnelController, AdminOperationController...    │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                       SERVICE                               │
│       UserService, CustomerService, TripService...         │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                      REPOSITORY                             │
│    UserRepository, CustomerRepository, TripRepository...   │
└──────────────────────────┬──────────────────────────────────┘
                           │
                           ▼
┌─────────────────────────────────────────────────────────────┐
│                    MySQL DATABASE                           │
│                     LogisticsDB                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 📂 Cấu trúc dự án

```
src/main/java/vn/DucBackend/
├── 📁 Config/                  # Cấu hình ứng dụng
│   └── DataLoader.java         # Khởi tạo 4 roles mặc định
├── 📁 Controllers/
│   └── 📁 Admin/               # Controllers quản trị
│       ├── AdminDashboardController.java
│       ├── AdminPersonnelController.java
│       ├── AdminOperationController.java
│       ├── AdminResourceController.java
│       └── AdminSystemController.java
├── 📁 Services/                # Business Logic
│   ├── UserService.java
│   ├── CustomerService.java
│   └── 📁 Impl/
├── 📁 DTO/                     # Data Transfer Objects
├── 📁 Entities/                # JPA Entities
└── 📁 Repositories/            # Data Access Layer

src/main/resources/
├── 📁 templates/admin/         # Thymeleaf Templates
│   ├── user/, customer/, shipper/, staff/
│   ├── role/, trip/, parcel/, payment/
│   └── layout-admin.html
├── 📁 static/                  # CSS, JS, Images
└── application.properties      # Cấu hình
```

---

## 🗃️ Database Schema

### Core Tables

| Bảng | Mô tả |
|------|-------|
| `users` | Tài khoản đăng nhập |
| `roles` | Vai trò phân quyền |
| `customers` | Thông tin khách hàng |
| `staff` | Nhân viên kho |
| `shippers` | Người giao hàng |

### Operation Tables

| Bảng | Mô tả |
|------|-------|
| `customer_requests` | Đơn hàng gửi |
| `parcels` | Kiện hàng chi tiết |
| `trips` | Chuyến vận chuyển |
| `tracking_codes` | Mã tra cứu |
| `parcel_actions` | Lịch sử tracking |

### Resource Tables

| Bảng | Mô tả |
|------|-------|
| `locations` | Địa điểm (kho, địa chỉ) |
| `routes` | Tuyến vận chuyển |
| `vehicles` | Phương tiện |
| `service_types` | Loại dịch vụ |

### Finance Tables

| Bảng | Mô tả |
|------|-------|
| `payments` | Khoản thanh toán |
| `payment_transactions` | Lịch sử giao dịch |
| `cods` | Thu hộ COD |

---

## 🚀 Cài đặt & Chạy

### Yêu cầu

- ☕ Java 21+
- 📦 Maven 3.8+
- 🐬 MySQL 8.0+

### Bước 1: Clone repository

```bash
git clone https://github.com/Duc247/Viet-Express.git
cd Viet-Express
```

### Bước 2: Cấu hình database

```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/LogisticsDB
spring.datasource.username=root
spring.datasource.password=your_password
```

### Bước 3: Chạy ứng dụng

```bash
mvn spring-boot:run
```

### Bước 4: Truy cập

| URL | Mô tả |
|-----|-------|
| `http://localhost:8081` | Trang chủ |
| `http://localhost:8081/admin/dashboard` | Admin Dashboard |

---

## 📸 Screenshots

<div align="center">

| Dashboard | Quản lý User |
|:---------:|:------------:|
| ![Dashboard](docs/screenshots/dashboard.png) | ![Users](docs/screenshots/users.png) |

| Quản lý Đơn hàng | Tracking |
|:----------------:|:--------:|
| ![Orders](docs/screenshots/orders.png) | ![Tracking](docs/screenshots/tracking.png) |

</div>

---

## 🔄 Luồng xử lý đơn hàng

```
┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐
│  PENDING │───▶│CONFIRMED │───▶│PICKUP    │───▶│IN_TRANSIT│
│  Chờ XL  │    │ Đã XN    │    │ASSIGNED  │    │Đang VC   │
└──────────┘    └──────────┘    └──────────┘    └──────────┘
                                                      │
     ┌───────────────────────────────────────────────┘
     │
     ▼
┌──────────┐    ┌──────────┐    ┌──────────┐
│PICKED_UP │───▶│OUT_FOR   │───▶│DELIVERED │  ✅ HOÀN THÀNH
│ Đã lấy   │    │DELIVERY  │    │ Đã giao  │
└──────────┘    └──────────┘    └──────────┘
     │
     │ (Thất bại)
     ▼
┌──────────┐    ┌──────────┐
│  FAILED  │───▶│ RETURNED │  ❌ HOÀN TRẢ
│Giao thất │    │ Đã hoàn  │
└──────────┘    └──────────┘
```

---

## 🛠️ Tech Stack

<div align="center">

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 4.0.1, Spring MVC, Spring Data JPA |
| **Frontend** | Thymeleaf, Bootstrap 5, Font Awesome |
| **Database** | MySQL 8.0, Hibernate ORM |
| **Build** | Maven |
| **Security** | Spring Security (coming soon) |

</div>

---

## 📄 API Documentation

> 📝 **Swagger UI:** `http://localhost:8081/swagger-ui.html` (coming soon)

---

## 🤝 Contributing

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit changes (`git commit -m 'Add AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.

---

## 📞 Liên hệ

<div align="center">

**Viet Express Team**

[![Email](https://img.shields.io/badge/Email-contact@vietexpress.vn-red?style=for-the-badge&logo=gmail)](mailto:contact@vietexpress.vn)
[![GitHub](https://img.shields.io/badge/GitHub-Duc247-black?style=for-the-badge&logo=github)](https://github.com/Duc247)

---

<img src="https://raw.githubusercontent.com/andreasbm/readme/master/assets/lines/rainbow.png" alt="line" width="100%">

**⭐ Star this repository if you find it helpful!**

</div>
