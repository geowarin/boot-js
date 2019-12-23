package com.geowarin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
class ReverseProxyApp

fun main(args: Array<String>) {
	runApplication<ReverseProxyApp>(*args)
}

@Configuration
class RouterConfig {
	@Bean
	fun routes() = router {
		path("/api").nest {
			GET("/message") { ServerResponse.ok().bodyValue("Hello") }
		}
	}
}
