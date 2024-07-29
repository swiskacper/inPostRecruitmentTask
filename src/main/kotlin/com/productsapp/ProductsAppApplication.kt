package com.productsapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ProductsAppApplication

fun main(args: Array<String>) {
	runApplication<ProductsAppApplication>(*args)
}
