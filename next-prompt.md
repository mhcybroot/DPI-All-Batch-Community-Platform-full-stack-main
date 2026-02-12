---
page: business_biological_machine
---
Redesign the Business Directory to match the "Biological Machine" aesthetic. The Corporate Network.

**DESIGN SYSTEM (REQUIRED):**
-   **Theme System (Dual Mode)**:
    -   **Biological (Default)**: Deep Obsidian (#0B0E14) + Moss Emerald (#2D5A27).
    -   **Frosted Silicon (Light)**: Platinum (#F0F4F8) + Obsidian Text.
-   **Glassmorphism**: "Frosted Silicon" cards with dynamic edge lighting.
-   **Typography**: Kinetic Headers (Serif -> Mono glitch).
-   **Layout**: Asymmetric Grid of Glass Cards.

**Page Structure:**
1.  **Header**: "Corporate Directory" with kinetic typography.
2.  **Action Bar**:
    -   "Register Entity" (Primary Magnetic)
    -   "My Assets" (Secondary Magnetic)
3.  **Business Grid (Index)**:
    -   **Entity Cards**: Glass cards displaying business info.
    -   **Category Tags**: "Cyber Copper" data chips.
    -   **Location**: Muted monospace text.
4.  **Management Console (My Business)**:
    -   Dashboard-style list of owned entities.
    -   Edit/Delete actions as "Ghost" buttons.

**Interaction Specs:**
-   **Entry**: Staggered GSAP reveal.
-   **Search**: HTMX-powered real-time filtering with `glass-input`.
-   **Hover**: Cards lift and glow (Magnetic field effect).

**Context**:
Redesign of `business/index.html` and `business/my-business.html`. Use `digital-mycelium.css` classes.
