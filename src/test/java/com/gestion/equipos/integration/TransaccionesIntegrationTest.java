package com.gestion.equipos.integration;

import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.EstadoEquipo;
import com.gestion.equipos.entity.enums.EstadoReporte;
import com.gestion.equipos.entity.enums.PrioridadReporte;
import com.gestion.equipos.repository.*;
import com.gestion.equipos.util.TestDataBuilder;
import jakarta.persistence.EntityManager;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * INTEGRATION TESTS - Transacciones y Validaciones de BD
 *
 * Estos tests prueban:
 * - Constraints de base de datos (NOT NULL, UNIQUE, FK)
 * - Transacciones @Transactional
 * - Validaciones de Bean Validation
 * - Rollback automático en caso de error
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@DisplayName("Integration Tests - Transacciones y Validaciones")
class TransaccionesIntegrationTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private MarcaRepository marcaRepository;

    @Autowired
    private EquipoRepository equipoRepository;

    @Autowired
    private CausaRepository causaRepository;

    @Autowired
    private ReporteRepository reporteRepository;

    @Autowired
    private EntityManager entityManager;

    private Usuario usuarioCreador;

    @BeforeEach
    void setUp() {
        usuarioCreador = TestDataBuilder.crearUsuario("Admin", "Test");
        usuarioCreador.setIsAdmin(true);
        usuarioCreador = usuarioRepository.save(usuarioCreador);
    }

    /**
     * INT-05: Bean Validation debe rechazar campo obligatorio vacío
     *
     * ¿Qué prueba? Que Bean Validation (@NotNull) rechaza guardar entidades
     * con campos obligatorios en NULL ANTES de llegar a la BD
     */
    @Test
    @DisplayName("INT-05: Bean Validation debe rechazar campo obligatorio NULL")
    void beanValidationDebeRechazarCampoObligatorioNull() {
        // ARRANGE - Crear área SIN nombre (campo obligatorio)
        Area areaSinNombre = new Area();
        areaSinNombre.setNombre(null); // ⚠️ NULL en campo @NotNull
        areaSinNombre.setActivo(true);
        areaSinNombre.setCreadoPor(usuarioCreador);

        // ACT & ASSERT - Debe lanzar ConstraintViolationException
        assertThrows(ConstraintViolationException.class, () -> {
            areaRepository.save(areaSinNombre);
            areaRepository.flush();
        }, "Bean Validation debería rechazar área sin nombre");
    }

    /**
     * INT-06: Constraint UNIQUE debe rechazar duplicados
     *
     * ¿Qué prueba? Que la base de datos no permite duplicar valores
     * en campos marcados como UNIQUE (ej: codigo de usuario)
     */
    @Test
    @DisplayName("INT-06: BD debe rechazar código de usuario duplicado")
    void bdDebeRechazarCodigoDuplicado() {
        // ARRANGE - Crear primer usuario con código único
        Usuario usuario1 = TestDataBuilder.crearUsuario("Pedro", "López");
        usuario1.setCodigo("USR001");
        usuario1 = usuarioRepository.save(usuario1);
        usuarioRepository.flush();

        // Crear segundo usuario con el MISMO código
        Usuario usuario2 = TestDataBuilder.crearUsuario("Luis", "García");
        usuario2.setCodigo("USR001"); // ⚠️ Código duplicado

        // ACT & ASSERT - Debe lanzar excepción por duplicado
        assertThrows(DataIntegrityViolationException.class, () -> {
            usuarioRepository.save(usuario2);
            usuarioRepository.flush();
        }, "La BD debería rechazar código de usuario duplicado");
    }

    /**
     * INT-07: @Transactional debe hacer rollback en caso de error
     *
     * ¿Qué prueba? Que cuando una transacción falla, TODOS los cambios
     * se revierten automáticamente (rollback)
     */
    @Test
    @DisplayName("INT-07: Transacción debe hacer rollback automático en caso de error")
    void transaccionDebeHacerRollbackEnError() {
        // ARRANGE - Preparar datos válidos
        Marca marca = TestDataBuilder.crearMarca("HP", usuarioCreador);
        marca = marcaRepository.save(marca);

        Equipo equipo = TestDataBuilder.crearEquipo("Laptop", marca, EstadoEquipo.ACTIVO, usuarioCreador);
        equipo = equipoRepository.save(equipo);

        Causa causa = TestDataBuilder.crearCausa("Falla", usuarioCreador);
        causa = causaRepository.save(causa);

        // Guardar ID del equipo para verificar después
        Integer equipoId = equipo.getId();

        // ACT - Intentar crear reporte inválido (debería fallar)
        try {
            Reporte reporteInvalido = new Reporte();
            reporteInvalido.setIdEquipo(equipo);
            reporteInvalido.setIdCausa(causa);
            reporteInvalido.setIdUsuario(null); // ⚠️ NULL en campo obligatorio
            reporteInvalido.setObservacion("Test");
            reporteInvalido.setEstado(EstadoReporte.ABIERTO);

            reporteRepository.save(reporteInvalido);
            reporteRepository.flush(); // Esto debería fallar
        } catch (Exception e) {
            // Esperamos esta excepción (puede ser ConstraintViolation o DataIntegrity)
            // Limpiar sesión de Hibernate para evitar errores en assertions
            entityManager.clear();
        }

        // ASSERT - Verificar que el equipo sigue existiendo (no se borró por el rollback)
        assertTrue(equipoRepository.findById(equipoId).isPresent(),
                "El equipo no debería haberse eliminado por el rollback");

        // Verificar que NO se creó el reporte inválido
        assertEquals(0, reporteRepository.count(),
                "No debería existir ningún reporte después del rollback");
    }
}