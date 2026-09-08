package com.pragma.jamarlesf.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.pragma.jamarlesf.api.RouterConstants.FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PRODUCTS_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PRODUCT_STOCK_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class ProductRouter {

    @Bean
    public RouterFunction<ServerResponse> productRouterFunction(ProductHandler handler) {
        return route(POST(PRODUCTS_PATH), handler::addProduct)
                .andRoute(PATCH(PRODUCT_STOCK_PATH), handler::updateStock)
                .andRoute(GET(FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH), handler::getHighestStockProducts);
    }
}

