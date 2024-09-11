BreninSul WebClient interceptor with Spring Boot (WebFlux) starter.

Logging interceptor implementation and stater to auto register it in Spring context

Client is not registered in this starter. Interceptor should be manually added by

````kotlin
    WebClient
    .builder()
    .filter(filtersList)
    .build()
````


| Parameter                                                                     | Type             | Description                                                             |
|-------------------------------------------------------------------------------|------------------|-------------------------------------------------------------------------|
| `web-client.logging-interceptor.enabled`                                      | Boolean          | Enable autoconfig for this starter                                      |
| `web-client.logging-interceptor.logging-level`                                | JavaLoggingLevel | Logging level of messages                                               |
| `web-client.logging-interceptor.max-body-size`                                | Int              | Max logging body size                                                   |
| `web-client.logging-interceptor.order`                                        | Int              | Filter order (Ordered interface)                                        |
| `web-client.logging-interceptor.new-line-column-symbols`                      | Int              | How many symbols in first column (param name)                           |
| `web-client.logging-interceptor.request.id-included`                          | Boolean          | Is request id included to log message (request)                         |
| `web-client.logging-interceptor.request.uri-included`                         | Boolean          | Is uri included to log message (request)                                |
| `web-client.logging-interceptor.request.took-time-included`                   | Boolean          | Is timing included to log message (request)                             |
| `web-client.logging-interceptor.request.headers-included`                     | Boolean          | Is headers included to log message (request)                            |
| `web-client.logging-interceptor.request.body-included`                        | Boolean          | Is body included to log message (request)                               |
| `web-client.logging-interceptor.request.body-included`                        | Boolean          | Is body included to log message (request)                               |
| `web-client.logging-interceptor.request.mask.mask-headers`                    | String           | Comma separated headers to mask in logs (request)                       |
| `web-client.logging-interceptor.request.mask.mask-query-parameters`           | String           | Comma separated query parameters to mask in logs (request/response)     |
| `web-client.logging-interceptor.request.mask.mask-mask-json-body-keys`        | String           | Comma separated body json keys(fields) to mask in logs (request)        |
| `web-client.logging-interceptor.request.mask.mask-mask-form-urlencoded-body`  | String           | Comma separated form urlencoded keys(fields) to mask in logs (request)  |
| `web-client.logging-interceptor.response.id-included`                         | Boolean          | Is request id included to log message (response)                        |
| `web-client.logging-interceptor.response.uri-included`                        | Boolean          | Is uri included to log message (response)                               |
| `web-client.logging-interceptor.response.took-time-included`                  | Boolean          | Is timing included to log message (response)                            |
| `web-client.logging-interceptor.response.headers-included`                    | Boolean          | Is headers included to log message (response)                           |
| `web-client.logging-interceptor.response.body-included`                       | Boolean          | Is body included to log message (response)                              |
| `web-client.logging-interceptor.response.mask.mask-headers`                   | String           | Comma separated headers to mask in logs (response)                      |
| `web-client.logging-interceptor.response.mask.mask-mask-json-body-keys`       | String           | Comma separated body json keys(fields) to mask in logs (response)       |
| `web-client.logging-interceptor.response.mask.mask-mask-form-urlencoded-body` | String           | Comma separated form urlencoded keys(fields) to mask in logs (response) |


You can additionally configure logging for each request by passing attributes from `io.github.breninsul.webclient.logging.WebClientConfigAttributes` to request


add the following dependency:

````kotlin
dependencies {
//Other dependencies
    implementation("io.github.breninsul:web-client-logging-interceptor:${version}")
//Other dependencies
}

````
### Example of log messages

````
===========================WebClient Request begin===========================
=ID           : [22fba58c] 
=URI          : POST https://test-c.free.beeceptor.com
=Headers      : 
=Body         : {"someKey":"someval"}
===========================WebClient Request end  ===========================


===========================WebClient Response begin===========================
=ID           : [22fba58c] [be626517-1] 
=URI          : 200 POST https://test-c.free.beeceptor.com
=Took         : 994 ms
=Headers      : Access-Control-Allow-Origin:*;Alt-Svc:h3=":443"; ma=2592000;Content-Length:125;Content-Type:text/plain;Date:Wed, 11 Sep 2024 12:21:41 GMT;Vary:Accept-Encoding
=Body         : Hey ya! Great to see you here. Btw, nothing is configured for this request path. Create a rule and start building a mock API.
===========================WebClient Response end  ===========================
````


