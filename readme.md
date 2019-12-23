# Run your frontend alongside spring boot

This repository contains 4 projects featuring spring boot + kotlin and react + typescript
with an html5 router.

The topic we are discussing here is how to run the javascript bundler (parcel) alongside the spring application
for a good developer experience with hot module reload (HMR).

You will find intellij [compound run configurations](https://geowarin.com/share-intellij-run-configurations-with-git/) 
for each example, as most solutions use at least 2 processes.

You can read the full article [on my blog](https://geowarin.com/javascript-framework-with-spring-backend).

## Cors

[In this example](/cors) we run the frontend with its included web server on `localhost:1234`.
The spring backend runs on `localhost:8080`. 
Navigating on `localhost:1234`, you will see that the frontend is able to call web services
because the backend is configured to accept CORS request coming from this host.

```kotlin
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
``` 

## Embedded

[In this example](/embedded) we run the frontend in watch mode.
The backend runs on `locahost:8080` and serves the frontend resources 

```kotlin
val acceptsHtmlOnly: RequestPredicate = RequestPredicate { request ->
  request.headers().accept().contains(MediaType.TEXT_HTML) &&
      !request.headers().accept().contains(MediaType.ALL)
}

@Configuration
class RouterConfig {
  @Bean
  fun indexRoutes(props: EmbeddedProperties) = router {
    (GET("*") and acceptsHtmlOnly) {
      val indexHtml = DefaultResourceLoader().getResource(props.frontendDirectory)
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
```

So navigating on `locahost:8080` you will see that the react application is able to call web services because they
both run on the same host.

This configuration might have some occasional problems with HMR not fully reloaded so it's not my favourite.

However, by generating the result of the frontend build in `src/main/resources/static` we both have a very simple
way to distribute the full web application, as well as a dev environment that is very similar to the production environment.

## Proxy

[In this example](/proxy) we run the frontend with a custom `proxy.js` script which launches an express server on
`locahost:3000`.
This web server, as well as serving our js assets, also proxies `/api/` requests to `localhost:8080`

```javascript
const Bundler = require('parcel');
const express = require('express');
const proxy = require('http-proxy-middleware');
const history = require('connect-history-api-fallback');

const bundler = new Bundler('index.html');
const app = express();

app.use(history());
app.use(proxy('/api', {target: 'http://localhost:8080', changeOrigin: true}));

app.use(bundler.middleware());

app.listen(3000, 'localhost', (err) => {
    if (err) {
        console.log(err);
        return;
    }

    console.log('Listening at http://localhost:3000');
});
```

So going to `localhost:3000` we can see that the frontend is able to make web requests as if it is running on the
same host as the backend.

## Reverse-proxy

[In this fancy example](/reverse-proxy) we run an nginx application with docker.
It will proxy the `/api/` to `locahost:8080` and the rest to the static web application.

```
server {
    listen       8081;
    server_name  localhost;

    location /api {
        proxy_pass   http://host.docker.internal:8080;
    }

    location / {
        root /usr/share/nginx/html;
        set $fallback_file /index.html;
        if ($http_accept !~ text/html) {
            set $fallback_file /null;
        }
        try_files $uri $fallback_file;
    }
}
``` 

So navigating to the nginx server on `localhost:8081`, we can see that the backend and the frontend appear to be on the
same host.

## Conclusion

Depending on how you wish to deploy your application, you might choose one of the approaches above or even mix them
to achieve your goals.
