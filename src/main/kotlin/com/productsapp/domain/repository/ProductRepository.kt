package com.productsapp.domain.repository

import com.productsapp.domain.model.Product
import org.springframework.stereotype.Repository

@Repository
class ProductRepository {
    fun getProductsList(): List<Product> {
        return listOf(
            Product("550e8400-e29b-41d4-a716-446655440000", "TV", 2541.0),
            Product("550e8400-e29b-41d4-a716-446655440001", "Washing machine", 1540.0),
            Product("550e8400-e29b-41d4-a716-446655440002", "Microwave", 214.0),
            Product("550e8400-e29b-41d4-a716-446655440003", "Fridge", 1540.0),
            Product("550e8400-e29b-41d4-a716-446655440004", "Oven", 540.0),
            Product("550e8400-e29b-41d4-a716-446655440005", "Dishwasher", 1540.0),
            Product("550e8400-e29b-41d4-a716-446655440006", "Vacuum Cleaner", 540.0),
            Product("550e8400-e29b-41d4-a716-446655440007", "Iron", 425.0),
            Product("550e8400-e29b-41d4-a716-446655440008", "Blender", 642.0),
            Product("550e8400-e29b-41d4-a716-446655440009", "Toaster", 100.0),
            Product("550e8400-e29b-41d4-a716-446655440010", "Coffee maker", 1542.0)
        )
    }

    fun getProductById(id: String): Product? {
        return getProductsList().firstOrNull { it.id == id }
    }

    fun removeProductById(id: String) {}
}