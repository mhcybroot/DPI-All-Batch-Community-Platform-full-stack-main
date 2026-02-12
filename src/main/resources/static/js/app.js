// Mobile nav toggle
document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.nav-toggle');
    const links = document.querySelector('.nav-links');
    if (toggle && links) {
        toggle.addEventListener('click', () => links.classList.toggle('open'));
    }

    // --- The Biological Machine: Initialization ---
    initSystemBoot();
    initMagneticButtons();
    initKineticTypography();
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

// HTMX: Re-init animations after swap
document.body.addEventListener('htmx:afterSwap', (e) => {
    initGSAPScroll(); // Re-bind scroll triggers for new content
    initMagneticButtons();
    initKineticTypography();
});

/* --- GSAP "System Boot" Sequence --- */
function initSystemBoot() {
    if (typeof gsap === 'undefined') return;

    const timeline = gsap.timeline();

    // 1. Grid/Background stabilization (Simulated)
    timeline.fromTo('body',
        { backgroundColor: '#000000' },
        { backgroundColor: 'var(--color-obsidian)', duration: 0.8, ease: 'power2.out' }
    );

    // 2. Glass Cards "Crystallize"
    timeline.fromTo('.glass-card',
        {
            scale: 0.95,
            opacity: 0,
            backdropFilter: 'blur(0px)'
        },
        {
            scale: 1,
            opacity: 1,
            backdropFilter: 'saturate(180%) blur(24px)',
            duration: 1,
            stagger: 0.1,
            ease: "cubic-bezier(0.19, 1, 0.22, 1)" // expo.out
        },
        "-=0.5"
    );

    // 3. Text Streams In
    timeline.fromTo('h1, h2, p, .stat-value',
        { y: 10, opacity: 0 },
        { y: 0, opacity: 1, duration: 0.5, stagger: 0.05, ease: "power2.out" },
        "-=0.8"
    );
}

function initGSAPScroll() {
    if (typeof gsap !== 'undefined' && typeof ScrollTrigger !== 'undefined') {
        gsap.registerPlugin(ScrollTrigger);
        gsap.utils.toArray('.gsap-reveal').forEach((elem) => {
            if (!elem.classList.contains('booted')) {
                gsap.fromTo(elem,
                    { y: 30, opacity: 0 },
                    {
                        scrollTrigger: {
                            trigger: elem,
                            start: "top 90%",
                        },
                        y: 0,
                        opacity: 1,
                        duration: 0.6,
                        ease: "cubic-bezier(0.23, 1, 0.32, 1)",
                        onComplete: () => elem.classList.add('booted')
                    }
                );
            }
        });
    }
}

/* --- Magnetic Interactivity --- */
function initMagneticButtons() {
    if (typeof gsap === 'undefined') return;

    const magnets = document.querySelectorAll('.btn-magnetic');

    magnets.forEach((magnet) => {
        magnet.addEventListener('mousemove', (e) => {
            const rect = magnet.getBoundingClientRect();
            // Calculate distance from center
            const x = e.clientX - rect.left - rect.width / 2;
            const y = e.clientY - rect.top - rect.height / 2;

            gsap.to(magnet, {
                duration: 0.3,
                x: x * 0.2,
                y: y * 0.2,
                rotation: x * 0.02,
                ease: "power2.out"
            });
        });

        magnet.addEventListener('mouseleave', () => {
            gsap.to(magnet, {
                duration: 0.8,
                x: 0,
                y: 0,
                rotation: 0,
                ease: "elastic.out(1, 0.5)"
            });
        });
    });
}

/* --- Kinetic Typography --- */
function initKineticTypography() {
    const headers = document.querySelectorAll('h1, h2, .card-title');
    headers.forEach(header => {
        header.classList.add('kinetic-text');
    });
}
