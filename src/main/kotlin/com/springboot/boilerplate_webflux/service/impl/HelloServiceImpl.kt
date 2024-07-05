package com.springboot.boilerplate_webflux.service.impl

import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.service.HelloService
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class HelloServiceImpl: HelloService {
    override fun sayHello(): Mono<ApiResponse<String>> {
        return Mono.just(
            ApiResponse(
                HttpStatus.OK.value().toString(),
                "SUCCESS" ,
                "Hello World!",
            )
        )
    }
}