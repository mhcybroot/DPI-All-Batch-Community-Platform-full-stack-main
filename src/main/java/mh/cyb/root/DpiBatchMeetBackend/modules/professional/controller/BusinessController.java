package mh.cyb.root.DpiBatchMeetBackend.modules.professional.controller;

import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BusinessProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateBusinessRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.BusinessService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
public class BusinessController {

    private final BusinessService businessService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<BusinessProfileDto> registerBusiness(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBusinessRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ResponseEntity<>(businessService.registerBusiness(request, user), HttpStatus.CREATED);
    }

    @GetMapping("/mine")
    @Operation(summary = "Get my businesses", description = "Retrieves businesses owned by the currently authenticated user.")
    public ResponseEntity<Page<BusinessProfileDto>> getMyBusinesses(
            @AuthenticationPrincipal UserDetails userDetails,
            @PageableDefault(sort = "businessName") Pageable pageable) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(businessService.getBusinessesByUser(user, pageable));
    }

    @GetMapping
    public ResponseEntity<Page<BusinessProfileDto>> getAllBusinesses(
            @RequestParam(required = false) String search,
            @PageableDefault(sort = "businessName") Pageable pageable) {
        return ResponseEntity.ok(businessService.getAllBusinesses(search, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BusinessProfileDto> getBusinessById(@PathVariable Long id) {
        return ResponseEntity.ok(businessService.getBusinessById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BusinessProfileDto> updateBusiness(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody UpdateBusinessRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(businessService.updateBusiness(id, request, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBusiness(@AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        businessService.deleteBusiness(id, user);
        return ResponseEntity.noContent().build();
    }
}
