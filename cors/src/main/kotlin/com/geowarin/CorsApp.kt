package com.geowarin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsWebFilter
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
@EnableConfigurationProperties(CorsProperties::class)
class CorsApp

fun main(args: Array<String>) {
  runApplication<CorsApp>(*args)
}

@Configuration
class CorsConfig {

  @Bean
  @ConditionalOnProperty(name = ["com.geowarin.cors.allowedOrigin"])
  fun corsFilter(corsProperties: CorsProperties): CorsWebFilter {
    val source = UrlBasedCorsConfigurationSource().apply {
      registerCorsConfiguration("/api/**", CorsConfiguration().apply {
        addAllowedOrigin(corsProperties.allowedOrigin!!)
        addAllowedHeader("*")
        addAllowedMethod("*")
      })
    }
    return CorsWebFilter(source)
  }
}

@ConstructorBinding
@ConfigurationProperties(prefix = "com.geowarin.cors")
data class CorsProperties(
  val allowedOrigin: String?
)


@Configuration
class RouterConfig {
  @Bean
  fun routes() = router {
    path("/api").nest {
      GET("/message") { ServerResponse.ok().bodyValue("Hello") }
    }
  }
}
