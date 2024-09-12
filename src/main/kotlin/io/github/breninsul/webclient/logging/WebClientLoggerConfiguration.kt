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

import io.github.breninsul.logging.HttpMaskSettings
import io.github.breninsul.logging.HttpRegexFormUrlencodedBodyMasking
import io.github.breninsul.logging.HttpRegexJsonBodyMasking
import io.github.breninsul.logging.HttpRegexUriMasking
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.core.io.buffer.DataBufferFactory
import org.springframework.core.io.buffer.DefaultDataBufferFactory


@ConditionalOnProperty(value = ["web-client.logging-interceptor.enabled"], havingValue = "true", matchIfMissing = true)
@AutoConfiguration
@EnableConfigurationProperties(WebClientLoggerProperties::class)
open class WebClientLoggerConfiguration {

    @Bean
    open fun registerWebClientLoggingInterceptor(properties: WebClientLoggerProperties): WebClientLoggingInterceptor {
        val requestMaskers= listOf(
            webClientRequestRegexJsonBodyMasking(properties.request.mask),
            webClientRequestFormUrlencodedBodyMasking(properties.request.mask)
            )
        val responseMaskers= listOf(
            webClientResponseRegexJsonBodyMasking(properties.request.mask),
            webClientResponseFormUrlencodedBodyMasking(properties.request.mask)
        )
        val uriMaskers= listOf(webClientUriMasking(properties.request.mask))
        return WebClientLoggingInterceptor(properties, dataBufferFactory(),uriMaskers,requestMaskers,responseMaskers)
    }

    open fun dataBufferFactory() = DefaultDataBufferFactory()


    open fun webClientRequestRegexJsonBodyMasking(properties: HttpMaskSettings):WebClientRequestBodyMasking{
        return WebClientRequestBodyMaskingDelegate(HttpRegexJsonBodyMasking(properties.maskJsonBodyKeys))
    }


    open fun webClientResponseRegexJsonBodyMasking(properties: HttpMaskSettings):WebClientResponseBodyMasking{
        return WebClientResponseBodyMaskingDelegate(HttpRegexJsonBodyMasking(properties.maskJsonBodyKeys))
    }


    open fun webClientRequestFormUrlencodedBodyMasking(properties: HttpMaskSettings):WebClientRequestBodyMasking{
        return WebClientRequestBodyMaskingDelegate(HttpRegexFormUrlencodedBodyMasking(properties.maskJsonBodyKeys))
    }

    open fun webClientResponseFormUrlencodedBodyMasking(properties: HttpMaskSettings):WebClientResponseBodyMasking{
        return WebClientResponseBodyMaskingDelegate(HttpRegexFormUrlencodedBodyMasking(properties.maskJsonBodyKeys))
    }

    open fun webClientUriMasking(properties: HttpMaskSettings):WebClientUriMasking{
        return WebClientUriMaskingDelegate(HttpRegexUriMasking(properties.maskQueryParameters))
    }
}