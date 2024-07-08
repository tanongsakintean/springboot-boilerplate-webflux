package com.springboot.boilerplate_webflux.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Id

@Entity
@Table(name = "book")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "name")
    val name: String? = null,

    @Column(name = "page_count")
    val pageCount: Int? = null,

    @Column(name="author_id")
    val authorId: Int? = null,
)
