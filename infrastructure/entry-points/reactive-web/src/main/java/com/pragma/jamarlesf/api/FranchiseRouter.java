package com.pragma.jamarlesf.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.pragma.jamarlesf.api.RouterConstants.FRANCHISES_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.FRANCHISE_NAME_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class FranchiseRouter {

    @Bean
    public RouterFunction<ServerResponse> franchiseRouterFunction(FranchiseHandler handler) {
        return route(POST(FRANCHISES_PATH), handler::createFranchise)
                .andRoute(PATCH(FRANCHISE_NAME_PATH), handler::updateFranchiseName);
    }
}
