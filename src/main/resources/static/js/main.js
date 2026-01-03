/**
 * Viet-Express Logistics - Main JavaScript
 * File này chứa các hàm JS dùng chung cho toàn bộ ứng dụng
 */

document.addEventListener('DOMContentLoaded', function () {
    console.log('Viet-Express Logistics - Ready!');

    // Tự động ẩn alert sau 5 giây
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent)');
    alerts.forEach(function (alert) {
        setTimeout(function () {
            alert.classList.add('fade');
            setTimeout(function () {
                alert.remove();
            }, 500);
        }, 5000);
    });

    // Format số tiền VND
    const moneyElements = document.querySelectorAll('[data-money]');
    moneyElements.forEach(function (el) {
        const value = parseFloat(el.getAttribute('data-money'));
        if (!isNaN(value)) {
            el.textContent = new Intl.NumberFormat('vi-VN', {
                style: 'currency',
                currency: 'VND'
            }).format(value);
        }
    });

    // Tooltip Bootstrap (nếu có)
    const tooltipTriggerList = document.querySelectorAll('[data-bs-toggle="tooltip"]');
    if (tooltipTriggerList.length > 0 && typeof bootstrap !== 'undefined') {
        tooltipTriggerList.forEach(function (el) {
            new bootstrap.Tooltip(el);
        });
    }
});

// Hàm format tiền VND
function formatVND(amount) {
    return new Intl.NumberFormat('vi-VN', {
        style: 'currency',
        currency: 'VND'
    }).format(amount);
}

// Hàm format ngày giờ
function formatDateTime(dateStr) {
    const date = new Date(dateStr);
    return date.toLocaleString('vi-VN');
}

// Hàm xác nhận xóa
function confirmDelete(message) {
    return confirm(message || 'Bạn có chắc chắn muốn xóa?');
}
