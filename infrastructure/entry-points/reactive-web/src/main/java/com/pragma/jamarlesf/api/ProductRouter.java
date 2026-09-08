package com.pragma.jamarlesf.api;

import com.pragma.jamarlesf.api.dto.request.ProductRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductNameRequest;
import com.pragma.jamarlesf.api.dto.request.UpdateProductStockRequest;
import com.pragma.jamarlesf.api.dto.response.ErrorResponse;
import com.pragma.jamarlesf.api.dto.response.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
import static com.pragma.jamarlesf.api.RouterConstants.BRANCH_PRODUCT_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_BRANCH_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_FRANCHISE_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PATH_VAR_PRODUCT_ID;
import static com.pragma.jamarlesf.api.RouterConstants.PRODUCTS_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PRODUCT_NAME_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.PRODUCT_STOCK_PATH;
import static com.pragma.jamarlesf.api.RouterConstants.TAG_PRODUCTS;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.PATCH;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@SuppressWarnings("java:S1075")
public class ProductRouter {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = PRODUCTS_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.POST,
                    beanClass = ProductHandler.class,
                    beanMethod = "addProduct",
                    operation = @Operation(
                            operationId = "addProductToBranch",
                            summary = "Add a product to a branch",
                            description = "Creates and associates a new product with an existing branch",
                            tags = {TAG_PRODUCTS},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_BRANCH_ID, description = "Branch identifier", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "Product data to create",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = ProductRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Product created successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid product data or stock",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Branch not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = BRANCH_PRODUCT_PATH,
                    method = RequestMethod.DELETE,
                    beanClass = ProductHandler.class,
                    beanMethod = "deleteProduct",
                    operation = @Operation(
                            operationId = "deleteProductFromBranch",
                            summary = "Delete a product from a branch",
                            description = "Removes a product belonging to the specified branch",
                            tags = {TAG_PRODUCTS},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_BRANCH_ID, description = "Branch identifier", required = true),
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_PRODUCT_ID, description = "Product identifier", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Branch or Product not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PRODUCT_STOCK_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateStock",
                    operation = @Operation(
                            operationId = "modifyProductStock",
                            summary = "Modify product stock",
                            description = "Updates the stock count of an existing product",
                            tags = {TAG_PRODUCTS},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_PRODUCT_ID, description = "Product identifier", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "New stock payload",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateProductStockRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Stock updated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid stock (must be >= 0)",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = PRODUCT_NAME_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.PATCH,
                    beanClass = ProductHandler.class,
                    beanMethod = "updateProductName",
                    operation = @Operation(
                            operationId = "updateProductName",
                            summary = "Update product name",
                            description = "Updates the name of an existing product",
                            tags = {TAG_PRODUCTS},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_PRODUCT_ID, description = "Product identifier", required = true)
                            },
                            requestBody = @RequestBody(
                                    description = "New product name payload",
                                    required = true,
                                    content = @Content(schema = @Schema(implementation = UpdateProductNameRequest.class))
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Product name updated successfully",
                                            content = @Content(schema = @Schema(implementation = ProductResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Invalid product name",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                                    @ApiResponse(responseCode = "404", description = "Product not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            ),
            @RouterOperation(
                    path = FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH,
                    produces = {APPLICATION_JSON},
                    method = RequestMethod.GET,
                    beanClass = ProductHandler.class,
                    beanMethod = "getHighestStockProducts",
                    operation = @Operation(
                            operationId = "getHighestStockProductsByFranchise",
                            summary = "Get product with highest stock per branch for a franchise",
                            description = "Returns the product with the maximum stock for each branch belonging to the given franchise",
                            tags = {TAG_PRODUCTS},
                            parameters = {
                                    @Parameter(in = ParameterIn.PATH, name = PATH_VAR_FRANCHISE_ID, description = "Franchise identifier", required = true)
                            },
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "List of products with highest stock per branch",
                                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = ProductResponse.class)))),
                                    @ApiResponse(responseCode = "404", description = "Franchise not found",
                                            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> productRouterFunction(ProductHandler handler) {
        return route(POST(PRODUCTS_PATH), handler::addProduct)
                .andRoute(DELETE(BRANCH_PRODUCT_PATH), handler::deleteProduct)
                .andRoute(PATCH(PRODUCT_STOCK_PATH), handler::updateStock)
                .andRoute(PATCH(PRODUCT_NAME_PATH), handler::updateProductName)
                .andRoute(GET(FRANCHISE_HIGHEST_STOCK_PRODUCTS_PATH), handler::getHighestStockProducts);
    }
}
