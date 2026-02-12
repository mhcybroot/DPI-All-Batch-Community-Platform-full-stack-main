package mh.cyb.root.DpiBatchMeetBackend.modules.professional.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.domain.BloodGroup;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.BloodDonorProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.CreateBloodDonorRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.professional.service.BloodDonorService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blood-donors")
@RequiredArgsConstructor
public class BloodDonorController {

    private final BloodDonorService bloodDonorService;
    private final UserService userService;

    @GetMapping("/search")
    public ResponseEntity<List<BloodDonorProfileDto>> searchDonors(
            @RequestParam BloodGroup bloodGroup,
            @RequestParam(required = false) String location) {
        return ResponseEntity.ok(bloodDonorService.searchDonors(bloodGroup, location));
    }

    @PostMapping("/profile")
    public ResponseEntity<BloodDonorProfileDto> registerDonor(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateBloodDonorRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new ResponseEntity<>(bloodDonorService.registerData(request, user), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    public ResponseEntity<BloodDonorProfileDto> getMyProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bloodDonorService.getMyProfile(user));
    }

    @PatchMapping("/status")
    public ResponseEntity<BloodDonorProfileDto> updateStatus(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam boolean isAvailable) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bloodDonorService.updateAvailability(isAvailable, user));
    }

    @PatchMapping("/last-donation")
    public ResponseEntity<BloodDonorProfileDto> updateLastDonationDate(@AuthenticationPrincipal UserDetails userDetails,
            @RequestBody mh.cyb.root.DpiBatchMeetBackend.modules.professional.dto.UpdateLastDonationRequest request) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(bloodDonorService.updateLastDonationDate(request.getLastDonationDate(), user));
    }
}
