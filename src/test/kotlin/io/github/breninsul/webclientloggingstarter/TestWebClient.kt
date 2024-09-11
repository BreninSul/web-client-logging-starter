package io.github.breninsul.webclientloggingstarter

import io.github.breninsul.webclient.logging.WebClientLoggerConfiguration
import io.github.breninsul.webclient.logging.WebClientLoggerProperties
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient

open class TestWebClient {
    fun getWebClientLogging(): WebClient {
        return WebClient
            .builder()
            .filter(WebClientLoggerConfiguration().registerWebClientLoggingInterceptor(WebClientLoggerProperties()))
            .build()
    }

        @Test
        fun testWebClientPost() {
            // Create a new client
            val client=getWebClientLogging()

                client.post().uri("https://test-c.free.beeceptor.com?id=12323&id2=dsfsdf&token=someToken")
                    .bodyValue(mapOf("someKey" to "someval"))
                    .retrieve()
                    .toEntity(String::class.java)
                    .block()

    }
}