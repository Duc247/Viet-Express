/**
 * Seasonal Theme JavaScript
 * Tạo các hiệu ứng động theo mùa
 * Author: Viet Express Team
 * 
 * LƯU Ý:
 * - Icon nằm PHÍA SAU nội dung (z-index thấp)
 * - Có 2 loại: Rơi xuống (ít) và Động tại chỗ (góc màn hình)
 */

// Khởi tạo seasonal theme
document.addEventListener('DOMContentLoaded', function() {
    initSeasonalTheme();
});

function initSeasonalTheme() {
    // Lấy theme hiện tại từ body class hoặc từ config
    const body = document.body;
    const themeClasses = ['theme-spring', 'theme-summer', 'theme-autumn', 'theme-winter', 'theme-none'];
    let currentTheme = 'theme-none';
    
    themeClasses.forEach(theme => {
        if (body.classList.contains(theme)) {
            currentTheme = theme;
        }
    });
    
    if (currentTheme === 'theme-none') {
        return; // Không có theme
    }
    
    // Tạo container cho seasonal items
    let container = document.querySelector('.seasonal-container');
    if (!container) {
        container = document.createElement('div');
        container.className = 'seasonal-container';
        document.body.appendChild(container);
    }
    
    // Tạo hiệu ứng theo mùa
    switch(currentTheme) {
        case 'theme-spring':
            createSpringEffects(container);
            break;
        case 'theme-summer':
            createSummerEffects(container);
            break;
        case 'theme-autumn':
            createAutumnEffects(container);
            break;
        case 'theme-winter':
            createWinterEffects(container);
            break;
    }
}

// ==========================================
// FLOATING DECORATIONS - Icon động tại chỗ (góc màn hình)
// ==========================================
function createFloatingDecorations(container, icons) {
    const positions = ['top-left', 'top-right', 'bottom-left', 'bottom-right', 'mid-left', 'mid-right'];
    
    positions.forEach((pos, index) => {
        const deco = document.createElement('div');
        deco.className = `floating-decoration ${pos}`;
        deco.innerHTML = icons[index % icons.length];
        deco.style.animationDelay = (index * 0.5) + 's';
        container.appendChild(deco);
    });
}

// ==========================================
// MÙA XUÂN - Hoa đào, Lì xì
// ==========================================
function createSpringEffects(container) {
    const fallingItems = ['🌸', '🏮', '🧧'];
    const floatingIcons = ['🌸', '🧧', '🏮', '🌼', '💮', '🎋'];
    const count = 8; // Giảm số lượng icon rơi
    
    // Tạo icon động tại chỗ (góc màn hình)
    createFloatingDecorations(container, floatingIcons);
    
    // Tạo một vài icon rơi (ít thôi)
    for (let i = 0; i < count; i++) {
        setTimeout(() => {
            createFallingItem(container, fallingItems, 'spring', 20000);
        }, i * 3000);
    }
    
    // Tiếp tục tạo sau khi hoàn thành vòng đầu (chậm hơn)
    setInterval(() => {
        if (document.body.classList.contains('theme-spring')) {
            createFallingItem(container, fallingItems, 'spring', 20000);
        }
    }, 4000);
}

// ==========================================
// MÙA HẠ - Mặt trời, Dưa hấu
// ==========================================
function createSummerEffects(container) {
    const fallingItems = ['☀️', '🌴', '🍉'];
    const floatingIcons = ['☀️', '🌴', '🍉', '🌊', '🏖️', '🌺'];
    const count = 6;
    
    // Tạo icon động tại chỗ
    createFloatingDecorations(container, floatingIcons);
    
    for (let i = 0; i < count; i++) {
        setTimeout(() => {
            createFallingItem(container, fallingItems, 'summer', 18000);
        }, i * 4000);
    }
    
    setInterval(() => {
        if (document.body.classList.contains('theme-summer')) {
            createFallingItem(container, fallingItems, 'summer', 18000);
        }
    }, 5000);
}

// ==========================================
// MÙA THU - Lá vàng, Trung thu
// ==========================================
function createAutumnEffects(container) {
    const fallingItems = ['🍂', '🍁'];
    const floatingIcons = ['🍂', '🍁', '🏮', '🥮', '🌕', '🎑'];
    const count = 8;
    
    // Tạo icon động tại chỗ
    createFloatingDecorations(container, floatingIcons);
    
    // Tạo mặt trăng cố định
    const moon = document.createElement('div');
    moon.className = 'seasonal-item autumn-moon';
    moon.innerHTML = '🌕';
    container.appendChild(moon);
    
    for (let i = 0; i < count; i++) {
        setTimeout(() => {
            createFallingItem(container, fallingItems, 'autumn', 22000);
        }, i * 3000);
    }
    
    setInterval(() => {
        if (document.body.classList.contains('theme-autumn')) {
            createFallingItem(container, fallingItems, 'autumn', 22000);
        }
    }, 4000);
}

