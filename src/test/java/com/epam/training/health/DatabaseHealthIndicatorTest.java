package com.epam.training.health;

import com.epam.training.dto.response.TrainingTypeResponse;
import com.epam.training.service.TrainingTypeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DatabaseHealthIndicatorTest {

    @Mock
    private DataSource dataSource;

    @Mock
    private TrainingTypeService trainingTypeService;

    @InjectMocks
    private DatabaseHealthIndicator healthIndicator;

    @Test
    void health_returnsUp_whenDbConnectsAndTypesLoaded() throws Exception {
        Connection conn = mockConnectionSuccess();
        when(trainingTypeService.findAll()).thenReturn(List.of(
                new TrainingTypeResponse(1L, "FITNESS"),
                new TrainingTypeResponse(2L, "YOGA")
        ));

        Health health = healthIndicator.health();

        assertEquals(Status.UP, health.getStatus());
        assertEquals("OK", health.getDetails().get("db.connection"));
        assertEquals(2, health.getDetails().get("training_types.count"));

        verify(conn).close();
    }

    @Test
    void health_returnsDown_whenDbConnectionFails() throws Exception {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection refused"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertNotNull(health.getDetails().get("error"));
    }

    @Test
    void health_returnsDown_whenNoTrainingTypes() throws Exception {
        mockConnectionSuccess();
        when(trainingTypeService.findAll()).thenReturn(List.of());

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("OK", health.getDetails().get("db.connection"));
        assertEquals(0, health.getDetails().get("training_types.count"));
    }

    @Test
    void health_returnsDown_whenServiceThrows() throws Exception {
        mockConnectionSuccess();
        when(trainingTypeService.findAll()).thenThrow(new RuntimeException("Service unavailable"));

        Health health = healthIndicator.health();

        assertEquals(Status.DOWN, health.getStatus());
        assertEquals("Service unavailable", health.getDetails().get("error"));
    }

    private Connection mockConnectionSuccess() throws Exception {
        Connection conn = mock(Connection.class);
        PreparedStatement stmt = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);

        when(dataSource.getConnection()).thenReturn(conn);
        when(conn.prepareStatement(anyString())).thenReturn(stmt);
        when(stmt.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);

        return conn;
    }
}