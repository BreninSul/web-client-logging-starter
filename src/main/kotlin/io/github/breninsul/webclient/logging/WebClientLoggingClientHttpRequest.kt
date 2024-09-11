/*
 * MIT License
 *
 * Copyright (c) 2023 BreninSul
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
 *
 */

package io.github.breninsul.webclient.logging

import io.github.breninsul.logging.HttpLoggingHelper
import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.web.reactive.function.client.ClientRequest
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.nio.charset.Charset
import java.util.function.Supplier


open class WebClientLoggingClientHttpRequest(
    protected open val request: ClientRequest,
    protected open val delegate: ClientHttpRequest,
    protected open val helper: HttpLoggingHelper,
    protected open val properties: WebClientLoggerProperties
) : ClientHttpRequest by delegate {

    /**
     * writeWith writes the request with a specified body and request
     *
     * @param[body] the body of the request to be logged
     * @return a Mono<Void> after the operation is complete
     */
    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        val contentLength = delegate.headers.contentLength
        val bodyExist = contentLength > 0
        val haveToLogBody = request.logRequestBody() ?: properties.request.bodyIncluded

        if (contentLength > properties.request.maxBodySize) {
            logRequest(request) { helper.constructTooBigMsg(contentLength) }
            return delegate.writeWith(body)
        } else if (!haveToLogBody || !bodyExist) {
            logRequest(request){ "" }
            return delegate.writeWith(body)
        } else {
            return delegate.writeWith(
                Mono.from(body)
                    .publishOn(Schedulers.boundedElastic())
                    .doOnNext { dataBuffer ->
                        val contentType: MediaType? = headers.contentType
                        val charset = contentType?.charset ?: Charset.defaultCharset()
                        logRequest(request) { getDataBufferContent(dataBuffer)?.let { String(it, charset) } }
                    },
            )
        }
    }
    protected open fun getDataBufferContent(dataBuffer: DataBuffer?) = dataBuffer.getContentBytes()

    protected open fun logRequest(request: ClientRequest, contentSupplier: Supplier<String?>) {
        val logBody=helper.constructRqBody(request,contentSupplier)
        WebClientLoggingInterceptor.logger.log(helper.loggingLevel, logBody)
    }
}