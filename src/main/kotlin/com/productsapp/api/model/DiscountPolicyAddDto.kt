package com.productsapp.api.model

import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountType
import java.util.*

data class DiscountPolicyAddDto(
    val name: String?,
    val discountType: DiscountType?,
    val content: List<DiscountPolicyContentAddDto>?
) {
    fun toDiscountPolicy(): DiscountPolicy {
        return DiscountPolicy(
            id = UUID.randomUUID().toString(),
            name = name!!,
            discountType = discountType!!,
            content = content!!.map { it.toDiscountPolicyContent() }
        )
    }
}