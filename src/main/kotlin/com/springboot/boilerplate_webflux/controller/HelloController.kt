package com.springboot.boilerplate_webflux.controller

import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.service.HelloService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class HelloController {

    @Autowired
    lateinit var helloService: HelloService

    @GetMapping("/")
    fun hell(): Mono<ApiResponse<String>>{
        return helloService.sayHello()
    }
}