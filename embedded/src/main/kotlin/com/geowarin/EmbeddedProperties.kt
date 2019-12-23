package com.geowarin

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding
import org.springframework.http.CacheControl
import java.time.Duration

@ConstructorBinding
@ConfigurationProperties(prefix = "com.geowarin.embedded")
data class EmbeddedProperties(
  val frontendDirectory: String,
  val resourceCache: String?
) {
  val cacheControl: CacheControl get() = when (resourceCache) {
    null -> CacheControl.noCache()
    else -> CacheControl.maxAge(Duration.parse(resourceCache))
  }
}
