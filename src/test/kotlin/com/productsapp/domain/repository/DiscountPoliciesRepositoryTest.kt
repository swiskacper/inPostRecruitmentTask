package com.productsapp.domain.repository

import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountType
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class DiscountPoliciesRepositoryTest {
    private var repository: DiscountPoliciesRepository = DiscountPoliciesRepository()

    @Test
    fun testAddPolicy() {
        val policy = DiscountPolicy("1", "One", DiscountType.FIXED_PERCENTAGE, listOf())
        repository.add(policy)
        val retrievedPolicy = repository.getById("1")
        assertNotNull(retrievedPolicy)
        assertEquals("One", retrievedPolicy?.name)
    }

    @Test
    fun testGetById() {
        val policy = DiscountPolicy("1", "One", DiscountType.FIXED_PERCENTAGE, listOf())
        repository.add(policy)
        val retrievedPolicy = repository.getById("1")
        assertNotNull(retrievedPolicy)
        assertEquals("1", retrievedPolicy?.id)
        assertEquals("One", retrievedPolicy?.name)
    }

    @Test
    fun testGetAll() {
        val policy1 = DiscountPolicy("1", "One", DiscountType.FIXED_PERCENTAGE, listOf())
        val policy2 = DiscountPolicy("2", "Two", DiscountType.FIXED_PERCENTAGE, listOf())
        repository.add(policy1)
        repository.add(policy2)
        val allPolicies = repository.getAll()
        assertEquals(2, allPolicies.size)
        assertTrue(allPolicies.contains(policy1))
        assertTrue(allPolicies.contains(policy2))
    }

    @Test
    fun testDeleteById() {
        val policy = DiscountPolicy("1", "One", DiscountType.FIXED_PERCENTAGE, listOf())
        repository.add(policy)
        val isDeleted = repository.deleteById("1")
        assertTrue(isDeleted)
        assertNull(repository.getById("1"))
    }

    @Test
    fun testDeleteNonExistentPolicy() {
        val isDeleted = repository.deleteById("nfjwbh3u4gf2iufb")
        assertFalse(isDeleted)
    }
}