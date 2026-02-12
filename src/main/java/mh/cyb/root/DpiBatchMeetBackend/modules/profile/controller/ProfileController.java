package mh.cyb.root.DpiBatchMeetBackend.modules.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileUpdateRequest;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.service.ProfileService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile Management", description = "Endpoints for managing user profiles")
public class ProfileController {

        private final ProfileService profileService;
        private final UserService userService;

        public ProfileController(ProfileService profileService, UserService userService) {
                this.profileService = profileService;
                this.userService = userService;
        }

        @GetMapping("/me")
        @Operation(summary = "Get My Profile", description = "Retrieves the profile of the currently logged-in user.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profile retrieved successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ProfileDto> getMyProfile(
                        @AuthenticationPrincipal UserDetails userDetails) {
                User user = userService.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                return ResponseEntity.ok(profileService.getProfile(user));
        }

        @PutMapping("/me")
        @Operation(summary = "Update My Profile", description = "Updates profile details for the currently logged-in user.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ProfileDto> updateMyProfile(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody ProfileUpdateRequest profile) {
                User user = userService.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                return ResponseEntity.ok(profileService.updateProfile(user, profile));
        }

        @PostMapping("/me/skills")
        @Operation(summary = "Add Skills", description = "Adds new skills to the user's profile.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Skills added successfully"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ProfileDto> addSkills(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestBody Set<String> skillNames) {
                User user = userService.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                return ResponseEntity.ok(profileService.addSkills(user, skillNames));
        }

        @PutMapping("/privacy")
        @Operation(summary = "Update Privacy Settings", description = "Updates profile visibility settings for the currently logged-in user.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Privacy settings updated"),
                        @ApiResponse(responseCode = "404", description = "User not found")
        })
        public ResponseEntity<ProfileDto> updatePrivacy(
                        @AuthenticationPrincipal UserDetails userDetails,
                        @RequestParam(defaultValue = "false") boolean showEmail,
                        @RequestParam(defaultValue = "false") boolean showPhone,
                        @RequestParam(defaultValue = "false") boolean showLocation,
                        @RequestParam(defaultValue = "false") boolean showEmployment) {
                User user = userService.findByEmail(userDetails.getUsername())
                                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                return ResponseEntity.ok(profileService.updatePrivacy(user, showEmail, showPhone,
                                showLocation, showEmployment));
        }
}
