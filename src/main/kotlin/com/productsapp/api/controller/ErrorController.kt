package com.productsapp.api.controller

import com.productsapp.domain.exception.ValidationException
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class ErrorController {

    @ExceptionHandler(ValidationException::class)
    fun handleValidationException(ex: ValidationException): ResponseEntity<String> {
        return ResponseEntity.badRequest()
            .body(ex.message)
    }
}