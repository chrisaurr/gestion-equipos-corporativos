package com.gestion.equipos.integration;

import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.EstadoUsuario;
import com.gestion.equipos.entity.enums.TipoCambioEmpleado;
import com.gestion.equipos.repository.*;
import com.gestion.equipos.util.TestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * INTEGRATION TESTS - Historial Empleado con Triggers PostgreSQL
 *
 * Estos tests prueban que los TRIGGERS de la base de datos funcionan correctamente:
 * - fn_empleado_historial() → Registra cambios automáticamente
 * - fn_usuario_reingreso() → Registra reingresos
 *
 * @SpringBootTest → Levanta TODO Spring Boot
 * @ActiveProfiles("test") → Usa application-test.properties
 * @Transactional → Hace ROLLBACK automático después de cada test (limpia BD)
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - Historial Empleado (DB Triggers)")
class HistorialEmpleadoIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private HistorialEmpleadoRepository historialEmpleadoRepository;

    private Usuario usuarioCreador;
    private Area area1;
    private Area area2;

    @BeforeEach
    void setUp() {
        // Crear usuario creador para los tests
        usuarioCreador = TestDataBuilder.crearUsuario("Admin", "Sistema");
        usuarioCreador.setIsAdmin(true);
        usuarioCreador = usuarioRepository.save(usuarioCreador);

        // Crear áreas para pruebas de transferencia
        area1 = TestDataBuilder.crearArea("IT", usuarioCreador);
        area1 = areaRepository.save(area1);

        area2 = TestDataBuilder.crearArea("Contabilidad", usuarioCreador);
        area2 = areaRepository.save(area2);
    }

    /**
     * INT-01: Crear empleado NO genera historial (solo UPDATE lo genera)
     *
     * ¿Qué prueba? Que al crear un empleado nuevo NO se genera historial
     * automáticamente, porque el trigger es AFTER UPDATE, no AFTER INSERT.
     */
    @Test
    @DisplayName("INT-01: Crear empleado NO genera historial inicialmente")
    void crearEmpleadoNoGeneraHistorial() {
        // ARRANGE - Preparar usuario y empleado
        Usuario usuario = TestDataBuilder.crearUsuario("Juan", "Pérez");
        usuario = usuarioRepository.save(usuario);

        Empleado empleado = TestDataBuilder.crearEmpleado(usuario, area1, "Desarrollador");

        // ACT - Guardar empleado (NO debería activar trigger porque es INSERT)
        empleado = empleadoRepository.save(empleado);
        empleadoRepository.flush();

        // ASSERT - Verificar que NO se creó historial (trigger es AFTER UPDATE)
        List<HistorialEmpleado> historial = historialEmpleadoRepository
                .findByEmpleadoOrderByFechaCambioDesc(empleado);

        assertTrue(historial.isEmpty(),
                "No debería haber historial al crear un empleado (trigger es AFTER UPDATE)");
    }

    /**
     * INT-02: Trigger debe crear historial CAMBIO_CARGO al actualizar cargo
     *
     * ¿Qué prueba? Que cuando cambias el cargo de un empleado existente,
     * el trigger registra automáticamente el cambio.
     */
    @Test
    @DisplayName("INT-02: Trigger debe crear historial CAMBIO_CARGO al actualizar cargo")
    void triggerDebeCrearHistorialCambioCargo() {
        // ARRANGE - Crear empleado con cargo inicial
        Usuario usuario = TestDataBuilder.crearUsuario("María", "González");
        usuario = usuarioRepository.save(usuario);

        Empleado empleado = TestDataBuilder.crearEmpleado(usuario, area1, "Analista Junior");
        empleado = empleadoRepository.save(empleado);
        empleadoRepository.flush();

        String cargoAnterior = empleado.getCargo();

        // ACT - Cambiar cargo
        empleado.setCargo("Analista Senior");
        empleado = empleadoRepository.save(empleado);
        empleadoRepository.flush();

        // ASSERT - Verificar que el trigger registró el cambio
        List<HistorialEmpleado> historial = historialEmpleadoRepository
                .findByEmpleadoOrderByFechaCambioDesc(empleado);

        assertFalse(historial.isEmpty(),
                "Debería haber al menos 1 registro de CAMBIO_CARGO");

        HistorialEmpleado ultimoCambio = historial.get(0); // Primer elemento = más reciente
        assertEquals(TipoCambioEmpleado.CAMBIO_CARGO, ultimoCambio.getTipoCambio(),
                "El cambio debería ser CAMBIO_CARGO");
        assertEquals(cargoAnterior, ultimoCambio.getCargoAnterior(),
                "El cargo anterior debería ser Analista Junior");
        assertEquals("Analista Senior", ultimoCambio.getCargoNuevo(),
                "El cargo nuevo debería ser Analista Senior");
    }

    /**
     * INT-03: Trigger debe crear historial TRANSFERENCIA al cambiar área
     *
     * ¿Qué prueba? Que cuando transfieres un empleado a otra área,
     * el trigger registra la transferencia con ambas áreas.
     */
    @Test
    @DisplayName("INT-03: Trigger debe crear historial TRANSFERENCIA al cambiar área")
    void triggerDebeCrearHistorialTransferencia() {
        // ARRANGE - Empleado en área1
        Usuario usuario = TestDataBuilder.crearUsuario("Carlos", "Rodríguez");
        usuario = usuarioRepository.save(usuario);

        Empleado empleado = TestDataBuilder.crearEmpleado(usuario, area1, "Contador");
        empleado = empleadoRepository.save(empleado);
        empleadoRepository.flush();

        Integer areaAnteriorId = empleado.getIdArea().getId();

        // ACT - Transferir a área2
        empleado.setIdArea(area2);
        empleado = empleadoRepository.save(empleado);
        empleadoRepository.flush();

        // ASSERT - Verificar la transferencia en el historial
        List<HistorialEmpleado> historial = historialEmpleadoRepository
                .findByEmpleadoOrderByFechaCambioDesc(empleado);

        assertFalse(historial.isEmpty(),
                "Debería haber al menos 1 registro de TRANSFERENCIA");

        HistorialEmpleado ultimoCambio = historial.get(0);
        assertEquals(TipoCambioEmpleado.TRANSFERENCIA, ultimoCambio.getTipoCambio(),
                "El cambio debería ser TRANSFERENCIA");
        assertEquals(areaAnteriorId, ultimoCambio.getAreaAnterior().getId(),
                "El área anterior debería ser IT");
        assertEquals(area2.getId(), ultimoCambio.getAreaNueva().getId(),
                "El área nueva debería ser Contabilidad");
    }

    /**
     * INT-04: Trigger debe crear historial REINGRESO al reactivar usuario
     *
     * ¿Qué prueba? Que cuando un usuario INACTIVO vuelve a ACTIVO,
     * el trigger registra un REINGRESO en el historial.
     */
    @Test
    @DisplayName("INT-04: Trigger debe crear historial REINGRESO al reactivar usuario")
    void triggerDebeCrearHistorialReingreso() {
        // ARRANGE - Usuario con empleado
        Usuario usuario = TestDataBuilder.crearUsuario("Ana", "Martínez");
        usuario = usuarioRepository.save(usuario);

        Empleado empleado = TestDataBuilder.crearEmpleado(usuario, area1, "Asistente");
        empleado = empleadoRepository.save(empleado);
        empleadoRepository.flush();

        // Desactivar usuario
        usuario.setEstado(EstadoUsuario.INACTIVO);
        usuario = usuarioRepository.save(usuario);
        usuarioRepository.flush();

        // ACT - Reactivar usuario
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario = usuarioRepository.save(usuario);
        usuarioRepository.flush();

        // ASSERT - Verificar que existe el REINGRESO
        List<HistorialEmpleado> historial = historialEmpleadoRepository
                .findByEmpleadoOrderByFechaCambioDesc(empleado);

        boolean tieneReingreso = historial.stream()
                .anyMatch(h -> h.getTipoCambio() == TipoCambioEmpleado.REINGRESO);

        assertTrue(tieneReingreso,
                "Debería existir un registro de tipo REINGRESO en el historial");
    }
}