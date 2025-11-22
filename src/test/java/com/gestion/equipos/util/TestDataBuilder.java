package com.gestion.equipos.util;

import com.gestion.equipos.entity.*;
import com.gestion.equipos.entity.enums.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Builder para crear datos de prueba de forma consistente y fácil.
 * Genera valores únicos automáticamente para evitar conflictos entre tests.
 */
public class TestDataBuilder {

    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private static long counter = System.currentTimeMillis();

    /**
     * Crea un usuario básico con datos únicos
     * @param nombre Primer nombre del usuario
     * @param apellido Primer apellido del usuario
     * @return Usuario sin persistir (debes guardarlo con repository.save())
     */
    public static Usuario crearUsuario(String nombre, String apellido) {
        counter++;
        Usuario usuario = new Usuario();
        usuario.setCodigo("USR" + counter);
        usuario.setUsuario("user" + counter);
        usuario.setPrimerNombre(nombre);
        usuario.setPrimerApellido(apellido);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        usuario.setFechaIngreso(LocalDate.now());
        usuario.setPassword(passwordEncoder.encode("test1234"));
        usuario.setIsAdmin(false);
        usuario.setFechaCommit(LocalDateTime.now());
        return usuario;
    }

    /**
     * Crea un usuario administrador
     */
    public static Usuario crearUsuarioAdmin(String nombre, String apellido) {
        Usuario usuario = crearUsuario(nombre, apellido);
        usuario.setIsAdmin(true);
        return usuario;
    }

    /**
     * Crea un área básica con nombre único
     * @param nombre Nombre base del área (se le añade contador para unicidad)
     * @param creador Usuario que crea el área
     * @return Área sin persistir
     */
    public static Area crearArea(String nombre, Usuario creador) {
        counter++;
        Area area = new Area();
        area.setNombre(nombre + " " + counter);
        area.setActivo(true);
        area.setCreadoPor(creador);
        area.setFechaCommit(LocalDateTime.now());
        return area;
    }

    /**
     * Crea un empleado asociado a un usuario y área
     * @param usuario Usuario al que se asocia el empleado
     * @param area Área del empleado
     * @param cargo Cargo del empleado
     * @return Empleado sin persistir
     */
    public static Empleado crearEmpleado(Usuario usuario, Area area, String cargo) {
        Empleado empleado = new Empleado();
        empleado.setIdUsuario(usuario);
        empleado.setIdArea(area);
        empleado.setCargo(cargo);
        empleado.setObservaciones("Empleado de prueba");
        return empleado;
    }

    /**
     * Crea una marca básica con nombre único
     * @param nombre Nombre base de la marca
     * @param creador Usuario que crea la marca
     * @return Marca sin persistir
     */
    public static Marca crearMarca(String nombre, Usuario creador) {
        counter++;
        Marca marca = new Marca();
        marca.setNombre(nombre + " " + counter);
        marca.setActivo(true);
        marca.setCreadoPor(creador);
        marca.setFechaCommit(LocalDateTime.now());
        return marca;
    }

    /**
     * Crea una ubicación básica con nombre único
     */
    public static Ubicacion crearUbicacion(String nombre, Usuario creador) {
        counter++;
        Ubicacion ubicacion = new Ubicacion();
        ubicacion.setNombre(nombre + " " + counter);
        ubicacion.setActivo(true);
        ubicacion.setCreadoPor(creador);
        ubicacion.setFechaCommit(LocalDateTime.now());
        return ubicacion;
    }

    /**
     * Crea una causa básica con nombre único
     */
    public static Causa crearCausa(String nombre, Usuario creador) {
        counter++;
        Causa causa = new Causa();
        causa.setNombre(nombre + " " + counter);
        causa.setDescripcion("Causa de prueba");
        causa.setPrioridadReporte(PrioridadReporte.MEDIA);
        causa.setActivo(true);
        causa.setCreadoPor(creador);
        causa.setFechaCommit(LocalDateTime.now());
        return causa;
    }

    /**
     * Crea un equipo básico
     * @param nombre Nombre base del equipo
     * @param marca Marca del equipo
     * @param estado Estado del equipo
     * @param creador Usuario que crea el equipo
     * @return Equipo sin persistir
     */
    public static Equipo crearEquipo(String nombre, Marca marca, EstadoEquipo estado, Usuario creador) {
        counter++;
        Equipo equipo = new Equipo();
        equipo.setNombre(nombre + " " + counter);
        equipo.setIdMarca(marca);
        equipo.setEstado(estado);
        equipo.setValor(new BigDecimal("1000.00"));
        equipo.setTipoAlimentacion(Alimentacion.V110);
        equipo.setTipoEquipo(TipoEquipo.ELECTRONICO); // ✅ Campo obligatorio
        equipo.setCreadoPor(creador); // ✅ Campo obligatorio
        equipo.setFechaCommit(LocalDateTime.now());
        return equipo;
    }

    /**
     * Resetea el contador (útil entre tests si necesitas IDs predecibles)
     */
    public static void resetCounter() {
        counter = System.currentTimeMillis();
    }
}