package com.example.active.workout;

import com.example.active.workout.dto.PersonalRecordResponse;
import com.example.active.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/exercises")
@RequiredArgsConstructor

public class PersonalRecordController {
    private final PersonalRecordService personalRecordService;

    @GetMapping("/{id}/personal-record")
    public ResponseEntity<PersonalRecordResponse> getPr(@PathVariable Long id,
                                                        @AuthenticationPrincipal User userAutenticado) {
        return ResponseEntity.ok(personalRecordService.getPersonalRecord(userAutenticado.getId(), id));
    }
}
