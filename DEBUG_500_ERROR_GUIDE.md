# HƯỚNG DẪN DEBUG LỖI 500 KHI ẤN LẦN ĐẦU

## 🔍 NGUYÊN NHÂN THƯỜNG GẶP

Lỗi 500 khi ấn lần đầu, lần 2 mới hoạt động thường do:

1. **LazyInitializationException** (Hibernate) - Phổ biến nhất
2. **Transaction timeout** - Kết nối DB bị timeout
3. **Connection pool chưa khởi tạo** - Lần đầu cần thời gian init
4. **Session/Entity Manager đã đóng** - Truy cập entity ngoài transaction

---

## 📋 BƯỚC 1: BẬT LOGGING CHI TIẾT

### 1.1. Thêm vào `application.properties`:

```properties
# ==============================================================
# DEBUG LOGGING - BẬT LOG CHI TIẾT ĐỂ DEBUG
# ==============================================================

# Log level cho root
logging.level.root=INFO

# Log level cho package của bạn (CHI TIẾT HƠN)
logging.level.vn.DucBackend=DEBUG

# Log SQL queries (đã có)
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Log Hibernate events
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

# Log Spring transactions
logging.level.org.springframework.transaction=DEBUG
logging.level.org.springframework.orm.jpa=DEBUG

# Log exceptions chi tiết
logging.level.org.springframework.web=DEBUG
logging.level.org.springframework.web.servlet.DispatcherServlet=DEBUG

# Log HTTP requests
logging.level.org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping=DEBUG
```

### 1.2. Tạo file `logback-spring.xml` trong `src/main/resources/`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <include resource="org/springframework/boot/logging/logback/defaults.xml"/>
    
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/application-%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    
    <!-- Log exceptions chi tiết -->
    <logger name="vn.DucBackend" level="DEBUG"/>
    <logger name="org.hibernate" level="DEBUG"/>
    <logger name="org.springframework.web" level="DEBUG"/>
    <logger name="org.springframework.transaction" level="DEBUG"/>
    
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
    </root>
</configuration>
```

---

## 🔧 BƯỚC 2: CẢI THIỆN EXCEPTION HANDLER

### 2.1. Kiểm tra `GlobalExceptionHandler.java`:

Đảm bảo có xử lý `LazyInitializationException`:

```java
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(LazyInitializationException.class)
    public ResponseEntity<Map<String, Object>> handleLazyInitialization(
            LazyInitializationException ex, HttpServletRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        body.put("error", "LazyInitializationException");
        body.put("message", "Không thể load dữ liệu liên quan. Vui lòng thử lại.");
        body.put("path", request.getRequestURI());
        
        // Log chi tiết
        logger.error("LazyInitializationException tại: " + request.getRequestURI(), ex);
        logger.error("Stack trace: ", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model, HttpServletRequest request) {
        logger.error("Exception tại: " + request.getRequestURI(), ex);
        
        model.addAttribute("error", "Có lỗi xảy ra: " + ex.getMessage());
        model.addAttribute("stackTrace", getStackTrace(ex));
        
        return "error/500";
    }
}
```

---

## 🐛 BƯỚC 3: TÌM VÀ SỬA CÁC ĐIỂM NGHI VẤN

### 3.1. **CustomerDashboardController** - Dòng 53-58:

**VẤN ĐỀ:** Truy cập `p.getRequest().getSender()` ngoài transaction

**CÁCH SỬA:**

```java
// THAY VÌ:
java.math.BigDecimal totalUnpaid = paymentRepository.findAll().stream()
        .filter(p -> p.getRequest().getSender() != null
                && p.getRequest().getSender().getId().equals(customerId))
        .map(p -> p.getExpectedAmount()
                .subtract(p.getPaidAmount() != null ? p.getPaidAmount() : java.math.BigDecimal.ZERO))
        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);

// SỬA THÀNH:
java.math.BigDecimal totalUnpaid = paymentRepository
        .findByRequestSenderId(customerId)  // Tạo query method mới
        .stream()
        .map(p -> p.getExpectedAmount()
                .subtract(p.getPaidAmount() != null ? p.getPaidAmount() : java.math.BigDecimal.ZERO))
        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
