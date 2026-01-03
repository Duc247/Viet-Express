# Quick Fix Database Connection

## Thông Tin Cấu Hình

**File**: `src/main/resources/application.properties`

```properties
# Database URL
spring.datasource.url=jdbc:mysql://localhost:3306/LogisticsDB?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8

# Credentials
spring.datasource.username=root
spring.datasource.password=123456
```

## Kiểm Tra Nhanh

### 1. MySQL Đang Chạy?
✅ Port 3306 đang LISTENING (đã kiểm tra)

### 2. Test Connection

**Nếu có MySQL Workbench:**
- Host: `localhost`
- Port: `3306`
- Username: `root`
- Password: `123456`
- Test Connection

**Nếu không có tool:**
- Chạy Spring Boot và xem error message

## Sửa Lỗi Nhanh

### Nếu Password Sai:

1. Mở `src/main/resources/application.properties`
2. Tìm dòng: `spring.datasource.password=123456`
3. Đổi thành password MySQL của bạn
4. Save và restart Spring Boot

### Nếu Không Có Database:

Database sẽ tự tạo nhờ `createDatabaseIfNotExist=true`

Nếu muốn tạo thủ công:
```sql
CREATE DATABASE IF NOT EXISTS LogisticsDB 
CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### Nếu User Không Có Quyền:

```sql
GRANT ALL PRIVILEGES ON LogisticsDB.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

## Common Errors

| Error | Solution |
|-------|----------|
| Access denied | Đổi password trong application.properties |
| Connection refused | Start MySQL service: `net start MySQL80` |
| Unknown database | Database sẽ tự tạo, hoặc tạo thủ công |
| Timeout | Kiểm tra MySQL đang chạy, firewall không block |

## Test Nhanh

Chạy Spring Boot và xem log:
```bash
mvn spring-boot:run
```

Nếu connection success, sẽ thấy:
```
Hibernate: ... (SQL queries)
```

Nếu connection failed, sẽ thấy error message chi tiết.

