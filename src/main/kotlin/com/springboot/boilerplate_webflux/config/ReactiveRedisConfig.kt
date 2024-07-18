package com.springboot.boilerplate_webflux.config

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext


@Configuration
@ComponentScan
class ReactiveRedisConfig {
    @Value("\${spring.cache.host}")
    lateinit var redisHost: String

    @Value("\${spring.cache.port}")
    lateinit var redisPort: String

    @Bean
    fun redisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory(redisHost, redisPort.toInt())
    }

    @Bean("reactiveRedisTemplate")
    @Primary
    fun reactiveRedisTemplate(
     factory: ReactiveRedisConnectionFactory
    ): ReactiveRedisTemplate<String, String> {
        return ReactiveRedisTemplate(factory, RedisSerializationContext.string())
    }
}