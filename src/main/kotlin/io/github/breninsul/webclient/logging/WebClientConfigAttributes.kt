package io.github.breninsul.webclient.logging

import io.github.breninsul.logging.HttpConfigHeaders
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import kotlin.jvm.optionals.getOrNull


/**
 * Converts the HttpHeaders to a MultiMap where keys are strings and values
 * are lists of strings.
 *
 * @return The converted MultiMap.
 */
fun HttpHeaders.mapToMultiMap(): Map<String, List<String>> =
    this.toMap()


fun ClientRequest.Builder.logRequestUri(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_REQUEST_URI, value)}

fun ClientRequest.logRequestUri(): Boolean? = attribute(HttpConfigHeaders.LOG_REQUEST_URI).getOrNull() as Boolean?

fun ClientRequest.Builder.logRequestHeaders(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_REQUEST_HEADERS, value)}

fun ClientRequest.logRequestHeaders(): Boolean? = attribute(HttpConfigHeaders.LOG_REQUEST_HEADERS).getOrNull() as Boolean?

fun ClientRequest.Builder.logRequestBody(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_REQUEST_BODY, value)}

fun ClientRequest.logRequestBody(): Boolean? = attribute(HttpConfigHeaders.LOG_REQUEST_BODY).getOrNull() as Boolean?

fun ClientRequest.Builder.logRequestTookTime(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_REQUEST_TOOK_TIME, value)}

fun ClientRequest.logRequestTookTime(): Boolean? = attribute(HttpConfigHeaders.LOG_REQUEST_TOOK_TIME).getOrNull() as Boolean?

fun ClientRequest.Builder.logResponseUri(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_RESPONSE_URI, value)}

fun ClientRequest.logResponseUri(): Boolean? = attribute(HttpConfigHeaders.LOG_RESPONSE_URI).getOrNull() as Boolean?

fun ClientRequest.Builder.logResponseHeaders(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_RESPONSE_HEADERS, value)}

fun ClientRequest.logResponseHeaders(): Boolean? = attribute(HttpConfigHeaders.LOG_RESPONSE_HEADERS).getOrNull() as Boolean?

fun ClientRequest.Builder.logResponseBody(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_RESPONSE_BODY, value)}

fun ClientRequest.logResponseBody(): Boolean? = attribute(HttpConfigHeaders.LOG_RESPONSE_BODY).getOrNull() as Boolean?

fun ClientRequest.Builder.logResponseTookTime(value:Boolean):ClientRequest.Builder {return attribute(HttpConfigHeaders.LOG_RESPONSE_TOOK_TIME, value)}

fun ClientRequest.logResponseTookTime(): Boolean? = attribute(HttpConfigHeaders.LOG_RESPONSE_TOOK_TIME).getOrNull() as Boolean?

