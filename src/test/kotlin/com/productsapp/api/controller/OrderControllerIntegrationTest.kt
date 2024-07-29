package com.productsapp.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.productsapp.api.model.OrderCalculationRequestDto
import com.productsapp.api.model.OrderCalculationResultDto
import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountPolicyContent
import com.productsapp.domain.model.DiscountType
import com.productsapp.domain.repository.DiscountPoliciesRepository
import com.productsapp.domain.service.OrderCalculationService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var discountPoliciesRepository: DiscountPoliciesRepository


    @BeforeEach
    fun setup() {
        clearRepositories()
        discountPoliciesRepository.add(
            DiscountPolicy(
                "1",
                "Percentage Discount",
                DiscountType.FIXED_PERCENTAGE,
                listOf(DiscountPolicyContent(null, 0.1))
            )
        )
        discountPoliciesRepository.add(
            DiscountPolicy(
                "2",
                "Quantity Based Discount",
                DiscountType.QUANTITY_BASED,
                listOf(
                    DiscountPolicyContent(1, 0.05),
                    DiscountPolicyContent(5, 0.1)
                )
            )
        )
    }

    private fun clearRepositories() {
        val policies = discountPoliciesRepository.getAll()
        policies.forEach { discountPoliciesRepository.deleteById(it.id) }
    }

    @Test
    fun testCalculateTotalPriceWithFixedPercentageDiscount() {
        val request = OrderCalculationRequestDto(
            productId = "550e8400-e29b-41d4-a716-446655440000",
            quantity = 2,
            discountPolicyId = "1"
        )
        val requestJson = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(post("/api/orders/calculate-total-price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val response = objectMapper.readValue(responseContent, OrderCalculationResultDto::class.java)
        assertEquals(4573.8, response.totalPrice, 0.01)
    }

    @Test
    fun testCalculateTotalPriceWithQuantityBasedDiscount() {
        val request = OrderCalculationRequestDto(
            productId = "550e8400-e29b-41d4-a716-446655440000",
            quantity = 5,
            discountPolicyId = "2"
        )
        val requestJson = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(post("/api/orders/calculate-total-price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val response = objectMapper.readValue(responseContent, OrderCalculationResultDto::class.java)
        assertEquals(11434.5, response.totalPrice, 0.01)
    }

    @Test
    fun testCalculateTotalPriceWithoutDiscountPolicy() {
        val request = OrderCalculationRequestDto(
            productId = "550e8400-e29b-41d4-a716-446655440000",
            quantity = 2,
            discountPolicyId = null
        )
        val requestJson = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(post("/api/orders/calculate-total-price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isBadRequest)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.contains("Discount id must be provided"))
    }

    @Test
    fun testCalculateTotalPriceWithoutProductId() {
        val request = OrderCalculationRequestDto(
            productId = null,
            quantity = 2,
            discountPolicyId = "1"
        )
        val requestJson = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(post("/api/orders/calculate-total-price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isBadRequest)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.contains("Product id must be provided"))
    }

    @Test
    fun testCalculateTotalPriceWithoutQuantity() {
        val request = OrderCalculationRequestDto(
            productId = "550e8400-e29b-41d4-a716-446655440000",
            quantity = null,
            discountPolicyId = "1"
        )
        val requestJson = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(post("/api/orders/calculate-total-price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isBadRequest)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.contains("Quantity must be provided"))
    }
}
