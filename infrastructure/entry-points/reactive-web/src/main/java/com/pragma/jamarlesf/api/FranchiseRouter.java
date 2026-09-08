package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.FranchiseRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateFranchiseNameRequest;
import com.pragma.jamarlesf.api.dto.response.ErrorResponse;
import com.pragma.jamarlesf.api.dto.response.FranchiseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.pragma.jamarlesf.api.RouterConstants.APPLICATION_JSON;
import static com.pragma.jamarlesf.api.RouterConstants.FRANCHISES_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.FRANCHISE_NAME_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;
import static com.pragma.jamarlesf.api.RouterConstants.TAG_FRANCHISES;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@SuppressWarnings("java:S1075")
public class FranchiseRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = FRANCHISES_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.POST,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "createFranchise",
                    operation = @Operation(
                            operationId = "createFranchise",
                            summary = "Create a new franchise",
                            description = "Registers a new franchise in the system with reactive non-blocking persistence",
                            tags = {TAG_FRANCHISES},
                            requestBody = @RequestBody(
                                    description = "Franchise data to create",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = FranchiseRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Franchise created successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid franchise name",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = FRANCHISE_NAME_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.PATCH,
                    beanClass = FranchiseHandler.class,
                    beanMethod = "updateFranchiseName",
                    operation = @Operation(
                            operationId = "updateFranchiseName",
                            summary = "Update franchise name",
                            description = "Updates the name of an existing franchise",
                            tags = {TAG_FRANCHISES},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_FRANCHISE_ID, description = "Franchise identifier", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "New franchise name payload",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateFranchiseNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Franchise name updated successfully",
                                            content = @Content(schema = @Schema(implementation = FranchiseResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid franchise name",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> franchiseRouterFunction(FranchiseHandler handler) {
        return route(POST(FRANCHISES_PATH), handler::createFranchise)
                .andRoute(PATCH(FRANCHISE_NAME_PATH), handler::updateFranchiseName);
    }
}
