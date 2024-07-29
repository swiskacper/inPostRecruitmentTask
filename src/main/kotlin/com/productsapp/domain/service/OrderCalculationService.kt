package com.productsapp.domain.service

import com.productsapp.api.model.OrderCalculationRequestDto
import com.productsapp.api.model.OrderCalculationResultDto
import com.productsapp.domain.exception.ValidationException
import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountType
import com.productsapp.domain.model.Product
import com.productsapp.domain.repository.DiscountPoliciesRepository
import com.productsapp.domain.repository.ProductRepository
import org.springframework.stereotype.Service

@Service
class OrderCalculationService(
    private val productRepository: ProductRepository,
    private val discountPolicyRepository: DiscountPoliciesRepository
) {
    fun calculateTotalPriceWithDiscount(calculation: OrderCalculationRequestDto): OrderCalculationResultDto {
        if (calculation.productId == null) {
            throw ValidationException("Product id must be provided")
        }
        if (calculation.discountPolicyId == null) {
            throw ValidationException("Discount id must be provided")
        }
        if (calculation.quantity == null) {
            throw ValidationException("Quantity must be provided")
        }
        if (calculation.quantity <= 0) {
            throw ValidationException("Quantity must be bigger than 0")
        }

        val discountPolicy = discountPolicyRepository.getById(calculation.discountPolicyId)
            ?: throw ValidationException("Discount policy not found")
        val product = productRepository.getProductById(calculation.productId)
            ?: throw ValidationException("Product not found")

        val totalPriceWithoutDiscount = product.price * calculation.quantity
        val totalPrice = calculateTotalPriceWithDiscount(product, calculation.quantity, discountPolicy)
        return OrderCalculationResultDto(
            product = product,
            discountPolicy = discountPolicy,
            totalPrice = totalPrice,
            quantity = calculation.quantity,
            totalPriceWithoutDiscount = totalPriceWithoutDiscount
        )
    }

    fun calculateTotalPriceWithDiscount(product: Product, quantity: Int, discountPolicy: DiscountPolicy): Double {
        val discount = if (discountPolicy.discountType == DiscountType.FIXED_PERCENTAGE) {
            discountPolicy.content[0].discount
        } else {
            discountPolicy.content
                .sortedByDescending { it.quantityGreaterThanOrEqual!! }
                .firstOrNull { quantity >= it.quantityGreaterThanOrEqual!! }
                ?.discount ?: 0.0
        }
        return product.price * quantity * (1 - discount)
    }
}
