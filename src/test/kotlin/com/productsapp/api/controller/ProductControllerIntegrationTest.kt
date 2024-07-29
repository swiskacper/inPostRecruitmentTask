package com.productsapp.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.productsapp.domain.model.Product
import com.productsapp.domain.repository.ProductRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIntegrationTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun testGetProducts() {
        val result = mockMvc.perform(get("/api/products"))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val products = objectMapper.readValue(responseContent, Array<Product>::class.java)
        assertEquals(11, products.size) // Assuming there are 11 products in the static list
    }

    @Test
    fun testGetProductById() {
        val result = mockMvc.perform(get("/api/products/550e8400-e29b-41d4-a716-446655440000"))
            .andExpect(status().isOk)
            .andReturn()

        val responseContent = result.response.contentAsString
        val product = objectMapper.readValue(responseContent, Product::class.java)
        assertEquals("550e8400-e29b-41d4-a716-446655440000", product.id)
        assertEquals("TV", product.name)
        assertEquals(2541.0, product.price)
    }

    @Test
    fun testGetNonExistentProductById() {
        val result = mockMvc.perform(get("/api/products/non-existent-id"))
            .andExpect(status().isNotFound)
            .andReturn()

        val responseContent = result.response.contentAsString
        assertTrue(responseContent.isEmpty())
    }
}
