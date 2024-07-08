package com.springboot.boilerplate_webflux.query

import com.springboot.boilerplate_webflux.models.dto.AuthorDTO
import com.springboot.boilerplate_webflux.models.dto.BookDTO
import com.springboot.boilerplate_webflux.models.entity.Author
import com.springboot.boilerplate_webflux.models.entity.Book
import com.springboot.boilerplate_webflux.service.HelloService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Controller
class HelloResolver {

    @Autowired
    lateinit var helloService: HelloService

    @QueryMapping
    fun bookById(
        @Argument id: Long
    ): Mono<Map<String, Any?>> {
        return helloService.getById(id)
    }

    @QueryMapping
    fun books():Flux<Map<String, Any>> {
        return helloService.findAll()
    }

    @MutationMapping
    fun createAuthor(
        @Argument input: AuthorDTO,
    ): Mono<Author> {
        return helloService.addAuthor(input)
    }

    @MutationMapping
    fun createBook(
        @Argument input: BookDTO
    ): Mono<Map<String, Any?>> {
        return helloService.addBook(input)
    }
}