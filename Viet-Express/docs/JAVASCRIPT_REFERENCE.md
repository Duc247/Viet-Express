# ⚡ JavaScript - Tài Liệu Hướng Dẫn Chi Tiết

Tài liệu giải thích chi tiết các câu lệnh JavaScript thường dùng.

---

## 📋 Mục Lục
1. [Biến và Kiểu dữ liệu](#1-biến-và-kiểu-dữ-liệu)
2. [Operators](#2-operators)
3. [Điều kiện và Vòng lặp](#3-điều-kiện-và-vòng-lặp)
4. [Functions](#4-functions)
5. [Arrays](#5-arrays)
6. [Objects](#6-objects)
7. [DOM Manipulation](#7-dom-manipulation)
8. [Events](#8-events)
9. [Async JavaScript](#9-async-javascript)
10. [ES6+ Features](#10-es6-features)

---

## 1. Biến và Kiểu Dữ Liệu

### Khai báo biến

| Keyword | Ý nghĩa | Scope | Reassign | Redeclare |
|---------|---------|-------|----------|-----------|
| `var` | Cũ (tránh dùng) | Function | ✓ | ✓ |
| `let` | Biến có thể thay đổi | Block | ✓ | ✗ |
| `const` | Hằng số | Block | ✗ | ✗ |

```javascript
// let - có thể thay đổi giá trị
let name = 'Nguyễn Văn A';
name = 'Trần Văn B';  // OK

// const - không thể thay đổi giá trị
const PI = 3.14159;
PI = 3.14;  // Error!

// const với object/array - có thể thay đổi nội dung
const user = { name: 'A' };
user.name = 'B';  // OK
user = {};        // Error!
```

### Kiểu dữ liệu

| Kiểu | Ý nghĩa | Ví dụ |
|------|---------|-------|
| `String` | Chuỗi | `'Hello'`, `"World"`, `` `Template` `` |
| `Number` | Số | `42`, `3.14`, `NaN`, `Infinity` |
| `Boolean` | Logic | `true`, `false` |
| `undefined` | Chưa gán giá trị | `undefined` |
| `null` | Không có giá trị | `null` |
| `Object` | Đối tượng | `{}`, `[]`, `function(){}` |
| `Symbol` | Unique identifier | `Symbol('id')` |
| `BigInt` | Số nguyên lớn | `9007199254740991n` |

```javascript
// Kiểm tra kiểu
typeof 'Hello'      // 'string'
typeof 42           // 'number'
typeof true         // 'boolean'
typeof undefined    // 'undefined'
typeof null         // 'object' (bug lịch sử)
typeof {}           // 'object'
typeof []           // 'object'
typeof function(){} // 'function'

// Kiểm tra array
Array.isArray([])   // true

// Kiểm tra null
value === null      // true nếu null
```

### Ép kiểu (Type Conversion)

```javascript
// Sang String
String(123)         // '123'
(123).toString()    // '123'
123 + ''            // '123'

// Sang Number
Number('123')       // 123
parseInt('123')     // 123 (số nguyên)
parseFloat('3.14')  // 3.14 (số thực)
+'123'              // 123

// Sang Boolean
Boolean(1)          // true
Boolean(0)          // false
Boolean('')         // false
Boolean('text')     // true
!!value             // Nhanh nhất
```

---

## 2. Operators

### Toán tử số học

| Operator | Ý nghĩa | Ví dụ |
|----------|---------|-------|
| `+` | Cộng | `5 + 3` → `8` |
| `-` | Trừ | `5 - 3` → `2` |
| `*` | Nhân | `5 * 3` → `15` |
| `/` | Chia | `5 / 2` → `2.5` |
| `%` | Chia lấy dư | `5 % 2` → `1` |
| `**` | Lũy thừa | `2 ** 3` → `8` |
| `++` | Tăng 1 | `i++` hoặc `++i` |
| `--` | Giảm 1 | `i--` hoặc `--i` |

### Toán tử gán

| Operator | Ý nghĩa | Tương đương |
|----------|---------|-------------|
| `=` | Gán | `x = 5` |
| `+=` | Cộng và gán | `x = x + 5` |
| `-=` | Trừ và gán | `x = x - 5` |
| `*=` | Nhân và gán | `x = x * 5` |
| `/=` | Chia và gán | `x = x / 5` |
| `%=` | Chia dư và gán | `x = x % 5` |

### Toán tử so sánh

| Operator | Ý nghĩa |
|----------|---------|
| `==` | Bằng (so sánh giá trị, ép kiểu) |
| `===` | Bằng tuyệt đối (cả kiểu và giá trị) |
| `!=` | Không bằng |
| `!==` | Không bằng tuyệt đối |
| `>` | Lớn hơn |
| `<` | Nhỏ hơn |
| `>=` | Lớn hơn hoặc bằng |
| `<=` | Nhỏ hơn hoặc bằng |

```javascript
5 == '5'    // true (ép kiểu)
5 === '5'   // false (khác kiểu)
5 === 5     // true

// Luôn dùng === và !== để tránh bug
```

### Toán tử logic

| Operator | Ý nghĩa |
|----------|---------|
| `&&` | AND (và) |
| `\|\|` | OR (hoặc) |
| `!` | NOT (phủ định) |
| `??` | Nullish coalescing |

```javascript
// AND - trả về giá trị falsy đầu tiên hoặc giá trị cuối
true && 'Hello'     // 'Hello'
false && 'Hello'    // false
'' && 'Hello'       // ''

// OR - trả về giá trị truthy đầu tiên hoặc giá trị cuối
false || 'Hello'    // 'Hello'
'' || 'Default'     // 'Default'
'Value' || 'Default'// 'Value'

// Nullish coalescing - chỉ kiểm tra null/undefined
null ?? 'Default'   // 'Default'
undefined ?? 'Default' // 'Default'
'' ?? 'Default'     // '' (chuỗi rỗng không phải null)
0 ?? 'Default'      // 0 (0 không phải null)

// NOT
!true               // false
!false              // true
!''                 // true
!'text'             // false
```

### Toán tử 3 ngôi (Ternary)

```javascript
// condition ? valueIfTrue : valueIfFalse
const status = age >= 18 ? 'Người lớn' : 'Trẻ em';

// Nested (tránh dùng, khó đọc)
const grade = score >= 90 ? 'A' : score >= 80 ? 'B' : score >= 70 ? 'C' : 'D';
```

### Optional Chaining

```javascript
// Tránh error khi truy cập thuộc tính của null/undefined
const name = user?.profile?.name;  // undefined nếu user hoặc profile là null

// Với method
user?.getName?.();

// Với array
arr?.[0];
```

---

## 3. Điều Kiện và Vòng Lặp

### If / Else

```javascript
if (condition) {
    // Thực hiện nếu condition là true
} else if (anotherCondition) {
    // Thực hiện nếu anotherCondition là true
} else {
    // Thực hiện nếu tất cả false
}
```

### Switch

```javascript
switch (value) {
    case 'A':
        // Xử lý A
        break;
    case 'B':
    case 'C':
        // Xử lý B hoặc C
        break;
    default:
        // Xử lý mặc định
}
```

### For Loop

```javascript
// For cơ bản
for (let i = 0; i < 10; i++) {
    console.log(i);
}

// For...of (lặp qua giá trị của iterable)
const arr = ['a', 'b', 'c'];
for (const item of arr) {
    console.log(item);  // 'a', 'b', 'c'
}

// For...in (lặp qua key của object)
const obj = { a: 1, b: 2 };
for (const key in obj) {
    console.log(key, obj[key]);  // 'a' 1, 'b' 2
}
```

### While / Do-While

```javascript
// While - kiểm tra trước
while (condition) {
    // Lặp khi condition là true
}

// Do-While - thực hiện ít nhất 1 lần
do {
    // Thực hiện
} while (condition);
```

### Break và Continue

```javascript
for (let i = 0; i < 10; i++) {
    if (i === 5) break;      // Thoát vòng lặp
    if (i % 2 === 0) continue; // Bỏ qua, tiếp tục vòng tiếp
    console.log(i);
}
```

---

## 4. Functions

### Khai báo Function

```javascript
// Function Declaration (hoisting)
function greet(name) {
    return `Hello, ${name}!`;
}

// Function Expression
const greet = function(name) {
    return `Hello, ${name}!`;
};

// Arrow Function (ES6)
const greet = (name) => {
    return `Hello, ${name}!`;
};

// Arrow Function rút gọn (1 expression)
const greet = name => `Hello, ${name}!`;

// Arrow Function không có tham số
const sayHello = () => 'Hello!';
```

### Default Parameters

```javascript
function greet(name = 'Guest', greeting = 'Hello') {
    return `${greeting}, ${name}!`;
}

greet();              // 'Hello, Guest!'
greet('John');        // 'Hello, John!'
greet('John', 'Hi');  // 'Hi, John!'
```

### Rest Parameters

```javascript
// Gom các tham số còn lại vào array
function sum(...numbers) {
    return numbers.reduce((total, n) => total + n, 0);
}

sum(1, 2, 3, 4);  // 10
```

### Spread Operator

```javascript
// Trải array/object
const arr1 = [1, 2, 3];
const arr2 = [...arr1, 4, 5];  // [1, 2, 3, 4, 5]

const obj1 = { a: 1, b: 2 };
const obj2 = { ...obj1, c: 3 };  // { a: 1, b: 2, c: 3 }

// Gọi function với array
Math.max(...arr1);  // 3
```

---

## 5. Arrays

### Tạo Array

```javascript
const arr = [1, 2, 3];
const arr = new Array(3);      // [empty × 3]
const arr = Array.from('abc'); // ['a', 'b', 'c']
const arr = Array(5).fill(0);  // [0, 0, 0, 0, 0]
```

### Truy cập và Sửa đổi

```javascript
const arr = ['a', 'b', 'c'];

arr[0]           // 'a'
arr[arr.length - 1]  // 'c' (phần tử cuối)
arr[1] = 'B';    // Sửa đổi
```

### Methods thường dùng

| Method | Ý nghĩa | Return | Mutate |
|--------|---------|--------|--------|
| `push(item)` | Thêm vào cuối | length | ✓ |
| `pop()` | Xóa cuối | item đã xóa | ✓ |
| `unshift(item)` | Thêm vào đầu | length | ✓ |
| `shift()` | Xóa đầu | item đã xóa | ✓ |
| `splice(i, n)` | Xóa/thêm tại vị trí | items đã xóa | ✓ |
| `slice(start, end)` | Cắt array | array mới | ✗ |
| `concat(arr)` | Nối arrays | array mới | ✗ |
| `indexOf(item)` | Tìm vị trí | index hoặc -1 | ✗ |
| `includes(item)` | Kiểm tra có tồn tại | boolean | ✗ |
| `find(fn)` | Tìm phần tử | item hoặc undefined | ✗ |
| `findIndex(fn)` | Tìm vị trí | index hoặc -1 | ✗ |
| `filter(fn)` | Lọc | array mới | ✗ |
| `map(fn)` | Biến đổi | array mới | ✗ |
| `reduce(fn, init)` | Gom thành 1 giá trị | giá trị | ✗ |
| `forEach(fn)` | Lặp qua | undefined | ✗ |
| `every(fn)` | Tất cả thỏa mãn? | boolean | ✗ |
| `some(fn)` | Có ít nhất 1 thỏa mãn? | boolean | ✗ |
| `sort(fn)` | Sắp xếp | array | ✓ |
| `reverse()` | Đảo ngược | array | ✓ |
| `join(sep)` | Nối thành string | string | ✗ |

```javascript
const numbers = [1, 2, 3, 4, 5];

// filter - lọc theo điều kiện
numbers.filter(n => n > 2);  // [3, 4, 5]

// map - biến đổi từng phần tử
numbers.map(n => n * 2);     // [2, 4, 6, 8, 10]

// reduce - gom thành 1 giá trị
numbers.reduce((sum, n) => sum + n, 0);  // 15

// find - tìm phần tử đầu tiên thỏa mãn
numbers.find(n => n > 3);    // 4

// some / every
numbers.some(n => n > 4);    // true (có ít nhất 1)
numbers.every(n => n > 0);   // true (tất cả)

// sort - sắp xếp
numbers.sort((a, b) => a - b);  // Tăng dần
numbers.sort((a, b) => b - a);  // Giảm dần
```

### Destructuring

```javascript
const [first, second, ...rest] = [1, 2, 3, 4, 5];
// first = 1, second = 2, rest = [3, 4, 5]

const [a, , c] = [1, 2, 3];  // Bỏ qua phần tử
// a = 1, c = 3
```

---

## 6. Objects

### Tạo Object

```javascript
const obj = { key: 'value', name: 'John' };

// Với computed property
const key = 'dynamicKey';
const obj = { [key]: 'value' };  // { dynamicKey: 'value' }
```

### Truy cập

```javascript
obj.name           // 'John' (dot notation)
obj['name']        // 'John' (bracket notation)
obj['key-name']    // Bracket cho key đặc biệt
```

### Methods thường dùng

```javascript
// Lấy keys
Object.keys(obj)      // ['key', 'name']

// Lấy values
Object.values(obj)    // ['value', 'John']

// Lấy entries [key, value]
Object.entries(obj)   // [['key', 'value'], ['name', 'John']]

// Kiểm tra có key
obj.hasOwnProperty('name')  // true
'name' in obj              // true

// Merge objects
Object.assign({}, obj1, obj2)
{ ...obj1, ...obj2 }

// Clone (shallow)
const clone = { ...obj };
const clone = Object.assign({}, obj);

// Clone (deep)
const clone = JSON.parse(JSON.stringify(obj));
const clone = structuredClone(obj);  // Modern
```

### Destructuring

```javascript
const user = { name: 'John', age: 25, city: 'HN' };

const { name, age } = user;
// name = 'John', age = 25

const { name: userName, age: userAge } = user;  // Đổi tên
// userName = 'John', userAge = 25

const { name, ...rest } = user;
// name = 'John', rest = { age: 25, city: 'HN' }

// Default value
const { country = 'VN' } = user;
```

---

## 7. DOM Manipulation

### Lấy phần tử

```javascript
// Theo ID - trả về 1 element
document.getElementById('myId')

// Theo class - trả về HTMLCollection (live)
document.getElementsByClassName('myClass')

// Theo tag - trả về HTMLCollection (live)
document.getElementsByTagName('div')

// Theo selector - trả về 1 element đầu tiên
document.querySelector('.myClass')
document.querySelector('#myId')
document.querySelector('div.card > p')

// Theo selector - trả về NodeList (static)
document.querySelectorAll('.myClass')
```

### Thay đổi nội dung

```javascript
element.textContent = 'Text mới';     // Text thuần
element.innerHTML = '<b>HTML</b>';    // HTML
element.innerText = 'Text hiển thị';  // Text visible
```

### Thay đổi thuộc tính

```javascript
// Get/Set attribute
element.getAttribute('href')
element.setAttribute('href', '/new-url')
element.removeAttribute('disabled')
element.hasAttribute('disabled')

// Trực tiếp (một số thuộc tính)
element.id = 'newId';
element.href = '/new-url';
element.value = 'input value';
element.checked = true;
element.disabled = true;

// Data attributes
element.dataset.userId    // Đọc data-user-id
element.dataset.role = 'admin'  // Set data-role
```

### Thay đổi Style

```javascript
// Inline style
element.style.color = 'red';
element.style.backgroundColor = '#fff';  // camelCase
element.style.fontSize = '16px';
element.style.display = 'none';

// Lấy computed style
getComputedStyle(element).color
```

### Thay đổi Class

```javascript
element.className = 'class1 class2';  // Thay thế hoàn toàn

element.classList.add('active');
element.classList.remove('active');
element.classList.toggle('active');     // Thêm/xóa
element.classList.toggle('active', true);  // Force thêm
element.classList.contains('active');   // Kiểm tra
element.classList.replace('old', 'new');
```

### Tạo và Xóa phần tử

```javascript
// Tạo phần tử
const div = document.createElement('div');
div.textContent = 'Hello';
div.className = 'card';

// Thêm vào DOM
parent.appendChild(div);           // Cuối parent
parent.insertBefore(div, referenceNode);  // Trước node
parent.prepend(div);               // Đầu parent
parent.append(div);                // Cuối parent
element.before(newElement);        // Trước element
element.after(newElement);         // Sau element

// Xóa phần tử
element.remove();
parent.removeChild(child);

// Clone
const clone = element.cloneNode(true);  // true = deep clone
```

---

## 8. Events

### Thêm Event Listener

```javascript
// addEventListener (khuyến khích)
element.addEventListener('click', function(event) {
    console.log('Clicked!', event.target);
});

// Arrow function
element.addEventListener('click', (e) => {
    e.preventDefault();   // Ngăn hành vi mặc định
    e.stopPropagation(); // Ngăn lan truyền
});

// Xóa listener (cần cùng function reference)
const handler = (e) => console.log(e);
element.addEventListener('click', handler);
element.removeEventListener('click', handler);
```

### Các Events phổ biến

| Event | Ý nghĩa |
|-------|---------|
| `click` | Click chuột |
| `dblclick` | Double click |
| `mousedown` | Nhấn chuột |
| `mouseup` | Thả chuột |
| `mouseenter` | Chuột vào (không bubble) |
| `mouseleave` | Chuột rời (không bubble) |
| `mouseover` | Chuột vào (bubble) |
| `mouseout` | Chuột rời (bubble) |
| `mousemove` | Di chuột |
| `keydown` | Nhấn phím |
| `keyup` | Thả phím |
| `keypress` | Nhấn phím (deprecated) |
| `focus` | Focus |
| `blur` | Mất focus |
| `change` | Giá trị thay đổi (sau blur) |
| `input` | Đang nhập |
| `submit` | Submit form |
| `scroll` | Cuộn |
| `resize` | Thay đổi kích thước window |
| `load` | Tải xong |
| `DOMContentLoaded` | DOM ready |

### Event Object

```javascript
element.addEventListener('click', (e) => {
    e.target          // Phần tử được click
    e.currentTarget   // Phần tử gắn listener
    e.type            // 'click'
    e.clientX, e.clientY  // Vị trí chuột (viewport)
    e.pageX, e.pageY      // Vị trí chuột (page)
    e.key             // Phím được nhấn
    e.keyCode         // Mã phím (deprecated)
    e.ctrlKey, e.shiftKey, e.altKey  // Modifier keys
});
```

### Event Delegation

```javascript
// Thay vì gắn listener cho mỗi item
// Gắn cho parent và kiểm tra target
document.querySelector('.list').addEventListener('click', (e) => {
    if (e.target.classList.contains('item')) {
        console.log('Item clicked:', e.target);
    }
});
```

---

## 9. Async JavaScript

### setTimeout và setInterval

```javascript
// Chạy sau delay
const timeoutId = setTimeout(() => {
    console.log('Sau 2 giây');
}, 2000);

clearTimeout(timeoutId);  // Hủy

// Chạy lặp lại
const intervalId = setInterval(() => {
    console.log('Mỗi 1 giây');
}, 1000);

clearInterval(intervalId);  // Dừng
```

### Promises

```javascript
// Tạo Promise
const promise = new Promise((resolve, reject) => {
    // Async operation
    if (success) {
        resolve(result);
    } else {
        reject(error);
    }
});

// Sử dụng Promise
promise
    .then(result => {
        console.log('Success:', result);
        return anotherResult;
    })
    .then(data => {
        console.log('Chained:', data);
    })
    .catch(error => {
        console.error('Error:', error);
    })
    .finally(() => {
        console.log('Always runs');
    });

// Promise.all - chờ tất cả
Promise.all([promise1, promise2, promise3])
    .then(([result1, result2, result3]) => { });

// Promise.race - lấy kết quả đầu tiên
Promise.race([promise1, promise2])
    .then(firstResult => { });
```

### Async/Await

```javascript
// Async function
async function fetchData() {
    try {
        const response = await fetch('/api/data');
        const data = await response.json();
        return data;
    } catch (error) {
        console.error('Error:', error);
        throw error;
    }
}

// Gọi async function
fetchData().then(data => console.log(data));

// Trong async function khác
async function main() {
    const data = await fetchData();
    console.log(data);
}
```

### Fetch API

```javascript
// GET request
fetch('/api/data')
    .then(response => {
        if (!response.ok) throw new Error('HTTP error');
        return response.json();
    })
    .then(data => console.log(data))
    .catch(error => console.error(error));

// POST request
fetch('/api/data', {
    method: 'POST',
    headers: {
        'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name: 'John' }),
})
    .then(response => response.json())
    .then(data => console.log(data));

// Với async/await
async function postData(data) {
    const response = await fetch('/api/data', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(data),
    });
    return response.json();
}
```

---

## 10. ES6+ Features

### Template Literals

```javascript
const name = 'John';
const greeting = `Hello, ${name}!`;  // String interpolation

// Multi-line
const html = `
    <div>
        <h1>${title}</h1>
        <p>${content}</p>
    </div>
`;
```

### Destructuring

```javascript
// Array
const [a, b, ...rest] = [1, 2, 3, 4];

// Object
const { name, age, city = 'Unknown' } = user;

// Trong function parameter
function greet({ name, age }) {
    console.log(name, age);
}
```

### Spread Operator

```javascript
const arr1 = [1, 2];
const arr2 = [...arr1, 3, 4];

const obj1 = { a: 1 };
const obj2 = { ...obj1, b: 2 };
```

### Modules

```javascript
// Export
export const PI = 3.14;
export function add(a, b) { return a + b; }
export default class User { }

// Import
import User from './User.js';
import { PI, add } from './math.js';
import { add as sum } from './math.js';  // Alias
import * as math from './math.js';       // All
```

### Classes

```javascript
class Animal {
    constructor(name) {
        this.name = name;
    }
    
    speak() {
        console.log(`${this.name} makes a sound.`);
    }
    
    static create(name) {
        return new Animal(name);
    }
}

class Dog extends Animal {
    constructor(name, breed) {
        super(name);
        this.breed = breed;
    }
    
    speak() {
        console.log(`${this.name} barks.`);
    }
}
```

---

## 📝 Best Practices

1. **Dùng `const` mặc định**, chỉ dùng `let` khi cần thay đổi
2. **Dùng `===` thay vì `==`** để so sánh
3. **Dùng Template literals** cho string phức tạp
4. **Dùng Arrow functions** cho callbacks
5. **Dùng Destructuring** để code gọn hơn
6. **Dùng async/await** thay vì .then() khi có thể
7. **Xử lý errors** với try/catch
8. **Dùng Optional chaining** (`?.`) để tránh null errors
