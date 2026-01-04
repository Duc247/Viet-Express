# 🌿 Thymeleaf - Tài Liệu Hướng Dẫn Chi Tiết

Tài liệu giải thích chi tiết các câu lệnh Thymeleaf thường dùng trong Spring Boot.

---

## 📋 Mục Lục
1. [Giới thiệu](#1-giới-thiệu)
2. [Hiển thị Text](#2-hiển-thị-text)
3. [URLs và Links](#3-urls-và-links)
4. [Điều kiện](#4-điều-kiện)
5. [Vòng lặp](#5-vòng-lặp)
6. [Thuộc tính HTML](#6-thuộc-tính-html)
7. [Forms](#7-forms)
8. [Fragments và Layouts](#8-fragments-và-layouts)
9. [Utility Objects](#9-utility-objects)
10. [Expressions](#10-expressions)

---

## 1. Giới Thiệu

### Thymeleaf là gì?
- Template engine cho Java
- Tích hợp tốt với Spring Boot
- File HTML có thể mở trực tiếp trong browser (Natural Templates)

### Namespace

```html
<html xmlns:th="http://www.thymeleaf.org">
```

### Luồng dữ liệu Controller → View

```java
// Controller
@GetMapping("/hello")
public String hello(Model model) {
    model.addAttribute("name", "John");
    model.addAttribute("users", userList);
    return "hello";  // → templates/hello.html
}
```

```html
<!-- templates/hello.html -->
<p th:text="${name}">Default Name</p>
```

---

## 2. Hiển Thị Text

### th:text

**Ý nghĩa:** Thay thế nội dung text của thẻ (escape HTML)

```html
<p th:text="${message}">Default message</p>
<!-- Output: <p>Hello World</p> -->

<!-- Nếu message = "<b>Bold</b>", sẽ hiển thị: <b>Bold</b> (không parse HTML) -->
```

### th:utext

**Ý nghĩa:** Thay thế nội dung text (không escape HTML - unescaped)

```html
<p th:utext="${htmlContent}">Default</p>
<!-- Nếu htmlContent = "<b>Bold</b>", sẽ hiển thị: Bold (in đậm) -->
```

> ⚠️ **Cảnh báo:** Chỉ dùng `th:utext` với nội dung tin cậy, tránh XSS attack

### Nối chuỗi

```html
<!-- Cách 1: Dùng + -->
<p th:text="'Xin chào, ' + ${name} + '!'">Default</p>

<!-- Cách 2: Literal substitution (khuyến khích) -->
<p th:text="|Xin chào, ${name}!|">Default</p>

<!-- Cách 3: Kết hợp -->
<p th:text="|ID: ${user.id} - Name: ${user.name}|">Default</p>
```

### Inline expressions

**Ý nghĩa:** Chèn expression trực tiếp vào text

```html
<p th:inline="text">
    Xin chào, [[${name}]]! Bạn có [[${messageCount}]] tin nhắn.
</p>

<!-- [[...]] = th:text (escape) -->
<!-- [(...)]] = th:utext (không escape) -->
```

---

## 3. URLs và Links

### @{...} - URL Expression

**Ý nghĩa:** Tạo URL với context path tự động

```html
<!-- Static URL -->
<link th:href="@{/css/style.css}" rel="stylesheet">
<script th:src="@{/js/app.js}"></script>
<img th:src="@{/images/logo.png}">

<!-- Relative URL -->
<a th:href="@{/users}">Danh sách users</a>
<!-- Output: /context-path/users -->
```

### URL với Path Variables

```html
<a th:href="@{/users/{id}(id=${user.id})}">Chi tiết</a>
<!-- Nếu user.id = 123 → /users/123 -->

<!-- Nhiều path variables -->
<a th:href="@{/users/{userId}/posts/{postId}(userId=${user.id}, postId=${post.id})}">
    View Post
</a>
```

### URL với Query Parameters

```html
<a th:href="@{/users(page=${currentPage}, size=10)}">Next Page</a>
<!-- Output: /users?page=1&size=10 -->

<!-- Kết hợp path variable và query param -->
<a th:href="@{/users/{id}(id=${user.id}, tab='profile')}">Profile</a>
<!-- Output: /users/123?tab=profile -->
```

### Fragment trong URL

```html
<a th:href="@{/page#section1}">Go to Section 1</a>

<!-- Với expression -->
<a th:href="@{/page(id=${id})#section}">Link</a>
```

---

## 4. Điều Kiện

### th:if

**Ý nghĩa:** Hiển thị phần tử nếu điều kiện true

```html
<div th:if="${user != null}">
    Xin chào, <span th:text="${user.name}">User</span>
</div>

<span th:if="${users.empty}">Không có dữ liệu</span>

<div th:if="${user.role == 'ADMIN'}">Admin Panel</div>
```

### th:unless

**Ý nghĩa:** Hiển thị phần tử nếu điều kiện false (ngược với th:if)

```html
<div th:unless="${user != null}">
    Vui lòng đăng nhập
</div>

<!-- Tương đương -->
<div th:if="${user == null}">
    Vui lòng đăng nhập
</div>
```

### th:switch / th:case

**Ý nghĩa:** Switch-case statement

```html
<div th:switch="${user.role}">
    <p th:case="'ADMIN'">Quản trị viên</p>
    <p th:case="'USER'">Người dùng</p>
    <p th:case="'GUEST'">Khách</p>
    <p th:case="*">Không xác định</p>  <!-- default -->
</div>
```

### Toán tử 3 ngôi

```html
<span th:text="${user.active} ? 'Hoạt động' : 'Ngừng hoạt động'">Status</span>

<!-- Với class -->
<div th:class="${error} ? 'alert-danger' : 'alert-success'">Message</div>
```

### Elvis Operator (?:)

**Ý nghĩa:** Giá trị mặc định nếu null

```html
<span th:text="${user.nickname} ?: 'Chưa có nickname'">Nickname</span>

<!-- Tương đương -->
<span th:text="${user.nickname != null} ? ${user.nickname} : 'Chưa có nickname'">Nickname</span>
```

### Safe navigation (?.)

**Ý nghĩa:** Tránh NullPointerException

```html
<span th:text="${user?.address?.city}">City</span>
<!-- Trả về null nếu user hoặc address là null -->
```

---

## 5. Vòng Lặp

### th:each

**Ý nghĩa:** Lặp qua collection (List, Set, Map, Array)

```html
<tr th:each="user : ${users}">
    <td th:text="${user.id}">1</td>
    <td th:text="${user.name}">Name</td>
    <td th:text="${user.email}">Email</td>
</tr>
```

### Biến trạng thái (Status Variable)

```html
<tr th:each="user, stat : ${users}">
    <td th:text="${stat.index}">0</td>      <!-- 0, 1, 2... (0-indexed) -->
    <td th:text="${stat.count}">1</td>      <!-- 1, 2, 3... (1-indexed) -->
    <td th:text="${stat.size}">10</td>      <!-- Tổng số phần tử -->
    <td th:text="${stat.current}">User</td> <!-- Phần tử hiện tại -->
    <td th:text="${stat.first}">true</td>   <!-- true nếu đầu tiên -->
    <td th:text="${stat.last}">false</td>   <!-- true nếu cuối cùng -->
    <td th:text="${stat.odd}">true</td>     <!-- true nếu index lẻ -->
    <td th:text="${stat.even}">false</td>   <!-- true nếu index chẵn -->
</tr>
```

### Sử dụng Status Variable

```html
<!-- Zebra striping -->
<tr th:each="user, stat : ${users}" 
    th:class="${stat.odd} ? 'odd' : 'even'">
</tr>

<!-- Thêm class cho phần tử đầu/cuối -->
<li th:each="item, stat : ${items}"
    th:classappend="${stat.first} ? 'first' : (${stat.last} ? 'last' : '')">
</li>

<!-- Separator -->
<span th:each="tag, stat : ${tags}">
    <span th:text="${tag}">Tag</span>
    <span th:unless="${stat.last}">, </span>
</span>
```

### Lặp qua Map

```html
<div th:each="entry : ${map}">
    <span th:text="${entry.key}">Key</span>: 
    <span th:text="${entry.value}">Value</span>
</div>
```

---

## 6. Thuộc Tính HTML

### th:attr

**Ý nghĩa:** Set bất kỳ thuộc tính nào

```html
<img th:attr="src=${imageUrl}, alt=${imageAlt}">

<!-- Tương đương -->
<img th:src="${imageUrl}" th:alt="${imageAlt}">
```

### Các thuộc tính phổ biến

| Attribute | Ý nghĩa |
|-----------|---------|
| `th:id` | Set id |
| `th:class` | Thay thế class |
| `th:classappend` | Thêm class (giữ class cũ) |
| `th:style` | Thay thế style |
| `th:styleappend` | Thêm style |
| `th:src` | src của img, script |
| `th:href` | href của link, a |
| `th:value` | value của input |
| `th:action` | action của form |
| `th:name` | name attribute |
| `th:title` | title attribute |
| `th:alt` | alt của img |
| `th:placeholder` | placeholder của input |

```html
<input th:value="${user.name}" 
       th:placeholder="'Nhập tên'" 
       th:id="'input-' + ${user.id}">
```

### th:classappend

**Ý nghĩa:** Thêm class mà không thay thế class cũ

```html
<a class="nav-link" 
   th:classappend="${isActive} ? 'active' : ''">
    Link
</a>
<!-- Output: <a class="nav-link active">Link</a> -->
```

### Boolean Attributes

```html
<input type="checkbox" th:checked="${user.active}">
<input type="text" th:disabled="${user.locked}">
<input type="text" th:readonly="${!user.editable}">
<option th:selected="${item.id == selectedId}">Option</option>
<button th:disabled="${form.invalid}">Submit</button>
```

---

## 7. Forms

### Form cơ bản

```html
<form th:action="@{/users/save}" method="post" th:object="${userDTO}">
    <!-- th:object bind form với object -->
</form>
```

### th:field

**Ý nghĩa:** Bind input với property của object (tự động set name, id, value)

```html
<form th:action="@{/users/save}" method="post" th:object="${userDTO}">
    <!-- Text input -->
    <input type="text" th:field="*{name}">
    <!-- Output: <input type="text" id="name" name="name" value="John"> -->
    
    <!-- Textarea -->
    <textarea th:field="*{description}"></textarea>
    
    <!-- Checkbox -->
    <input type="checkbox" th:field="*{active}">
    
    <!-- Radio -->
    <input type="radio" th:field="*{gender}" value="MALE"> Nam
    <input type="radio" th:field="*{gender}" value="FEMALE"> Nữ
    
    <!-- Hidden -->
    <input type="hidden" th:field="*{id}">
    
    <button type="submit">Lưu</button>
</form>
```

### *{...} - Selection Expression

**Ý nghĩa:** Truy cập property của object đã bind với th:object

```html
<form th:object="${user}">
    <!-- *{name} tương đương ${user.name} -->
    <input th:value="*{name}">
    <input th:value="*{email}">
</form>
```

### Select / Option

```html
<select th:field="*{cityId}">
    <option value="">-- Chọn thành phố --</option>
    <option th:each="city : ${cities}" 
            th:value="${city.id}" 
            th:text="${city.name}">City</option>
</select>

<!-- Với selected -->
<select th:field="*{roleId}">
    <option th:each="role : ${roles}" 
            th:value="${role.id}" 
            th:text="${role.name}"
            th:selected="${role.id == user.roleId}">Role</option>
</select>
```

### Validation Errors

```html
<!-- Hiển thị tất cả errors -->
<div th:if="${#fields.hasErrors('*')}" class="alert alert-danger">
    <ul>
        <li th:each="err : ${#fields.errors('*')}" th:text="${err}">Error</li>
    </ul>
</div>

<!-- Error cho field cụ thể -->
<input type="text" th:field="*{email}" th:errorclass="is-invalid">
<span th:if="${#fields.hasErrors('email')}" 
      th:errors="*{email}" 
      class="text-danger">Email error</span>
```

---

## 8. Fragments và Layouts

### Định nghĩa Fragment

```html
<!-- fragments/header.html -->
<header th:fragment="header">
    <nav>Navigation here</nav>
</header>

<!-- Fragment với parameters -->
<div th:fragment="card(title, content)">
    <div class="card">
        <h3 th:text="${title}">Title</h3>
        <p th:text="${content}">Content</p>
    </div>
</div>
```

### Sử dụng Fragment

```html
<!-- th:replace - thay thế hoàn toàn thẻ hiện tại -->
<div th:replace="~{fragments/header :: header}"></div>
<!-- Kết quả: <header>...</header> (không còn div) -->

<!-- th:insert - chèn vào bên trong thẻ hiện tại -->
<div th:insert="~{fragments/header :: header}"></div>
<!-- Kết quả: <div><header>...</header></div> -->

<!-- th:include - chỉ lấy nội dung (deprecated, dùng th:insert) -->
<div th:include="~{fragments/header :: header}"></div>
<!-- Kết quả: <div><nav>...</nav></div> -->
```

### Fragment với Parameters

```html
<!-- Gọi fragment với tham số -->
<div th:replace="~{fragments/common :: card('Tiêu đề', 'Nội dung')}"></div>

<!-- Với biến -->
<div th:replace="~{fragments/common :: card(${title}, ${content})}"></div>
```

### Thymeleaf Layout Dialect

```html
<!-- Layout: templates/layouts/main.html -->
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <title layout:title-pattern="$CONTENT_TITLE - My App">My App</title>
    <link rel="stylesheet" th:href="@{/css/style.css}">
</head>
<body>
    <header th:replace="~{fragments/header :: header}"></header>
    
    <main layout:fragment="content">
        <!-- Nội dung mặc định -->
    </main>
    
    <footer th:replace="~{fragments/footer :: footer}"></footer>
    
    <th:block layout:fragment="scripts">
        <!-- Scripts mặc định -->
    </th:block>
</body>
</html>
```

```html
<!-- Page: templates/users/list.html -->
<!DOCTYPE html>
<html layout:decorate="~{layouts/main}">
<head>
    <title>Danh sách Users</title>
</head>
<body>
    <main layout:fragment="content">
        <h1>Danh sách Users</h1>
        <table>...</table>
    </main>
    
    <th:block layout:fragment="scripts">
        <script th:src="@{/js/users.js}"></script>
    </th:block>
</body>
</html>
```

---

## 9. Utility Objects

### #strings

**Ý nghĩa:** Xử lý chuỗi

```html
${#strings.isEmpty(name)}                <!-- Kiểm tra rỗng/null -->
${#strings.defaultString(name, 'N/A')}   <!-- Giá trị mặc định -->
${#strings.contains(name, 'admin')}      <!-- Chứa substring -->
${#strings.startsWith(name, 'Mr.')}      <!-- Bắt đầu bằng -->
${#strings.endsWith(name, '@gmail.com')} <!-- Kết thúc bằng -->
${#strings.toUpperCase(name)}            <!-- Viết hoa -->
${#strings.toLowerCase(name)}            <!-- Viết thường -->
${#strings.capitalize(name)}             <!-- Viết hoa chữ đầu -->
${#strings.trim(name)}                   <!-- Xóa khoảng trắng 2 đầu -->
${#strings.length(name)}                 <!-- Độ dài -->
${#strings.abbreviate(text, 100)}        <!-- Rút gọn với ... -->
${#strings.substring(name, 0, 5)}        <!-- Lấy substring -->
${#strings.replace(name, ' ', '-')}      <!-- Thay thế -->
```

### #numbers

**Ý nghĩa:** Format số

```html
${#numbers.formatInteger(num, 3)}          <!-- 001, 002... -->
${#numbers.formatDecimal(num, 1, 2)}       <!-- 1.00 -->
${#numbers.formatCurrency(price)}          <!-- $1,000.00 -->
${#numbers.formatPercent(rate)}            <!-- 50% -->

<!-- Custom format -->
${#numbers.formatDecimal(num, 0, 'COMMA', 2, 'POINT')}  <!-- 1,234.56 -->
```

### #dates / #temporals

**Ý nghĩa:** Format ngày giờ

```html
<!-- Với java.util.Date -->
${#dates.format(date, 'dd/MM/yyyy')}
${#dates.format(date, 'dd/MM/yyyy HH:mm:ss')}
${#dates.day(date)}
${#dates.month(date)}
${#dates.year(date)}

<!-- Với Java 8+ LocalDate, LocalDateTime -->
${#temporals.format(localDate, 'dd/MM/yyyy')}
${#temporals.format(localDateTime, 'dd/MM/yyyy HH:mm')}
${#temporals.day(localDate)}
${#temporals.month(localDate)}
${#temporals.year(localDate)}
```

### #lists / #sets / #maps

**Ý nghĩa:** Xử lý collections

```html
${#lists.isEmpty(list)}        <!-- Kiểm tra rỗng -->
${#lists.size(list)}           <!-- Số phần tử -->
${#lists.contains(list, item)} <!-- Chứa phần tử -->
${#lists.sort(list)}           <!-- Sắp xếp -->

${#maps.isEmpty(map)}
${#maps.size(map)}
${#maps.containsKey(map, key)}
${#maps.containsValue(map, value)}
```

### #objects

**Ý nghĩa:** Xử lý objects

```html
${#objects.nullSafe(obj, 'default')}  <!-- Giá trị mặc định nếu null -->
```

### #bools

**Ý nghĩa:** Xử lý boolean

```html
${#bools.isTrue(value)}
${#bools.isFalse(value)}
```

---

## 10. Expressions

### ${...} - Variable Expression

**Ý nghĩa:** Truy cập biến từ Model

```html
${user}              <!-- Object user -->
${user.name}         <!-- Property name -->
${user.getName()}    <!-- Method -->
${user['name']}      <!-- Bracket notation -->
${users[0]}          <!-- Array/List index -->
${map['key']}        <!-- Map key -->
```

### *{...} - Selection Expression

**Ý nghĩa:** Truy cập property của object đã bind

```html
<form th:object="${user}">
    *{name}   <!-- = ${user.name} -->
    *{email}  <!-- = ${user.email} -->
</form>
```

### @{...} - URL Expression

**Ý nghĩa:** Tạo URL

```html
@{/path}                     <!-- Relative URL -->
@{/users/{id}(id=${userId})} <!-- Path variable -->
@{/search(q=${query})}       <!-- Query param -->
@{https://example.com}       <!-- Absolute URL -->
```

### ~{...} - Fragment Expression

**Ý nghĩa:** Tham chiếu fragment

```html
~{templateName :: fragmentName}
~{templateName :: #elementId}
~{templateName}  <!-- Toàn bộ template -->
~{:: fragmentName}  <!-- Fragment trong cùng file -->
```

### #{...} - Message Expression

**Ý nghĩa:** Internationalization (i18n)

```html
#{welcome.message}
#{hello.name(${name})}

<!-- messages.properties -->
<!-- welcome.message=Chào mừng! -->
<!-- hello.name=Xin chào, {0}! -->
```

### |...| - Literal Substitution

**Ý nghĩa:** Nối chuỗi dễ đọc

```html
<p th:text="|Hello, ${name}! You have ${count} messages.|">Text</p>

<!-- Thay vì -->
<p th:text="'Hello, ' + ${name} + '! You have ' + ${count} + ' messages.'">Text</p>
```

---

## 📝 Best Practices

1. **Dùng Layout Dialect** cho project lớn
2. **Tách fragments** cho các component tái sử dụng
3. **Dùng th:text** thay vì th:utext khi có thể
4. **Dùng *{...}** trong form thay vì ${object.property}
5. **Dùng @{...}** cho tất cả URLs
6. **Đặt giá trị mặc định** trong HTML để preview được
7. **Dùng utility objects** cho format ngày/số
8. **Validate form** và hiển thị errors với #fields
