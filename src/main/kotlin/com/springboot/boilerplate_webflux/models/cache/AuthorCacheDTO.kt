package com.springboot.boilerplate_webflux.models.cache

import org.springframework.data.annotation.Id
import org.springframework.data.redis.core.RedisHash

@RedisHash("Author")
data class AuthorCacheDTO(
    val firstName: String,
    val lastName: String,
){
    @get:Id
    var id: String? = null
}
