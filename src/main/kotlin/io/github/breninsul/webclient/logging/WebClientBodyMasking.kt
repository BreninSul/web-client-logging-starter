package io.github.breninsul.webclient.logging

import io.github.breninsul.logging.HttpBodyMasking
import io.github.breninsul.logging.HttpRequestBodyMasking
import io.github.breninsul.logging.HttpResponseBodyMasking


interface WebClientRequestBodyMasking : HttpRequestBodyMasking

interface WebClientResponseBodyMasking : HttpResponseBodyMasking

open class WebClientRequestBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking,
) : WebClientRequestBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}
open class WebClientResponseBodyMaskingDelegate(
    protected open val delegate: HttpBodyMasking
) : WebClientResponseBodyMasking {
    override fun mask(message: String?): String = delegate.mask(message)
}
