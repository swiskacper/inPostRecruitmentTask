package com.productsapp.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.productsapp.api.model.DiscountPolicyAddDto
import com.productsapp.api.model.DiscountPolicyContentAddDto
import org.junit.jupiter.params.provider.Arguments
import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountPolicyContent
import com.productsapp.domain.model.DiscountType
import com.productsapp.domain.repository.DiscountPoliciesRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.util.stream.Stream

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class DiscountPolicyControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var discountPoliciesRepository: DiscountPoliciesRepository

    @BeforeEach
    fun setup() {
        clearRepository()
        discountPoliciesRepository.add(DiscountPolicy("1", "Policy 1", DiscountType.FIXED_PERCENTAGE, listOf(DiscountPolicyContent(null, 0.1))))
        discountPoliciesRepository.add(DiscountPolicy("2", "Policy 2", DiscountType.QUANTITY_BASED, listOf(DiscountPolicyContent(5, 0.2))))
    }

    private fun clearRepository() {
        val policies = discountPoliciesRepository.getAll()
        policies.forEach { discountPoliciesRepository.deleteById(it.id) }
    }

    @Test
    fun testGetDiscountPolicies() {
        val result = mockMvc.perform(get("/api/discount-policies"))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val policies = objectMapper.readValue(responseContent, Array<DiscountPolicy>::class.java)
        assertEquals(2, policies.size)
    }

    @Test
    fun testDeleteDiscountPolicy() {
        mockMvc.perform(delete("/api/discount-policies/1"))
            .andExpect(status().isNoContent)

        assertNull(discountPoliciesRepository.getById("1"))
        assertEquals(1, discountPoliciesRepository.getAll().size)
    }

    @Test
    fun testDeleteNonExistentDiscountPolicy() {
        mockMvc.perform(delete("/api/discount-policies/99"))
            .andExpect(status().isNotFound)
    }

    @Test
    fun testAddDiscountPolicy() {
        val policy = DiscountPolicyAddDto(
            name = "Policy 3",
            discountType = DiscountType.FIXED_PERCENTAGE,
            content = listOf(DiscountPolicyContentAddDto(null, 0.15))
        )
        val policyJson = objectMapper.writeValueAsString(policy)

        val result = mockMvc.perform(post("/api/discount-policies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(policyJson))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val addedPolicy = objectMapper.readValue(responseContent, DiscountPolicy::class.java)
        assertEquals("Policy 3", addedPolicy.name)
    }

    @Test
    fun testAddDuplicateDiscountPolicyName() {
        val policy = DiscountPolicyAddDto(
            name = "Policy 1", // Duplicate name
            discountType = DiscountType.FIXED_PERCENTAGE,
            content = listOf(DiscountPolicyContentAddDto(null, 0.1))
        )
        val policyJson = objectMapper.writeValueAsString(policy)

        val result = mockMvc.perform(post("/api/discount-policies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(policyJson))
            .andExpect(status().isBadRequest)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.contains("Discount policy name must be unique"))
    }

    @Test
    fun testUpdateDiscountPolicy() {
        val policy = DiscountPolicyAddDto(
            name = "Updated Policy",
            discountType = DiscountType.FIXED_PERCENTAGE,
            content = listOf(DiscountPolicyContentAddDto(null, 0.25))
        )
        val policyJson = objectMapper.writeValueAsString(policy)

        val result = mockMvc.perform(put("/api/discount-policies/1")
            .contentType(MediaType.APPLICATION_JSON)
            .content(policyJson))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val updatedPolicy = objectMapper.readValue(responseContent, DiscountPolicy::class.java)
        assertEquals("Updated Policy", updatedPolicy.name)
    }

    @ParameterizedTest
    @MethodSource("provideInvalidDiscountPolicies")
    fun testAddInvalidDiscountPolicy(policy: DiscountPolicyAddDto, expectedErrorMessage: String) {
        val policyJson = objectMapper.writeValueAsString(policy)

        val result = mockMvc.perform(post("/api/discount-policies")
            .contentType(MediaType.APPLICATION_JSON)
            .content(policyJson))
            .andExpect(status().isBadRequest)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.contains(expectedErrorMessage))
    }

    @Test
    fun testUpdateNonExistentDiscountPolicy() {
        val policy = DiscountPolicyAddDto(
            name = "Non-existent Policy",
            discountType = DiscountType.FIXED_PERCENTAGE,
            content = listOf(DiscountPolicyContentAddDto(null, 0.30))
        )
        val policyJson = objectMapper.writeValueAsString(policy)

        mockMvc.perform(put("/api/discount-policies/99")
            .contentType(MediaType.APPLICATION_JSON)
            .content(policyJson))
            .andExpect(status().isNotFound)
    }


    companion object {
        @JvmStatic
        fun provideInvalidDiscountPolicies(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    DiscountPolicyAddDto(
                        name = null,
                        discountType = DiscountType.FIXED_PERCENTAGE,
                        content = listOf(DiscountPolicyContentAddDto(null, 0.15))
                    ),
                    "Required name for discount policy"
                ),
                Arguments.of(
                    DiscountPolicyAddDto(
                        name = "Invalid Discount",
                        discountType = DiscountType.FIXED_PERCENTAGE,
                        content = listOf(DiscountPolicyContentAddDto(null, 1.5))
                    ),
                    "Discount must be between 0 and 1 for all policies"
                ),
                Arguments.of(
                    DiscountPolicyAddDto(
                        name = "Negative Quantity",
                        discountType = DiscountType.QUANTITY_BASED,
                        content = listOf(DiscountPolicyContentAddDto(-1, 0.1))
                    ),
                    "Required positive quantityGreaterThan for quantity based discount policy"
                )
            )
        }
    }
}
