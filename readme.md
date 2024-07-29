# ProductsApp API

## Overview

The ProductsApp API is a Spring Boot application that provides RESTful endpoints for managing discount policies, calculating order prices, and retrieving product information. The API includes the following functionalities:

1. **Discount Policy Management**: CRUD operations for discount policies.
2. **Order Price Calculation**: Calculation of total order prices with discounts.
3. **Product Information**: Retrieval of product details.


The application is quite simple. Some things can be improved, e.g. adding corutins/CompletableFutures to the contollers or separate classes responsible for validation or adding more abstractions. However, I did not want to make this task too complicated.

I hope that BASIC requirements are fulfilled :) 

## How to run
1.
```
docker pull swiskacper/inpost_recrutiment_task
```

2. 
```
docker run -p 8080:8080 swiskacper/inpost_recrutiment_task
```


## Endpoints

### Discount Policy Management

- **Add New Discount Policy**
    - **Description**: Adds a new discount policy.
    - **HTTP Request**:
      ```http
      POST http://localhost:8080/api/discount-policies
      Content-Type: application/json
  
      {
          "name": "Policy 3",
          "discountType": "FIXED_PERCENTAGE",
          "content": [
              {
                  "quantityGreaterThanOrEqual": null,
                  "discount": 0.15
              }
          ]
      }
      ```

  OR different type of discount
    - **HTTP Request**:
      ```http
      POST http://localhost:8080/api/discount-policies
      Content-Type: application/json
  
      {
          "name": "Policy 32",
          "discountType": "QUANTITY_BASED",
          "content": [
        {
            "quantityGreaterThanOrEqual": 5,
            "discount": 0.10
        },
        {
            "quantityGreaterThanOrEqual": 10,
            "discount": 0.15
        }
  ]
  }
  
- **Get All Discount Policies**
    - **Description**: Retrieves a list of all discount policies.
    - **HTTP Request**:
      ```http
      GET http://localhost:8080/api/discount-policies
      ```

- **Update Discount Policy by ID**
    - **Description**: Updates an existing discount policy by ID.
    - **HTTP Request**:
      ```http
      PUT http://localhost:8080/api/discount-policies/{id}
      Content-Type: application/json
  
      {
          "name": "Updated Policy",
          "discountType": "FIXED_PERCENTAGE",
          "content": [
              {
                  "quantityGreaterThanOrEqual": null,
                  "discount": 0.25
              }
          ]
      }
      ```

- **Delete Discount Policy by ID**
    - **Description**: Deletes a specific discount policy by ID.
    - **HTTP Request**:
      ```http
      DELETE /api/discount-policies/{id}
      ```

### Order Price Calculation

- **Calculate Total Price with Discount**
    - **Description**: Calculates the total price of an order applying the specified discount policy.
    - **HTTP Request**:
      ```http
      POST http://localhost:8080/api/orders/calculate-total-price
      Content-Type: application/json
  
      {
          "productId": "550e8400-e29b-41d4-a716-446655440000",
          "quantity": 2,
          "discountPolicyId": "1"
      }
      ```
    
### Product Information

- **Get All Products**
    - **Description**: Retrieves a list of all available products. Products are hard coded.
    - **HTTP Request**:
      ```http
      GET http://localhost:8080/api/products
      ```

- **Get Product by ID**
    - **Description**: Retrieves details of a specific product by ID.
    - **HTTP Request**:
      ```http
      GET http://localhost:8080/api/products/{id}
      ```
      