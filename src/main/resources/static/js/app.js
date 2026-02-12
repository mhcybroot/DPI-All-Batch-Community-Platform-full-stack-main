// Mobile nav toggle
document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.nav-toggle');
    const links = document.querySelector('.nav-links');
    if (toggle && links) {
        toggle.addEventListener('click', () => links.classList.toggle('open'));
    }
});

// Auto-dismiss alerts after 5s
document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.alert[data-auto-dismiss]').forEach(el => {
        setTimeout(() => {
            el.style.transition = 'opacity 0.3s ease';
            el.style.opacity = '0';
            setTimeout(() => el.remove(), 300);
        }, 5000);
    });
});

// HTMX: confirm delete dialogs
document.body.addEventListener('htmx:confirm', (e) => {
    if (e.detail.question) {
        e.preventDefault();
        if (confirm(e.detail.question)) {
            e.detail.issueRequest(true);
        }
    }
});
