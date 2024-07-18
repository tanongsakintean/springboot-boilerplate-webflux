package com.springboot.boilerplate_webflux.service.impl

import com.springboot.boilerplate_webflux.models.cache.AuthorCacheDTO
import com.springboot.boilerplate_webflux.models.dto.requests.AuthorDTO
import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.models.entity.Author
import com.springboot.boilerplate_webflux.models.entity.Book
import com.springboot.boilerplate_webflux.repository.AuthorRepository
import com.springboot.boilerplate_webflux.repository.BookRepository
import com.springboot.boilerplate_webflux.service.HelloService
import com.springboot.boilerplate_webflux.util.Log
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono


@Service
class HelloServiceImpl() : HelloService {


    companion object : Log()

    @Autowired
    lateinit var bookRepository: BookRepository

    @Autowired
    lateinit var authorRepository: AuthorRepository

    @Autowired
    @Qualifier("reactiveRedisTemplate")
    lateinit var redisTemplate: ReactiveRedisTemplate<String,String>


    override fun sayHello(): Mono<ApiResponse<String>> {
        return Mono.just(
            ApiResponse(
                HttpStatus.OK.value().toString(),
                "SUCCESS",
                "Hello World!",
            )
        )
    }

    //    @Cacheable(value=["books"])
    override fun getBooks(): Mono<ApiResponse<List<Book>>> {
        return bookRepository.findAll().collectList().flatMap {
            Mono.just(
                ApiResponse(
                    "200", "SUCCESS", it
                )
            )
        }
    }

    override fun createAuthor(body: AuthorDTO): Mono<ApiResponse<Author>> {
        val entityToSave = Author(
            firstName = body.firstName, lastName = body.lastName
        )
        return authorRepository.save(entityToSave).flatMap {
            Mono.just(
                ApiResponse(
                    HttpStatus.OK.value().toString(), "SUCCESS", it
                )
            )
        }
    }

    override fun getAuthor(): Mono<ApiResponse<List<Author>>> {
        return authorRepository.findAll().collectList().flatMap {
            Mono.just(
                ApiResponse(
                    HttpStatus.OK.value().toString(), "SUCCESS", it
                )
            )
        }.switchIfEmpty(
            Mono.just(
                ApiResponse(
                    HttpStatus.OK.value().toString(), "SUCCESS", emptyList()
                )
            )
        )
    }

//    @Cacheable(value = ["author"], key = "#id")
//    override fun getAuthorById(id: Int): Mono<ApiResponse<AuthorCacheDTO>> {
//        logger.info("1")
//        return redisTemplate.opsForValue().get(id)
//            .flatMap {
//                Mono.just(
//                    ApiResponse(
//                        HttpStatus.OK.value().toString(), "SUCCESS", it
//                    )
//                )
//            }
//            .switchIfEmpty(
//                Mono.just(
//                    ApiResponse(
//                        HttpStatus.INTERNAL_SERVER_ERROR.value().toString(), "not have id in cache", null
//                    )
//                )
//            )
//            .onErrorResume { e->
//                Mono.just(
//                    ApiResponse(
//                        HttpStatus.INTERNAL_SERVER_ERROR.value().toString(), e.message.toString(), null
//                    )
//                )
//            }
//    }

   override fun save(key: String, value: String): Mono<Boolean> {
        return redisTemplate.opsForValue().set(key, value)
    }

    override fun find(key: String): Mono<String?> {
        return redisTemplate.opsForValue().get(key)
    }
}