```

**Tạo query method mới trong `PaymentRepository.java`:**

```java
@Query("SELECT p FROM Payment p WHERE p.request.sender.id = :customerId")
List<Payment> findByRequestSenderId(@Param("customerId") Long customerId);
```

### 3.2. **Thêm `@Transactional` vào các controller methods:**

```java
@GetMapping("/dashboard")
@Transactional(readOnly = true)  // THÊM DÒNG NÀY
public String dashboard(Model model, HttpSession session) {
    // ...
}
```

### 3.3. **Sử dụng Fetch Join trong Repository:**

Thay vì:
```java
List<CustomerRequest> findByCustomerId(Long customerId);
```

Sử dụng:
```java
@Query("SELECT r FROM CustomerRequest r " +
       "LEFT JOIN FETCH r.sender " +
       "LEFT JOIN FETCH r.receiver " +
       "WHERE r.sender.id = :customerId")
List<CustomerRequest> findByCustomerIdWithRelations(@Param("customerId") Long customerId);
```

---

## 🧪 BƯỚC 4: TEST VÀ QUAN SÁT LOGS

### 4.1. Chạy ứng dụng và xem logs:

```bash
# Windows PowerShell
Get-Content logs\application.log -Wait -Tail 50

# Hoặc xem console output
```

### 4.2. Tìm các dòng log quan trọng:

- `LazyInitializationException`
- `could not initialize proxy`
- `no session`
- `Transaction timeout`
- `Connection pool`

### 4.3. Kiểm tra stack trace:

Tìm dòng có:
```
org.hibernate.LazyInitializationException: could not initialize proxy
```

---

## 🔍 BƯỚC 5: SỬ DỤNG DEBUGGER

### 5.1. Đặt breakpoint tại:

- Controller method đầu tiên được gọi
- Service method
- Repository method
- Nơi truy cập entity relationship

### 5.2. Kiểm tra:

- Entity có được load đầy đủ không?
- Session/EntityManager còn active không?
- Transaction có đang mở không?

---

## ✅ BƯỚC 6: CÁC FIX PHỔ BIẾN

### Fix 1: Thêm `@Transactional` vào Service methods

```java
@Service
@Transactional  // THÊM VÀO CLASS
public class CustomerRequestService {
    
    @Transactional(readOnly = true)  // HOẶC VÀO METHOD
    public List<CustomerRequestDTO> findAllRequests() {
        // ...
    }
}
```

### Fix 2: Sử dụng DTO thay vì Entity trực tiếp

```java
// THAY VÌ trả về Entity
public List<CustomerRequest> findByCustomerId(Long id);

// SỬA THÀNH trả về DTO
public List<CustomerRequestDTO> findByCustomerId(Long id);
```

### Fix 3: Eager fetch cho các relationship quan trọng

```java
@ManyToOne(fetch = FetchType.EAGER)  // THAY VÌ LAZY
@JoinColumn(name = "sender_id")
private Customer sender;
```

### Fix 4: Sử dụng `@EntityGraph`

```java
@EntityGraph(attributePaths = {"sender", "receiver", "parcels"})
@Query("SELECT r FROM CustomerRequest r WHERE r.id = :id")
Optional<CustomerRequest> findByIdWithRelations(@Param("id") Long id);
```

---

## 📊 BƯỚC 7: MONITORING VÀ METRICS

### 7.1. Thêm Actuator (nếu chưa có):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```properties
management.endpoints.web.exposure.include=health,metrics,info
management.endpoint.health.show-details=always
```

### 7.2. Kiểm tra metrics:

- `http://localhost:8081/actuator/health`
- `http://localhost:8081/actuator/metrics`

---

## 🎯 CHECKLIST DEBUG

- [ ] Đã bật logging DEBUG
- [ ] Đã kiểm tra `GlobalExceptionHandler`
- [ ] Đã thêm `@Transactional` vào controller/service
- [ ] Đã sửa các query truy cập lazy relationship
- [ ] Đã test và xem logs
- [ ] Đã sử dụng debugger
- [ ] Đã áp dụng các fix phổ biến

---

## 📝 GHI CHÚ QUAN TRỌNG

1. **Lần đầu chạy** có thể chậm do:
   - Hibernate khởi tạo
   - Connection pool init
   - Class loading

2. **Lần 2 chạy nhanh hơn** vì:
   - Entity đã được cache
   - Connection pool đã sẵn sàng
   - Session đã được reuse

3. **Nếu vẫn lỗi**, kiểm tra:
   - Database connection
   - Network latency
   - Database indexes
   - Query performance

---

## 🚀 NEXT STEPS

Sau khi debug xong, hãy:
1. Ghi lại lỗi cụ thể từ logs
2. Áp dụng fix phù hợp
3. Test lại nhiều lần
4. Tối ưu performance nếu cần

