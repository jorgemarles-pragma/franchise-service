package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.BranchRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateBranchNameRequest;
import com.pragma.jamarlesf.api.dto.response.BranchResponse;
import com.pragma.jamarlesf.api.dto.response.ErrorResponse;
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
import static com.pragma.jamarlesf.api.RouterConstants.BRANCHES_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.BRANCH_NAME_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_BRANCH_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;
import static com.pragma.jamarlesf.api.RouterConstants.TAG_BRANCHES;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@SuppressWarnings("java:S1075")
public class BranchRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = BRANCHES_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.POST,
                    beanClass = BranchHandler.class,
                    beanMethod = "addBranch",
                    operation = @Operation(
                            operationId = "addBranchToFranchise",
                            summary = "Add a branch to a franchise",
                            description = "Adds a new branch associated with the specified franchise",
                            tags = {TAG_BRANCHES},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_FRANCHISE_ID, description = "Franchise identifier", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "Branch creation data",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = BranchRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Branch created successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid branch data",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = BRANCH_NAME_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.PATCH,
                    beanClass = BranchHandler.class,
                    beanMethod = "updateBranchName",
                    operation = @Operation(
                            operationId = "updateBranchName",
                            summary = "Update branch name",
                            description = "Updates the name of an existing branch",
                            tags = {TAG_BRANCHES},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_BRANCH_ID, description = "Branch identifier", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "New branch name payload",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateBranchNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Branch name updated successfully",
                                            content = @Content(schema = @Schema(implementation = BranchResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid branch name",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> branchRouterFunction(BranchHandler handler) {
        return route(POST(BRANCHES_PATH), handler::addBranch)
                .andRoute(PATCH(BRANCH_NAME_PATH), handler::updateBranchName);
    }
}
