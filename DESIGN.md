# DESIGN.md: Digital Mycelium (Organic Logic)

## Vibe
A "Digital Mycelium" ecosystem where high-end IDE meets botanical garden. The interface should feel alive, interconnected, and premium.

## 1. Color Palette

| Token Name | Color | Usage |
| :--- | :--- | :--- |
| `--color-obsidian` | `#0a0b10` (Approx) | Primary Background |
| `--color-terminal-green` | `#00ff41` (Approx) | Primary Accent / Success |
| `--color-electric-copper` | `#b87333` (Approx) | Secondary Accent / Highlights |
| `--color-lichen-gray` | `#8c92ac` (Approx) | Text / Neutral Elements |
| `--color-glass-border` | `rgba(255, 255, 255, 0.1)` | Subtle Borders |

## 2. Typography

- **Headers/Display**: High-contrast Serif (e.g., *Playfair Display*, *Cinzel*) for prestige.
- **Body/Code**: Razor-sharp Monospace (e.g., *JetBrains Mono*, *Fira Code*) for engineering soul.

## 3. Architecture: The 'Flow' State

### Deconstructed Layouts
- No standard containers.
- Asymmetrical "floating nodes".
- Connected by subtle, glowing SVG paths (neural network / mycelium roots).

### Glassmorphism 2.0
- **Effect**: High-refraction glass.
- **CSS**:
  ```css
  .glass-card {
    background: rgba(10, 11, 16, 0.6);
    backdrop-filter: saturate(180%) blur(20px);
    border: 0.5px solid rgba(255, 255, 255, 0.1);
    box-shadow: 0 8px 32px 0 rgba(0, 0, 0, 0.37);
  }
  ```

## 4. Advanced Interactivity & Animation

### GSAP 'Code-Flow' Scrolling
- Elements "compile" into place.
- Staggered reveals.
- SVG line-drawing effects.
- Easing: `cubic-bezier(0.23, 1, 0.32, 1)` (Nature-Logic).

### Magnetic Interactivity
- Magnetic cursor effect for primary buttons.
- Physics-based spring logic (button warps toward cursor).

### HTMX State-Morphing
- Smooth morphing between states (height adjustments, content slide/type).
- Tools: GSAP Flip or Framer Motion logic (via custom JS for HTMX events).

## 5. Accessibility
- Minimum **4.5:1** contrast ratio for text on glass backgrounds.
- Semantic HTML structure.
