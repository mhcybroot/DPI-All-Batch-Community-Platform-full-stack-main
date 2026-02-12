// Mobile nav toggle
document.addEventListener('DOMContentLoaded', () => {
    const toggle = document.querySelector('.nav-toggle');
    const links = document.querySelector('.nav-links');
    if (toggle && links) {
        toggle.addEventListener('click', () => links.classList.toggle('open'));
    }

    // --- The Biological Machine: Initialization ---
    initSystemBoot(); // Now handles both overlay and page reveal
    initNeuralDeck(); // New Navbar Animation
    initMagneticButtons();
    initKineticTypography();
});

// ... (alerts and htmx listeners remain unchanged)

/* --- GSAP "System Boot" Sequence --- */
function initSystemBoot() {
    if (typeof gsap === 'undefined') return;

    const isBooting = document.documentElement.classList.contains('boot-pending');

    // Convert current "boot" logic to "Page Reveal"
    const pageRevealTimeline = () => {
        const tl = gsap.timeline();

        // 1. Grid/Background stabilization (Simulated)
        tl.fromTo('body',
            { backgroundColor: '#000000' },
            { backgroundColor: 'var(--bg)', duration: 0.8, ease: 'power2.out' }
        );

        // 2. Glass Cards "Crystallize"
        tl.fromTo('.glass-card',
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
        tl.fromTo('h1, h2, p, .stat-value',
            { y: 10, opacity: 0 },
            { y: 0, opacity: 1, duration: 0.5, stagger: 0.05, ease: "power2.out" },
            "-=0.8"
        );
        return tl;
    };

    if (isBooting) {
        // --- Full Session Boot Sequence ---
        const bootTl = gsap.timeline({
            onComplete: () => {
                document.getElementById('boot-overlay').style.display = 'none';
                document.documentElement.classList.remove('boot-pending');
                sessionStorage.setItem('dpi_booted', 'true');
            }
        });

        // 1. Initial State
        gsap.set('.boot-bar', { width: '0%' });
        gsap.set('.boot-text', { text: 'INITIALIZING...' });

        // 2. Progress Bar
        bootTl.to('.boot-bar', {
            width: '100%',
            duration: 1.5,
            ease: "power2.inOut"
        });

        // 3. Text Updates (Simulated)
        bootTl.to('.boot-text', {
            duration: 0.2,
            opacity: 0,
            onComplete: () => document.querySelector('.boot-text').innerText = 'SYSTEM READY'
        }, "-=0.5");

        bootTl.to('.boot-text', {
            duration: 0.2,
            opacity: 1
        });

        // 4. Fade Out Overlay
        bootTl.to('#boot-overlay', {
            opacity: 0,
            duration: 0.8,
            ease: "power2.inOut"
        }, "+=0.2");

        // 5. Trigger Page Reveal slightly before overlay is gone
        bootTl.add(pageRevealTimeline(), "-=0.5");

    } else {
        // --- Just Page Reveal (Subsequent loads) ---
        pageRevealTimeline();
    }
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
/* --- Neural Command Bar Animation --- */
function initNeuralDeck() {
    if (typeof gsap === 'undefined') return;

    const deck = document.querySelector('.command-deck');
    const modules = document.querySelectorAll('.deck-module');

    if (deck) {
        // Entrance: Expand width, then fade in content
        gsap.fromTo(deck,
            { width: '0%', opacity: 0 },
            { width: '90%', opacity: 1, duration: 1, ease: 'power4.out', delay: 0.5, clearProps: "all", force3D: true }
        );

        // Modules: Stagger in
        gsap.fromTo(modules,
            { y: -10, opacity: 0 },
            { y: 0, opacity: 1, duration: 0.5, stagger: 0.05, delay: 1, ease: 'back.out(1.7)', clearProps: "all", force3D: true }
        );
    }
}
