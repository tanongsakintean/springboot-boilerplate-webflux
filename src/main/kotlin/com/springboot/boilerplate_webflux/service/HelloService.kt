package com.springboot.boilerplate_webflux.service

import com.springboot.boilerplate_webflux.models.cache.AuthorCacheDTO
import com.springboot.boilerplate_webflux.models.dto.requests.AuthorDTO
import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.models.entity.Author
import com.springboot.boilerplate_webflux.models.entity.Book
import reactor.core.publisher.Mono

interface HelloService {
    fun sayHello(): Mono<ApiResponse<String>>
    fun getBooks(): Mono<ApiResponse<List<Book>>>
    fun createAuthor(body: AuthorDTO): Mono<ApiResponse<Author>>
    fun getAuthor(): Mono<ApiResponse<List<Author>>>
    fun save(key: String, value: String): Mono<Boolean>
    fun find(key: String): Mono<String?>
//    fun getAuthorById(id: Int):Mono<ApiResponse<AuthorCacheDTO>>
}