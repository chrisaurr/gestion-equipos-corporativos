# PRUEBAS MANUALES - TÉCNICAS DE CAJA NEGRA
## Sistema de Gestión Integral de Equipos Corporativos

**Versión**: 1.0.0
**Fecha**: Noviembre 2025
**Técnicas aplicadas**: Partición de Equivalencia, Valores Límite, Tabla de Decisión

---

## ÍNDICE

1. [PARTICIÓN DE EQUIVALENCIA (7 pruebas)](#partición-de-equivalencia)
2. [VALORES LÍMITE (7 pruebas)](#valores-límite)
3. [TABLA DE DECISIÓN (7 pruebas)](#tabla-de-decisión)

---

# PARTICIÓN DE EQUIVALENCIA

**Objetivo**: Dividir datos de entrada en grupos (particiones) donde todos los valores se comportan de manera similar. Se prueba un valor representativo por partición.

---

## PE-01: Valor de Equipo - Partición Válida (valores >= 0)

**Campo probado**: `EquipoCreateDTO.valor`
**Ubicación código**: `EquipoCreateDTO.java` líneas 35-37
**Validación**: `@NotNull` + `@DecimalMin(value = "0.0")`

### Definición de la partición:

**Partición**: Todos los valores BigDecimal >= 0.0
**Conjunto de valores equivalentes**: {0.00, 0.01, 1.50, 1500.00, 99999.99, ...}
**Valor representativo seleccionado**: `1500.00`

### Comportamiento esperado:

✅ **ACEPTA** el valor y crea el equipo exitosamente

### Pasos para ejecutar:

1. Login como admin (`admin2` / `admin`)
2. Navegar a `/equipos/crear`
3. Llenar campos obligatorios:
   - Nombre: "Equipo PE-01"
   - Marca: Seleccionar cualquier marca (ej: "HP")
   - **Valor**: Ingresar `1500.00`
   - Tipo de Equipo: Seleccionar "Electrónico"
4. Llenar campos específicos de electrónico (IMEI si aplica)
5. Click en "Guardar"

### Resultado esperado:

- ✅ Equipo se crea exitosamente
- ✅ Redirige a `/equipos`
- ✅ Equipo aparece en la tabla con valor $1,500.00

### Nota técnica:

**Todos los valores dentro de esta partición producen el mismo resultado** (ACEPTA), por ejemplo: 0.00, 10.50, 5000.00 también pasarían. Solo probamos un representativo.

---

## PE-02: Valor de Equipo - Partición Inválida (valores negativos)

**Campo probado**: `EquipoCreateDTO.valor`
**Ubicación código**: `EquipoCreateDTO.java` líneas 35-37
**Validación**: `@DecimalMin(value = "0.0", message = "El valor no puede ser negativo")`

### Definición de la partición:

**Partición**: Todos los valores BigDecimal < 0
**Conjunto de valores equivalentes**: {-0.01, -1.00, -100.00, -9999.99, ...}
**Valor representativo seleccionado**: `-100.00`

### Comportamiento esperado:

❌ **RECHAZA** con mensaje: "El valor no puede ser negativo"

### Pasos para ejecutar:

1. Login como admin
2. Navegar a `/equipos/crear`
3. Llenar campos obligatorios:
   - Nombre: "Equipo PE-02"
   - Marca: Seleccionar cualquier marca
   - **Valor**: Ingresar `-100.00`
   - Tipo de Equipo: Seleccionar "Electrónico"
4. Llenar campos específicos de electrónico
5. Click en "Guardar"

### Resultado esperado:

- ❌ Formulario NO se envía
- ❌ Muestra error de validación: "El valor no puede ser negativo"
- ❌ Permanece en la página de creación

### Nota técnica:

**Todos los valores negativos producen el mismo resultado** (RECHAZA con mismo mensaje). Probar -0.01 o -5000.00 daría el mismo error.

---

## PE-03: Estado Usuario para Responsable - Partición Válida (ACTIVO)

**Campo probado**: `Usuario.estado` al asignar como responsable de área
**Ubicación código**: `AreaServiceImpl.java` líneas 153-155
**Validación**: `if (usuario.getEstado() != EstadoUsuario.ACTIVO)`

### Definición de la partición:

**Partición**: Estado ACTIVO
**Conjunto de valores equivalentes**: {ACTIVO}
**Valor representativo seleccionado**: `ACTIVO`

### Comportamiento esperado:

✅ **ACEPTA** la asignación del usuario como responsable de área

### Pasos para ejecutar:

1. Login como admin
2. Verificar que existe un usuario con estado ACTIVO:
   - Ir a `/usuarios`
   - Confirmar que hay al menos un usuario con estado "Activo"
   - Si no existe, crear uno
3. Navegar a `/areas`
4. Seleccionar un área existente (ej: "Recursos Humanos")
5. Click en botón "Gestionar Responsable" (icono de usuario)
6. En el modal, seleccionar el usuario ACTIVO
7. Click en "Asignar"

### Resultado esperado:

- ✅ Modal se cierra
- ✅ Área muestra el nombre del usuario como responsable
- ✅ Si el usuario es empleado, se genera historial ASCENSO_JEFE

### Nota técnica:

Solo usuarios con estado ACTIVO pueden ser responsables. Este es el único valor de la partición válida.

---

## PE-04: Estado Usuario para Responsable - Partición Inválida (NO ACTIVO)

**Campo probado**: `Usuario.estado` al asignar como responsable de área
**Ubicación código**: `AreaServiceImpl.java` líneas 153-155, `AreaWebController.java` filtrado de dropdown
**Validación Frontend**: Dropdown solo carga usuarios con estado ACTIVO

### Definición de la partición:

**Partición**: Estados NO ACTIVOS
**Conjunto de valores equivalentes**: {INACTIVO, VACACIONES, LICENCIA}
**Valor representativo seleccionado**: `VACACIONES`

### Comportamiento esperado:

✅ **UI PREVIENE** la operación - usuarios NO ACTIVOS no aparecen en el dropdown

### Pasos para ejecutar:

1. Login como admin
2. Crear o editar un usuario para que tenga estado VACACIONES:
   - Ir a `/usuarios`
   - Editar un usuario existente
   - Cambiar "Estado" a "VACACIONES"
   - Guardar
3. Navegar a `/areas`
4. Seleccionar un área existente
5. Click en "Gestionar Responsable"
6. **Verificar que el usuario con estado VACACIONES NO aparece en el dropdown**

### Resultado esperado:

- ✅ Dropdown solo muestra usuarios con estado ACTIVO
- ✅ Usuario con estado VACACIONES NO está disponible para selección
- ✅ UI previene correctamente la asignación de usuarios inactivos

### Nota técnica:

**Todos los estados NO ACTIVOS son bloqueados por UI** (no aparecen en dropdown). Probar con INACTIVO o LICENCIA daría el mismo comportamiento.

---

## PE-05: Estado Equipo para Asignación - Partición Inválida (NO ACTIVO)

**Campo probado**: `Equipo.estado` al intentar asignar equipo a usuario
**Ubicación código**: `EquipoAsignacionServiceImpl.java` líneas 66-68
**Validación**: `throw new RuntimeException("Solo se pueden asignar equipos en estado ACTIVO")`

### Definición de la partición:

**Partición**: Estados NO ACTIVOS
**Conjunto de valores equivalentes**: {INACTIVO, MANTENIMIENTO, SUSPENDIDO, BAJA}
**Valor representativo seleccionado**: `MANTENIMIENTO`

### Comportamiento esperado:

❌ **RECHAZA** con mensaje: "Solo se pueden asignar equipos en estado ACTIVO"

### Pasos para ejecutar:

1. Login como admin
2. Crear o editar un equipo para que tenga estado MANTENIMIENTO:
   - Ir a `/equipos`
   - Editar un equipo existente sin asignar
   - Cambiar "Estado" a "MANTENIMIENTO"
   - Guardar
3. Regresar a `/equipos`
4. Buscar el equipo con estado MANTENIMIENTO
5. Intentar click en botón "Asignar" (si está disponible)
6. O intentar asignarlo desde el modal

### Resultado esperado:

- ❌ Muestra mensaje de error: "Solo se pueden asignar equipos en estado ACTIVO"
- ❌ NO se realiza la asignación
- ❌ Equipo permanece sin usuario asignado

### Nota técnica:

**Todos los estados NO ACTIVOS producen el mismo resultado** (RECHAZA). Probar con INACTIVO, SUSPENDIDO o BAJA daría el mismo error.

---

## PE-06: Material de Mobiliario - Partición Válida (strings no vacíos)

**Campo probado**: `MobiliarioCreateDTO.material`
**Ubicación código**: `MobiliarioCreateDTO.java` líneas 18-19
**Validación**: `@NotBlank` + `@Size(max = 50)`

### Definición de la partición:

**Partición**: Strings no vacíos de 1-50 caracteres
**Conjunto de valores equivalentes**: {"M", "Madera", "Metal", "Plástico", "Vidrio", "Acero Inoxidable", ...}
**Valor representativo seleccionado**: `"Madera"`

### Comportamiento esperado:

✅ **ACEPTA** el material y crea el mobiliario exitosamente

### Pasos para ejecutar:

1. Login como admin
2. Navegar a `/equipos/crear`
3. Llenar campos base:
   - Nombre: "Escritorio PE-06"
   - Marca: Seleccionar cualquier marca
   - Valor: 500.00
4. Seleccionar tipo de equipo: **Radio button "MOBILIARIO"**
5. Esperar que aparezcan campos específicos de mobiliario
6. Llenar campos de mobiliario:
   - **Material**: Ingresar `"Madera"`
   - Altura: 0.75
   - Ancho: 1.20
   - Profundidad: 0.60
7. Click en "Guardar"

### Resultado esperado:

- ✅ Equipo mobiliario se crea exitosamente
- ✅ Redirige a `/equipos`
- ✅ Equipo aparece como tipo "Mobiliario" con material "Madera"

### Nota técnica:

**Todos los strings válidos (1-50 chars) producen el mismo resultado** (ACEPTA). Probar "Metal", "Plástico" o "Vidrio" también pasaría.

---

## PE-07: Nombre Área - Partición Reactivación (existe inactiva)

**Campo probado**: `AreaCreateDTO.nombre`
**Ubicación código**: `AreaServiceImpl.java` líneas 79-86
**Lógica**: Reactivación automática de áreas inactivas (ignore case)

### Definición de la partición:

**Partición**: Nombres iguales (ignore case) a áreas INACTIVAS existentes
**Conjunto de valores equivalentes**: {"rrhh", "RRHH", "recursos humanos" cuando "Recursos Humanos" existe inactiva}
**Valor representativo seleccionado**: `"recursos humanos"` (cuando "Recursos Humanos" existe pero está inactiva)

### Comportamiento esperado:

✅ **ACEPTA** y **REACTIVA** el área existente (cambia activo de false a true)

### Pasos para ejecutar:

**Preparación**:
1. Login como admin
2. Ir a `/areas`
3. Crear área con nombre "Recursos Humanos PE-07"
4. Eliminar el área (soft delete → activo=false)
5. Cambiar filtro a "Inactivos" para confirmar que existe pero inactiva

**Prueba**:
6. Cambiar filtro de nuevo a "Activos"
7. Verificar que "Recursos Humanos PE-07" NO aparece
8. Click en "Nueva Área"
9. Ingresar nombre: `"recursos humanos pe-07"` (minúsculas, ignore case)
10. Click en "Guardar"

### Resultado esperado:

- ✅ Modal se cierra
- ✅ Área "Recursos Humanos PE-07" aparece en lista de ACTIVOS
- ✅ NO se crea nueva área (se reutiliza el ID anterior)
- ✅ Al cambiar a filtro "Inactivos", ya NO aparece ahí

### Verificación adicional:

- Ir a consola de BD o revisar por API: `activo = true` y `fecha_commit` actualizada

### Nota técnica:

**Todos los nombres que coincidan (ignore case) con áreas inactivas producen el mismo resultado** (REACTIVA). La lógica es case-insensitive.

---

# VALORES LÍMITE

**Objetivo**: Probar valores en los bordes exactos de rangos válidos e inválidos para detectar errores en límites.

---

## VL-01: Longitud Primer Nombre Usuario (max 50 caracteres)

**Campo probado**: `UsuarioEmpleadoCreateDTO.primerNombre`
**Ubicación código**: `UsuarioEmpleadoCreateDTO.java` líneas 26-28
**Validación**: `@NotBlank` + `@Size(max = 50)`

### Valores límite a probar:

| Límite | Valor | Longitud | Esperado | Mensaje |
|--------|-------|----------|----------|---------|
| **Inferior - 1** | `""` (vacío) | 0 chars | ❌ RECHAZA | "El primer nombre es obligatorio" |
| **Inferior** | `"A"` | 1 char | ✅ ACEPTA | - |
| **Superior** | String de 50 'A' | 50 chars | ✅ ACEPTA | - |
| **Superior + 1** | String de 51 'A' | 51 chars | ❌ RECHAZA | "El primer nombre no puede tener más de 50 caracteres" |

### Pasos para ejecutar:

**Caso 1 (Inferior - 1: vacío)**:
1. Login como admin
2. Ir a `/usuarios/crear`
3. Dejar campo "Primer Nombre" **vacío**
4. Llenar otros campos obligatorios (código único, usuario único, password, apellido, fecha)
5. Submit

**Resultado**: ❌ Error "El primer nombre es obligatorio"

---

**Caso 2 (Inferior: 1 carácter)**:
1. Ir a `/usuarios/crear`
2. Primer Nombre: `"A"`
3. Llenar otros campos obligatorios
4. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 3 (Superior: 50 caracteres)**:
1. Ir a `/usuarios/crear`
2. Primer Nombre: `"AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDDEEEEEEEEEE"` (exactamente 50)
3. Llenar otros campos obligatorios
4. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 4 (Superior + 1: 51 caracteres)**:
1. Ir a `/usuarios/crear`
2. Primer Nombre: `"AAAAAAAAAABBBBBBBBBBCCCCCCCCCCDDDDDDDDDDEEEEEEEEEEEF"` (51 chars)
3. Llenar otros campos obligatorios
4. Submit

**Resultado**: ❌ Error "El primer nombre no puede tener más de 50 caracteres"

### Nota técnica:

Generar strings largos en JavaScript console:
```javascript
"A".repeat(50)  // 50 caracteres
"A".repeat(51)  // 51 caracteres
```

---

## VL-02: Longitud Código Usuario (max 20 caracteres)

**Campo probado**: `UsuarioEmpleadoCreateDTO.codigo`
**Ubicación código**: `UsuarioEmpleadoCreateDTO.java` líneas 18-20
**Validación**: `@NotBlank` + `@Size(max = 20)`

### Valores límite a probar:

| Límite | Valor | Longitud | Esperado | Mensaje |
|--------|-------|----------|----------|---------|
| **Inferior - 1** | `""` (vacío) | 0 chars | ❌ RECHAZA | "El código es obligatorio" |
| **Inferior** | `"U"` | 1 char | ✅ ACEPTA | - |
| **Superior** | `"12345678901234567890"` | 20 chars | ✅ ACEPTA | - |
| **Superior + 1** | String de 21 chars | 21 chars | ❌ RECHAZA | "El código no puede tener más de 20 caracteres" |

### Pasos para ejecutar:

**Caso 1 (Inferior - 1: vacío)**:
1. Ir a `/usuarios/crear`
2. Dejar campo "Código" **vacío**
3. Llenar otros campos obligatorios
4. Submit

**Resultado**: ❌ Error "El código es obligatorio"

---

**Caso 2 (Inferior: 1 carácter)**:
1. Ir a `/usuarios/crear`
2. Código: `"U"`
3. Llenar otros campos (asegurar usuario único)
4. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 3 (Superior: 20 caracteres)**:
1. Ir a `/usuarios/crear`
2. Código: `"12345678901234567890"` (exactamente 20)
3. Llenar otros campos
4. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 4 (Superior + 1: 21 caracteres)**:
1. Ir a `/usuarios/crear`
2. Código: `"123456789012345678901"` (21 chars)
3. Llenar otros campos
4. Submit

**Resultado**: ❌ Error "El código no puede tener más de 20 caracteres"

### Nota importante:

Asegurar que los códigos sean únicos para evitar conflicto con validación de duplicados.

---

## VL-03: Longitud Contraseña (min 4, max 72 caracteres)

**Campo probado**: `UsuarioEmpleadoCreateDTO.password`
**Ubicación código**: `UsuarioEmpleadoCreateDTO.java` línea 50
**Validación**: `@NotBlank` + `@Size(min = 4, max = 72)`
**Nota técnica**: BCrypt tiene límite de 72 bytes para encriptación

### Valores límite a probar:

| Límite | Valor | Longitud | Esperado | Mensaje |
|--------|-------|----------|----------|---------|
| **Inferior - 1** | `"abc"` | 3 chars | ❌ RECHAZA | "La contraseña debe tener entre 4 y 72 caracteres" |
| **Inferior** | `"abcd"` | 4 chars | ✅ ACEPTA | - |
| **Superior** | String de 72 'a' | 72 chars | ✅ ACEPTA | - |
| **Superior + 1** | String de 73 'a' | 73 chars | ❌ RECHAZA | "La contraseña debe tener entre 4 y 72 caracteres" |

### Pasos para ejecutar:

**Caso 1 (Inferior - 1: 3 caracteres)**:
1. Ir a `/usuarios/crear`
2. Password: `"abc"`
3. Confirmar Password: `"abc"`
4. Llenar otros campos obligatorios
5. Submit

**Resultado**: ❌ Error "La contraseña debe tener entre 4 y 72 caracteres"

---

**Caso 2 (Inferior: 4 caracteres)**:
1. Ir a `/usuarios/crear`
2. Password: `"abcd"`
3. Confirmar Password: `"abcd"`
4. Llenar otros campos
5. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 3 (Superior: 72 caracteres)**:
1. Ir a `/usuarios/crear`
2. Password: `"a".repeat(72)` (72 chars - usar JavaScript console para generar)
3. Confirmar Password: igual
4. Llenar otros campos
5. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 4 (Superior + 1: 73 caracteres)**:
1. Ir a `/usuarios/crear`
2. Password: `"a".repeat(73)` (73 chars)
3. Confirmar Password: igual
4. Llenar otros campos
5. Submit

**Resultado**: ❌ Error "La contraseña debe tener entre 4 y 72 caracteres"

---

## VL-04: Longitud Nombre Área (max 50 caracteres)

**Campo probado**: `AreaCreateDTO.nombre`
**Ubicación código**: `AreaCreateDTO.java` líneas 15-17
**Validación**: `@NotBlank` + `@Size(max = 50)`

### Valores límite a probar:

| Límite | Valor | Longitud | Esperado | Mensaje |
|--------|-------|----------|----------|---------|
| **Inferior - 1** | `""` (vacío) | 0 chars | ❌ RECHAZA | "El nombre es obligatorio" |
| **Inferior** | `"A"` | 1 char | ✅ ACEPTA | - |
| **Superior** | String de 50 chars | 50 chars | ✅ ACEPTA | - |
| **Superior + 1** | String de 51 chars | 51 chars | ❌ RECHAZA | "El nombre no puede tener más de 50 caracteres" |

### Pasos para ejecutar:

**Caso 1 (Inferior - 1: vacío)**:
1. Ir a `/areas`
2. Click "Nueva Área"
3. Dejar campo "Nombre" **vacío**
4. Submit

**Resultado**: ❌ Error "El nombre es obligatorio"

---

**Caso 2 (Inferior: 1 carácter)**:
1. Click "Nueva Área"
2. Nombre: `"A"`
3. Submit

**Resultado**: ✅ Área creada exitosamente

---

**Caso 3 (Superior: 50 caracteres)**:
1. Click "Nueva Área"
2. Nombre: String de 50 caracteres (ej: "A".repeat(50))
3. Submit

**Resultado**: ✅ Área creada exitosamente

---

**Caso 4 (Superior + 1: 51 caracteres)**:
1. Click "Nueva Área"
2. Nombre: String de 51 caracteres
3. Submit

**Resultado**: ❌ Error "El nombre no puede tener más de 50 caracteres"

---

## VL-05: Longitud Descripción Causa (max 300 caracteres)

**Campo probado**: `CausaCreateDTO.descripcion`
**Ubicación código**: `CausaCreateDTO.java` línea 20
**Validación**: `@Size(max = 300)` (campo opcional)

### Valores límite a probar:

| Límite | Valor | Longitud | Esperado | Mensaje |
|--------|-------|----------|----------|---------|
| **Inferior** | `""` (vacío) | 0 chars | ✅ ACEPTA | - (campo opcional) |
| **Superior** | String de 300 chars | 300 chars | ✅ ACEPTA | - |
| **Superior + 1** | String de 301 chars | 301 chars | ❌ RECHAZA | "La descripción no puede exceder 300 caracteres" |

### Pasos para ejecutar:

**Caso 1 (Inferior: vacío - opcional)**:
1. Ir a `/causas`
2. Click "Nueva Causa"
3. Nombre: "Causa VL-05"
4. Dejar campo "Descripción" **vacío**
5. Prioridad: Seleccionar cualquiera
6. Submit

**Resultado**: ✅ Causa creada exitosamente (descripción es opcional)

---

**Caso 2 (Superior: 300 caracteres)**:
1. Click "Nueva Causa"
2. Nombre: "Causa VL-05-Max"
3. Descripción: String de exactamente 300 caracteres
4. Prioridad: Seleccionar
5. Submit

**Resultado**: ✅ Causa creada exitosamente

---

**Caso 3 (Superior + 1: 301 caracteres)**:
1. Click "Nueva Causa"
2. Nombre: "Causa VL-05-Over"
3. Descripción: String de 301 caracteres
4. Prioridad: Seleccionar
5. Submit

**Resultado**: ❌ Error "La descripción no puede exceder 300 caracteres"

---

## VL-06: Valor Equipo en Borde Cero (DecimalMin = 0.0)

**Campo probado**: `EquipoCreateDTO.valor`
**Ubicación código**: `EquipoCreateDTO.java` líneas 35-37
**Validación**: `@DecimalMin(value = "0.0")`

### Valores límite a probar:

| Límite | Valor | Esperado | Mensaje |
|--------|-------|----------|---------|
| **Inferior - 1** | `-0.01` | ❌ RECHAZA | "El valor no puede ser negativo" |
| **Inferior** | `0.00` | ✅ ACEPTA | - |
| **Inferior + 1** | `0.01` | ✅ ACEPTA | - |

### Pasos para ejecutar:

**Caso 1 (Inferior - 1: -0.01)**:
1. Ir a `/equipos/crear`
2. Nombre: "Equipo VL-06-Neg"
3. Marca: Seleccionar
4. **Valor**: `-0.01`
5. Tipo: Electrónico
6. Submit

**Resultado**: ❌ Error "El valor no puede ser negativo"

---

**Caso 2 (Inferior: 0.00 - borde exacto)**:
1. Ir a `/equipos/crear`
2. Nombre: "Equipo VL-06-Cero"
3. Marca: Seleccionar
4. **Valor**: `0.00`
5. Tipo: Electrónico
6. Submit

**Resultado**: ✅ Equipo creado exitosamente con valor $0.00

---

**Caso 3 (Inferior + 1: 0.01)**:
1. Ir a `/equipos/crear`
2. Nombre: "Equipo VL-06-Pos"
3. Marca: Seleccionar
4. **Valor**: `0.01`
5. Tipo: Electrónico
6. Submit

**Resultado**: ✅ Equipo creado exitosamente con valor $0.01

### Nota técnica:

Este caso prueba específicamente el **borde del rango mínimo** permitido (0.0).

---

## VL-07: Longitud IMEI Electrónico (max 20 caracteres)

**Campo probado**: `ElectronicoCreateDTO.imei`
**Ubicación código**: `ElectronicoCreateDTO.java` línea 15
**Validación**: `@Size(max = 20)` (campo opcional)

### Valores límite a probar:

| Límite | Valor | Longitud | Esperado | Mensaje |
|--------|-------|----------|----------|---------|
| **Inferior** | `""` (vacío) | 0 chars | ✅ ACEPTA | - (campo opcional) |
| **Superior** | `"12345678901234567890"` | 20 chars | ✅ ACEPTA | - |
| **Superior + 1** | String de 21 chars | 21 chars | ❌ RECHAZA | "El IMEI no puede exceder 20 caracteres" |

### Pasos para ejecutar:

**Caso 1 (Inferior: vacío - opcional)**:
1. Ir a `/equipos/crear`
2. Nombre: "Laptop VL-07"
3. Marca: Seleccionar
4. Valor: 1000.00
5. Tipo: **Electrónico**
6. Dejar campo **IMEI vacío**
7. Submit

**Resultado**: ✅ Equipo electrónico creado sin IMEI

---

**Caso 2 (Superior: 20 caracteres)**:
1. Ir a `/equipos/crear`
2. Nombre: "Laptop VL-07-Max"
3. Marca: Seleccionar
4. Valor: 1000.00
5. Tipo: Electrónico
6. **IMEI**: `"12345678901234567890"` (exactamente 20)
7. Submit

**Resultado**: ✅ Equipo creado con IMEI de 20 caracteres

---

**Caso 3 (Superior + 1: 21 caracteres)**:
1. Ir a `/equipos/crear`
2. Nombre: "Laptop VL-07-Over"
3. Marca: Seleccionar
4. Valor: 1000.00
5. Tipo: Electrónico
6. **IMEI**: `"123456789012345678901"` (21 chars)
7. Submit

**Resultado**: ❌ Error "El IMEI no puede exceder 20 caracteres"

---

# TABLA DE DECISIÓN

**Objetivo**: Probar combinaciones de múltiples condiciones de entrada que afectan el resultado del sistema.

---

## TD-01: Crear Usuario - Combinaciones esEmpleado + cargo + área

**Campos probados**: `esEmpleado`, `cargo`, `areaId`
**Ubicación código**: `UsuarioServiceImpl.java` líneas 312-318
**Reglas de negocio**:
- Si `esEmpleado = true` → `cargo` y `areaId` son OBLIGATORIOS
- Si `esEmpleado = false` → `cargo` y `areaId` se ignoran

### Tabla de decisión:

| Caso | esEmpleado | cargo | areaId | Resultado | Mensaje |
|------|-----------|-------|--------|-----------|---------|
| **1** | false | null | null | ✅ ACEPTA | Usuario regular creado |
| **2** | false | "Analista" | 1 | ✅ ACEPTA | Usuario regular (ignora cargo/área) |
| **3** | true | "Analista" | 1 | ✅ ACEPTA | Empleado creado con cargo y área |
| **4** | true | null | 1 | ❌ RECHAZA | "El cargo es obligatorio para empleados" |
| **5** | true | "Analista" | null | ❌ RECHAZA | "El área es obligatoria para empleados" |

### Pasos para ejecutar:

**Caso 1: Usuario regular sin empleado**:
1. Ir a `/usuarios/crear`
2. Código: "TD01-C1"
3. Usuario: "td01caso1"
4. Password: "test1234"
5. Primer Nombre: "Test"
6. Primer Apellido: "Uno"
7. Fecha Ingreso: 2025-01-01
8. **NO** marcar checkbox "Es Empleado"
9. Submit

**Resultado**: ✅ Usuario creado exitosamente

---

**Caso 2: Usuario regular con datos de empleado (se ignoran)**:
1. Ir a `/usuarios/crear`
2. Llenar campos base (código único TD01-C2)
3. **NO** marcar checkbox "Es Empleado"
4. Llenar Cargo: "Analista" (aunque esEmpleado=false)
5. Seleccionar Área (aunque esEmpleado=false)
6. Submit

**Resultado**: ✅ Usuario creado, pero cargo y área se ignoran

---

**Caso 3: Empleado completo (válido)**:
1. Ir a `/usuarios/crear`
2. Llenar campos base (código único TD01-C3)
3. **Marcar** checkbox "Es Empleado"
4. Cargo: "Analista"
5. Área: Seleccionar "Tecnología"
6. Submit

**Resultado**: ✅ Empleado creado con cargo y área asignados

---

**Caso 4: Empleado sin cargo (inválido)**:
1. Ir a `/usuarios/crear`
2. Llenar campos base (código único TD01-C4)
3. **Marcar** checkbox "Es Empleado"
4. **Dejar Cargo vacío**
5. Área: Seleccionar "Tecnología"
6. Submit

**Resultado**: ❌ Error "El cargo es obligatorio para empleados"

---

**Caso 5: Empleado sin área (inválido)**:
1. Ir a `/usuarios/crear`
2. Llenar campos base (código único TD01-C5)
3. **Marcar** checkbox "Es Empleado"
4. Cargo: "Analista"
5. **NO seleccionar Área** (dejar vacío)
6. Submit

**Resultado**: ❌ Error "El área es obligatoria para empleados"

---

## TD-02: Asignar Equipo - Combinaciones estado + asignado + reportes

**Campos probados**: `Equipo.estado`, `Equipo.idUsuario`, reportes ABIERTOS
**Ubicación código**: `EquipoAsignacionServiceImpl.java` líneas 58-68
**Reglas de negocio**:
- Solo equipos ACTIVO pueden asignarse
- No se puede asignar si ya tiene usuario
- No se puede asignar si tiene reportes ABIERTOS/EN_PROCESO

### Tabla de decisión:

| Caso | Estado | Asignado | Reportes ABIERTOS | Resultado | Mensaje |
|------|--------|----------|-------------------|-----------|---------|
| **1** | ACTIVO | null | 0 | ✅ ACEPTA | Asignación exitosa |
| **2** | ACTIVO | Usuario A | 0 | ❌ RECHAZA | "El equipo ya está asignado" |
| **3** | ACTIVO | null | 1 | ❌ RECHAZA | "tiene reportes abiertos que deben resolverse primero" |
| **4** | INACTIVO | null | 0 | ❌ RECHAZA | "Solo se pueden asignar equipos en estado ACTIVO" |

### Pasos para ejecutar:

**Caso 1: Equipo ACTIVO sin asignar sin reportes (válido)**:
1. Ir a `/equipos`
2. Buscar un equipo con estado ACTIVO, sin usuario asignado, sin reportes
3. Click botón "Asignar"
4. Seleccionar usuario
5. Submit

**Resultado**: ✅ Equipo asignado exitosamente

---

**Caso 2: Equipo ya asignado (inválido)**:
1. Usar el equipo del Caso 1 (ahora asignado)
2. Intentar click en "Asignar" nuevamente
3. O verificar que el botón no esté disponible

**Resultado**: ❌ Error "El equipo ya está asignado" o botón deshabilitado

---

**Caso 3: Equipo con reporte ABIERTO (inválido)**:
1. Seleccionar equipo ACTIVO sin asignar
2. Crear reporte ABIERTO para ese equipo:
   - Ir a `/reportes/crear`
   - Seleccionar el equipo
   - Llenar observación y causa
   - Submit (queda en estado ABIERTO)
3. Regresar a `/equipos`
4. Intentar asignar el equipo

**Resultado**: ❌ Error "tiene reportes abiertos que deben resolverse primero"

---

**Caso 4: Equipo INACTIVO (inválido)**:
1. Editar un equipo sin asignar
2. Cambiar estado a INACTIVO
3. Guardar
4. Intentar asignar el equipo

**Resultado**: ❌ Error "Solo se pueden asignar equipos en estado ACTIVO"

---

## TD-03: Crear Reporte - Combinaciones equipo + reportes existentes

**Campos probados**: `equipoId`, reportes activos existentes
**Ubicación código**: `ReporteServiceImpl.java` líneas 89-93
**Regla de negocio**: Solo 1 reporte ABIERTO o EN_PROCESO por equipo

### Tabla de decisión:

| Caso | Equipo | Reportes ABIERTOS | Reportes EN_PROCESO | Resultado | Mensaje |
|------|--------|-------------------|---------------------|-----------|---------|
| **1** | Equipo A | 0 | 0 | ✅ ACEPTA | Reporte creado |
| **2** | Equipo A | 1 | 0 | ❌ RECHAZA | "El equipo ya tiene un reporte activo" |
| **3** | Equipo A | 0 | 1 | ❌ RECHAZA | "El equipo ya tiene un reporte activo" |
| **4** | Equipo A (con RESUELTO) | 0 | 0 | ✅ ACEPTA | Reporte creado (RESUELTO no bloquea) |

### Pasos para ejecutar:

**Caso 1: Equipo sin reportes activos (válido)**:
1. Ir a `/reportes/crear`
2. Seleccionar un equipo sin reportes
3. Observación: "Reporte TD-03 Caso 1"
4. Causa: Seleccionar cualquiera
5. Submit

**Resultado**: ✅ Reporte creado en estado ABIERTO

---

**Caso 2: Equipo con reporte ABIERTO (inválido)**:
1. Usar el mismo equipo del Caso 1 (ahora tiene 1 ABIERTO)
2. Intentar crear otro reporte:
   - Ir a `/reportes/crear`
   - Seleccionar el mismo equipo
   - Submit

**Resultado**: ❌ Error "El equipo ya tiene un reporte activo. Debe cerrarse antes de crear uno nuevo."

---

**Caso 3: Equipo con reporte EN_PROCESO (inválido)**:
1. Tomar el reporte del Caso 1 y cambiarlo a EN_PROCESO:
   - Ir a vista del reporte
   - Click "Tomar en Proceso"
2. Intentar crear nuevo reporte para ese equipo

**Resultado**: ❌ Error "El equipo ya tiene un reporte activo"

---

**Caso 4: Equipo con reporte RESUELTO (válido)**:
1. Resolver el reporte del Caso 3:
   - Click "Marcar Resuelto"
2. Crear nuevo reporte para el mismo equipo:
   - Ir a `/reportes/crear`
   - Seleccionar el equipo
   - Submit

**Resultado**: ✅ Reporte creado (reportes RESUELTOS no bloquean)

---

## TD-04: Editar/Eliminar Reporte - Estado actual + acción

**Campo probado**: `Reporte.estado`
**Ubicación código**: `ReporteServiceImpl.java` líneas 123-125, 164-166
**Regla de negocio**: Solo reportes ABIERTOS pueden editarse/eliminarse

### Tabla de decisión:

| Caso | Estado Reporte | Acción Editar | Acción Eliminar | Resultado | Mensaje |
|------|---------------|--------------|-----------------|-----------|---------|
| **1** | ABIERTO | Permitido | Permitido | ✅ ACEPTA | - |
| **2** | EN_PROCESO | Prohibido | Prohibido | ❌ RECHAZA | "Solo se pueden editar reportes en estado ABIERTO" |
| **3** | RESUELTO | Prohibido | Prohibido | ❌ RECHAZA | "Solo se pueden editar reportes en estado ABIERTO" |

### Pasos para ejecutar:

**Caso 1: Reporte ABIERTO (permitido editar/eliminar)**:
1. Crear reporte nuevo (queda en ABIERTO)
2. Ir a `/reportes`
3. **Probar Editar**: Click botón "Editar", hacer cambios, Submit → ✅ ACEPTA
4. **Probar Eliminar**: Click botón "Eliminar", confirmar → ✅ ACEPTA

**Resultado**: ✅ Ambas acciones permitidas

---

**Caso 2: Reporte EN_PROCESO (prohibido)**:
1. Crear reporte y pasarlo a EN_PROCESO:
   - Vista del reporte
   - Click "Tomar en Proceso"
2. Regresar a `/reportes`
3. **Probar Editar**: Click "Editar", hacer cambios, Submit

**Resultado**: ❌ Error "Solo se pueden editar reportes en estado ABIERTO"

4. **Probar Eliminar**: Click "Eliminar"

**Resultado**: ❌ Error "Solo se pueden eliminar reportes en estado ABIERTO"

---

**Caso 3: Reporte RESUELTO (prohibido)**:
1. Crear reporte, pasar a EN_PROCESO, luego RESUELTO
2. Intentar editar

**Resultado**: ❌ Error "Solo se pueden editar reportes en estado ABIERTO"

3. Intentar eliminar

**Resultado**: ❌ Error "Solo se pueden eliminar reportes en estado ABIERTO"

---

## TD-05: Crear Área - Nombre + existencia en BD

**Campo probado**: `AreaCreateDTO.nombre`
**Ubicación código**: `AreaServiceImpl.java` líneas 78-92
**Reglas de negocio**:
- Si no existe: CREA nueva
- Si existe inactiva (ignore case): REACTIVA
- Si existe activa: RECHAZA

### Tabla de decisión:

| Caso | Nombre | Existe Activa | Existe Inactiva | Resultado | Acción |
|------|--------|---------------|----------------|-----------|--------|
| **1** | "Nueva Área TD-05" | No | No | ✅ ACEPTA | CREA nueva |
| **2** | "rrhh" | No | Sí ("RRHH") | ✅ ACEPTA | REACTIVA existente |
| **3** | "Tecnología" | Sí | No | ❌ RECHAZA | "Ya existe un área con el nombre" |

### Pasos para ejecutar:

**Caso 1: Nombre completamente nuevo (crea)**:
1. Ir a `/areas`
2. Click "Nueva Área"
3. Nombre: "Nueva Área TD-05-Caso1"
4. Submit

**Resultado**: ✅ Área creada con activo=true

---

**Caso 2: Nombre igual a inactiva (reactiva)**:
1. Crear área "Test Reactivación TD-05"
2. Eliminarla (soft delete → activo=false)
3. Cambiar filtro a "Inactivos" para confirmar
4. Cambiar filtro a "Activos"
5. Click "Nueva Área"
6. Nombre: "test reactivacion td-05" (minúsculas, ignore case)
7. Submit

**Resultado**: ✅ Área reactivada (misma ID, activo=true)

**Verificación**: Cambiar a "Inactivos" → ya no aparece

---

**Caso 3: Nombre igual a activa (rechaza)**:
1. Click "Nueva Área"
2. Nombre: "Tecnología" (ya existe activa)
3. Submit

**Resultado**: ❌ Error "Ya existe un área con el nombre: Tecnología"

---

## TD-06: Liberar Equipo - Combinaciones asignado + reportes

**Campos probados**: `Equipo.idUsuario`, reportes ABIERTOS
**Ubicación código**: `EquipoAsignacionServiceImpl.java` líneas 105-116
**Reglas de negocio**:
- Solo equipos asignados pueden liberarse
- No se puede liberar si tiene reportes ABIERTOS/EN_PROCESO

### Tabla de decisión:

| Caso | Asignado | Reportes ABIERTOS | Resultado | Mensaje |
|------|----------|-------------------|-----------|---------|
| **1** | Usuario A | 0 | ✅ ACEPTA | Devolución PENDIENTE |
| **2** | null | 0 | ❌ RECHAZA | "El equipo no está asignado" |
| **3** | Usuario A | 1 | ❌ RECHAZA | "tiene reportes abiertos que deben resolverse primero" |

### Pasos para ejecutar:

**Caso 1: Equipo asignado sin reportes (válido)**:
1. Asignar equipo a usuario
2. Ir a `/equipos`
3. Click "Liberar" en el equipo asignado
4. Seleccionar motivo: DEVOLUCION_VOLUNTARIA
5. Submit

**Resultado**: ✅ Devolución marcada como PENDIENTE

**Verificar**: Equipo muestra badge "Devolución Pendiente"

---

**Caso 2: Equipo sin asignar (inválido)**:
1. Seleccionar equipo sin usuario asignado
2. Verificar que botón "Liberar" no esté disponible
3. O intentar forzar la liberación

**Resultado**: ❌ Error "El equipo no está asignado"

---

**Caso 3: Equipo asignado con reporte ABIERTO (inválido)**:
1. Asignar equipo a usuario
2. Crear reporte ABIERTO para ese equipo
3. Intentar liberar el equipo

**Resultado**: ❌ Error "No se puede liberar el equipo: tiene reportes abiertos que deben resolverse primero"

---

## TD-07: Asignar Responsable Área - Estado + esEmpleado + historial

**Campos probados**: `Usuario.estado`, existencia de `Empleado`
**Ubicación código**: `AreaServiceImpl.java` líneas 153-188
**Reglas de negocio**:
- Solo usuarios ACTIVO pueden ser responsables
- Si el usuario es empleado: genera historial ASCENSO_JEFE
- Si el usuario NO es empleado: asigna pero NO genera historial

### Tabla de decisión:

| Caso | Estado Usuario | Es Empleado | Resultado | Genera Historial |
|------|---------------|-------------|-----------|------------------|
| **1** | ACTIVO | true | ✅ ACEPTA | Sí (ASCENSO_JEFE) |
| **2** | ACTIVO | false | ✅ ACEPTA | No |
| **3** | VACACIONES | true | ❌ RECHAZA | No |
| **4** | INACTIVO | false | ❌ RECHAZA | No |

### Pasos para ejecutar:

**Caso 1: Usuario ACTIVO empleado (válido + historial)**:
1. Verificar que existe usuario ACTIVO que es empleado
2. Ir a `/areas`
3. Seleccionar área
4. Click "Gestionar Responsable"
5. Seleccionar el empleado ACTIVO
6. Submit

**Resultado**: ✅ Asignado como responsable

**Verificación historial**:
- Ir a `/usuarios`
- Buscar el empleado
- Click "Ver Historial"
- Confirmar registro ASCENSO_JEFE con cargo "Jefe de Área"

---

**Caso 2: Usuario ACTIVO regular (válido sin historial)**:
1. Crear usuario ACTIVO que NO sea empleado (esEmpleado=false)
2. Asignarlo como responsable de área
3. Submit

**Resultado**: ✅ Asignado como responsable

**Verificación**:
- Usuario no tiene historial (no es empleado)
- Área muestra su nombre como responsable

---

**Caso 3: Usuario VACACIONES empleado (inválido)**:
1. Editar empleado, cambiar estado a VACACIONES
2. Intentar asignarlo como responsable de área

**Resultado**: ❌ Error "El usuario debe estar activo para ser responsable de área"

---

**Caso 4: Usuario INACTIVO regular (inválido)**:
1. Editar usuario regular, cambiar estado a INACTIVO
2. Intentar asignarlo como responsable

**Resultado**: ❌ Error "El usuario debe estar activo para ser responsable de área"

---

## NOTAS FINALES

### Credenciales de prueba:
- **Admin**: `admin2` / `admin`

### Datos semilla disponibles (V14__seed_initial_data.sql):
- **Áreas**: Recursos Humanos, Tecnología, Administración, Ventas
- **Marcas**: HP, Dell, Lenovo, Apple, Samsung, Toyota, Honda, IKEA
- **Ubicaciones**: Oficina Central, Sucursal Norte, Sucursal Sur, Bodega Principal, Centro de Distribución
- **Causas**: Daño Físico (ALTA), Falla Software (MEDIA), Falla Hardware (ALTA), Pérdida (CRITICA), Robo (CRITICA), Desgaste Normal (BAJA)

### Recomendaciones de ejecución:

1. **Orden sugerido**: PE → VL → TD
2. **Limpiar datos**: Usar datos únicos (códigos, nombres) para evitar conflictos
3. **Verificar mensajes**: Los mensajes exactos están documentados en cada prueba
4. **Browser DevTools**: Útil para inspeccionar validaciones frontend vs backend
5. **Generar strings largos**: Usar console JavaScript `"A".repeat(N)`

### Cobertura técnica:

- ✅ **Partición de Equivalencia** (7): Validaciones de tipos de datos y estados
- ✅ **Valores Límite** (7): Bordes de rangos numéricos y de longitud de strings
- ✅ **Tabla de Decisión** (7): Combinaciones de múltiples condiciones de negocio

**TOTAL**: 21 pruebas manuales de caja negra, todas ejecutables y validadas contra el código actual.

### Puntos críticos cubiertos:

- ✅ Validaciones de formularios (DTOs)
- ✅ Reglas de negocio complejas (Services)
- ✅ Estados de entidades (Enums)
- ✅ Combinaciones de condiciones (lógica AND/OR)
- ✅ Flujos de workflow (asignaciones, reportes, áreas)
- ✅ Historial automático (triggers + Java)

---

*Documento generado para curso de Aseguramiento de la Calidad del Software*
*Sistema de Gestión Integral de Equipos Corporativos v1.0.0*