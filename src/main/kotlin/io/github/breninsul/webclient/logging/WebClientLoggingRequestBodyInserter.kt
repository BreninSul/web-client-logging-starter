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
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.web.reactive.function.BodyInserter
import org.springframework.web.reactive.function.client.ClientRequest
import reactor.core.publisher.Mono
import java.util.function.Supplier

/**
 * The `WebClientLoggingRequestBodyInserter` class is a
 * specialized `BodyInserter` implementation that provides logging
 * functionality for HTTP client requests in a reactive web client.
 *
 * @property request The client request object to be logged.
 * @property helper The helper object used for constructing log messages.
 */
open class WebClientLoggingRequestBodyInserter(
    protected open val request: ClientRequest,
    protected open val properties: WebClientLoggerProperties,
    protected open val helper: HttpLoggingHelper,
) : BodyInserter<Any, ClientHttpRequest> {
    protected open val delegate: BodyInserter<*, in ClientHttpRequest> = request.body()

    /**
     * Inserts the client request body into the provided output message, with
     * additional logging functionality.
     *
     * @param outputMessage The HTTP client request message to which the body
     *    will be inserted.
     * @param context Context containing additional information for body
     *    insertion.
     * @return A Mono that completes when the body insertion process is
     *    finished.
     */
    override fun insert(
        outputMessage: ClientHttpRequest,
        context: BodyInserter.Context,
    ): Mono<Void> {
        val haveToLogBody = request.logRequestBody() ?: properties.request.bodyIncluded

        if  (!haveToLogBody) {
            logRequest(request){ "" }
            return delegate.insert(outputMessage, context)
        } else {
            return delegate.insert(
                WebClientLoggingClientHttpRequest(
                    request,
                    outputMessage,
                    helper,
                    properties
                ),
                context,
            )
        }
    }

    protected open fun logRequest(request: ClientRequest, contentSupplier: Supplier<String?>) {
        val logBody=helper.constructRqBody(request,contentSupplier)
        WebClientLoggingInterceptor.logger.log(helper.loggingLevel, logBody)
    }
}