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

import io.github.breninsul.logging.HttpLogSettings
import io.github.breninsul.logging.HttpLoggingProperties
import io.github.breninsul.logging.JavaLoggingLevel
import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties("web-client.logging-interceptor")
open class WebClientLoggerProperties(
     enabled: Boolean = true,
     loggingLevel: JavaLoggingLevel = JavaLoggingLevel.INFO,
     request: HttpLogSettings = HttpLogSettings(tookTimeIncluded = false),
     response: HttpLogSettings = HttpLogSettings(tookTimeIncluded = true),
     order: Int = 0,
     newLineColumnSymbols: Int = 14,
):HttpLoggingProperties(enabled, loggingLevel, request, response, order, newLineColumnSymbols)
