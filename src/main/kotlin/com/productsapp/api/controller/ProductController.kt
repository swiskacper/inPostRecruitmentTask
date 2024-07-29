package com.productsapp.api.controller

import com.productsapp.domain.model.Product
import com.productsapp.domain.repository.ProductRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/products")
class ProductController(private val productRepository: ProductRepository) {

    @GetMapping
    fun getProducts(): ResponseEntity<List<Product>> {
        return productRepository.getProductsList().let {
            ResponseEntity.ok(it)
        }
    }

    @GetMapping("/{id}")
    fun getProduct(@PathVariable("id") id: String): ResponseEntity<Product> {
        return productRepository.getProductById(id)
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }
}