package com.productsapp.domain.service

import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountPolicyContent
import com.productsapp.domain.model.DiscountType
import com.productsapp.domain.model.Product
import com.productsapp.domain.repository.DiscountPoliciesRepository
import com.productsapp.domain.repository.ProductRepository
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class OrderCalculationServiceTest {

    private val orderCalculationService = OrderCalculationService(ProductRepository(), DiscountPoliciesRepository())

    @Test
    fun testCalculateTotalPriceWithDiscountWithFixedPercentageDiscountPolicy() {
        val product = Product(price = 100.0, id = "1", name = "One")
        val discountPolicy = DiscountPolicy(
            id = "1",
            name = "Fixed 10%",
            discountType = DiscountType.FIXED_PERCENTAGE,
            content = listOf(DiscountPolicyContent(quantityGreaterThanOrEqual = null, discount = 0.1))
        )
        val totalPrice = orderCalculationService.calculateTotalPriceWithDiscount(product, 2, discountPolicy)
        assertEquals(180.0, totalPrice)
    }

    @Test
    fun testCalculateTotalPriceWithDiscountWithQuantityBasedDiscountPolicy() {
        val product = Product(price = 100.0, id = "1", name = "One")
        val discountPolicy = DiscountPolicy(
            id = "2",
            name = "Tiered Discount",
            discountType = DiscountType.QUANTITY_BASED,
            content = listOf(
                DiscountPolicyContent(quantityGreaterThanOrEqual = 5, discount = 0.2),
                DiscountPolicyContent(quantityGreaterThanOrEqual = 2, discount = 0.1),
                DiscountPolicyContent(quantityGreaterThanOrEqual = 0, discount = 0.0)
            )
        )
        val totalPrice = orderCalculationService.calculateTotalPriceWithDiscount(product, 3, discountPolicy)
        assertEquals(270.0, totalPrice)
    }

}