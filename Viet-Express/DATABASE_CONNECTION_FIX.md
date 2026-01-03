# Hướng Dẫn Kiểm Tra và Sửa Lỗi Kết Nối Database

## Thông Tin Cấu Hình Hiện Tại

File: `src/main/resources/application.properties`

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/LogisticsDB?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true&useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=root
spring.datasource.password=123456
```

**MySQL đang chạy**: ✅ Port 3306 đang LISTENING

---

## Các Bước Kiểm Tra

### 1. Kiểm Tra MySQL Service

**Windows:**
```powershell
# Kiểm tra service
sc query MySQL80

# Nếu không chạy, start service
net start MySQL80
```

**Hoặc kiểm tra trong Services:**
- Mở `services.msc`
- Tìm "MySQL80" hoặc "MySQL"
- Đảm bảo Status = Running

### 2. Kiểm Tra Kết Nối MySQL

**Cách 1: Dùng MySQL Workbench hoặc phpMyAdmin**
- Host: localhost
- Port: 3306
- Username: root
- Password: 123456

**Cách 2: Dùng Command Line (nếu MySQL trong PATH)**
```bash
mysql -u root -p123456
```

**Cách 3: Test từ Java (đã có trong app)**
- Chạy Spring Boot và xem log
- Nếu connection failed, sẽ có error message chi tiết

### 3. Kiểm Tra Database Exists

Sau khi connect được, kiểm tra:
```sql
SHOW DATABASES;
```

Nếu chưa có `LogisticsDB`, database sẽ được tạo tự động nhờ `createDatabaseIfNotExist=true`

### 4. Kiểm Tra User Permissions

```sql
SELECT user, host FROM mysql.user WHERE user='root';
SHOW GRANTS FOR 'root'@'localhost';
```

---

## Các Lỗi Thường Gặp và Cách Sửa

### Lỗi 1: Access Denied (Wrong Password)

**Triệu chứng:**
```
Access denied for user 'root'@'localhost' (using password: YES)
```

**Giải pháp:**
1. Đổi password trong `application.properties`
2. Hoặc reset MySQL root password:
   ```sql
   ALTER USER 'root'@'localhost' IDENTIFIED BY 'new_password';
   FLUSH PRIVILEGES;
   ```

### Lỗi 2: Cannot Connect to Database

**Triệu chứng:**
```
Communications link failure
```

**Giải pháp:**
1. Kiểm tra MySQL service đang chạy
2. Kiểm tra firewall không block port 3306
3. Thử đổi `localhost` thành `127.0.0.1`

### Lỗi 3: Unknown Database

**Triệu chứng:**
```
Unknown database 'LogisticsDB'
```

**Giải pháp:**
- App sẽ tự tạo database nếu `createDatabaseIfNotExist=true`
- Hoặc tạo thủ công:
  ```sql
  CREATE DATABASE LogisticsDB CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  ```

### Lỗi 4: Public Key Retrieval

**Triệu chứng:**
```
Public Key Retrieval is not allowed
```

**Giải pháp:**
- Đã có `allowPublicKeyRetrieval=true` trong URL
- Hoặc thêm vào MySQL config:
  ```
  [mysqld]
  default_authentication_plugin=mysql_native_password
  ```

### Lỗi 5: Timezone

**Triệu chứng:**
```
The server time zone value '...' is unrecognized
```

**Giải pháp:**
- Đã có `serverTimezone=UTC` trong URL
- Hoặc set timezone MySQL:
  ```sql
  SET GLOBAL time_zone = '+00:00';
  ```

---

## Cấu Hình Mới (Nếu Cần Thay Đổi)

### Option 1: Đổi Password

Sửa file `src/main/resources/application.properties`:

```properties
spring.datasource.password=YOUR_NEW_PASSWORD
```

### Option 2: Đổi Database Name

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/YOUR_DB_NAME?createDatabaseIfNotExist=true&...
```

### Option 3: Đổi Username

```properties
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
```

### Option 4: Remote MySQL Server

```properties
spring.datasource.url=jdbc:mysql://YOUR_HOST:3306/LogisticsDB?...
```

---

## Test Connection Nhanh

**Tạo file test: `TestConnection.java`**

```java
import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/LogisticsDB?createDatabaseIfNotExist=true&useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
        String username = "root";
        String password = "123456";
        
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("✅ Connection successful!");
            conn.close();
        } catch (Exception e) {
            System.out.println("❌ Connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
```

---

## Quick Fix Checklist

- [ ] MySQL service đang chạy (Port 3306 LISTENING)
- [ ] Username/password đúng trong application.properties
- [ ] Có thể connect bằng MySQL client (Workbench, etc.)
- [ ] Database LogisticsDB tồn tại hoặc có quyền tạo
- [ ] User có đủ quyền (CREATE, DROP, SELECT, INSERT, UPDATE, DELETE)
- [ ] Firewall không block port 3306
- [ ] Không có lỗi timeout

---

## Nếu Vẫn Không Kết Nối Được

1. **Xem log chi tiết Spring Boot:**
   - Chạy `mvn spring-boot:run`
   - Xem error message đầy đủ trong console

2. **Test connection bằng tool:**
   - MySQL Workbench
   - DBeaver
   - phpMyAdmin
   - Command line: `mysql -u root -p`

3. **Kiểm tra MySQL config:**
   - File `my.ini` hoặc `my.cnf`
   - Port = 3306
   - Bind-address = 127.0.0.1 hoặc 0.0.0.0

4. **Restart MySQL:**
   ```powershell
   net stop MySQL80
   net start MySQL80
   ```

5. **Kiểm tra log MySQL:**
   - Windows: `C:\ProgramData\MySQL\MySQL Server 8.0\Data\*.err`
   - Xem có error nào không

---

## Thông Tin Bổ Sung

- **Port mặc định MySQL**: 3306
- **Host mặc định**: localhost (127.0.0.1)
- **Database name**: LogisticsDB (sẽ tự tạo nếu chưa có)
- **Character encoding**: UTF-8
- **Timezone**: UTC

---

## Liên Hệ

Nếu vẫn không kết nối được, cung cấp:
1. Error message đầy đủ từ Spring Boot log
2. MySQL version: `SELECT VERSION();`
3. OS và MySQL installation method

