package com.gestion.equipos.unit.service;

import com.gestion.equipos.dto.EquipoDTO;
import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.HistorialAsignacion;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoDevolucion;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.MotivoCambio;
import com.gestion.equipos.repository.EquipoRepository;
import com.gestion.equipos.repository.HistorialAsignacionRepository;
import com.gestion.equipos.repository.UsuarioRepository;
import com.gestion.equipos.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTS - EquipoAsignacionService
 *
 * Pruebas unitarias con mocks (sin contexto Spring, sin BD)
 * Validan la lógica de negocio de asignación de equipos de forma aislada
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - EquipoAsignacionService")
class EquipoAsignacionServiceTest {

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private HistorialAsignacionRepository historialAsignacionRepository;

    @Mock
    private EquipoService equipoService;

    @Mock
    private AuthService authService;

    @Mock
    private ReporteService reporteService;

    @InjectMocks
    private EquipoAsignacionServiceImpl equipoAsignacionService;

    private Equipo equipoMock;
    private Usuario usuarioMock;
    private Usuario usuarioCreadorMock;

    @BeforeEach
    void setUp() {
        // Setup mocks comunes para todos los tests
        equipoMock = new Equipo();
        equipoMock.setId(1);
        equipoMock.setNombre("Laptop Test");
        equipoMock.setEstado(EstadoEquipo.ACTIVO);
        equipoMock.setIdUsuario(null); // Sin asignar

        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setUsuario("testuser");

        usuarioCreadorMock = new Usuario();
        usuarioCreadorMock.setId(99);
        usuarioCreadorMock.setUsuario("admin2");
    }

    /**
     * UNIT-01: Asignar equipo ACTIVO disponible → Éxito
     *
     * Verifica que un equipo ACTIVO sin asignar y sin reportes abiertos
     * se puede asignar correctamente a un usuario.
     */
    @Test
    @DisplayName("UNIT-01: Debería asignar equipo ACTIVO disponible exitosamente")
    void deberiaAsignarEquipoActivoDisponible() {
        // Given - Configurar mocks
        when(equipoRepository.findById(1)).thenReturn(Optional.of(equipoMock));
        when(usuarioRepository.findById(1)).thenReturn(Optional.of(usuarioMock));
        when(authService.getCurrentUser()).thenReturn(usuarioCreadorMock);
        when(reporteService.equipoTieneReportesAbiertos(1)).thenReturn(false);
        when(historialAsignacionRepository.findByIdEquipo_IdAndEstadoDevolucion(1, EstadoDevolucion.PENDIENTE))
                .thenReturn(Optional.empty());
        when(historialAsignacionRepository.findActiveAssignmentByEquipo(1))
                .thenReturn(Optional.empty());

        EquipoDTO equipoDTOMock = new EquipoDTO();
        equipoDTOMock.setId(1);
        when(equipoService.convertToDTO(any(Equipo.class))).thenReturn(equipoDTOMock);

        // When - Ejecutar asignación
        EquipoDTO resultado = equipoAsignacionService.asignarEquipoConMotivo(
                1, 1, MotivoCambio.ASIGNACION_INICIAL, "Asignación de prueba"
        );

        // Then - Verificar comportamiento
        assertNotNull(resultado, "El resultado no debería ser null");
        assertEquals(1, resultado.getId(), "El ID del equipo debería ser 1");

        // Verificar que se guardó el equipo con el usuario asignado
        verify(equipoRepository).save(argThat(equipo ->
            equipo.getIdUsuario() != null && equipo.getIdUsuario().getId() == 1
        ));

        // Verificar que se creó el historial de asignación
        verify(historialAsignacionRepository).save(any(HistorialAsignacion.class));

        // Verificar que se llamó al servicio de equipos para convertir a DTO
        verify(equipoService).convertToDTO(any(Equipo.class));
    }

