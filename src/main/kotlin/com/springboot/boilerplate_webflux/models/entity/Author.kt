package com.springboot.boilerplate_webflux.models.entity

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor
import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash
import org.springframework.data.relational.core.mapping.Table
import java.io.Serializable

@Entity
@Table(name = "author")
@NoArgsConstructor
@AllArgsConstructor
@Data
data class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val firstName: String? = null,
    val lastName: String? = null,
): Serializable

