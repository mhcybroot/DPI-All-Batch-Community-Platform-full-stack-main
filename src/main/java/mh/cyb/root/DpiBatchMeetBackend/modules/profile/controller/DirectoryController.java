package mh.cyb.root.DpiBatchMeetBackend.modules.profile.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.domain.Profile;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.service.DirectoryService;
import mh.cyb.root.DpiBatchMeetBackend.modules.profile.dto.ProfileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directory")
@Tag(name = "Directory Search", description = "Endpoints for searching and filtering member profiles")
public class DirectoryController {

    private final DirectoryService directoryService;

    public DirectoryController(DirectoryService directoryService) {
        this.directoryService = directoryService;
    }

    @GetMapping("/search")
    @Operation(summary = "Search Profiles", description = "Search for members by name or bio content.")
    @ApiResponse(responseCode = "200", description = "Search results retrieved")
    public ResponseEntity<List<ProfileDto>> search(@RequestParam String query) {
        return ResponseEntity.ok(directoryService.searchProfiles(query));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter Profiles", description = "Filter members by skill, location, or employment status.")
    @ApiResponse(responseCode = "200", description = "Filtered results retrieved")
    public ResponseEntity<List<ProfileDto>> filter(
            @RequestParam(required = false) String skill,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentStatus) {
        return ResponseEntity.ok(directoryService.filterProfiles(skill, location, employmentStatus));
    }
}
