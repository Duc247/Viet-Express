# 📚 Hướng Dẫn Câu Lệnh Thường Dùng

Tài liệu tổng hợp các câu lệnh, cú pháp thường dùng trong dự án Spring Boot + Thymeleaf.

---

## 📋 Mục Lục
1. [Thymeleaf - Kết nối View với Controller](#1-thymeleaf---kết-nối-view-với-controller)
2. [Thymeleaf - View trong View (Fragments)](#2-thymeleaf---view-trong-view-fragments)
3. [Thymeleaf - Hiển thị dữ liệu](#3-thymeleaf---hiển-thị-dữ-liệu)
4. [HTML - Cấu trúc cơ bản](#4-html---cấu-trúc-cơ-bản)
5. [CSS - Cú pháp thường dùng](#5-css---cú-pháp-thường-dùng)
6. [JavaScript - Tương tác với DOM](#6-javascript---tương-tác-với-dom)
7. [Bootstrap - Classes phổ biến](#7-bootstrap---classes-phổ-biến)

---

## 1. Thymeleaf - Kết Nối View với Controller

### 🔗 Controller trả về View

```java
// Controller
@Controller
@RequestMapping("/shipper")
public class ShipperDashboardController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Gửi dữ liệu sang View
        model.addAttribute("trips", tripService.findAll());
        model.addAttribute("username", "Bác tài");
        
        return "shipper/dashboard";  // → templates/shipper/dashboard.html
    }
}
```

### 🔗 View nhận dữ liệu từ Controller

```html
<!-- templates/shipper/dashboard.html -->
<h1 th:text="${username}">Tên mặc định</h1>

<!-- Lặp qua danh sách -->
<div th:each="trip : ${trips}">
    <span th:text="${trip.id}">ID</span>
</div>
```

### 🔗 Luồng dữ liệu

```
[Browser] → GET /shipper/dashboard
    ↓
[Controller] dashboard(Model model)
    ↓
model.addAttribute("trips", data)
    ↓
return "shipper/dashboard"
    ↓
[Thymeleaf Engine] Render HTML với data
    ↓
[Browser] ← HTML Response
```

---

## 2. Thymeleaf - View trong View (Fragments)

### 📦 Định nghĩa Fragment

```html
<!-- templates/shipper/fragments/header.html -->
<nav th:fragment="header" class="navbar">
    <a th:href="@{/shipper/dashboard}">Home</a>
    <span th:text="${username}">User</span>
</nav>
```

### 📦 Sử dụng Fragment

```html
<!-- templates/shipper/dashboard.html -->

<!-- Cách 1: th:replace (thay thế hoàn toàn) -->
<div th:replace="~{shipper/fragments/header :: header}"></div>

<!-- Cách 2: th:insert (giữ thẻ bao ngoài) -->
<div th:insert="~{shipper/fragments/header :: header}"></div>

<!-- Cách 3: th:include (chỉ lấy nội dung bên trong) -->
<div th:include="~{shipper/fragments/header :: header}"></div>
```

### 📦 Layout Decorator (Thymeleaf Layout Dialect)

```html
<!-- Layout chính: templates/shipper/layout-shipper.html -->
<!DOCTYPE html>
<html xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <title layout:title-pattern="$CONTENT_TITLE - Driver App">App</title>
</head>
<body>
    <div th:replace="~{shipper/fragments/header :: header}"></div>
    
    <!-- Nội dung con sẽ được chèn vào đây -->
    <div layout:fragment="content"></div>
    
    <div th:replace="~{shipper/fragments/footer :: footer}"></div>
</body>
</html>
```

```html
<!-- Trang con: templates/shipper/dashboard.html -->
<html layout:decorate="~{shipper/layout-shipper}">
<head>
    <title>Tổng Quan</title>
</head>
<body>
    <!-- Nội dung sẽ được chèn vào layout:fragment="content" -->
    <div layout:fragment="content">
        <h1>Dashboard</h1>
        <p>Nội dung trang...</p>
    </div>
</body>
</html>
```

---

## 3. Thymeleaf - Hiển Thị Dữ Liệu

### 📝 Hiển thị Text

```html
<!-- Thay thế nội dung -->
<span th:text="${user.name}">Tên mặc định</span>

<!-- Không escape HTML -->
<div th:utext="${htmlContent}">HTML content</div>

<!-- Nối chuỗi -->
<p th:text="'Xin chào, ' + ${user.name} + '!'">Xin chào!</p>

<!-- Sử dụng |...| (literal substitution) -->
<p th:text="|Xin chào, ${user.name}!|">Xin chào!</p>
```

### 🔗 URL và Link

```html
<!-- Static resource -->
<link th:href="@{/css/style.css}" rel="stylesheet">
<script th:src="@{/js/app.js}"></script>
<img th:src="@{/images/logo.png}">

<!-- Link động với path variable -->
<a th:href="@{/shipper/trip/{id}(id=${trip.id})}">Chi tiết</a>
<!-- Kết quả: /shipper/trip/123 -->

<!-- Link với query parameters -->
<a th:href="@{/shipper/trips(filter='active', page=1)}">Lọc</a>
<!-- Kết quả: /shipper/trips?filter=active&page=1 -->

<!-- Context path tự động -->
<a th:href="@{/logout}">Đăng xuất</a>
```

### ❓ Điều kiện

```html
<!-- th:if - Hiển thị nếu true -->
<div th:if="${user.isActive}">Đang hoạt động</div>

<!-- th:unless - Hiển thị nếu false -->
<div th:unless="${user.isActive}">Ngừng hoạt động</div>

<!-- th:switch / th:case -->
<div th:switch="${user.role}">
    <p th:case="'ADMIN'">Quản trị viên</p>
    <p th:case="'SHIPPER'">Tài xế</p>
    <p th:case="*">Khách</p>  <!-- default -->
</div>

<!-- Toán tử 3 ngôi -->
<span th:text="${user.isActive} ? 'Active' : 'Inactive'">Status</span>

<!-- Elvis operator (giá trị mặc định nếu null) -->
<span th:text="${user.name} ?: 'Không có tên'">Name</span>
```

### 🔄 Vòng lặp

```html
<!-- Lặp qua List -->
<tr th:each="trip : ${trips}">
    <td th:text="${trip.id}">1</td>
    <td th:text="${trip.destination}">Hà Nội</td>
</tr>

<!-- Với biến trạng thái (iterStat) -->
<tr th:each="trip, stat : ${trips}">
    <td th:text="${stat.index}">0</td>      <!-- 0, 1, 2... -->
    <td th:text="${stat.count}">1</td>      <!-- 1, 2, 3... -->
    <td th:text="${stat.size}">10</td>      <!-- Tổng số phần tử -->
    <td th:text="${stat.first}">true</td>   <!-- Phần tử đầu? -->
    <td th:text="${stat.last}">false</td>   <!-- Phần tử cuối? -->
    <td th:text="${stat.odd}">true</td>     <!-- Vị trí lẻ? -->
    <td th:text="${stat.even}">false</td>   <!-- Vị trí chẵn? -->
</tr>

<!-- CSS class theo điều kiện trong vòng lặp -->
<tr th:each="trip : ${trips}" 
    th:class="${trip.isActive} ? 'table-success' : 'table-secondary'">
</tr>
```

### 🎨 Thuộc tính động

```html
<!-- Class động -->
<div th:class="${isError} ? 'alert-danger' : 'alert-success'">Message</div>

<!-- Thêm class (giữ class cũ) -->
<a class="nav-link" th:classappend="${isActive} ? 'active' : ''">Link</a>

<!-- Thuộc tính bất kỳ -->
<input th:value="${user.name}" th:disabled="${user.isLocked}">

<!-- Multiple attributes -->
<input th:attr="value=${user.name}, placeholder='Nhập tên'">

<!-- Checked/Selected -->
<input type="checkbox" th:checked="${user.isActive}">
<option th:selected="${item.id == selectedId}">Option</option>
```

### 📅 Format dữ liệu

```html
<!-- Format số -->
<span th:text="${#numbers.formatDecimal(price, 0, 'COMMA', 0, 'POINT')}">1,000,000</span>

<!-- Format ngày giờ (Java 8+) -->
<span th:text="${#temporals.format(date, 'dd/MM/yyyy')}">01/01/2026</span>
<span th:text="${#temporals.format(dateTime, 'HH:mm dd/MM/yyyy')}">12:00 01/01/2026</span>

<!-- String utilities -->
<span th:text="${#strings.toUpperCase(name)}">TÊN</span>
<span th:text="${#strings.abbreviate(text, 50)}">Text dài...</span>
<span th:if="${#strings.isEmpty(name)}">Không có tên</span>
<span th:if="${#strings.contains(name, 'admin')}">Admin</span>
```

---

## 4. HTML - Cấu Trúc Cơ Bản

### 📄 Template cơ bản

```html
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Tiêu đề trang</title>
    
    <!-- CSS -->
    <link rel="stylesheet" href="/css/style.css">
</head>
<body>
    <header>Header</header>
    <nav>Navigation</nav>
    <main>Nội dung chính</main>
    <aside>Sidebar</aside>
    <footer>Footer</footer>
    
    <!-- JavaScript -->
    <script src="/js/app.js"></script>
</body>
</html>
```

### 📝 Form

```html
<form th:action="@{/shipper/trip/create}" method="post" th:object="${tripDTO}">
    <!-- Input text -->
    <input type="text" th:field="*{destination}" class="form-control">
    
    <!-- Textarea -->
    <textarea th:field="*{notes}" class="form-control"></textarea>
    
    <!-- Select -->
    <select th:field="*{vehicleId}" class="form-select">
        <option value="">-- Chọn xe --</option>
        <option th:each="v : ${vehicles}" th:value="${v.id}" th:text="${v.name}">Xe</option>
    </select>
    
    <!-- Checkbox -->
    <input type="checkbox" th:field="*{isUrgent}"> Gấp
    
    <!-- Radio -->
    <input type="radio" th:field="*{status}" value="PENDING"> Chờ
    <input type="radio" th:field="*{status}" value="ACTIVE"> Hoạt động
    
    <!-- Hidden -->
    <input type="hidden" th:field="*{id}">
    
    <button type="submit" class="btn btn-primary">Lưu</button>
</form>
```

---

## 5. CSS - Cú Pháp Thường Dùng

### 🎨 Selectors

```css
/* Element */
div { }

/* Class */
.card { }

/* ID */
#header { }

/* Kết hợp */
div.card { }           /* div có class card */
.card .title { }       /* .title bên trong .card */
.card > .title { }     /* .title là con trực tiếp của .card */
.card + .footer { }    /* .footer ngay sau .card */

/* Pseudo-class */
a:hover { }            /* Khi hover */
input:focus { }        /* Khi focus */
li:first-child { }     /* Phần tử đầu */
li:last-child { }      /* Phần tử cuối */
li:nth-child(odd) { }  /* Phần tử lẻ */

/* Pseudo-element */
p::before { content: "→ "; }
p::after { content: " ←"; }
```

### 📐 Flexbox

```css
.container {
    display: flex;
    flex-direction: row;        /* row | column */
    justify-content: center;    /* Căn ngang: flex-start | center | flex-end | space-between | space-around */
    align-items: center;        /* Căn dọc: flex-start | center | flex-end | stretch */
    flex-wrap: wrap;            /* Xuống dòng khi hết chỗ */
    gap: 10px;                  /* Khoảng cách giữa items */
}

.item {
    flex: 1;                    /* Chiếm đều không gian */
    flex-grow: 1;               /* Tỉ lệ phóng to */
    flex-shrink: 0;             /* Không co lại */
    flex-basis: 200px;          /* Kích thước cơ bản */
}
```

### 🎬 Animation

```css
/* Định nghĩa animation */
@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

@keyframes slide {
    0% { transform: translateX(-100%); }
    100% { transform: translateX(0); }
}

/* Sử dụng animation */
.element {
    animation: fadeIn 1s ease-in-out;
    animation: slide 0.5s ease forwards;
    
    /* Chi tiết */
    animation-name: fadeIn;
    animation-duration: 1s;
    animation-timing-function: ease-in-out;
    animation-delay: 0.5s;
    animation-iteration-count: infinite;  /* Lặp vô hạn */
    animation-direction: alternate;       /* Đảo chiều */
}

/* Transition (chuyển đổi mượt) */
.button {
    transition: all 0.3s ease;
    /* transition: property duration timing-function delay; */
}

.button:hover {
    transform: scale(1.1);
    background-color: #ff0000;
}
```

---

## 6. JavaScript - Tương Tác với DOM

### 🔍 Lấy phần tử

```javascript
// Theo ID
const element = document.getElementById('myId');

// Theo class (trả về HTMLCollection)
const elements = document.getElementsByClassName('myClass');

// Theo tag (trả về HTMLCollection)
const divs = document.getElementsByTagName('div');

// Theo CSS selector (trả về phần tử đầu tiên)
const element = document.querySelector('.myClass');
const element = document.querySelector('#myId');
const element = document.querySelector('div.card > p');

// Theo CSS selector (trả về NodeList - tất cả phần tử)
const elements = document.querySelectorAll('.myClass');
```

### ✏️ Thao tác DOM

```javascript
// Thay đổi nội dung
element.textContent = 'Nội dung mới';      // Text thuần
element.innerHTML = '<b>HTML</b> mới';     // HTML

// Thay đổi thuộc tính
element.setAttribute('href', '/new-url');
element.getAttribute('href');
element.removeAttribute('disabled');

// Thay đổi style
element.style.color = 'red';
element.style.backgroundColor = '#fff';
element.style.display = 'none';

// Thay đổi class
element.classList.add('active');
element.classList.remove('active');
element.classList.toggle('active');
element.classList.contains('active');  // true/false

// Tạo phần tử mới
const newDiv = document.createElement('div');
newDiv.textContent = 'Hello';
newDiv.className = 'card';
document.body.appendChild(newDiv);

// Xóa phần tử
element.remove();
parentElement.removeChild(childElement);
```

### 🖱️ Sự kiện (Events)

```javascript
// Cách 1: addEventListener
element.addEventListener('click', function(event) {
    console.log('Clicked!', event.target);
});

// Cách 2: Arrow function
element.addEventListener('click', (e) => {
    e.preventDefault();  // Ngăn hành vi mặc định
    e.stopPropagation(); // Ngăn lan truyền event
});

// Các event phổ biến
element.addEventListener('click', handler);      // Click
element.addEventListener('dblclick', handler);   // Double click
element.addEventListener('mouseenter', handler); // Hover vào
element.addEventListener('mouseleave', handler); // Hover ra
element.addEventListener('keydown', handler);    // Nhấn phím
element.addEventListener('keyup', handler);      // Thả phím
element.addEventListener('submit', handler);     // Submit form
element.addEventListener('change', handler);     // Thay đổi input
element.addEventListener('input', handler);      // Đang nhập
element.addEventListener('load', handler);       // Tải xong
element.addEventListener('scroll', handler);     // Cuộn

// Cách 3: Inline (không khuyến khích)
<button onclick="handleClick()">Click</button>
```

### ⏰ Timer

```javascript
// Sau X giây
setTimeout(() => {
    console.log('Sau 2 giây');
}, 2000);

// Lặp lại mỗi X giây
const intervalId = setInterval(() => {
    console.log('Mỗi 3 giây');
}, 3000);

// Dừng interval
clearInterval(intervalId);
```

### 🌐 Fetch API (AJAX)

```javascript
// GET request
fetch('/api/trips')
    .then(response => response.json())
    .then(data => {
        console.log(data);
    })
    .catch(error => {
        console.error('Error:', error);
    });

// POST request
fetch('/api/trips', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({
        destination: 'Hà Nội',
        status: 'PENDING'
    })
})
.then(response => response.json())
.then(data => console.log(data));

// Async/Await
async function loadTrips() {
    try {
        const response = await fetch('/api/trips');
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error:', error);
    }
}
```

---

## 7. Bootstrap - Classes Phổ Biến

### 📦 Container và Grid

```html
<!-- Container -->
<div class="container">960px max-width</div>
<div class="container-fluid">100% width</div>

<!-- Grid -->
<div class="row">
    <div class="col-12">Full width (12 cột)</div>
    <div class="col-6">Half width (6 cột)</div>
    <div class="col-md-4">4 cột từ màn hình md trở lên</div>
    <div class="col-lg-3">3 cột từ màn hình lg trở lên</div>
</div>

<!-- Responsive: xs < 576px < sm < 768px < md < 992px < lg < 1200px < xl < 1400px < xxl -->
```

### 🎨 Màu sắc

```html
<!-- Background -->
<div class="bg-primary">Xanh dương</div>
<div class="bg-secondary">Xám</div>
<div class="bg-success">Xanh lá</div>
<div class="bg-danger">Đỏ</div>
<div class="bg-warning">Vàng</div>
<div class="bg-info">Xanh ngọc</div>
<div class="bg-light">Sáng</div>
<div class="bg-dark">Tối</div>

<!-- Text -->
<p class="text-primary">Text xanh</p>
<p class="text-muted">Text mờ</p>
<p class="text-white">Text trắng</p>
```

### 📐 Spacing (Margin & Padding)

```html
<!-- m = margin, p = padding -->
<!-- t = top, b = bottom, s = start(left), e = end(right), x = left+right, y = top+bottom -->
<!-- 0, 1, 2, 3, 4, 5, auto -->

<div class="m-3">margin: 1rem</div>
<div class="mt-3">margin-top: 1rem</div>
<div class="mb-3">margin-bottom: 1rem</div>
<div class="mx-auto">margin left+right auto (căn giữa)</div>

<div class="p-3">padding: 1rem</div>
<div class="py-2">padding top+bottom: 0.5rem</div>
<div class="px-4">padding left+right: 1.5rem</div>
```

### 📝 Text

```html
<p class="text-start">Căn trái</p>
<p class="text-center">Căn giữa</p>
<p class="text-end">Căn phải</p>

<p class="fw-bold">In đậm</p>
<p class="fw-normal">Bình thường</p>
<p class="fst-italic">In nghiêng</p>

<p class="text-uppercase">VIẾT HOA</p>
<p class="text-lowercase">viết thường</p>
<p class="text-capitalize">Viết Hoa Chữ Cái Đầu</p>

<p class="fs-1">Font size lớn nhất</p>
<p class="fs-6">Font size nhỏ nhất</p>
<p class="small">Text nhỏ</p>
```

### 📦 Flexbox (Bootstrap)

```html
<div class="d-flex">Display flex</div>
<div class="d-flex justify-content-center">Căn giữa ngang</div>
<div class="d-flex justify-content-between">Căng đều 2 bên</div>
<div class="d-flex align-items-center">Căn giữa dọc</div>
<div class="d-flex flex-column">Xếp dọc</div>
<div class="d-flex gap-3">Khoảng cách 1rem</div>
```

### 🔘 Buttons

```html
<button class="btn btn-primary">Primary</button>
<button class="btn btn-secondary">Secondary</button>
<button class="btn btn-success">Success</button>
<button class="btn btn-danger">Danger</button>
<button class="btn btn-outline-primary">Outline</button>
<button class="btn btn-lg">Large</button>
<button class="btn btn-sm">Small</button>
```

### 📋 Cards

```html
<div class="card">
    <div class="card-header">Header</div>
    <div class="card-body">
        <h5 class="card-title">Tiêu đề</h5>
        <p class="card-text">Nội dung</p>
        <a href="#" class="btn btn-primary">Action</a>
    </div>
    <div class="card-footer">Footer</div>
</div>
```

### 📊 Table

```html
<table class="table">
    <thead>
        <tr><th>ID</th><th>Tên</th></tr>
    </thead>
    <tbody>
        <tr><td>1</td><td>Nguyễn Văn A</td></tr>
    </tbody>
</table>

<!-- Variants -->
<table class="table table-striped">Sọc</table>
<table class="table table-hover">Hover</table>
<table class="table table-bordered">Viền</table>
<table class="table table-sm">Nhỏ gọn</table>
```

### 🚨 Alerts

```html
<div class="alert alert-success">Thành công!</div>
<div class="alert alert-danger">Lỗi!</div>
<div class="alert alert-warning">Cảnh báo!</div>
<div class="alert alert-info">Thông tin</div>

<!-- Dismiss -->
<div class="alert alert-warning alert-dismissible fade show">
    Cảnh báo
    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
</div>
```

### 🏷️ Badges

```html
<span class="badge bg-primary">Primary</span>
<span class="badge bg-success">Success</span>
<span class="badge rounded-pill bg-danger">Pill</span>
```

---

## 🔗 Tham Khảo

- [Thymeleaf Documentation](https://www.thymeleaf.org/documentation.html)
- [Bootstrap 5 Documentation](https://getbootstrap.com/docs/5.3/)
- [MDN Web Docs - JavaScript](https://developer.mozilla.org/en-US/docs/Web/JavaScript)
- [CSS-Tricks](https://css-tricks.com/)
