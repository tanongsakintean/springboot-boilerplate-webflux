package com.springboot.boilerplate_webflux.aop


import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.springboot.boilerplate_webflux.models.dto.RequestResponseDTO
import com.springboot.boilerplate_webflux.models.dto.errors.ErrorResponseDTO
import com.springboot.boilerplate_webflux.models.dto.requests.RequestDTO
import com.springboot.boilerplate_webflux.models.dto.response.ResponseDTO
import com.springboot.boilerplate_webflux.util.JSONUtils
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestClientResponseException
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest
import java.text.SimpleDateFormat
import java.util.*
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import reactor.core.publisher.Mono
import kotlin.collections.HashMap
import kotlin.jvm.Throws
import com.springboot.boilerplate_webflux.util.Log
import io.opentelemetry.api.trace.Span

@Aspect
@Component
class ControllerAspect {

    companion object : Log()

    private val logger: Logger = LoggerFactory.getLogger(this.javaClass)

    val gson = GsonBuilder().disableHtmlEscaping().create()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")

    private fun getHeaders(request: HttpServletRequest?): Map<String, String> {
        val requestHeaders = HashMap<String, String>()
        val headerNames = request!!.headerNames
        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement() ?: ""
            val headerValue  = (request as? HttpServletRequest)?.getHeader(headerName)
            if(headerName != "x-request-id") {
                requestHeaders[headerName] = headerValue.toString()
            }
        }
        requestHeaders["x-trace-id"] = Span.current().spanContext.traceId

