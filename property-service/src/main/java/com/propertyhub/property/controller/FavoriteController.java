package com.propertyhub.property.controller;

import com.propertyhub.property.dto.response.FavoriteResponse;
import com.propertyhub.property.service.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/properties")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{propertyId}/favorites")
    public ResponseEntity<FavoriteResponse> add(@PathVariable Long propertyId, @RequestParam Long userId) {
        FavoriteResponse response = favoriteService.add(propertyId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{propertyId}/favorites")
    public ResponseEntity<Void> remove(@PathVariable Long propertyId, @RequestParam Long userId) {
        favoriteService.remove(propertyId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteResponse>> listByUser(@RequestParam Long userId) {
        return ResponseEntity.ok(favoriteService.listByUser(userId));
    }

}
