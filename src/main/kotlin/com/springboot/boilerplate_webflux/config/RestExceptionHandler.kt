package com.springboot.boilerplate_webflux.config


import com.google.gson.Gson
import com.springboot.boilerplate_webflux.models.dto.errors.ErrorResponseDTO
import com.springboot.boilerplate_webflux.models.dto.response.ApiResponse
import com.springboot.boilerplate_webflux.util.JSONUtils
import org.apache.coyote.BadRequestException
import org.springframework.data.rest.webmvc.ResourceNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import reactor.core.publisher.Mono


@ControllerAdvice
class RestExceptionHandler : ResponseEntityExceptionHandler() {

    @ExceptionHandler(Exception::class)
    fun handlerAllExceptions(ex: Exception, request: WebRequest) : Mono<ApiResponse<MutableList<Map<String, Any>>>> {
        val details: MutableList<Map<String, Any>> = ArrayList()

        var message = ""

        ex.message?.let {
            message = if(it.indexOf("[") >= 0) {
                it.substring(it.indexOf("]")).replace("\\", "")
                    .replace("[", "").replace("]", "")
            } else {
                it
            }
        }

        val jsonMessage = if(JSONUtils.isJSONValid(message)) {
            Gson().fromJson<Map<String, Any>>(message, Map::class.java)
        } else {
            mutableMapOf("errorDecks" to message)
        }

        if(jsonMessage.containsKey("details")) {
            val detailList = jsonMessage["details"] as Map<String, Any>
            if(detailList.containsKey("exception")) {
                val exceptionMap = detailList["exception"] as Map<String, Any>
                details.add(exceptionMap)
            } else {
                details.add(detailList)
            }
        } else {
            details.add(jsonMessage)
        }

        return Mono.just(
            ApiResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value().toString(),
                "SERVER ERROR",
                details
                )
        )
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException, exchange: ServerWebExchange?): Mono<ApiResponse<String>> {
        return Mono.just(
            ApiResponse(
                HttpStatus.BAD_REQUEST.value().toString(),
                "BAD REQUEST",
               ex.message
            )
        )
    }

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException,
        exchange: ServerWebExchange?
    ): Mono<ApiResponse<String>> {
        return Mono.just(
            ApiResponse(
                HttpStatus.NOT_FOUND.value().toString(),
                "NOT FOUND",
                ex.message
            )
        )
    }

}