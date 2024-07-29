package com.productsapp.domain.model

data class DiscountPolicy(
    val id: String,
    val name: String,
    val discountType: DiscountType,
    val content: List<DiscountPolicyContent>
)