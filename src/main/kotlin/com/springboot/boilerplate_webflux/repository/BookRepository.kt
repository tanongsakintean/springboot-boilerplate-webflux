package com.springboot.boilerplate_webflux.repository

import com.springboot.boilerplate_webflux.models.entity.Book
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
interface BookRepository: ReactiveCrudRepository<Book, Long> {
    fun findByAuthorId(authorId: Long): Flux<Book>
}