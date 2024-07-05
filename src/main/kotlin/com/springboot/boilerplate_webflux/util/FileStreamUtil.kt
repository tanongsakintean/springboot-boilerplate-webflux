package com.springboot.boilerplate_webflux.util

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import java.io.InputStream
import java.io.OutputStream

fun convertInputStreamToStreamResponseBody(finalObject: InputStream) : StreamingResponseBody {
    return StreamingResponseBody {outputStream: OutputStream ->
        var numberOfBytesToWrite = 0
        val data = ByteArray(1024)
        while (finalObject.read(data, 0, data.size).also {
                numberOfBytesToWrite = it
                } != -1) {
            outputStream.write(data, 0, numberOfBytesToWrite)
        }
        finalObject.close()
    }
}