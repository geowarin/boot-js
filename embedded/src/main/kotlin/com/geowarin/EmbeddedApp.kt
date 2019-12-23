package com.geowarin

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.DefaultResourceLoader
import org.springframework.core.io.Resource
import org.springframework.http.CacheControl
import org.springframework.http.MediaType
import org.springframework.web.reactive.config.EnableWebFlux
import org.springframework.web.reactive.config.ResourceHandlerRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.function.server.RequestPredicate
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@SpringBootApplication
@EnableConfigurationProperties(EmbeddedProperties::class)
class EmbeddedApp

fun main(args: Array<String>) {
  runApplication<EmbeddedApp>(*args)
}

val acceptsHtmlOnly: RequestPredicate = RequestPredicate { request ->
  request.headers().accept().contains(MediaType.TEXT_HTML) &&
      !request.headers().accept().contains(MediaType.ALL)
}

@Configuration
class RouterConfig {
  @Bean
  fun routes(@Value("classpath:/static/index.html") indexHtml: Resource) = router {
    path("/api").nest {
      GET("/message") { ServerResponse.ok().bodyValue("Hello") }
    }
  }

  @Bean
  fun indexRoutes(props: EmbeddedProperties) = router {
    (GET("*") and acceptsHtmlOnly) {
      val frontendDirectory = DefaultResourceLoader().getResource(props.frontendDirectory)
      val indexHtml = frontendDirectory.createRelative("index.html")
      ServerResponse.ok().contentType(MediaType.TEXT_HTML).bodyValue(indexHtml)
    }
  }
}

@Configuration
@EnableWebFlux
class WebConfig(val props: EmbeddedProperties) : WebFluxConfigurer {
  override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
    registry.addResourceHandler("/**")
      .addResourceLocations(props.frontendDirectory)
      .setCacheControl(props.cacheControl)
  }
}
