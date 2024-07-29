package com.productsapp.api.model

data class OrderCalculationRequestDto(
    val productId: String?,
    val quantity: Int?,
    val discountPolicyId: String?
)