package com.gestion.equipos.e2e;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E TESTS COMPLETOS - Selenium UI Tests
 *
 * 9 Tests E2E que prueban flujos completos end-to-end:
 * - E2E-01: Login → Crear Usuario Simple ✅
 * - E2E-02: Usuario + Empleado con Área ✅
 * - E2E-03: Equipos con Tipos Dinámicos (VEHICULO, ELECTRONICO) ✅
 * - E2E-04: Reporte ABIERTO bloquea asignación → Resolver → Permite asignación ✅
 * - E2E-05: Transferencia Empleado entre Áreas → Historial TRANSFERENCIA ✅
 * - E2E-06: Flujo Devolución PENDIENTE → CONFIRMADA completo ✅
 * - E2E-07: Asignar/Remover Responsable Área → Historial ASCENSO_JEFE/DESCENSO ✅
 * - E2E-08: Reactivación Catálogos con ignore case ✅
 * - E2E-09: Botón Atrás no regresa a login después de autenticarse ✅
 *
 * IMPORTANTE: Ejecutar con la aplicación levantada en perfil e2e
 * mvn spring-boot:run -Dspring-boot.run.profiles=e2e
 *
 * LIMPIEZA DE BD: Este test ejecuta automáticamente cleanup-and-seed.sql
 * antes de correr la suite, garantizando un estado limpio con solo datos seeders.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("e2e")
@Sql(scripts = "/cleanup-and-seed.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E Tests Completos - Selenium UI")
class UIE2ETestCompleto {

    private static WebDriver driver;
    private static WebDriverWait wait;
    private static final String BASE_URL = "http://localhost:8080";
    private static final String ADMIN_USERNAME = "admin2";
    private static final String ADMIN_PASSWORD = "admin";

    @BeforeAll
    static void setUpClass() {
        WebDriverManager.firefoxdriver().setup();
    }

    @BeforeEach
    void setUp() {
        FirefoxOptions options = new FirefoxOptions();
        //options.addArguments("--headless");
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");

        driver = new FirefoxDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void loginAsAdmin() {
        driver.get(BASE_URL + "/login");
        WebElement usernameInput = wait.until(ExpectedConditions.presenceOfElementLocated(By.id("usuario")));
        WebElement passwordInput = driver.findElement(By.id("password"));
        usernameInput.sendKeys(ADMIN_USERNAME);
        passwordInput.sendKeys(ADMIN_PASSWORD);
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/usuarios"));
    }

    /**
     * E2E-01: Login → Crear Usuario Simple → Verificar en tabla
     */
    @Test
    @Order(1)
    @DisplayName("E2E-01: Login → Crear Usuario Simple → Verificar en tabla")
    void deberiaCrearUsuarioSimpleYVerificarEnTabla() throws InterruptedException {
        loginAsAdmin();
        Thread.sleep(3000);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("body")));

        WebElement btnCrear = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a.btn[href*='/usuarios/crear']")));
        btnCrear.click();
        Thread.sleep(1000);

