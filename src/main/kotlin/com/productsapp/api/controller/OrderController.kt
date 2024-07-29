package com.productsapp.api.controller

import com.productsapp.api.model.OrderCalculationRequestDto
import com.productsapp.api.model.OrderCalculationResultDto
import com.productsapp.domain.service.OrderCalculationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val orderCalculationService: OrderCalculationService
) {

    @PostMapping("/calculate-total-price")
    fun calculateTotalPrice(
        @RequestBody orderCalculationRequestDto: OrderCalculationRequestDto
    ): ResponseEntity<OrderCalculationResultDto> {
        return orderCalculationService.calculateTotalPriceWithDiscount(orderCalculationRequestDto).let {
            ResponseEntity.ok(it)
        }
    }
}