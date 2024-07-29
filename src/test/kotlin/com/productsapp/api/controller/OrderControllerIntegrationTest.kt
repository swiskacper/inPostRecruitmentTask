package com.productsapp.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.productsapp.api.model.OrderCalculationRequestDto
import com.productsapp.api.model.OrderCalculationResultDto
import com.productsapp.domain.model.DiscountPolicy
import com.productsapp.domain.model.DiscountPolicyContent
import com.productsapp.domain.model.DiscountType
import com.productsapp.domain.repository.DiscountPoliciesRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.util.stream.Stream

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
                    DiscountPolicyContent(2, 0.05),
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

    @ParameterizedTest
    @CsvSource(
        "1, 2541.0",
        "5, 11434.5",
        "2, 4827.9"
    )
    fun testCalculateTotalPriceWithQuantityBasedDiscount(quantity: Int, expectedTotalPrice: Double) {
        val request = OrderCalculationRequestDto(
            productId = "550e8400-e29b-41d4-a716-446655440000",
            quantity = quantity,
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
        assertEquals(expectedTotalPrice, response.totalPrice, 0.01)
    }

    @ParameterizedTest
    @MethodSource("provideInvalidOrderRequests")
    fun testCalculateTotalPriceWithInvalidInput(productId: String?, quantity: Int?, discountPolicyId: String?, expectedErrorMessage: String) {
        val request = OrderCalculationRequestDto(
            productId = productId,
            quantity = quantity,
            discountPolicyId = discountPolicyId
        )
        val requestJson = objectMapper.writeValueAsString(request)

        val result = mockMvc.perform(post("/api/orders/calculate-total-price")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
            .andExpect(status().isBadRequest)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.contains(expectedErrorMessage))
    }

    companion object {
        @JvmStatic
        fun provideInvalidOrderRequests(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "550e8400-e29b-41d4-a716-446655440000", 2, null, "Discount id must be provided"
                ),
                Arguments.of(
                    null, 2, "1", "Product id must be provided"
                ),
                Arguments.of(
                    "550e8400-e29b-41d4-a716-446655440000", null, "1", "Quantity must be provided"
                )
            )
        }
    }
}