// ==========================================
// MÙA ĐÔNG - Tuyết, Giáng sinh
// ==========================================
function createWinterEffects(container) {
    const snowItems = ['❄️', '❅', '❆'];
    const floatingIcons = ['🎄', '🎅', '🎁', '⛄', '❄️', '🔔'];
    const count = 12; // Tuyết nhiều hơn chút
    
    // Tạo icon động tại chỗ
    createFloatingDecorations(container, floatingIcons);
    
    // Tạo cây thông cố định
    const treeLeft = document.createElement('div');
    treeLeft.className = 'seasonal-item winter-tree left';
    treeLeft.innerHTML = '🎄';
    container.appendChild(treeLeft);
    
    const treeRight = document.createElement('div');
    treeRight.className = 'seasonal-item winter-tree right';
    treeRight.innerHTML = '🎄';
    container.appendChild(treeRight);
    
    // Tạo tuyết rơi
    for (let i = 0; i < count; i++) {
        setTimeout(() => {
            createSnowflake(container, snowItems);
        }, i * 800);
    }
    
    // Tạo ông già Noel bay qua mỗi 60 giây
    setTimeout(() => {
        createSantaFly(container);
    }, 10000);
    
    setInterval(() => {
        if (document.body.classList.contains('theme-winter')) {
            createSantaFly(container);
        }
    }, 60000);
    
    // Tiếp tục tạo tuyết
    setInterval(() => {
        if (document.body.classList.contains('theme-winter')) {
            createSnowflake(container, snowItems);
        }
    }, 1000);
}

// ==========================================
// HELPER FUNCTIONS
// ==========================================
function createFallingItem(container, items, season, duration) {
    const item = document.createElement('div');
    item.className = `seasonal-item ${season}-flower falling`;
    item.innerHTML = items[Math.floor(Math.random() * items.length)];
    item.style.left = Math.random() * 100 + 'vw';
    item.style.animationDuration = (duration + Math.random() * 5000) + 'ms';
    item.style.animationDelay = Math.random() * 2000 + 'ms';
    item.style.fontSize = (0.8 + Math.random() * 0.5) + 'rem';
    container.appendChild(item);
    
    // Xóa sau khi animation kết thúc
    setTimeout(() => {
        if (item.parentNode) {
            item.parentNode.removeChild(item);
        }
    }, duration + 7000);
}

function createSnowflake(container, items) {
    const snow = document.createElement('div');
    snow.className = 'seasonal-item winter-snow';
    snow.innerHTML = items[Math.floor(Math.random() * items.length)];
    snow.style.left = Math.random() * 100 + 'vw';
    snow.style.animationDuration = (8000 + Math.random() * 7000) + 'ms';
    snow.style.animationDelay = Math.random() * 1000 + 'ms';
    snow.style.fontSize = (0.5 + Math.random() * 1) + 'rem';
    container.appendChild(snow);
    
    setTimeout(() => {
        if (snow.parentNode) {
            snow.parentNode.removeChild(snow);
        }
    }, 15000);
}

function createSantaFly(container) {
    const santa = document.createElement('div');
    santa.className = 'seasonal-item winter-santa';
    santa.innerHTML = '🎅🛷';
    santa.style.top = (50 + Math.random() * 100) + 'px';
    container.appendChild(santa);
    
    setTimeout(() => {
        if (santa.parentNode) {
            santa.parentNode.removeChild(santa);
        }
    }, 32000);
}

// Cho phép thay đổi theme động
function changeSeasonalTheme(theme) {
    const body = document.body;
    const themeClasses = ['theme-spring', 'theme-summer', 'theme-autumn', 'theme-winter', 'theme-none'];
    
    // Xóa tất cả theme classes
    themeClasses.forEach(t => body.classList.remove(t));
    
    // Xóa container cũ
    const oldContainer = document.querySelector('.seasonal-container');
    if (oldContainer) {
        oldContainer.innerHTML = '';
    }
    
    // Thêm theme mới
    body.classList.add('theme-' + theme);
    
    // Khởi tạo lại effects
    if (theme !== 'none') {
        initSeasonalTheme();
    }
}