        String codigoUnico = "E2E" + System.currentTimeMillis();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("codigo"))).sendKeys(codigoUnico);
        driver.findElement(By.id("usuario")).sendKeys("test" + System.currentTimeMillis());
        driver.findElement(By.id("password")).sendKeys("test1234");
        driver.findElement(By.id("confirmPassword")).sendKeys("test1234");
        driver.findElement(By.id("primerNombre")).sendKeys("Usuario");
        driver.findElement(By.id("primerApellido")).sendKeys("Prueba");
        driver.findElement(By.id("fechaIngreso")).sendKeys("2024-01-01");

        driver.findElement(By.cssSelector("button[type='submit'].btn-primary")).click();
        Thread.sleep(3000);

        assertTrue(driver.getCurrentUrl().contains("/usuarios") && !driver.getCurrentUrl().contains("/crear"),
                "Debería redirigir a lista de usuarios después de crear");

        WebElement userRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + codigoUnico + "')]")));
        WebElement editButton = userRow.findElement(By.cssSelector("a[href*='/editar']"));
        assertNotNull(editButton, "Debería existir botón de editar para el usuario creado");
    }

    /**
     * E2E-02: Usuario + Empleado con Área
     */
    @Test
    @Order(2)
    @DisplayName("E2E-02: Usuario + Empleado con dependencias (Área)")
    void deberiaCrearUsuarioConEmpleadoAsignadoAArea() throws InterruptedException {
        loginAsAdmin();

        // Crear área
        driver.get(BASE_URL + "/areas");
        Thread.sleep(3000);

        WebElement btnNuevaArea = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn[data-target='#createModal']")));
        btnNuevaArea.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(1000);

        String nombreAreaUnico = "Área E2E " + System.currentTimeMillis();
        WebElement campoNombre = wait.until(ExpectedConditions.elementToBeClickable(By.id("createNombre")));
        campoNombre.clear();
        campoNombre.sendKeys(nombreAreaUnico);
        Thread.sleep(500);

        WebElement btnGuardar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#createModal button[type='submit'].btn-success")));
        btnGuardar.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(2000);

        // Crear usuario con empleado
        driver.get(BASE_URL + "/usuarios");
        Thread.sleep(3000);

        WebElement btnNuevoUsuario = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("a.btn[href*='/usuarios/crear']")));
        btnNuevoUsuario.click();
        Thread.sleep(1000);

        String codigoUnico = "EMP" + System.currentTimeMillis();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("codigo"))).sendKeys(codigoUnico);
        driver.findElement(By.id("usuario")).sendKeys("empleado" + System.currentTimeMillis());
        driver.findElement(By.id("password")).sendKeys("test1234");
        driver.findElement(By.id("confirmPassword")).sendKeys("test1234");
        driver.findElement(By.id("primerNombre")).sendKeys("Empleado");
        driver.findElement(By.id("primerApellido")).sendKeys("Prueba");
        driver.findElement(By.id("fechaIngreso")).sendKeys("2024-01-01");

        WebElement checkboxEmpleado = driver.findElement(By.id("esEmpleado"));
        if (!checkboxEmpleado.isSelected()) {
            checkboxEmpleado.click();
        }
        Thread.sleep(1000);

        wait.until(ExpectedConditions.presenceOfElementLocated(By.id("cargo"))).sendKeys("Analista E2E");

        Select selectArea = new Select(driver.findElement(By.id("areaId")));
        for (int i = 0; i < selectArea.getOptions().size(); i++) {
            if (selectArea.getOptions().get(i).getText().contains(nombreAreaUnico)) {
                selectArea.selectByIndex(i);
                break;
            }
        }

        driver.findElement(By.cssSelector("button[type='submit'].btn-primary")).click();
        Thread.sleep(3000);

        assertTrue(driver.getCurrentUrl().contains("/usuarios") && !driver.getCurrentUrl().contains("/crear"),
                "Debería redirigir a lista de usuarios después de crear");

        WebElement employeeRow = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + codigoUnico + "')]")));
        WebElement editButton = employeeRow.findElement(By.cssSelector("a[href*='/editar']"));
        assertNotNull(editButton, "Debería existir botón de editar para el empleado creado");
    }

    /**
     * E2E-03: Crear Equipos con Tipos Dinámicos (prueba VEHICULO y ELECTRONICO)
     * Verifica que campos específicos aparecen/desaparecen según tipo seleccionado
     */
    @Test
    @Order(3)
    @DisplayName("E2E-03: Crear Equipos con Tipos Dinámicos (VEHICULO y ELECTRONICO)")
    void deberiaCrearEquiposConCamposDinamicosSegunTipo() throws InterruptedException {
        loginAsAdmin();

        // PARTE 1: Crear equipo tipo ELECTRONICO
        driver.get(BASE_URL + "/equipos/crear");
        Thread.sleep(2000);

        String nombreElectronico = "Laptop E2E " + System.currentTimeMillis();
        driver.findElement(By.id("nombre")).sendKeys(nombreElectronico);

        Select selectMarca = new Select(wait.until(ExpectedConditions.presenceOfElementLocated(By.id("marcaId"))));
        if (selectMarca.getOptions().size() > 1) {
            selectMarca.selectByIndex(1);
        }

        driver.findElement(By.id("valor")).sendKeys("1500.00");

        // Seleccionar tipo ELECTRONICO
        WebElement radioElectronico = wait.until(ExpectedConditions.elementToBeClickable(By.id("tipoElectronico")));
        radioElectronico.click();
        Thread.sleep(1000);

        // Verificar que campos de ELECTRONICO están visibles
        WebElement divElectronico = driver.findElement(By.id("datosElectronico"));
        assertTrue(divElectronico.isDisplayed(), "Campos de electrónico deberían estar visibles");

        // Llenar campos específicos de electrónico
        WebElement imeiField = driver.findElement(By.id("electronico-imei"));
        assertTrue(imeiField.isDisplayed(), "Campo IMEI debería estar visible");
        imeiField.sendKeys("123456789012345");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(3000);

        assertTrue(driver.getCurrentUrl().contains("/equipos") && !driver.getCurrentUrl().contains("/crear"),
                "Debería redirigir a lista de equipos");

        // Verificar que el equipo electrónico aparece en la tabla
        Thread.sleep(2000);
        WebElement equipoRow1 = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreElectronico + "')]")));
        assertTrue(equipoRow1.getText().contains("Electrónico"), "Equipo debería ser tipo Electrónico");

        // PARTE 2: Crear equipo tipo VEHICULO
        driver.get(BASE_URL + "/equipos/crear");
        Thread.sleep(2000);

        String nombreVehiculo = "Camioneta E2E " + System.currentTimeMillis();
        driver.findElement(By.id("nombre")).sendKeys(nombreVehiculo);

        Select selectMarca2 = new Select(driver.findElement(By.id("marcaId")));
        if (selectMarca2.getOptions().size() > 1) {
            selectMarca2.selectByIndex(1);
        }

        driver.findElement(By.id("valor")).sendKeys("25000.00");

        // Seleccionar tipo VEHICULO
        WebElement radioVehiculo = wait.until(ExpectedConditions.elementToBeClickable(By.id("tipoVehiculo")));
        radioVehiculo.click();
        Thread.sleep(1000);

        // Verificar que campos de VEHICULO están visibles y ELECTRONICO ocultos
        WebElement divVehiculo = driver.findElement(By.id("datosVehiculo"));
        assertTrue(divVehiculo.isDisplayed(), "Campos de vehículo deberían estar visibles");

        WebElement divElectronicoOculto = driver.findElement(By.id("datosElectronico"));
        assertFalse(divElectronicoOculto.isDisplayed(), "Campos de electrónico deberían estar ocultos");

        // Llenar campos específicos de vehículo (todos required)
        driver.findElement(By.id("vehiculo-noMotor")).sendKeys("MOTOR12345");
        driver.findElement(By.id("vehiculo-vin")).sendKeys("VIN67890ABCDEF");
        driver.findElement(By.id("vehiculo-cilindrada")).sendKeys("2000");
        driver.findElement(By.id("vehiculo-placa")).sendKeys("ABC-1234");
        driver.findElement(By.id("vehiculo-modelo")).sendKeys("2024");

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(3000);

        assertTrue(driver.getCurrentUrl().contains("/equipos") && !driver.getCurrentUrl().contains("/crear"),
                "Debería redirigir a lista de equipos");

        // Verificar que el equipo vehículo aparece en la tabla
        Thread.sleep(2000);
        WebElement equipoRow2 = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreVehiculo + "')]")));
        assertTrue(equipoRow2.getText().contains("Vehículo"), "Equipo debería ser tipo Vehículo");
    }

    /**
     * E2E-04: Reporte ABIERTO bloquea asignación → Resolver → Permite asignación
     * Flujo completo que prueba lógica de negocio crítica
     */
    @Test
    @Order(4)
    @DisplayName("E2E-04: Reporte ABIERTO bloquea asignación → Resolver → Permite asignación")
    void reporteAbiertoDeberiaBloquearAsignacionHastaResolver() throws InterruptedException {
        loginAsAdmin();

        // Paso 1: Obtener un equipo libre (primer equipo en la lista)
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        // Buscar primer equipo sin asignación
        WebElement primeraFila = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#equiposTableBody tr")));
        String nombreEquipo = primeraFila.findElements(By.tagName("td")).get(1).getText();

        // Paso 2: Crear reporte ABIERTO sobre ese equipo
        driver.get(BASE_URL + "/reportes/crear");
        Thread.sleep(2000);

        driver.findElement(By.id("observacion")).sendKeys("Problema crítico E2E - Bloquea asignación");

        // Seleccionar primera causa
        Select selectCausa = new Select(driver.findElement(By.id("causaId")));
        if (selectCausa.getOptions().size() > 1) {
            selectCausa.selectByIndex(1);
        }

        // Seleccionar el equipo que identificamos
        Select selectEquipo = new Select(driver.findElement(By.id("equipoId")));
        boolean equipoEncontrado = false;
        for (int i = 0; i < selectEquipo.getOptions().size(); i++) {
            if (selectEquipo.getOptions().get(i).getText().contains(nombreEquipo)) {
                selectEquipo.selectByIndex(i);
                equipoEncontrado = true;
                break;
            }
        }

        if (!equipoEncontrado && selectEquipo.getOptions().size() > 1) {
            // Si no encontramos el equipo específico, usar el primero disponible
            selectEquipo.selectByIndex(1);
        }

        driver.findElement(By.cssSelector("button[type='submit'].btn-primary")).click();
        Thread.sleep(3000);

        assertTrue(driver.getCurrentUrl().contains("/reportes"),
                "Debería redirigir a lista de reportes");

        // Paso 3: Intentar asignar el equipo → Debería estar bloqueado o mostrar badge "Con Reportes"
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        // Buscar el equipo en la tabla y verificar que tiene indicador de reportes
        WebElement equipoConReporte = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEquipo + "')]")));

        // El sistema debería mostrar que tiene reportes activos (badge o mensaje)
        assertTrue(equipoConReporte.getText().contains("Con Reportes") ||
                   !equipoConReporte.findElements(By.cssSelector(".badge-info")).isEmpty(),
                "Equipo debería mostrar indicador de reportes activos");

        // Paso 4: Resolver el reporte siguiendo el flujo correcto de estados
        // FLUJO: ABIERTO → EN_PROCESO → RESUELTO (usando botones en la vista)
        driver.get(BASE_URL + "/reportes");
        Thread.sleep(3000);

        // Sub-paso 4.1: Abrir la vista del reporte
        WebElement reporteFila = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., 'Problema crítico E2E')]")));
        WebElement btnVer = reporteFila.findElement(By.cssSelector("a.btn-info[href*='/reportes/']"));
        btnVer.click();
        Thread.sleep(2000);

        // Sub-paso 4.2: Click en "Tomar en Proceso" (ABIERTO → EN_PROCESO)
        WebElement btnTomarEnProceso = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(text(), 'Tomar en Proceso')]")));
        btnTomarEnProceso.click();

        // Aceptar confirmación
        Alert alert1 = wait.until(ExpectedConditions.alertIsPresent());
        alert1.accept();
        Thread.sleep(2000);

        // Sub-paso 4.3: Click en "Marcar Resuelto" (EN_PROCESO → RESUELTO)
        // El botón debería aparecer ahora que está EN_PROCESO
        WebElement btnMarcarResuelto = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//button[contains(text(), 'Marcar Resuelto')]")));
        btnMarcarResuelto.click();

        // Aceptar confirmación
        Alert alert2 = wait.until(ExpectedConditions.alertIsPresent());
        alert2.accept();
        Thread.sleep(3000);

        // Paso 5: Verificar que ahora el equipo SÍ se puede asignar (no tiene reportes activos)
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        WebElement equipoLiberado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEquipo + "')]")));

        // El indicador de reportes debería haber desaparecido
        assertFalse(equipoLiberado.getText().contains("Con Reportes"),
                "Equipo NO debería mostrar indicador de reportes después de resolver");
    }

    /**
     * E2E-05: Transferencia Empleado entre Áreas → Verificar Historial TRANSFERENCIA
     * Prueba triggers de base de datos y sistema de historial
     */
    @Test
    @Order(5)
    @DisplayName("E2E-05: Transferencia Empleado → Verificar Historial TRANSFERENCIA")
    void transferenciaEmpleadoDeberiaRegistrarHistorial() throws InterruptedException {
        loginAsAdmin();

        // Paso 1: Crear segunda área para transferencia
        driver.get(BASE_URL + "/areas");
        Thread.sleep(3000);

        WebElement btnNuevaArea = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button.btn[data-target='#createModal']")));
        btnNuevaArea.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(1000);

        String nombreAreaDestino = "Área Destino E2E " + System.currentTimeMillis();
        WebElement campoNombre = wait.until(ExpectedConditions.elementToBeClickable(By.id("createNombre")));
        campoNombre.clear();
        campoNombre.sendKeys(nombreAreaDestino);

        WebElement btnGuardar = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("#createModal button[type='submit'].btn-success")));
        btnGuardar.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(2000);

        // Paso 2: Buscar un empleado existente para transferir
        driver.get(BASE_URL + "/usuarios");
        Thread.sleep(3000);

        // Filtrar solo empleados
        Select filterTipo = new Select(driver.findElement(By.id("filterTipo")));
        filterTipo.selectByValue("true"); // true = Empleados
        Thread.sleep(2000);

        // Obtener primer empleado de la tabla
        WebElement primeraFilaEmpleado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#usuariosTableBody tr")));
        String nombreCompleto = primeraFilaEmpleado.findElements(By.tagName("td")).get(2).getText();
        WebElement btnEditarEmpleado = primeraFilaEmpleado.findElement(By.cssSelector("a[href*='/editar']"));
        String empleadoId = btnEditarEmpleado.getAttribute("href").split("/")[4];

        // Paso 3: Editar empleado y cambiar de área
        btnEditarEmpleado.click();
        Thread.sleep(2000);

        // Cambiar área a la nueva área creada
        Select selectArea = new Select(driver.findElement(By.id("areaId")));
        for (int i = 0; i < selectArea.getOptions().size(); i++) {
            if (selectArea.getOptions().get(i).getText().contains(nombreAreaDestino)) {
                selectArea.selectByIndex(i);
                break;
            }
        }

        driver.findElement(By.cssSelector("button[type='submit']")).click();
        Thread.sleep(3000);

        // Paso 4: Verificar historial TRANSFERENCIA
        driver.get(BASE_URL + "/usuarios");
        Thread.sleep(3000);

        // Buscar el empleado y abrir su historial
        WebElement empleadoFila = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreCompleto + "')]")));

        // Buscar botón de historial (icono fa-history)
        WebElement btnHistorial = empleadoFila.findElement(By.cssSelector("button[title='Ver Historial']"));
        btnHistorial.click();
        Thread.sleep(2000);

        // Verificar que el modal de historial se abrió
        WebElement modalHistorial = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historyModal")));

        // Verificar que existe registro de TRANSFERENCIA en el historial
        String contenidoHistorial = modalHistorial.getText();
        assertTrue(contenidoHistorial.contains("TRANSFERENCIA") || contenidoHistorial.contains("Transferencia"),
                "Historial debería contener registro de TRANSFERENCIA");

        assertTrue(contenidoHistorial.contains(nombreAreaDestino),
                "Historial debería mostrar la nueva área de destino");
    }

    /**
     * E2E-06: Flujo Devolución PENDIENTE → CONFIRMADA completo
     * Prueba workflow de estados de devolución de equipos
     */
    @Test
    @Order(6)
    @DisplayName("E2E-06: Flujo Devolución PENDIENTE → CONFIRMADA")
    void devolucionDeberiaCompletarFlujoEstados() throws InterruptedException {
        loginAsAdmin();

        // Paso 1: Asignar un equipo a un usuario
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        // Buscar un equipo libre (sin usuario asignado)
        List<WebElement> filas = driver.findElements(By.cssSelector("#equiposTableBody tr"));
        WebElement equipoLibre = null;
        String nombreEquipoAsignado = null;

        for (WebElement fila : filas) {
            String textoFila = fila.getText();
            if (textoFila.contains("Sin asignar")) {
                equipoLibre = fila;
                nombreEquipoAsignado = fila.findElements(By.tagName("td")).get(1).getText();
                break;
            }
        }

        assertNotNull(equipoLibre, "Debe haber al menos un equipo libre para el test");

        // Click en botón Asignar del equipo
        WebElement btnAsignar = equipoLibre.findElement(By.cssSelector("button[onclick*='abrirModalAsignacion']"));
        btnAsignar.click();
        Thread.sleep(2000);

        // Esperar que modal de asignación se abra
        WebElement modalAsignacion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalAsignacion")));

        // Seleccionar primer usuario disponible
        Select selectUsuario = new Select(driver.findElement(By.id("usuarioAsignacion")));
        if (selectUsuario.getOptions().size() > 1) {
            selectUsuario.selectByIndex(1);
        }

        // Submit asignación
        WebElement btnSubmitAsignacion = modalAsignacion.findElement(By.cssSelector("button[type='submit']"));
        btnSubmitAsignacion.click();
        Thread.sleep(3000);

        // Paso 2: Solicitar devolución (Estado PENDIENTE)
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        // Buscar el equipo ahora asignado
        WebElement equipoAsignado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEquipoAsignado + "')]")));

        // Click en botón Liberar
        WebElement btnLiberar = equipoAsignado.findElement(By.cssSelector("button[onclick*='abrirModalLiberacion']"));
        btnLiberar.click();
        Thread.sleep(2000);

        // Esperar modal de liberación
        WebElement modalLiberacion = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("modalLiberacion")));

        // Seleccionar motivo de liberación (IMPORTANTE: índice 0 es "Seleccionar motivo" - vacío)
        // Opciones válidas empiezan en índice 1
        Select selectMotivo = new Select(driver.findElement(By.id("motivoLiberacion")));
        if (selectMotivo.getOptions().size() > 1) {
            selectMotivo.selectByValue("DEVOLUCION_VOLUNTARIA"); // Más robusto que selectByIndex
        }

        // Submit liberación
        WebElement btnProcesarLiberacion = modalLiberacion.findElement(
                By.cssSelector("button[onclick='procesarLiberacion()']"));
        btnProcesarLiberacion.click();
        Thread.sleep(3000);

        // Paso 3: Verificar que estado es PENDIENTE
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        WebElement equipoConDevolucionPendiente = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEquipoAsignado + "')]")));

        assertTrue(equipoConDevolucionPendiente.getText().contains("Devolución Pendiente") ||
                   equipoConDevolucionPendiente.getText().contains("PENDIENTE"),
                "Equipo debería mostrar estado de Devolución Pendiente");

        // Paso 4: Confirmar devolución (Estado CONFIRMADA)
        WebElement btnConfirmarDevolucion = equipoConDevolucionPendiente.findElement(
                By.cssSelector("button[onclick*='confirmarDevolucion']"));
        btnConfirmarDevolucion.click();

        // Aceptar confirmación
        Alert alert = wait.until(ExpectedConditions.alertIsPresent());
        alert.accept();
        Thread.sleep(3000);

        // Paso 5: Verificar que equipo está completamente liberado
        driver.get(BASE_URL + "/equipos");
        Thread.sleep(3000);

        WebElement equipoLiberado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEquipoAsignado + "')]")));

        assertTrue(equipoLiberado.getText().contains("Sin asignar"),
                "Equipo debería estar completamente liberado (Sin asignar)");

        assertFalse(equipoLiberado.getText().contains("Devolución Pendiente"),
                "NO debería mostrar Devolución Pendiente después de confirmar");
    }

    /**
     * E2E-07: Asignar/Remover Responsable Área → Historial ASCENSO_JEFE/DESCENSO
     * Prueba integración Java service + triggers BD para historial de empleados
     */
    @Test
    @Order(7)
    @DisplayName("E2E-07: Responsable Área → Historial ASCENSO_JEFE y DESCENSO")
    void responsableAreaDeberiaRegistrarHistorialAscensoYDescenso() throws InterruptedException {
        loginAsAdmin();

        // Paso 1: Buscar un empleado para hacerlo responsable
        driver.get(BASE_URL + "/usuarios");
        Thread.sleep(3000);

        // Filtrar solo empleados
        Select filterTipo = new Select(driver.findElement(By.id("filterTipo")));
        filterTipo.selectByValue("true");
        Thread.sleep(2000);

        // Obtener primer empleado
        WebElement primeraFilaEmpleado = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#usuariosTableBody tr")));
        String nombreEmpleado = primeraFilaEmpleado.findElements(By.tagName("td")).get(2).getText();

        // Paso 2: Asignar como responsable de área
        driver.get(BASE_URL + "/areas");
        Thread.sleep(3000);

        // Buscar primera área y abrir modal de responsable
        WebElement primeraFilaArea = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.cssSelector("#areasTableBody tr")));
        String nombreArea = primeraFilaArea.findElements(By.tagName("td")).get(0).getText();

        // Click en botón Gestionar Responsable
        WebElement btnGestionarResp = primeraFilaArea.findElement(
                By.cssSelector("button[onclick*='manageResponsable']"));
        btnGestionarResp.click();
        Thread.sleep(2000);

        // Esperar modal de responsable
        WebElement modalResponsable = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.id("responsableModal")));

        // Seleccionar el empleado como responsable
        Select selectResponsable = new Select(driver.findElement(By.id("responsableUsuario")));
        boolean empleadoSeleccionado = false;
        for (int i = 0; i < selectResponsable.getOptions().size(); i++) {
            if (selectResponsable.getOptions().get(i).getText().contains(nombreEmpleado)) {
                selectResponsable.selectByIndex(i);
                empleadoSeleccionado = true;
                break;
            }
        }

        assertTrue(empleadoSeleccionado, "Empleado debería estar disponible para selección como responsable");

        // Submit asignación de responsable
        WebElement btnSubmitResponsable = modalResponsable.findElement(By.cssSelector("button[type='submit']"));
        btnSubmitResponsable.click();
        Thread.sleep(3000);

        // Paso 3: Verificar historial ASCENSO_JEFE
        driver.get(BASE_URL + "/usuarios");
        Thread.sleep(3000);

        WebElement empleadoFila = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEmpleado + "')]")));

        WebElement btnHistorial = empleadoFila.findElement(By.cssSelector("button[title='Ver Historial']"));
        btnHistorial.click();
        Thread.sleep(2000);

        WebElement modalHistorial = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historyModal")));
        String contenidoHistorial = modalHistorial.getText();

        assertTrue(contenidoHistorial.contains("ASCENSO_JEFE") || contenidoHistorial.contains("Ascenso a Jefe"),
                "Historial debería contener registro de ASCENSO_JEFE");

        assertTrue(contenidoHistorial.contains("Jefe de Área") || contenidoHistorial.contains(nombreArea),
                "Historial debería mostrar cargo Jefe de Área");

        // Cerrar modal
        WebElement btnCerrarHistorial = modalHistorial.findElement(By.cssSelector("button[data-dismiss='modal']"));
        btnCerrarHistorial.click();
        Thread.sleep(1000);

        // Paso 4: Remover como responsable (DESCENSO)
        driver.get(BASE_URL + "/areas");
        Thread.sleep(3000);

        // Buscar la misma área
        WebElement areaFila = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreArea + "')]")));

        WebElement btnGestionarResp2 = areaFila.findElement(
                By.cssSelector("button[onclick*='manageResponsable']"));
        btnGestionarResp2.click();
        Thread.sleep(2000);

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("responsableModal")));

        // Seleccionar "Sin responsable"
        Select selectResponsable2 = new Select(driver.findElement(By.id("responsableUsuario")));
        selectResponsable2.selectByValue(""); // Empty = Sin responsable

        WebElement btnSubmitResponsable2 = driver.findElement(
                By.cssSelector("#responsableModal button[type='submit']"));
        btnSubmitResponsable2.click();
        Thread.sleep(3000);

        // Paso 5: Verificar historial DESCENSO
        driver.get(BASE_URL + "/usuarios");
        Thread.sleep(3000);

        WebElement empleadoFila2 = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreEmpleado + "')]")));

        WebElement btnHistorial2 = empleadoFila2.findElement(By.cssSelector("button[title='Ver Historial']"));
        btnHistorial2.click();
        Thread.sleep(2000);

        WebElement modalHistorial2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("historyModal")));
        String contenidoHistorial2 = modalHistorial2.getText();

        assertTrue(contenidoHistorial2.contains("DESCENSO") || contenidoHistorial2.contains("Descenso"),
                "Historial debería contener registro de DESCENSO después de remover responsable");
    }

    /**
     * E2E-08: Reactivación de Catálogos con ignore case
     * Prueba lógica de negocio única: reactivar en lugar de error de duplicado
     */
    @Test
    @Order(8)
    @DisplayName("E2E-08: Reactivación Catálogos con ignore case")
    void reactivacionCatalogosDeberiaIgnorarCase() throws InterruptedException {
        loginAsAdmin();

        // Paso 1: Crear marca "LG"
        driver.get(BASE_URL + "/marcas");
        Thread.sleep(3000);

        WebElement btnNuevaMarca = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[data-target='#createModal']")));
        btnNuevaMarca.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(1000);

        String nombreMarca = "LG-E2E-" + System.currentTimeMillis();
        WebElement campoNombreMarca = wait.until(ExpectedConditions.elementToBeClickable(By.id("createNombre")));
        campoNombreMarca.clear();
        campoNombreMarca.sendKeys(nombreMarca);

        WebElement btnGuardarMarca = driver.findElement(By.cssSelector("#createModal button[type='submit']"));
        btnGuardarMarca.click();

        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(2000);

        // Verificar que se creó y está ACTIVA
        WebElement marcaFila = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreMarca + "')]")));
        assertTrue(marcaFila.getText().contains("Activo"), "Marca recién creada debería estar ACTIVA");

        // Paso 2: Soft delete de la marca (marcarla como inactiva)
        // CORRECCIÓN: El botón de eliminar usa onclick="deleteMarca(id)", no deleteRecord
        WebElement btnEliminar = marcaFila.findElement(By.cssSelector("button[onclick*='deleteMarca']"));
        btnEliminar.click();

        // CORRECCIÓN: La función deleteMarca usa confirm(), no alert()
        Thread.sleep(500); // Esperar que aparezca el confirm
        Alert confirm = wait.until(ExpectedConditions.alertIsPresent());
        confirm.accept();
        Thread.sleep(3000);

        // Paso 3: Cambiar filtro para ver inactivas y verificar soft delete
        Select filterActivo = new Select(driver.findElement(By.id("filterActivo")));
        filterActivo.selectByValue("false"); // Ver solo inactivos
        Thread.sleep(2000);

        // Verificar que la marca está inactiva
        WebElement marcaInactiva = wait.until(ExpectedConditions.presenceOfElementLocated(
                By.xpath("//tr[contains(., '" + nombreMarca + "')]")));
        assertTrue(marcaInactiva.getText().contains("Inactivo"),
                "Marca debería estar marcada como INACTIVA después de eliminar");

        // Paso 4: Intentar crear marca con mismo nombre pero diferente case "lg"
        filterActivo.selectByValue("true"); // Volver a ver solo activos
        Thread.sleep(2000);

        // Verificar que NO aparece en activos
        List<WebElement> filasActivas = driver.findElements(By.cssSelector("#marcasTableBody tr"));
        boolean marcaEnActivos = filasActivas.stream()
                .anyMatch(fila -> fila.getText().toUpperCase().contains(nombreMarca.toUpperCase()));
        assertFalse(marcaEnActivos, "Marca eliminada NO debería aparecer en lista de activos");

        // Abrir modal crear de nuevo
        WebElement btnNuevaMarca2 = wait.until(ExpectedConditions.elementToBeClickable(
                By.cssSelector("button[data-target='#createModal']")));
        btnNuevaMarca2.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("createModal")));
        Thread.sleep(1000);

        // CLAVE: Usar mismo nombre pero en minúsculas para probar ignore case
        WebElement campoNombreMarca2 = wait.until(ExpectedConditions.elementToBeClickable(By.id("createNombre")));
        campoNombreMarca2.clear();
        campoNombreMarca2.sendKeys(nombreMarca.toLowerCase()); // lg-e2e-...

        WebElement btnGuardarMarca2 = driver.findElement(By.cssSelector("#createModal button[type='submit']"));
        btnGuardarMarca2.click();
        Thread.sleep(3000);

        // Paso 5: Verificar que se REACTIVÓ en lugar de crear nueva
        // La lógica del sistema (MarcaServiceImpl:91-96) reactiva automáticamente

        // 5.1: Modal debería haberse cerrado (indica que el submit fue exitoso)
        boolean modalCerrado = driver.findElements(By.cssSelector("#createModal.show")).isEmpty();
        assertTrue(modalCerrado, "Modal debería cerrarse después de creación/reactivación exitosa");

        // 5.2: Buscar la marca en lista de ACTIVOS
        Thread.sleep(2000);
        List<WebElement> filasActivasDespues = driver.findElements(By.cssSelector("#marcasTableBody tr"));

        boolean marcaReactivada = false;
        int contador = 0;

        for (WebElement fila : filasActivasDespues) {
            String textoFila = fila.getText().toUpperCase();
            if (textoFila.contains(nombreMarca.toUpperCase())) {
                contador++;
                marcaReactivada = true;
                assertTrue(textoFila.contains("ACTIVO"),
                        "Marca debería estar ACTIVA después de reactivación");
            }
        }

        assertTrue(marcaReactivada, "Marca debería haberse reactivado y aparecer en lista de activos");

        // 5.3: CRÍTICO - Verificar que solo hay UNA instancia (reactivó, no creó nueva)
        assertEquals(1, contador,
                "Solo debería haber UNA instancia de la marca (reactivada), no duplicados");

        // 5.4: Verificar en inactivos que ya NO está ahí
        filterActivo.selectByValue("false");
        Thread.sleep(2000);

        List<WebElement> filasInactivasDespues = driver.findElements(By.cssSelector("#marcasTableBody tr"));
        boolean marcaEnInactivos = filasInactivasDespues.stream()
                .anyMatch(fila -> fila.getText().toUpperCase().contains(nombreMarca.toUpperCase()));

        assertFalse(marcaEnInactivos,
                "Marca NO debería estar en inactivos después de reactivación");
    }

    /**
     * E2E-09: Botón Atrás del navegador no regresa a login después de autenticarse
     * Prueba que el sistema JavaScript previene el acceso a /login cuando ya está autenticado
     */
    @Test
    @Order(9)
    @DisplayName("E2E-09: Botón Atrás no regresa a login después de autenticarse")
    void deberiaRedirigirUsuarioAutenticadoQueIntentaAccederALogin() throws InterruptedException {
        // Paso 1: Login exitoso
        loginAsAdmin();
        Thread.sleep(2000);

        // Verificar que estamos en /usuarios
        assertTrue(driver.getCurrentUrl().contains("/usuarios"),
                "Debería estar en página de usuarios después de login");

        // Paso 2: Navegar a otra página (áreas)
        driver.get(BASE_URL + "/areas");
        Thread.sleep(2000);
        assertTrue(driver.getCurrentUrl().contains("/areas"),
                "Debería navegar a áreas correctamente");

        // Paso 3: Usar botón "Atrás" del navegador
        driver.navigate().back();
        Thread.sleep(2000);

        // Verificar que regresó a /usuarios
        assertTrue(driver.getCurrentUrl().contains("/usuarios"),
                "Botón atrás debería regresar a /usuarios");

        // Paso 4: CLAVE - Intentar acceder directamente a /login estando autenticado
        driver.get(BASE_URL + "/login");
        Thread.sleep(2000);

        // VALIDACIÓN: Debería redirigir automáticamente a /usuarios, NO mostrar login
        assertTrue(driver.getCurrentUrl().contains("/usuarios"),
                "Usuario autenticado NO debería poder acceder a /login, debe redirigir a /usuarios");

        assertFalse(driver.getPageSource().contains("Iniciar Sesión") ||
                        driver.getPageSource().contains("Sign In"),
                "No debería mostrar el formulario de login");

        // Paso 5: Simular múltiples intentos de regresar con botón atrás
        driver.navigate().back();
        Thread.sleep(1000);
        driver.navigate().back();
        Thread.sleep(1000);

        // Verificar que NUNCA llegamos a la página de login
        assertFalse(driver.getCurrentUrl().contains("/login"),
                "Botón atrás NO debería permitir regresar a /login mientras está autenticado");
    }
}