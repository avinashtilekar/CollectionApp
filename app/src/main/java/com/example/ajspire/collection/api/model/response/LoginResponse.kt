package com.example.ajspire.collection.api.model.response

data class LoginResponse(
    val token: String,
    val invoice_prefix: String,
    val last_invoice_number: Int,
    val user: User
)
data class User(
    val address: String,
    val createdAt: String,
    val email: String,
    val id: Int,
    val mobile_no: String,
    val name: String,
    val password: String,
    val profile_photo: Any,
    val status: Int,
    val updatedAt: String,
    val user_type: Any
)