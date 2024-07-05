package com.springboot.boilerplate_webflux.models.dto.response

class ApiResponse<T> (
    val code: String,
    val message: String,
    val data: T?
)
