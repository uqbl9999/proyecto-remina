package com.colegioapp.iam.dto;

public record LoginResponse(String accessToken, String refreshToken, long expiresIn) { }
