package com.springboot.boilerplate_webflux.models.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.annotation.Id
import java.io.Serializable

@Entity
@Table(name = "book")
@AllArgsConstructor
@NoArgsConstructor
@Data
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String? = null,
    val pageCount: Int? = null,
    val authorId: Int? = null
) : Serializable
