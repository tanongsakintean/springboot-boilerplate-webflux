package com.springboot.boilerplate_webflux

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching

@SpringBootApplication
class BoilerplateWebfluxApplication

fun main(args: Array<String>) {
	runApplication<BoilerplateWebfluxApplication>(*args)
}
