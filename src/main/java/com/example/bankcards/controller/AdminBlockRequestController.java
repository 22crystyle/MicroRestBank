package com.example.bankcards.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/block-request")
@PreAuthorize("hasRole('ADMIN')")
public class AdminBlockRequestController {

    @PostMapping("/{id}/approve")
    public ResponseEntity<Void> approve(@PathVariable("id") long id) {
        return ResponseEntity.ok().build();
    }
}
