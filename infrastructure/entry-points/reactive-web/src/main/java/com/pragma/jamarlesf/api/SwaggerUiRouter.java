package com.pragma.jamarlesf.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import java.net.URI;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SwaggerUiRouter {

    private static final String SWAGGER_INITIALIZER_JS = """
            window.onload = function() {
              window.ui = SwaggerUIBundle({
                url: "/v3/api-docs",
                dom_id: '#swagger-ui',
                deepLinking: true,
                presets: [
                  SwaggerUIBundle.presets.apis,
                  SwaggerUIStandalonePreset
                ],
                plugins: [
                  SwaggerUIBundle.plugins.DownloadUrl
                ],
                layout: "StandaloneLayout",
                queryConfigEnabled: true
              });
            };
            """;

    private static final String SWAGGER_INDEX_HTML = """
            <!DOCTYPE html>
            <html lang="en">
              <head>
                <meta charset="UTF-8">
                <title>Franchise Management Reactive API (Reto Nequi)</title>
                <link rel="stylesheet" type="text/css" href="/webjars/swagger-ui/swagger-ui.css" />
                <link rel="stylesheet" type="text/css" href="/webjars/swagger-ui/index.css" />
                <link rel="icon" type="image/png" href="/webjars/swagger-ui/favicon-32x32.png" sizes="32x32" />
                <link rel="icon" type="image/png" href="/webjars/swagger-ui/favicon-16x16.png" sizes="16x16" />
              </head>
              <body>
                <div id="swagger-ui"></div>
                <script src="/webjars/swagger-ui/swagger-ui-bundle.js" charset="UTF-8"></script>
                <script src="/webjars/swagger-ui/swagger-ui-standalone-preset.js" charset="UTF-8"></script>
                <script src="/webjars/swagger-ui/swagger-initializer.js" charset="UTF-8"></script>
              </body>
            </html>
            """;

    @Bean
    public RouterFunction<ServerResponse> swaggerRoutes() {
        return route(GET("/webjars/swagger-ui/swagger-initializer.js"),
                request -> ServerResponse.ok()
                        .contentType(MediaType.parseMediaType("application/javascript;charset=UTF-8"))
                        .bodyValue(SWAGGER_INITIALIZER_JS))
                .andRoute(GET("/swagger-ui.html"),
                        request -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(SWAGGER_INDEX_HTML))
                .andRoute(GET("/swagger-ui/index.html"),
                        request -> ServerResponse.ok()
                                .contentType(MediaType.TEXT_HTML)
                                .bodyValue(SWAGGER_INDEX_HTML))
                .andRoute(GET("/swagger-ui"),
                        request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui/index.html")).build())
                .andRoute(GET("/docs"),
                        request -> ServerResponse.temporaryRedirect(URI.create("/swagger-ui/index.html")).build());
    }
}
