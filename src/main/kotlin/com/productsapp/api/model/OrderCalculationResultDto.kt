package com.productsapp.api.model

import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.Product

data class OrderCalculationResultDto(
    val product: Product,
    val quantity: Int,
    val discountPolicy: DiscountPolicy,
    val totalPrice: Double,
    val totalPriceWithoutDiscount: Double
)