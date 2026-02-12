# SYSTEM_ARCHITECTURE.md: The Biological Machine

## 1. Design Tokens

### Color Palette
-   **Obsidian (#0B0E14)**: Primary background. Deep, void-like, representing the "machine" core.
-   **Moss Emerald (#2D5A27)**: Primary accent. Organic growth, success states, active nodes.
-   **Cyber Copper (#B87333)**: Secondary accent/Highlight. Conductive traces, warnings, interaction hints.
-   **Frozen White (#E0E6ED)**: Primary text. High contrast against Obsidian.
-   **Silicon Gray (#8C92AC)**: Secondary text/Metadata.

### Glassmorphism ("Frosted Silicon")
-   **Surface**: `background: rgba(11, 14, 20, 0.7);`
-   **Blur**: `backdrop-filter: saturate(180%) blur(24px);`
-   **Border**: `1px solid rgba(255, 255, 255, 0.08);`
-   **Dynamic Edge**:
    -   *Idle*: Subtle whitish-gray.
    -   *Active/Success*: Glowing Moss Emerald (`box-shadow: 0 0 15px rgba(45, 90, 39, 0.4)`).
    -   *Error/Alert*: Glowing Cyber Copper.

### Typography (Kinetic)
-   **Serif (Human)**: *Playfair Display* or *Cinzel*. Used for static headlines and "thought".
-   **Monospace (Machine)**: *JetBrains Mono* or *Fira Code*. Used for code, metadata, and "active" states.
-   **Interaction**: Hovering over Serif headers triggers a "Glitch-Organic" shift to Monospace.

## 2. Animation Constants (GSAP & Physics)

### Easing Curves
-   **`--ease-boot`**: `expo.out` (Explosive start, slow settle) - Used for System Boot.
-   **`--ease-organic`**: `cubic-bezier(0.23, 1, 0.32, 1)` (Nature-like) - Used for UI reveals.
-   **`--ease-spring`**: `elastic.out(1, 0.5)` - Used for magnetic inputs and hover returns.
-   **`--ease-glitch`**: `steps(3)` - Used for kinetic typography shifts.

### Timelines
1.  **System Boot (Page Load)**:
    -   `T+0.0s`: Grid lines draw (SVG paths).
    -   `T+0.2s`: Obsidian background stabilizes.
    -   `T+0.5s`: Glass cards "crystallize" (Scale 0.9 -> 1.0, Opacity 0 -> 1, Blur 0 -> 20px).
    -   `T+0.8s`: Content streams in (Staggered text reveal).

2.  **HTMX State Morph (Content Swap)**:
    -   **Exit**: `y: -10px`, `opacity: 0`, `scale: 0.98`, `duration: 0.2s`.
    -   **Entry**: `y: 10px -> 0`, `opacity: 0 -> 1`, `scale: 1.02 -> 1`, `duration: 0.4s`, `ease: back.out(1.2)`.

## 3. Component DNA

### Layout Structure
-   **Asymmetric Nodes**: Avoid rigid grids. Use `masonry` or custom CSS Grid with overlapping areas.
-   **Connectors**: Absolute positioned SVG paths connecting related "nodes" (cards).
-   **Responsive Strategy ("Single-Helix")**:
    -   *Desktop*: Branching tree (Left/Right alternating nodes).
    -   *Mobile*: Central vertical line, nodes branching slightly off-center (like DNA base pairs).

### Thymeleaf & HTMX Integration
-   **Fragment**: `<div th:fragment="card(title, content)" class="node-container glass-morphic" hx-target="this" hx-swap="outerHTML swap:0.4s">`
-   **Trigger**: `hx-trigger="click, load delay:0.2s"`
-   **Logic**:
    -   `hx-on::before-request`: Add class `.morph-exit`.
    -   `hx-on::after-swap`: Remove `.morph-exit`, add `.morph-enter`, re-run `initGSAP()`.

### Accessibility (A11y)
-   **Contrast**: Text on glass must meet 4.5:1. Use `text-shadow` if background opacity varies.
-   **Reduced Motion**: `media (prefers-reduced-motion)` -> Disable GSAP timelines, use simple fades.
-   **Live Regions**: `<div aria-live="polite" id="notification-stream">` for status updates.
