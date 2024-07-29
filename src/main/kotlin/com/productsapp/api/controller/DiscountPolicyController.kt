package com.productsapp.api.controller

import com.productsapp.api.model.DiscountPolicyAddDto
import com.productsapp.domain.exception.ValidationException
import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountType
import com.productsapp.domain.repository.DiscountPoliciesRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/discount-policies")
class DiscountPolicyController(private val discountPoliciesRepository: DiscountPoliciesRepository) {

    @GetMapping
    fun getDiscountPolicies(): ResponseEntity<List<DiscountPolicy>> {
        return discountPoliciesRepository.getAll().let {
            ResponseEntity.ok(it)
        }
    }

    @DeleteMapping("/{id}")
    fun deleteDiscountPolicy(@PathVariable id: String): ResponseEntity<Unit> {
        return if (discountPoliciesRepository.deleteById(id)) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun addDiscountPolicy(@RequestBody policy: DiscountPolicyAddDto): ResponseEntity<DiscountPolicy> {
        validatePolicy(policy)
        val discountPolicy = policy.toDiscountPolicy()
        discountPoliciesRepository.add(discountPolicy)
        return ResponseEntity.ok(discountPolicy)
    }

    @PutMapping("/{id}")
    fun updateDiscountPolicy(
        @PathVariable id: String,
        @RequestBody policy: DiscountPolicyAddDto
    ): ResponseEntity<DiscountPolicy> {
        return discountPoliciesRepository.getById(id)
            ?.let {
                validatePolicy(policy)
                discountPoliciesRepository.deleteById(id)
                val updatedPolicy = policy.toDiscountPolicy().copy(id = id)
                discountPoliciesRepository.add(updatedPolicy)
                ResponseEntity.ok(updatedPolicy)
            }
            ?: ResponseEntity.notFound().build()
    }

    private fun validatePolicy(policy: DiscountPolicyAddDto) {
        if (policy.name.isNullOrBlank()) {
            throw ValidationException("Required name for discount policy")
        }

        if (policy.discountType == null) {
            throw ValidationException("Required discountType for discount policy")
        }

        if (policy.content.isNullOrEmpty()) {
            throw ValidationException("Required content for discount policy")
        }

        policy.content.forEach { content ->
            if (content.discount == null || content.discount <= 0.0 || content.discount > 1.0) {
                throw ValidationException("Discount must be between 0 and 1 for all policies")
            }
        }

        if (policy.discountType == DiscountType.FIXED_PERCENTAGE) {
            if (policy.content.size > 1) {
                throw ValidationException("Only one discount value is allowed for fixed percentage discount policy")
            }
        } else if (policy.discountType == DiscountType.QUANTITY_BASED) {
            policy.content.forEach { content ->
                if (content.quantityGreaterThanOrEqual == null || content.quantityGreaterThanOrEqual <= 0) {
                    throw ValidationException("Required positive quantityGreaterThan for quantity based discount policy")
                }
            }
        }

        if (discountPoliciesRepository.getAll().any { it.name == policy.name }) {
            throw ValidationException("Discount policy name must be unique")
        }
    }
}