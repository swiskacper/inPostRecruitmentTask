package com.productsapp.api.model

import com.productsapp.domain.model.DiscountPolicyContent

data class DiscountPolicyContentAddDto(
    val quantityGreaterThanOrEqual: Int?,
    val discount: Double?
) {
    fun toDiscountPolicyContent(): DiscountPolicyContent {
        return DiscountPolicyContent(
            quantityGreaterThanOrEqual = quantityGreaterThanOrEqual,
            discount = discount!!
        )
    }
}