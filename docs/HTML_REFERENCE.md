# 📄 HTML - Tài Liệu Hướng Dẫn Chi Tiết

Tài liệu giải thích chi tiết các thẻ HTML và thuộc tính thường dùng.

---

## 📋 Mục Lục
1. [Cấu trúc cơ bản](#1-cấu-trúc-cơ-bản)
2. [Thẻ Head](#2-thẻ-head)
3. [Thẻ Text](#3-thẻ-text)
4. [Thẻ Link và Media](#4-thẻ-link-và-media)
5. [Thẻ Container](#5-thẻ-container)
6. [Thẻ Form](#6-thẻ-form)
7. [Thẻ Table](#7-thẻ-table)
8. [Thuộc tính Global](#8-thuộc-tính-global)

---

## 1. Cấu Trúc Cơ Bản

### `<!DOCTYPE html>`
- **Ý nghĩa:** Khai báo loại tài liệu HTML5
- **Bắt buộc:** Có, phải đặt ở dòng đầu tiên
```html
<!DOCTYPE html>
```

### `<html>`
- **Ý nghĩa:** Thẻ gốc chứa toàn bộ nội dung trang
- **Thuộc tính:** `lang` - ngôn ngữ trang
```html
<html lang="vi">
    <!-- Nội dung trang -->
</html>
```

### `<head>`
- **Ý nghĩa:** Chứa thông tin meta, CSS, title (không hiển thị trên trang)
```html
<head>
    <meta charset="UTF-8">
    <title>Tiêu đề</title>
    <link rel="stylesheet" href="style.css">
</head>
```

### `<body>`
- **Ý nghĩa:** Chứa nội dung hiển thị trên trang
```html
<body>
    <h1>Nội dung trang</h1>
</body>
```

---

## 2. Thẻ Head

### `<meta>`
- **Ý nghĩa:** Khai báo thông tin về trang (không đóng thẻ)

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `charset="UTF-8"` | Bộ mã ký tự Unicode |
| `name="viewport"` | Thiết lập responsive |
| `name="description"` | Mô tả cho SEO |
| `name="keywords"` | Từ khóa cho SEO |
| `http-equiv="refresh"` | Tự động refresh trang |

```html
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="Mô tả trang web">
```

### `<title>`
- **Ý nghĩa:** Tiêu đề trang hiển thị trên tab trình duyệt
```html
<title>Trang chủ - Website</title>
```

### `<link>`
- **Ý nghĩa:** Liên kết tài nguyên bên ngoài (CSS, favicon)

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `rel` | Quan hệ (stylesheet, icon) |
| `href` | Đường dẫn file |
| `type` | Loại file |

```html
<link rel="stylesheet" href="style.css">
<link rel="icon" href="favicon.ico">
```

### `<script>`
- **Ý nghĩa:** Nhúng hoặc liên kết JavaScript

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `src` | Đường dẫn file JS |
| `defer` | Chạy sau khi DOM load xong |
| `async` | Chạy song song với DOM loading |

```html
<script src="app.js"></script>
<script src="app.js" defer></script>
<script>
    console.log('Inline script');
</script>
```

### `<style>`
- **Ý nghĩa:** CSS nội tuyến trong HTML
```html
<style>
    body { background: #fff; }
</style>
```

---

## 3. Thẻ Text

### Heading `<h1>` - `<h6>`
- **Ý nghĩa:** Tiêu đề, h1 lớn nhất, h6 nhỏ nhất
- **SEO:** Mỗi trang chỉ nên có 1 thẻ h1
```html
<h1>Tiêu đề chính</h1>
<h2>Tiêu đề phụ</h2>
<h3>Tiêu đề cấp 3</h3>
```

### `<p>`
- **Ý nghĩa:** Đoạn văn bản (paragraph)
```html
<p>Đây là một đoạn văn bản.</p>
```

### `<span>`
- **Ý nghĩa:** Container inline, không xuống dòng
- **Dùng để:** Style một phần text
```html
<p>Đây là <span class="highlight">từ quan trọng</span> trong câu.</p>
```

### `<strong>` và `<b>`
- **Ý nghĩa:** In đậm
- **Khác biệt:** `<strong>` có ý nghĩa ngữ nghĩa (quan trọng), `<b>` chỉ style
```html
<strong>Quan trọng</strong>
<b>In đậm</b>
```

### `<em>` và `<i>`
- **Ý nghĩa:** In nghiêng
- **Khác biệt:** `<em>` nhấn mạnh ngữ nghĩa, `<i>` chỉ style
```html
<em>Nhấn mạnh</em>
<i>In nghiêng</i>
```

### `<br>`
- **Ý nghĩa:** Xuống dòng (không đóng thẻ)
```html
Dòng 1<br>
Dòng 2
```

### `<hr>`
- **Ý nghĩa:** Đường kẻ ngang phân cách (không đóng thẻ)
```html
<p>Phần 1</p>
<hr>
<p>Phần 2</p>
```

### `<small>`
- **Ý nghĩa:** Text nhỏ hơn bình thường
```html
<small>Chú thích nhỏ</small>
```

### `<code>`
- **Ý nghĩa:** Hiển thị code (font monospace)
```html
<code>console.log('Hello');</code>
```

### `<pre>`
- **Ý nghĩa:** Giữ nguyên định dạng (khoảng trắng, xuống dòng)
```html
<pre>
    function hello() {
        return 'Hello';
    }
</pre>
```

---

## 4. Thẻ Link và Media

### `<a>`
- **Ý nghĩa:** Liên kết (anchor)

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `href` | URL đích |
| `target="_blank"` | Mở tab mới |
| `target="_self"` | Mở cùng tab (mặc định) |
| `title` | Tooltip khi hover |
| `download` | Tải file thay vì mở |

```html
<a href="https://google.com">Google</a>
<a href="/page" target="_blank">Mở tab mới</a>
<a href="#section1">Cuộn đến section1</a>
<a href="file.pdf" download>Tải PDF</a>
```

### `<img>`
- **Ý nghĩa:** Hình ảnh (không đóng thẻ)

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `src` | Đường dẫn ảnh |
| `alt` | Text thay thế (SEO, accessibility) |
| `width`, `height` | Kích thước |
| `loading="lazy"` | Lazy load |

```html
<img src="image.jpg" alt="Mô tả ảnh" width="300">
<img src="photo.png" alt="Ảnh" loading="lazy">
```

### `<video>`
- **Ý nghĩa:** Video

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `src` | Đường dẫn video |
| `controls` | Hiển thị nút điều khiển |
| `autoplay` | Tự động phát |
| `muted` | Tắt tiếng |
| `loop` | Lặp lại |
| `poster` | Ảnh thumbnail |

```html
<video src="video.mp4" controls width="640"></video>
<video autoplay muted loop>
    <source src="video.mp4" type="video/mp4">
</video>
```

### `<audio>`
- **Ý nghĩa:** Âm thanh
```html
<audio src="sound.mp3" controls></audio>
```

### `<iframe>`
- **Ý nghĩa:** Nhúng trang web khác

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `src` | URL trang nhúng |
| `width`, `height` | Kích thước |
| `frameborder` | Viền khung |
| `allowfullscreen` | Cho phép fullscreen |

```html
<iframe src="https://youtube.com/embed/xxx" width="560" height="315"></iframe>
```

---

## 5. Thẻ Container

### `<div>`
- **Ý nghĩa:** Container block (xuống dòng)
- **Dùng để:** Nhóm và layout các phần tử
```html
<div class="container">
    <div class="row">
        <div class="col">Cột 1</div>
        <div class="col">Cột 2</div>
    </div>
</div>
```

### Semantic Tags (HTML5)
- **Ý nghĩa:** Thẻ có ngữ nghĩa rõ ràng, tốt cho SEO và accessibility

| Thẻ | Ý nghĩa |
|-----|---------|
| `<header>` | Phần đầu trang/section |
| `<nav>` | Điều hướng |
| `<main>` | Nội dung chính (1 trang chỉ có 1) |
| `<section>` | Phần nội dung |
| `<article>` | Bài viết độc lập |
| `<aside>` | Nội dung phụ (sidebar) |
| `<footer>` | Phần chân trang/section |

```html
<header>Logo và navigation</header>
<nav>Menu</nav>
<main>
    <section>Phần 1</section>
    <article>Bài viết</article>
</main>
<aside>Sidebar</aside>
<footer>Copyright</footer>
```

### `<ul>` và `<ol>`
- **Ý nghĩa:** Danh sách không/có thứ tự

```html
<!-- Danh sách không thứ tự -->
<ul>
    <li>Mục 1</li>
    <li>Mục 2</li>
</ul>

<!-- Danh sách có thứ tự -->
<ol>
    <li>Bước 1</li>
    <li>Bước 2</li>
</ol>
```

---

## 6. Thẻ Form

### `<form>`
- **Ý nghĩa:** Form nhập liệu

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `action` | URL xử lý form |
| `method` | GET hoặc POST |
| `enctype` | Kiểu mã hóa (multipart/form-data cho upload file) |

```html
<form action="/submit" method="post">
    <!-- Các input -->
</form>
```

### `<input>`
- **Ý nghĩa:** Ô nhập liệu (không đóng thẻ)

| type | Ý nghĩa |
|------|---------|
| `text` | Text một dòng |
| `password` | Mật khẩu (ẩn ký tự) |
| `email` | Email (có validate) |
| `number` | Số |
| `tel` | Số điện thoại |
| `date` | Chọn ngày |
| `time` | Chọn giờ |
| `datetime-local` | Ngày giờ |
| `file` | Upload file |
| `checkbox` | Hộp kiểm |
| `radio` | Nút chọn (chọn 1) |
| `hidden` | Ẩn |
| `submit` | Nút gửi form |
| `button` | Nút bấm |
| `reset` | Xóa form |

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `name` | Tên field (gửi lên server) |
| `value` | Giá trị |
| `placeholder` | Gợi ý |
| `required` | Bắt buộc |
| `disabled` | Vô hiệu hóa |
| `readonly` | Chỉ đọc |
| `min`, `max` | Giá trị min/max |
| `minlength`, `maxlength` | Độ dài min/max |
| `pattern` | Regex validate |
| `autocomplete` | Tự động điền |

```html
<input type="text" name="username" placeholder="Nhập tên" required>
<input type="email" name="email" required>
<input type="password" name="password" minlength="6">
<input type="number" name="age" min="1" max="100">
<input type="file" name="avatar" accept="image/*">
<input type="checkbox" name="agree" checked>
<input type="radio" name="gender" value="male"> Nam
<input type="radio" name="gender" value="female"> Nữ
<input type="submit" value="Gửi">
```

### `<textarea>`
- **Ý nghĩa:** Ô nhập text nhiều dòng

```html
<textarea name="content" rows="5" cols="50" placeholder="Nhập nội dung"></textarea>
```

### `<select>` và `<option>`
- **Ý nghĩa:** Dropdown chọn

```html
<select name="city">
    <option value="">-- Chọn thành phố --</option>
    <option value="hn">Hà Nội</option>
    <option value="hcm" selected>TP.HCM</option>
    <option value="dn">Đà Nẵng</option>
</select>

<!-- Multiple select -->
<select name="skills" multiple>
    <option value="html">HTML</option>
    <option value="css">CSS</option>
</select>
```

### `<label>`
- **Ý nghĩa:** Nhãn cho input (click vào label = focus input)

```html
<label for="email">Email:</label>
<input type="email" id="email" name="email">

<!-- Hoặc bọc input -->
<label>
    <input type="checkbox" name="agree"> Đồng ý
</label>
```

### `<button>`
- **Ý nghĩa:** Nút bấm

| type | Ý nghĩa |
|------|---------|
| `submit` | Gửi form (mặc định) |
| `button` | Không gửi form |
| `reset` | Xóa form |

```html
<button type="submit">Gửi</button>
<button type="button" onclick="doSomething()">Click</button>
```

---

## 7. Thẻ Table

### Cấu trúc Table

```html
<table>
    <thead>
        <tr>
            <th>Cột 1</th>
            <th>Cột 2</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td>Dữ liệu 1</td>
            <td>Dữ liệu 2</td>
        </tr>
    </tbody>
    <tfoot>
        <tr>
            <td colspan="2">Footer</td>
        </tr>
    </tfoot>
</table>
```

| Thẻ | Ý nghĩa |
|-----|---------|
| `<table>` | Bảng |
| `<thead>` | Phần header |
| `<tbody>` | Phần body |
| `<tfoot>` | Phần footer |
| `<tr>` | Hàng (table row) |
| `<th>` | Ô header (in đậm, căn giữa) |
| `<td>` | Ô dữ liệu (table data) |

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `colspan` | Gộp ngang (số cột) |
| `rowspan` | Gộp dọc (số hàng) |

---

## 8. Thuộc Tính Global

Các thuộc tính có thể dùng cho mọi thẻ HTML:

| Thuộc tính | Ý nghĩa |
|------------|---------|
| `id` | ID duy nhất |
| `class` | Class CSS (có thể nhiều) |
| `style` | CSS inline |
| `title` | Tooltip khi hover |
| `hidden` | Ẩn phần tử |
| `data-*` | Dữ liệu tùy chỉnh |
| `tabindex` | Thứ tự tab |
| `contenteditable` | Cho phép sửa nội dung |
| `draggable` | Cho phép kéo thả |

```html
<div id="main" class="container active" style="color: red;" title="Tooltip">
    Nội dung
</div>

<div data-user-id="123" data-role="admin">
    Truy cập: element.dataset.userId
</div>

<p contenteditable="true">Click để sửa</p>
```

---

## 📝 Best Practices

1. **Luôn khai báo DOCTYPE** ở đầu file
2. **Sử dụng semantic tags** thay vì div khi có thể
3. **Luôn có alt cho img** (accessibility, SEO)
4. **Đặt CSS ở head, JS ở cuối body** (hoặc dùng defer)
5. **Đặt charset UTF-8** ở đầu head
6. **Sử dụng label cho form inputs**
7. **Validate HTML** với W3C Validator
