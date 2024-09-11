/*
 * MIT License
 *
 * Copyright (c) 2024 BreninSul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.github.breninsul.webclient.logging

import io.github.breninsul.logging.HttpLoggingHelper
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.ExchangeFunction
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.charset.Charset
import java.util.function.Supplier
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.jvm.optionals.getOrNull

/**
 * Provides logging functionality for HTTP requests and responses.
 *
 * @property properties Configuration properties for HTTP logging.
 * @param uriMaskers List of URI masking strategies.
 * @param requestBodyMaskers List of request body masking strategies.
 * @param responseBodyMaskers List of response body masking strategies.
 */
open class WebClientLoggingInterceptor(
    protected open val properties: WebClientLoggerProperties,
    uriMaskers: List<WebClientUriMasking>,
    requestBodyMaskers: List<WebClientRequestBodyMasking>,
    responseBodyMaskers: List<WebClientResponseBodyMasking>,
) : ExchangeFilterFunction, Ordered {
    /**
     * Provides logging functionality for HTTP requests and responses.
     *
     * @property helper The HTTP logging helper used for logging requests and responses.
     */
    protected open val helper = HttpLoggingHelper("WebClient", properties,uriMaskers, requestBodyMaskers, responseBodyMaskers)

    /**
     * Constructs the log message for an HTTP client response.
     *
     * @param response The HTTP client response received from the server.
     * @param request The original HTTP client request sent to the server.
     * @param contentSupplier A supplier that provides the response body content.
     * @return A log message representing the HTTP response.
     */
    protected open fun constructRsBody(
        response: ClientResponse,
        request: ClientRequest,
        contentSupplier: Supplier<String?>,
    ): String {
        val type = HttpLoggingHelper.Type.RESPONSE
        val message =
            listOf(
                helper.getHeaderLine(type),
                helper.getIdString(response.logPrefix(), type),
                helper.getUriString(request.logResponseUri(), "${response.statusCode().value()} ${request.method().name()} ${request.url()}", type),
                helper.getTookString(request.logResponseTookTime(),  request.attribute(START_TIME_ATTRIBUTE).orElse(0L) as Long, type),
                helper.getHeadersString(request.logResponseHeaders(), response.headers().asHttpHeaders(), type),
                helper.getBodyString(request.logResponseBody(), contentSupplier, type),
                helper.getFooterLine(type),
            ).filter { !it.isNullOrBlank() }
                .joinToString("\n")
        return message
    }

    /**
     * Logs the response of a web client request.
     *
     * @param response The HTTP client response received from the server.
     * @param request The original HTTP client request sent to the server.
     * @param contentSupplier A supplier that provides the response body content.
     */
    protected open fun logResponse(
        response: ClientResponse,
        request: ClientRequest,
        contentSupplier: Supplier<String?>,
    ) {
        val logBody=constructRsBody(response,request,contentSupplier)
        logger.log(helper.loggingLevel, logBody)
    }



    /**
     * Retrieves the order value of the `OKLoggingInterceptor`.
     *
     * @return The order value of the `OKLoggingInterceptor`.
     */
    override fun getOrder(): Int = properties.order

    override fun filter(
        request: ClientRequest,
        next: ExchangeFunction,
    ): Mono<ClientResponse> {
        if (helper.loggingLevel == Level.OFF) {
            return next.exchange(request)
        }
        val startTime = System.currentTimeMillis()
        val requestWithStartTime = ClientRequest
            .from(request)
            .attribute(START_TIME_ATTRIBUTE, startTime).build()
        val loggedRequest = ClientRequest.from(requestWithStartTime)
            .body(WebClientLoggingRequestBodyInserter(requestWithStartTime,properties,helper)).build()

        val responseMono =
            next.exchange(loggedRequest)
                .map { response ->
                    val contentLength = response.headers().contentLength().orElse(0)
                    val bodyExist=contentLength>0
                    val haveToLogBody = loggedRequest.logResponseBody() ?: properties.response.bodyIncluded

                    if (contentLength > properties.response.maxBodySize) {
                        logResponse(response,loggedRequest) { helper.constructTooBigMsg(contentLength) }
                        return@map  response
                    } else if (!haveToLogBody || !bodyExist) {
                        helper.constructRqBody(loggedRequest) { "" }
                        return@map  response
                    }
                    //Have to log body (and it's not too big or empty)
                    return@map response
                        .mutate()
                        .body { bytesFlux ->
                            //Join body
                            DataBufferUtils.join(bytesFlux)
                                .publishOn(Schedulers.boundedElastic())
                                .map dataBufferLog@ {dataBuffer->
                                    val contentType: MediaType?= response.headers().contentType().getOrNull()
                                    val charset = contentType?.charset ?: Charset.defaultCharset()
                                    //log body
                                    logResponse(response,loggedRequest) { getDataBufferContent(dataBuffer)?.let { String(it, charset) } }
                                    return@dataBufferLog dataBuffer
                                }
                                .flux()
                        }.build()
                }
        return responseMono
    }

    protected open fun getDataBufferContent(dataBuffer: DataBuffer?) = dataBuffer.getContentBytes()

    companion object{
        val START_TIME_ATTRIBUTE = "START_TIME_ATTRIBUTE"
        val logger: Logger = Logger.getLogger(WebClientLoggingInterceptor::class.java.name)
    }
}
