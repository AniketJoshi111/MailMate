package com.email_writer.controllers;

import com.email_writer.entity.EmailRequest;
import com.email_writer.service.EmailGeneratorService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/email")
@AllArgsConstructor
@CrossOrigin(origins = "*")
public class EmailGeneratorController {

    @Autowired
    private final EmailGeneratorService emailGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<?>  generateEmail(@RequestBody EmailRequest emailRequest)
    {
        String response = emailGeneratorService.generateEmailReply(emailRequest);

        return ResponseEntity.ok(response);

    }

    @PostMapping("/summarize")
    public ResponseEntity<?> summarizeEmail(@RequestBody EmailRequest emailRequest) {
        String summary = emailGeneratorService.summarizeEmail(emailRequest);
        String actionPoints = emailGeneratorService.extractKeyActionPoints(emailRequest);
        return ResponseEntity.ok(Map.of(
                "summary", summary,
                "actionPoints", actionPoints
        ));
    }

    @PostMapping("/sentiment-analysis")
    public ResponseEntity<?> analyzeEmailSentiment(@RequestBody EmailRequest emailRequest)
    {
        String response = emailGeneratorService.analyzeEmailSentiment(emailRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/categorize")
    public ResponseEntity<?> categorizeEmail(@RequestBody EmailRequest emailRequest) {
        String categories = emailGeneratorService.categorizeEmail(emailRequest);
        return ResponseEntity.ok(categories);
    }

    @PostMapping("/extract-calendar-event")
    public ResponseEntity<?> extractCalendarEvent(@RequestBody EmailRequest emailRequest) {
        String calendarEvent = emailGeneratorService.extractCalendarEvent(emailRequest);
        return ResponseEntity.ok(calendarEvent);
    }


}
