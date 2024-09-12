package io.github.breninsul.webclient.logging

import io.github.breninsul.logging.HttpLoggingHelper
import io.github.breninsul.webclient.logging.WebClientLoggingInterceptor.Companion.START_TIME_ATTRIBUTE
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.web.reactive.function.client.ClientRequest
import java.nio.file.Files
import java.util.function.Supplier
import kotlin.io.path.fileSize
import kotlin.io.path.outputStream

/**
 * Constructs the log message for an HTTP client request.
 *
 * @param request the HTTP client request to be logged
 * @param contentSupplier a supplier that provides the content of the request body
 * @return a log message representing the HTTP request
 */
fun HttpLoggingHelper.constructRqBody(
    request: ClientRequest,
    contentSupplier: Supplier<String?>,
): String {
    val type = HttpLoggingHelper.Type.REQUEST
    val message =
        listOf(
            this.getHeaderLine(type),
            this.getIdString(request.logPrefix(), type),
            this.getUriString(request.logRequestUri(), "${request.method()} ${request.url()}", type),
            this.getTookString(request.logRequestTookTime(),  request.attribute(START_TIME_ATTRIBUTE).orElse(0L) as Long, type),
            this.getHeadersString(request.logRequestHeaders(), request.headers(), type),
            this.getBodyString(request.logRequestBody(), contentSupplier, type),
            this.getFooterLine(type),
        ).filter { !it.isNullOrBlank() }
            .joinToString("\n")
    return message
}

/**
 * Calculates the content length of a nullable `DataBuffer` by considering its readable byte count
 * and current read position.
 *
 * @return The calculated content length if the `DataBuffer` is not null; otherwise, 0.
 */
 fun DataBuffer?.countContentLength() =
    if (this == null) {
        0
    } else {
        this.readableByteCount() - this.readPosition()
    }


fun DataBuffer?.getContentBytes(dataBufferFactory: DataBufferFactory): Pair<ByteArray?,DataBuffer?> {
    val contentLength = this.countContentLength()
    if (this==null||contentLength == 0) {
        return Pair(null,this)
    }
    val body = this.asInputStream(true).use {it.readAllBytes()}
    return body to dataBufferFactory.wrap(body)
}

