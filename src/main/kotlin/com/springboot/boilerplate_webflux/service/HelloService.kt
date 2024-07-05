package com.springboot.boilerplate_webflux.service

import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import reactor.core.publisher.Mono

interface HelloService {
    fun sayHello(): Mono<ApiResponse<String>>
}