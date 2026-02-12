package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.MemoryDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.UploadMemoryRequest;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.MemoryService;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.User;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.domain.Role;
import mh.cyb.root.DpiBatchMeetBackend.modules.user.service.UserService;
import mh.cyb.root.DpiBatchMeetBackend.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/memories")
@Tag(name = "Memory Wall", description = "Endpoints for shared media gallery")
public class MemoryController {

    private final MemoryService memoryService;
    private final UserService userService;

    public MemoryController(MemoryService memoryService, UserService userService) {
        this.memoryService = memoryService;
        this.userService = userService;
    }

    @GetMapping
    @Operation(summary = "Get all memories", description = "Retrieves all shared memories, sorted by most recent.")
    public ResponseEntity<List<MemoryDto>> getAllMemories() {
        return ResponseEntity.ok(memoryService.getAllMemories());
    }

    @PostMapping
    @Operation(summary = "Upload a memory", description = "Uploads a new memory to the wall.")
    public ResponseEntity<MemoryDto> uploadMemory(@RequestBody UploadMemoryRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return ResponseEntity.status(201).body(memoryService.uploadMemory(request, user.getId()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a memory", description = "Deletes a memory. Only the uploader or an admin can delete.")
    public ResponseEntity<Void> deleteMemory(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        boolean isAdmin = user.getRoles().contains(Role.ADMINISTRATOR);
        memoryService.deleteMemory(id, user.getId(), isAdmin);
        return ResponseEntity.noContent().build();
    }
}
