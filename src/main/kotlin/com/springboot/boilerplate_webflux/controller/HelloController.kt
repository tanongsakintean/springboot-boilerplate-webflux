package com.springboot.boilerplate_webflux.controller

import com.springboot.boilerplate_webflux.models.cache.AuthorCacheDTO
import com.springboot.boilerplate_webflux.models.dto.requests.AuthorDTO
import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.models.entity.Author
import com.springboot.boilerplate_webflux.models.entity.Book
import com.springboot.boilerplate_webflux.service.HelloService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
class HelloController {

    @Autowired
    lateinit var helloService: HelloService

    @GetMapping("/")
    fun hello(): Mono<ApiResponse<String>>{
        return helloService.sayHello()
    }

    @PostMapping("/author")
    fun createAuthor(
        @RequestBody body: AuthorDTO
    ): Mono<ApiResponse<Author>> {
       return helloService.createAuthor(body)
    }

    @GetMapping("/books")
    fun  books():Mono<ApiResponse<List<Book>>> {
        return helloService.getBooks()
    }

    @GetMapping("/authors")
    fun getAuthor(): Mono<ApiResponse<List<Author>>> {
        return helloService.getAuthor()
    }

    @PostMapping("/save")
    fun save(@RequestParam key: String, @RequestParam value: String): Mono<Boolean> {
        return helloService.save(key, value)
    }

    @GetMapping("/find")
    fun find(@RequestParam key: String): Mono<String?> {
        return helloService.find(key)
    }

//    @GetMapping("/author/{authorId}")
//    fun getAuthorById(
//        @PathVariable authorId: Int
//    ): Mono<ApiResponse<AuthorCacheDTO>>  {
//        return helloService.getAuthorById(authorId)
//    }


}