        return requestHeaders
    }

    private fun getHeadersResponse(httpHeaders: HttpHeaders?): Map<String, String> {
        val requestHeaders = HashMap<String, String>()
        if (httpHeaders != null) {
            for(headerName in httpHeaders) {
                val headerValue  = httpHeaders[headerName.key]
                requestHeaders[headerName.key] = headerValue?.get(0).toString()
            }
        }

        return requestHeaders
    }

    private fun getQueryString(request: HttpServletRequest?) : Map<String, Any?> {
        val parameterNames = request!!.parameterNames
        val map = HashMap<String, Any?>()
        while (parameterNames.hasMoreElements()) {
            val parameterName = parameterNames.nextElement() ?: ""
            val parameterValue = (request as? HttpServletRequest)?.getParameter(parameterName)
            map[parameterName] = parameterValue
        }

        return map
    }

    @Around("execution(* com.springboot.boilerplate_webflux.controller.*.*(..))")

    @Throws(Throwable::class)
    fun logController(joinPoint: ProceedingJoinPoint): Any? {
        val funName = "${joinPoint.signature.declaringTypeName}.${joinPoint.signature.name}"
        val request = (RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes).request
        val headers = getHeaders(request)
        val requestResponseDTO = RequestResponseDTO()
        requestResponseDTO.clientIP = request.remoteAddr

        val requestDTO = RequestDTO()
        requestDTO.method = request.method
        requestDTO.uri = request.requestURI
        requestDTO.requestUri = request.requestURL.toString()
        requestDTO.requestTime = dateFormat.format(Date())
        requestDTO.queryString = getQueryString(request)
        requestDTO.headers = headers

        if("POST".equals(request.method, true) || "PUT".equals(request.method, true) || "PATCH".equals(request.method, true)) {
            try {
                val objList = ArrayList<Any?>()
                for(element in joinPoint.args) {
                    if(!(element is StandardMultipartHttpServletRequest)) {
                        objList.add(element)
                    }
                }
                requestDTO.body = gson.toJson(objList)
            } catch (e: Throwable) {
                for(element in joinPoint.args) {
                    requestDTO.body += element.toString()
                }
            }
        }
        requestResponseDTO.request = requestDTO

        var responseTemp: Any? = null
        val responseDTO = ResponseDTO()

        try {
            responseTemp = joinPoint.proceed()
            val responseMono: Mono<ResponseEntity<Any>> = Mono.fromSupplier { joinPoint.proceed() }
                .flatMap { result ->
                    if (result is Mono<*>) {
                        Mono.just(result as ResponseEntity<Any>)
                    }
                    else {
                        Mono.just(ResponseEntity.ok(result))
                    }
                }
            responseMono.subscribe(
                { responseEntity ->
                    // Access the body of the ResponseEntity
//                    val responseBody = responseEntity.body
                    responseDTO.status = responseEntity.statusCodeValue
                    responseDTO.responseTime = dateFormat.format(Date())
                    responseDTO.headers = getHeadersResponse(responseEntity.headers)
                    responseDTO.body = responseEntity.body
                    responseDTO.exception = ""
                    // Handle the responseBody as needed
//                    logger.info("responseBody: $responseBody")
                },
                { error ->
                    // Handle any errors that occurred during the method invocation
                    // ...
                }
            )


        } catch (e: Throwable) {
            if (e is HttpClientErrorException) {
                val httpException: HttpClientErrorException = e

                responseDTO.status = httpException.statusCode.value()
                responseDTO.responseTime = dateFormat.format(Date())
                responseDTO.headers = getHeadersResponse(httpException.responseHeaders)
                responseDTO.body = httpException.responseBodyAsString
                responseDTO.exception = ""

                val details: MutableList<Map<String, Any>> = ArrayList()
                val exceptionValue = if(e.responseBodyAsString.isNullOrBlank()){
                    emptyMap()
                } else {
                    try {
                        val mapException = gson.fromJson(httpException.responseBodyAsString, Map::class.java) as Map<String, Any>
                        if(mapException.containsKey("exception")) {
                            mapOf("exception" to mapException["exception"] as Map<String, Any>)
                        } else {
                            mapOf("exception" to mapException)
                        }
                    } catch (ex: Throwable) {
                        try{
                            val mapException = gson.fromJson(httpException.responseBodyAsString, List::class.java) as List<Map<String, Any>>
                            if(mapException.isNotEmpty()) {
                                mapOf("exception" to mapException[0])
                            } else {
                                mapOf("exception" to emptyMap())
                            }
                        } catch (ex2: Exception) {
                            mapOf("exception" to httpException.responseBodyAsString)
                        }
                    }
                }
                details.add(exceptionValue)

                val error = ErrorResponseDTO(httpException.rawStatusCode, httpException.statusText, details)
                responseDTO.body = error

                responseTemp = ResponseEntity(error, httpException.statusCode)
            } else if (e is HttpStatusCodeException) {
                val httpException: HttpStatusCodeException = e

                responseDTO.status = httpException.rawStatusCode
                responseDTO.responseTime = dateFormat.format(Date())
                responseDTO.headers = getHeadersResponse(httpException.responseHeaders)
                responseDTO.body = httpException.responseBodyAsString
                responseDTO.exception = ""
                val details: MutableList<Map<String, Any>> = ArrayList()
                val exceptionValue = if(e.responseBodyAsString.isNullOrBlank()){
                    emptyMap()
                } else {
                    try {
                        val mapException = gson.fromJson(httpException.responseBodyAsString, Map::class.java) as Map<String, Any>
                        if(mapException.containsKey("exception")) {
                            mapOf("exception" to mapException["exception"] as Map<String, Any>)
                        } else {
                            mapOf("exception" to mapException)
                        }
                    } catch (ex: Throwable) {
                        try {
                            val mapException = gson.fromJson(httpException.responseBodyAsString, List::class.java) as List<Map<String, Any>>
                            if(mapException.isNotEmpty()) {
                                mapOf("exception" to mapException[0])
                            } else {
                                mapOf("exception" to emptyMap())
                            }
                        } catch (ex2: Exception) {
                            mapOf("exception" to httpException.responseBodyAsString)
                        }
                    }
                }
                details.add(exceptionValue)

                val error = ErrorResponseDTO(httpException.rawStatusCode, httpException.statusText, details)
                responseDTO.body = error

                responseTemp = ResponseEntity(error, HttpStatus.valueOf(httpException.rawStatusCode))
            } else if (e is RestClientResponseException) {
                val httpException: RestClientResponseException = e

                responseDTO.status = httpException.rawStatusCode
                responseDTO.responseTime = dateFormat.format(Date())
                responseDTO.headers = getHeadersResponse(httpException.responseHeaders)
                responseDTO.body = httpException.responseBodyAsString
                responseDTO.exception = ""
                val details: MutableList<Map<String, Any>> = ArrayList()
                val exceptionValue = if(e.responseBodyAsString.isNullOrBlank()){
                    emptyMap()
                } else {
                    try {
                        val mapException = gson.fromJson(httpException.responseBodyAsString, Map::class.java) as Map<String, Any>
                        if(mapException.containsKey("exception")) {
                            mapOf("exception" to mapException["exception"] as Map<String, Any>)
                        } else {
                            mapOf("exception" to mapException)
                        }
                    } catch (ex: Throwable) {
                        try {
                            val mapException = gson.fromJson(httpException.responseBodyAsString, List::class.java) as List<Map<String, Any>>
                            if (mapException.isNotEmpty()) {
                                mapOf("exception" to mapException[0])
                            } else {
                                mapOf("exception" to emptyMap())
                            }
                        } catch (ex2: Exception) {
                            mapOf("exception" to httpException.responseBodyAsString)
                        }
                    }
                }
                details.add(exceptionValue)

                val error = ErrorResponseDTO(httpException.rawStatusCode, httpException.statusText, details)
                responseDTO.body = error

                responseTemp = ResponseEntity(error, HttpStatus.valueOf(httpException.rawStatusCode))
            } else {
                val details: MutableList<Map<String, Any>> = ArrayList()

                var message = ""
                e.message?.let {
                    message = if (it.indexOf("[") >= 0) {
                        it.substring(it.indexOf("[")).replace("\\", "")
                            .replace("[", "").replace("]", "")
                    } else {
                        it
                    }
                }
//                var jsonConvert = gson.fromJson(message, Any::class.java) as Boolean
                val jsonMessage = if (JSONUtils.isJSONValid(message)) {
//                val jsonMessage = if (jsonConvert) {
                    Gson().fromJson<Map<String, Any>>(message, Map::class.java)
                } else {
                    mutableMapOf("errorDesc" to message)
                }

                if (jsonMessage.containsKey("details")) {
                    try {
                        val detailMap = jsonMessage["details"] as Map<String, Any>
                        if (detailMap.containsKey("exception")) {
                            try {
                                val exceptionMap = detailMap["exception"] as Map<String, Any>
                                details.add(mapOf("exception" to exceptionMap))
                            } catch (ex: Exception) {
                                try {
                                    val exceptionMap = detailMap["exception"] as List<String>
                                    details.add(mapOf("exception" to exceptionMap))
                                } catch (ex2: Exception) {
                                    details.add(mapOf("exception" to (detailMap["exception"] ?: "")))
                                }
                            }
                        } else {
                            details.add(mapOf("exception" to detailMap))
                        }
                    } catch (ex: Exception) {
                        val detailList = jsonMessage["details"] as List<String>
                        if(detailList.isNotEmpty()) {
                            val detailMap = detailList[0] as Map<String, Any>
                            if (detailMap.containsKey("exception")) {
                                try {
                                    val exceptionMap = detailMap["exception"] as Map<String, Any>
                                    details.add(mapOf("exception" to exceptionMap))
                                } catch (ex: Exception) {
                                    try {
                                        val exceptionMap = detailMap["exception"] as List<String>
                                        details.add(mapOf("exception" to exceptionMap))
                                    } catch (ex2: Exception) {
                                        details.add(mapOf("exception" to (detailMap["exception"] ?: "")))
                                    }
                                }
                            } else {
                                details.add(mapOf("exception" to detailMap))
                            }
                        }
                    }
                } else {
                    details.add(mapOf("exception" to jsonMessage))
                }

                val error = ErrorResponseDTO(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Server Error", details)

                responseDTO.status = HttpStatus.INTERNAL_SERVER_ERROR.value()
                responseDTO.responseTime = dateFormat.format(Date())
                responseDTO.body = "{}"
                responseDTO.exception = gson.toJson(error)

                responseTemp = ResponseEntity(error, HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

        requestResponseDTO.response = responseDTO

        val requestMap = mapOf(
            "client_ip" to requestResponseDTO.clientIP,
            "request" to mapOf(
                "method" to requestResponseDTO.request?.method,
                "uri" to requestResponseDTO.request?.uri,
                "request_uri" to requestResponseDTO.request?.requestUri,
                "request_time" to requestResponseDTO.request?.requestTime,
                "querystring" to requestResponseDTO.request?.queryString,
                "headers" to requestResponseDTO.request?.headers,
                "body" to requestResponseDTO.request?.body,
                "exception" to (requestResponseDTO.request?.exception ?: "")
            ),
            "response" to mapOf(
                "status" to requestResponseDTO.response?.status,
                "response_time" to requestResponseDTO.response?.responseTime,
                "headers" to requestResponseDTO.response?.headers,
                "body" to requestResponseDTO.response?.body,
                "exception" to (requestResponseDTO.response?.exception ?: "")
            )
        )

        logger.info("$funName : ${gson.toJson(requestMap)}")

        return responseTemp
    }
}