# 🎨 CSS - Tài Liệu Hướng Dẫn Chi Tiết

Tài liệu giải thích chi tiết các thuộc tính CSS thường dùng.

---

## 📋 Mục Lục
1. [Cú pháp cơ bản](#1-cú-pháp-cơ-bản)
2. [Selectors](#2-selectors)
3. [Box Model](#3-box-model)
4. [Typography](#4-typography)
5. [Colors và Background](#5-colors-và-background)
6. [Display và Position](#6-display-và-position)
7. [Flexbox](#7-flexbox)
8. [Grid](#8-grid)
9. [Transitions và Animations](#9-transitions-và-animations)
10. [Responsive Design](#10-responsive-design)
11. [Pseudo-classes và Pseudo-elements](#11-pseudo-classes-và-pseudo-elements)

---

## 1. Cú Pháp Cơ Bản

### Cấu trúc

```css
selector {
    property: value;
    property: value;
}
```

### Cách nhúng CSS

```html
<!-- 1. External CSS (khuyến khích) -->
<link rel="stylesheet" href="style.css">

<!-- 2. Internal CSS -->
<style>
    body { background: #fff; }
</style>

<!-- 3. Inline CSS (tránh dùng) -->
<div style="color: red;">Text</div>
```

### CSS Variables (Custom Properties)

```css
:root {
    --primary-color: #3498db;
    --spacing: 16px;
    --font-size: 14px;
}

.element {
    color: var(--primary-color);
    padding: var(--spacing);
    font-size: var(--font-size);
}
```

---

## 2. Selectors

### Selector cơ bản

| Selector | Ý nghĩa | Ví dụ |
|----------|---------|-------|
| `*` | Tất cả phần tử | `* { margin: 0; }` |
| `element` | Theo tên thẻ | `div { }` |
| `.class` | Theo class | `.card { }` |
| `#id` | Theo ID | `#header { }` |

### Selector kết hợp

| Selector | Ý nghĩa | Ví dụ |
|----------|---------|-------|
| `A B` | B là con/cháu của A | `.card p` |
| `A > B` | B là con trực tiếp của A | `.card > p` |
| `A + B` | B ngay sau A (cùng cấp) | `h1 + p` |
| `A ~ B` | Tất cả B sau A (cùng cấp) | `h1 ~ p` |
| `A.class` | A có class | `div.container` |
| `A, B` | Cả A và B | `h1, h2, h3` |

### Selector thuộc tính

```css
/* Có thuộc tính */
[disabled] { }

/* Thuộc tính = giá trị */
[type="text"] { }

/* Bắt đầu bằng */
[href^="https"] { }

/* Kết thúc bằng */
[src$=".png"] { }

/* Chứa */
[class*="btn"] { }
```

---

## 3. Box Model

### Cấu trúc Box Model

```
┌─────────────────────────────────┐
│           margin                │
│   ┌─────────────────────────┐   │
│   │        border           │   │
│   │   ┌─────────────────┐   │   │
│   │   │     padding     │   │   │
│   │   │   ┌─────────┐   │   │   │
│   │   │   │ content │   │   │   │
│   │   │   └─────────┘   │   │   │
│   │   └─────────────────┘   │   │
│   └─────────────────────────┘   │
└─────────────────────────────────┘
```

### Width và Height

| Property | Ý nghĩa |
|----------|---------|
| `width` | Chiều rộng |
| `height` | Chiều cao |
| `min-width` | Chiều rộng tối thiểu |
| `max-width` | Chiều rộng tối đa |
| `min-height` | Chiều cao tối thiểu |
| `max-height` | Chiều cao tối đa |

```css
.element {
    width: 100%;
    max-width: 1200px;
    height: auto;
    min-height: 100vh;
}
```

### Box-sizing

```css
/* Mặc định: width = content */
box-sizing: content-box;

/* width = content + padding + border (khuyến khích) */
box-sizing: border-box;

/* Áp dụng cho tất cả */
*, *::before, *::after {
    box-sizing: border-box;
}
```

### Padding

```css
/* Tất cả các hướng */
padding: 10px;

/* Trên-dưới | Trái-phải */
padding: 10px 20px;

/* Trên | Trái-phải | Dưới */
padding: 10px 20px 15px;

/* Trên | Phải | Dưới | Trái (theo chiều kim đồng hồ) */
padding: 10px 20px 15px 25px;

/* Từng hướng riêng */
padding-top: 10px;
padding-right: 20px;
padding-bottom: 15px;
padding-left: 25px;
```

### Margin

```css
/* Giống padding */
margin: 10px;
margin: 10px 20px;
margin-top: 10px;

/* Căn giữa block element */
margin: 0 auto;

/* Margin âm (kéo gần lại) */
margin-top: -10px;
```

### Border

```css
/* Shorthand: width style color */
border: 1px solid #000;

/* Từng thuộc tính */
border-width: 1px;
border-style: solid;  /* solid, dashed, dotted, double, none */
border-color: #000;

/* Từng hướng */
border-top: 2px dashed red;
border-bottom: none;

/* Bo góc */
border-radius: 5px;
border-radius: 50%;  /* Tròn */
border-radius: 10px 20px 30px 40px;  /* Từng góc */
```

### Outline

```css
/* Giống border nhưng không chiếm không gian */
outline: 2px solid blue;
outline-offset: 5px;  /* Khoảng cách với border */
```

---

## 4. Typography

### Font

| Property | Ý nghĩa | Giá trị |
|----------|---------|---------|
| `font-family` | Font chữ | `Arial, sans-serif` |
| `font-size` | Cỡ chữ | `16px`, `1rem`, `1.2em` |
| `font-weight` | Độ đậm | `normal`, `bold`, `100`-`900` |
| `font-style` | Kiểu chữ | `normal`, `italic` |
| `line-height` | Chiều cao dòng | `1.5`, `24px` |
| `letter-spacing` | Khoảng cách chữ | `1px`, `0.1em` |

```css
.text {
    font-family: 'Open Sans', Arial, sans-serif;
    font-size: 16px;
    font-weight: 600;
    font-style: normal;
    line-height: 1.6;
    letter-spacing: 0.5px;
}

/* Shorthand */
font: italic bold 16px/1.5 Arial, sans-serif;
```

### Text

| Property | Ý nghĩa | Giá trị |
|----------|---------|---------|
| `color` | Màu chữ | `#333`, `rgb()`, `hsl()` |
| `text-align` | Căn lề | `left`, `center`, `right`, `justify` |
| `text-decoration` | Trang trí | `none`, `underline`, `line-through` |
| `text-transform` | Chuyển đổi | `uppercase`, `lowercase`, `capitalize` |
| `text-indent` | Thụt đầu dòng | `20px` |
| `text-shadow` | Đổ bóng | `1px 1px 2px rgba(0,0,0,0.5)` |
| `white-space` | Xử lý khoảng trắng | `normal`, `nowrap`, `pre` |
| `word-break` | Ngắt từ | `normal`, `break-all`, `break-word` |
| `overflow-wrap` | Ngắt dòng | `normal`, `break-word` |

```css
.text {
    color: #333;
    text-align: center;
    text-decoration: underline;
    text-transform: uppercase;
    text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}
```

---

## 5. Colors và Background

### Cách viết màu

```css
/* Tên màu */
color: red;
color: transparent;

/* Hex */
color: #ff0000;
color: #f00;  /* Viết tắt */

/* RGB / RGBA */
color: rgb(255, 0, 0);
color: rgba(255, 0, 0, 0.5);  /* 50% trong suốt */

/* HSL / HSLA */
color: hsl(0, 100%, 50%);
color: hsla(0, 100%, 50%, 0.5);
```

### Background

| Property | Ý nghĩa |
|----------|---------|
| `background-color` | Màu nền |
| `background-image` | Ảnh nền |
| `background-size` | Kích thước ảnh nền |
| `background-position` | Vị trí ảnh nền |
| `background-repeat` | Lặp ảnh nền |
| `background-attachment` | Cuộn ảnh nền |

```css
.element {
    background-color: #f5f5f5;
    background-image: url('image.jpg');
    background-size: cover;  /* cover, contain, 100% */
    background-position: center center;
    background-repeat: no-repeat;
    background-attachment: fixed;  /* fixed, scroll */
}

/* Shorthand */
background: #f5f5f5 url('image.jpg') no-repeat center/cover;

/* Gradient */
background: linear-gradient(to right, #ff0000, #0000ff);
background: linear-gradient(45deg, #ff0000, #0000ff);
background: radial-gradient(circle, #ff0000, #0000ff);
```

---

## 6. Display và Position

### Display

| Giá trị | Ý nghĩa |
|---------|---------|
| `block` | Chiếm toàn bộ chiều ngang, xuống dòng |
| `inline` | Chỉ chiếm nội dung, không xuống dòng |
| `inline-block` | Như inline nhưng có width/height |
| `flex` | Flexbox container |
| `grid` | Grid container |
| `none` | Ẩn hoàn toàn |

```css
.element {
    display: block;
    display: inline;
    display: inline-block;
    display: flex;
    display: grid;
    display: none;
}
```

### Visibility

```css
/* Ẩn nhưng vẫn chiếm không gian */
visibility: hidden;

/* Hiển thị */
visibility: visible;
```

### Position

| Giá trị | Ý nghĩa |
|---------|---------|
| `static` | Mặc định, theo luồng văn bản |
| `relative` | Tương đối với vị trí gốc |
| `absolute` | Tuyệt đối với parent có position |
| `fixed` | Cố định với viewport |
| `sticky` | Dính khi cuộn |

```css
.parent {
    position: relative;
}

.child {
    position: absolute;
    top: 0;
    right: 0;
    bottom: 0;
    left: 0;
}

.fixed {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
}

.sticky {
    position: sticky;
    top: 20px;
}
```

### Z-index

```css
/* Thứ tự xếp chồng (cần position khác static) */
z-index: 1;
z-index: 100;
z-index: 9999;
z-index: -1;
```

### Overflow

```css
/* Xử lý nội dung tràn */
overflow: visible;  /* Hiển thị tràn (mặc định) */
overflow: hidden;   /* Ẩn phần tràn */
overflow: scroll;   /* Luôn có scrollbar */
overflow: auto;     /* Scrollbar khi cần */

overflow-x: hidden;
overflow-y: auto;
```

---

## 7. Flexbox

### Container (Parent)

```css
.container {
    display: flex;
    
    /* Hướng sắp xếp */
    flex-direction: row;           /* Ngang (mặc định) */
    flex-direction: row-reverse;   /* Ngang ngược */
    flex-direction: column;        /* Dọc */
    flex-direction: column-reverse;/* Dọc ngược */
    
    /* Xuống dòng */
    flex-wrap: nowrap;   /* Không xuống dòng (mặc định) */
    flex-wrap: wrap;     /* Xuống dòng */
    
    /* Căn chính (main axis) */
    justify-content: flex-start;   /* Đầu */
    justify-content: flex-end;     /* Cuối */
    justify-content: center;       /* Giữa */
    justify-content: space-between;/* Căng đều, không có space 2 đầu */
    justify-content: space-around; /* Căng đều, có space 2 đầu */
    justify-content: space-evenly; /* Căng đều hoàn toàn */
    
    /* Căn phụ (cross axis) */
    align-items: stretch;    /* Căng full (mặc định) */
    align-items: flex-start; /* Đầu */
    align-items: flex-end;   /* Cuối */
    align-items: center;     /* Giữa */
    align-items: baseline;   /* Theo baseline text */
    
    /* Khoảng cách giữa items */
    gap: 10px;
    row-gap: 10px;
    column-gap: 20px;
}
```

### Items (Children)

```css
.item {
    /* Tỉ lệ phóng to */
    flex-grow: 0;   /* Không phóng to (mặc định) */
    flex-grow: 1;   /* Phóng to đều */
    
    /* Tỉ lệ co lại */
    flex-shrink: 1; /* Co lại khi cần (mặc định) */
    flex-shrink: 0; /* Không co lại */
    
    /* Kích thước cơ bản */
    flex-basis: auto;
    flex-basis: 200px;
    
    /* Shorthand: grow shrink basis */
    flex: 1;       /* flex: 1 1 0% */
    flex: 0 0 auto;
    
    /* Ghi đè align-items cho item này */
    align-self: center;
    
    /* Thứ tự hiển thị */
    order: 0;  /* Mặc định */
    order: -1; /* Lên đầu */
    order: 1;  /* Xuống sau */
}
```

---

## 8. Grid

### Container

```css
.container {
    display: grid;
    
    /* Định nghĩa cột */
    grid-template-columns: 200px 200px 200px;
    grid-template-columns: repeat(3, 200px);
    grid-template-columns: repeat(3, 1fr);  /* 3 cột đều */
    grid-template-columns: 1fr 2fr 1fr;     /* Tỉ lệ 1:2:1 */
    grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
    
    /* Định nghĩa hàng */
    grid-template-rows: 100px auto 100px;
    
    /* Khoảng cách */
    gap: 10px;
    row-gap: 10px;
    column-gap: 20px;
    
    /* Căn chỉnh items */
    justify-items: start | end | center | stretch;
    align-items: start | end | center | stretch;
    
    /* Căn chỉnh grid */
    justify-content: start | end | center | space-between | space-around;
    align-content: start | end | center | space-between | space-around;
}
```

### Items

```css
.item {
    /* Vị trí cột */
    grid-column-start: 1;
    grid-column-end: 3;
    grid-column: 1 / 3;      /* Từ cột 1 đến cột 3 */
    grid-column: 1 / span 2; /* Từ cột 1, chiếm 2 cột */
    
    /* Vị trí hàng */
    grid-row: 1 / 3;
    
    /* Shorthand */
    grid-area: 1 / 1 / 3 / 3; /* row-start / col-start / row-end / col-end */
}
```

---

## 9. Transitions và Animations

### Transition

```css
.element {
    /* Shorthand: property duration timing-function delay */
    transition: all 0.3s ease;
    transition: background-color 0.3s ease-in-out;
    transition: transform 0.5s cubic-bezier(0.4, 0, 0.2, 1);
    
    /* Từng thuộc tính */
    transition-property: background-color, transform;
    transition-duration: 0.3s;
    transition-timing-function: ease;  /* ease, linear, ease-in, ease-out */
    transition-delay: 0.1s;
}

.element:hover {
    background-color: blue;
    transform: scale(1.1);
}
```

### Animation

```css
/* Định nghĩa animation */
@keyframes fadeIn {
    from { opacity: 0; }
    to { opacity: 1; }
}

@keyframes slide {
    0% { transform: translateX(-100%); }
    50% { transform: translateX(10%); }
    100% { transform: translateX(0); }
}

/* Sử dụng animation */
.element {
    /* Shorthand */
    animation: fadeIn 1s ease-in-out;
    animation: slide 0.5s ease forwards;
    
    /* Chi tiết */
    animation-name: fadeIn;
    animation-duration: 1s;
    animation-timing-function: ease-in-out;
    animation-delay: 0.5s;
    animation-iteration-count: infinite;  /* Số lần lặp */
    animation-direction: alternate;        /* normal, reverse, alternate */
    animation-fill-mode: forwards;         /* none, forwards, backwards, both */
    animation-play-state: running;         /* running, paused */
}
```

### Transform

```css
.element {
    /* Di chuyển */
    transform: translateX(50px);
    transform: translateY(-20px);
    transform: translate(50px, -20px);
    
    /* Xoay */
    transform: rotate(45deg);
    transform: rotateX(45deg);
    transform: rotateY(45deg);
    
    /* Phóng to/thu nhỏ */
    transform: scale(1.5);
    transform: scale(1.5, 2);
    
    /* Nghiêng */
    transform: skew(10deg);
    transform: skewX(10deg);
    
    /* Kết hợp */
    transform: translateX(50px) rotate(45deg) scale(1.2);
    
    /* Điểm gốc biến đổi */
    transform-origin: center center;  /* Mặc định */
    transform-origin: top left;
    transform-origin: 50% 50%;
}
```

---

## 10. Responsive Design

### Media Queries

```css
/* Mobile First (khuyến khích) */
.element { width: 100%; }

@media (min-width: 576px) {
    .element { width: 540px; }
}

@media (min-width: 768px) {
    .element { width: 720px; }
}

@media (min-width: 992px) {
    .element { width: 960px; }
}

@media (min-width: 1200px) {
    .element { width: 1140px; }
}

/* Desktop First */
@media (max-width: 768px) {
    .element { display: none; }
}

/* Kết hợp */
@media (min-width: 768px) and (max-width: 991px) {
    .element { width: 50%; }
}

/* Orientation */
@media (orientation: landscape) {
    .element { flex-direction: row; }
}

/* Print */
@media print {
    .no-print { display: none; }
}
```

### Units

| Unit | Ý nghĩa |
|------|---------|
| `px` | Pixels (cố định) |
| `%` | Phần trăm parent |
| `em` | Tỉ lệ với font-size parent |
| `rem` | Tỉ lệ với font-size root (html) |
| `vw` | 1% viewport width |
| `vh` | 1% viewport height |
| `vmin` | Min của vw và vh |
| `vmax` | Max của vw và vh |

---

## 11. Pseudo-classes và Pseudo-elements

### Pseudo-classes (Trạng thái)

```css
/* Hover */
a:hover { color: red; }

/* Focus */
input:focus { border-color: blue; }

/* Active (đang click) */
button:active { transform: scale(0.95); }

/* Visited (link đã thăm) */
a:visited { color: purple; }

/* First/Last child */
li:first-child { font-weight: bold; }
li:last-child { border-bottom: none; }

/* Nth child */
tr:nth-child(odd) { background: #f5f5f5; }
tr:nth-child(even) { background: #fff; }
tr:nth-child(3n) { /* Mỗi 3 phần tử */ }

/* Not */
p:not(.special) { color: gray; }

/* Empty */
div:empty { display: none; }

/* Disabled/Enabled */
input:disabled { opacity: 0.5; }
input:enabled { opacity: 1; }

/* Checked */
input:checked + label { font-weight: bold; }
```

### Pseudo-elements (Phần tử ảo)

```css
/* Before - chèn trước nội dung */
.element::before {
    content: "→ ";
    color: red;
}

/* After - chèn sau nội dung */
.element::after {
    content: " ←";
    color: blue;
}

/* First letter */
p::first-letter {
    font-size: 2em;
    font-weight: bold;
}

/* First line */
p::first-line {
    font-weight: bold;
}

/* Selection (khi bôi đen) */
::selection {
    background: yellow;
    color: black;
}

/* Placeholder */
input::placeholder {
    color: gray;
    font-style: italic;
}

/* Scrollbar */
::-webkit-scrollbar {
    width: 8px;
}
::-webkit-scrollbar-thumb {
    background: #888;
    border-radius: 4px;
}
```

---

## 📝 Best Practices

1. **Dùng `box-sizing: border-box`** cho tất cả phần tử
2. **Mobile-first approach** cho responsive
3. **Dùng CSS Variables** cho colors, spacing
4. **Đặt tên class theo BEM** (Block__Element--Modifier)
5. **Tránh dùng `!important`**
6. **Tránh inline styles**
7. **Nhóm CSS theo component**
8. **Dùng shorthand properties** khi có thể
