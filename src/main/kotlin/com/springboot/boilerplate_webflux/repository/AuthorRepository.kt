package com.springboot.boilerplate_webflux.repository

import com.springboot.boilerplate_webflux.models.entity.Author
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthorRepository: ReactiveCrudRepository<Author,Long> {
}