    /**
     * UNIT-02: Asignar equipo con reporte ABIERTO → Excepción
     *
     * Verifica que no se puede asignar un equipo que tiene reportes abiertos.
     */
    @Test
    @DisplayName("UNIT-02: Debería rechazar asignación cuando equipo tiene reportes abiertos")
    void deberiaRechazarAsignacionConReporteAbierto() {
        // Given
        when(equipoRepository.findById(1)).thenReturn(Optional.of(equipoMock));
        when(historialAsignacionRepository.findByIdEquipo_IdAndEstadoDevolucion(1, EstadoDevolucion.PENDIENTE))
                .thenReturn(Optional.empty());
        when(reporteService.equipoTieneReportesAbiertos(1)).thenReturn(true); // ⬅️ Tiene reportes

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            equipoAsignacionService.asignarEquipoConMotivo(
                    1, 1, MotivoCambio.ASIGNACION_INICIAL, "Test"
            );
        });

        // Verificar mensaje de error
        assertTrue(exception.getMessage().contains("tiene reportes abiertos"),
                "El mensaje debería indicar que tiene reportes abiertos");

        // Verificar que NO se guardó nada
        verify(equipoRepository, never()).save(any());
        verify(historialAsignacionRepository, never()).save(any());
    }

    /**
     * UNIT-03: Asignar equipo ya asignado → Excepción
     *
     * Verifica que no se puede asignar un equipo que ya tiene usuario asignado.
     */
    @Test
    @DisplayName("UNIT-03: Debería rechazar asignación cuando equipo ya está asignado")
    void deberiaRechazarAsignacionCuandoYaEstaAsignado() {
        // Given - Equipo ya tiene usuario asignado
        Usuario usuarioAnterior = new Usuario();
        usuarioAnterior.setId(99);
        equipoMock.setIdUsuario(usuarioAnterior); // ⬅️ Ya está asignado

        when(equipoRepository.findById(1)).thenReturn(Optional.of(equipoMock));
        when(historialAsignacionRepository.findByIdEquipo_IdAndEstadoDevolucion(1, EstadoDevolucion.PENDIENTE))
                .thenReturn(Optional.empty());
        when(reporteService.equipoTieneReportesAbiertos(1)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            equipoAsignacionService.asignarEquipoConMotivo(
                    1, 1, MotivoCambio.ASIGNACION_INICIAL, "Test"
            );
        });

        assertTrue(exception.getMessage().contains("ya está asignado"),
                "El mensaje debería indicar que el equipo ya está asignado");

        verify(equipoRepository, never()).save(any());
    }

    /**
     * UNIT-04: Asignar equipo INACTIVO → Excepción
     *
     * Verifica que solo se pueden asignar equipos en estado ACTIVO.
     */
    @Test
    @DisplayName("UNIT-04: Debería rechazar asignación de equipo INACTIVO")
    void deberiaRechazarAsignacionEquipoInactivo() {
        // Given - Equipo INACTIVO
        equipoMock.setEstado(EstadoEquipo.INACTIVO); // ⬅️ No ACTIVO

        when(equipoRepository.findById(1)).thenReturn(Optional.of(equipoMock));
        when(historialAsignacionRepository.findByIdEquipo_IdAndEstadoDevolucion(1, EstadoDevolucion.PENDIENTE))
                .thenReturn(Optional.empty());
        when(reporteService.equipoTieneReportesAbiertos(1)).thenReturn(false);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            equipoAsignacionService.asignarEquipoConMotivo(
                    1, 1, MotivoCambio.ASIGNACION_INICIAL, "Test"
            );
        });

        assertTrue(exception.getMessage().contains("Solo se pueden asignar equipos en estado ACTIVO"),
                "El mensaje debería indicar que solo equipos ACTIVOS se pueden asignar");
    }

    /**
     * UNIT-05: Asignar equipo con devolución PENDIENTE → Excepción
     *
     * Verifica que no se puede asignar un equipo con devolución pendiente de confirmación.
     */
    @Test
    @DisplayName("UNIT-05: Debería rechazar asignación cuando hay devolución PENDIENTE")
    void deberiaRechazarAsignacionConDevolucionPendiente() {
        // Given - Existe devolución PENDIENTE
        HistorialAsignacion devolucionPendiente = new HistorialAsignacion();
        devolucionPendiente.setEstadoDevolucion(EstadoDevolucion.PENDIENTE);

        when(equipoRepository.findById(1)).thenReturn(Optional.of(equipoMock));
        when(historialAsignacionRepository.findByIdEquipo_IdAndEstadoDevolucion(1, EstadoDevolucion.PENDIENTE))
                .thenReturn(Optional.of(devolucionPendiente)); // ⬅️ Devolución pendiente

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            equipoAsignacionService.asignarEquipoConMotivo(
                    1, 1, MotivoCambio.ASIGNACION_INICIAL, "Test"
            );
        });

        assertTrue(exception.getMessage().contains("devolución pendiente"),
                "El mensaje debería indicar que hay devolución pendiente");
    }
}