# Thymeleaf Spring Security Namespace Fix

## Issue
**Error**: Template parsing error on `/web/dashboard` and potentially other pages
**Status Code**: 500 Internal Server Error
**Message**: "An error happened during template parsing (template: class path resource [templates/dashboard.html])"

## Root Cause
The Spring Security Thymeleaf namespace declaration was incorrect in multiple template files:

**Incorrect Namespace**:
```html
xmlns:sec="http://www.thymeleaf.org/extras/spring-security"
```

**Correct Namespace** (for Spring Security 6 / Spring Boot 3+):
```html
xmlns:sec="http://www.thymeleaf.org/thymeleaf-extras-springsecurity6"
```

This caused Thymeleaf to fail parsing any `sec:authorize` or `sec:authentication` attributes, resulting in template parsing exceptions.

## Solution Applied

### Fixed Namespace Declaration in All Affected Templates

1. **dashboard.html** - Fixed namespace
2. **layout/base.html** - Fixed namespace (affects all pages using this layout)
3. **forum/index.html** - Fixed namespace
4. **notices/index.html** - Fixed namespace
5. **profile/view.html** - Fixed namespace

### Dependency Verification
Confirmed that `thymeleaf-extras-springsecurity6:3.1.3.RELEASE` is properly included in `build.gradle.kts`.

## Files Modified
- `src/main/resources/templates/dashboard.html`
- `src/main/resources/templates/layout/base.html`
- `src/main/resources/templates/forum/index.html`
- `src/main/resources/templates/notices/index.html`
- `src/main/resources/templates/profile/view.html`

## Testing
After applying these changes:
1. Restart the application (or let Spring DevTools reload)
2. Navigate to `/web/dashboard` - should load successfully
3. Test other pages with `sec:authorize` attributes:
   - Forum page (admin-only "Create Category" button)
   - Notices page (admin-only "New Notice" button)
   - Navbar (admin-only "ROOT ACCESS" link)

## Prevention
- Always use the correct namespace for the Spring Security version
- For Spring Security 6 (Spring Boot 3+): `thymeleaf-extras-springsecurity6`
- For Spring Security 5 (Spring Boot 2): `thymeleaf-extras-springsecurity5`
- Check official Thymeleaf documentation when upgrading Spring versions

## Reference
- Thymeleaf + Spring Security Integration: https://www.thymeleaf.org/doc/articles/springsecurity.html
- Dependency: `org.thymeleaf.extras:thymeleaf-extras-springsecurity6`
