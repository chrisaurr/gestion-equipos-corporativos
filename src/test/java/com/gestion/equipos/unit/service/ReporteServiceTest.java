package com.gestion.equipos.unit.service;

import com.gestion.equipos.dto.ReporteCreateDTO;
import com.gestion.equipos.dto.ReporteDTO;
import com.gestion.equipos.entity.Causa;
import com.gestion.equipos.entity.Equipo;
import com.gestion.equipos.entity.Reporte;
import com.gestion.equipos.entity.Usuario;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.EstadoReporte;
import com.gestion.equipos.entity.enums.PrioridadReporte;
import com.gestion.equipos.repository.CausaRepository;
import com.gestion.equipos.repository.EquipoRepository;
import com.gestion.equipos.repository.ReporteRepository;
import com.gestion.equipos.repository.UsuarioRepository;
import com.gestion.equipos.service.AuthService;
import com.gestion.equipos.service.ReporteServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * UNIT TESTS - ReporteService
 *
 * Pruebas unitarias con mocks para validar la lógica de negocio
 * del sistema de reportes (estados, validaciones, restricciones)
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Unit Tests - ReporteService")
class ReporteServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private EquipoRepository equipoRepository;

    @Mock
    private CausaRepository causaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private ReporteServiceImpl reporteService;

    private Equipo equipoMock;
    private Causa causaMock;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        equipoMock = new Equipo();
        equipoMock.setId(1);
        equipoMock.setNombre("Laptop HP");
        equipoMock.setEstado(EstadoEquipo.ACTIVO);

        causaMock = new Causa();
        causaMock.setId(1);
        causaMock.setNombre("Falla de hardware");
        causaMock.setPrioridadReporte(PrioridadReporte.ALTA);

        usuarioMock = new Usuario();
        usuarioMock.setId(1);
        usuarioMock.setUsuario("testuser");
    }

    /**
     * UNIT-06: Crear reporte válido → Éxito con estado ABIERTO por defecto
     *
     * Verifica que al crear un reporte:
     * - Se asigna automáticamente estado ABIERTO
     * - Se asocia correctamente con equipo y causa
     * - Se registra el usuario que lo crea
     */
    @Test
    @DisplayName("UNIT-06: Debería crear reporte con estado ABIERTO por defecto")
    void deberiaCrearReporteConEstadoAbierto() {
        // Given
        ReporteCreateDTO createDTO = new ReporteCreateDTO();
        createDTO.setObservacion("Problema con la pantalla del equipo");
        createDTO.setCausaId(1);
        createDTO.setEquipoId(1);

        when(equipoRepository.findById(1)).thenReturn(Optional.of(equipoMock));
        when(causaRepository.findById(1)).thenReturn(Optional.of(causaMock));
        when(authService.getCurrentUser()).thenReturn(usuarioMock);
        when(reporteRepository.save(any(Reporte.class))).thenAnswer(invocation -> {
            Reporte reporte = invocation.getArgument(0);
            reporte.setId(1);
            return reporte;
        });

        // When
        ReporteDTO resultado = reporteService.create(createDTO);

        // Then
        assertNotNull(resultado, "El resultado no debería ser null");

        // Verificar que se guardó el reporte con los valores correctos
        verify(reporteRepository).save(argThat(reporte ->
            reporte.getEstado() == EstadoReporte.ABIERTO &&
            reporte.getIdEquipo().getId() == 1 &&
            reporte.getIdCausa().getId() == 1 &&
            reporte.getObservacion().equals("Problema con la pantalla del equipo")
        ));
    }

    /**
     * UNIT-07: Verificar que equipo con reportes abiertos está bloqueado
     *
     * Verifica la lógica de negocio que detecta cuando un equipo
     * tiene reportes en estado ABIERTO o EN_PROCESO (no disponible para asignación)
     */
    @Test
    @DisplayName("UNIT-07: Equipo con reportes abiertos debería estar bloqueado")
    void equipoConReportesAbiertosBloqueado() {
        // Given - Existe un reporte activo
        Reporte reporteActivo = new Reporte();
        reporteActivo.setId(1);
        reporteActivo.setEstado(EstadoReporte.ABIERTO);
        reporteActivo.setIdEquipo(equipoMock);

        when(reporteRepository.findActiveReportByEquipo(
            eq(1),
            eq(EstadoReporte.ABIERTO),
            eq(EstadoReporte.EN_PROCESO)
        )).thenReturn(Optional.of(reporteActivo));

        // When
        boolean tieneReportesAbiertos = reporteService.equipoTieneReportesAbiertos(1);

        // Then
        assertTrue(tieneReportesAbiertos, "El equipo debería tener reportes abiertos");
        verify(reporteRepository).findActiveReportByEquipo(1, EstadoReporte.ABIERTO, EstadoReporte.EN_PROCESO);
    }
}