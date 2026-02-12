package mh.cyb.root.DpiBatchMeetBackend.modules.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.dto.BirthdayAlertDto;
import mh.cyb.root.DpiBatchMeetBackend.modules.community.service.BirthdayService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/birthdays")
@Tag(name = "Birthday Dashboard", description = "Endpoints for community birthday alerts")
public class BirthdayController {

    private final BirthdayService birthdayService;

    public BirthdayController(BirthdayService birthdayService) {
        this.birthdayService = birthdayService;
    }

    @GetMapping("/today")
    @Operation(summary = "Get today's birthdays", description = "Retrieves all members celebrating their birthday today.")
    public ResponseEntity<List<BirthdayAlertDto>> getTodayBirthdays() {
        return ResponseEntity.ok(birthdayService.getTodayBirthdays());
    }

    @GetMapping("/upcoming")
    @Operation(summary = "Get upcoming birthdays", description = "Retrieves members with birthdays in the next 7 days.")
    public ResponseEntity<List<BirthdayAlertDto>> getUpcomingBirthdays(@RequestParam(defaultValue = "7") int days) {
        return ResponseEntity.ok(birthdayService.getUpcomingBirthdays(days));
    }
}
