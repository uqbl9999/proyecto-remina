package com.colegioapp.shared.dto;

public record ErrorResponse(int status, String error, String message) { }
