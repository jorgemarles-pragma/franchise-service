package com.pragma.jamarlesf.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.pragma.jamarlesf.api.RouterConstants.BRANCHES_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.BRANCH_NAME_PATH;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BranchRouter {

    @Bean
    public RouterFunction<ServerResponse> branchRouterFunction(BranchHandler handler) {
        return route(POST(BRANCHES_PATH), handler::addBranch)
                .andRoute(PATCH(BRANCH_NAME_PATH), handler::updateBranchName);
    }
}
