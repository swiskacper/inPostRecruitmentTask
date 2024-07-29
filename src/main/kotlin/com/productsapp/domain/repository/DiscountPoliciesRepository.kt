package com.productsapp.domain.repository

import com.productsapp.domain.model.DiscountPolicy
import org.springframework.stereotype.Repository

@Repository
class DiscountPoliciesRepository {
    private val policies = mutableListOf<DiscountPolicy>()

    fun getById(id: String): DiscountPolicy? {
        return policies.find { it.id == id }
    }

    fun getAll(): List<DiscountPolicy> {
        return policies.toList()
    }

    fun deleteById(id: String): Boolean {
        return policies.removeIf { it.id == id }
    }

    fun add(policy: DiscountPolicy) {
        policies.add(policy)
    }
}