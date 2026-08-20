package com.propertyhub.property.controller;

import com.propertyhub.property.dto.request.VisitRequest;
import com.propertyhub.property.dto.response.VisitResponse;
import com.propertyhub.property.exception.InvalidSearchException;
import com.propertyhub.property.service.VisitService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class VisitController {

    private final VisitService visitService;

    public VisitController(VisitService visitService) {
        this.visitService = visitService;
    }

    @PostMapping("/{propertyId}/visits")
    public ResponseEntity<VisitResponse> schedule(@PathVariable Long propertyId, @Valid @RequestBody VisitRequest request) {
        VisitResponse response = visitService.schedule(propertyId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/visits")
    public ResponseEntity<List<VisitResponse>> list(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long agentId
    ) {
        if ((userId == null) == (agentId == null)) {
            throw new InvalidSearchException("Exactly one of userId or agentId must be provided");
        }
        List<VisitResponse> visits = userId != null ? visitService.listByUser(userId) : visitService.listByAgent(agentId);
        return ResponseEntity.ok(visits);
    }

}
