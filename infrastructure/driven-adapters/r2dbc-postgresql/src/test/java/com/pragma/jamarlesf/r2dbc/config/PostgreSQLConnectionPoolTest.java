package com.pragma.jamarlesf.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.DB_DATABASE;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.DB_HOST;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.DB_PASSWORD;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.DB_PORT;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.DB_SCHEMA;
import static com.pragma.jamarlesf.r2dbc.constant.R2dbcTestConstants.DB_USERNAME;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PostgreSQLConnectionPoolTest {

    @InjectMocks
    private PostgreSQLConnectionPool connectionPool;

    @Mock
    private PostgresqlConnectionProperties properties;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        when(properties.host()).thenReturn(DB_HOST);
        when(properties.port()).thenReturn(DB_PORT);
        when(properties.database()).thenReturn(DB_DATABASE);
        when(properties.schema()).thenReturn(DB_SCHEMA);
        when(properties.username()).thenReturn(DB_USERNAME);
        when(properties.password()).thenReturn(DB_PASSWORD);
    }

    @Test
    void getConnectionConfigSuccess() {
        assertNotNull(connectionPool.getConnectionConfig(properties));
    }

    @Test
    void initializerSuccess() {
        ConnectionPool pool = mock(ConnectionPool.class);
        assertNotNull(connectionPool.initializer(pool));
    }
}
