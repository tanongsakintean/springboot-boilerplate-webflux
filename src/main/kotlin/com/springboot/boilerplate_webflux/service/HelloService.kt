package com.springboot.boilerplate_webflux.service

import com.springboot.boilerplate_webflux.models.dto.AuthorDTO
import com.springboot.boilerplate_webflux.models.dto.BookDTO
import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.models.entity.Author
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface HelloService {
    fun sayHello(): Mono<ApiResponse<String>>
    fun getById(id: Long): Mono<Map<String, Any?>>
    fun addAuthor(body: AuthorDTO): Mono<Author>
    fun addBook(body: BookDTO): Mono<Map<String, Any?>>
    fun findAll():Flux<Map<String, Any>>
}