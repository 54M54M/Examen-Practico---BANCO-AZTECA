# Cajero Automático — Ejercicio Práctico Banco Azteca

Simulación fullstack de un cajero automático con dispensación inteligente de billetes y monedas. El sistema calcula automáticamente la combinación óptima de denominaciones para entregar el monto solicitado.

---

## Estructura del Proyecto

```
cajero-azteca/
├── backend/            # Spring Boot 3.2.11 (Java 21)
└── database/           # Oracle SQL
└── frontend/           # Angular 18.2.21
```

---

## Tecnologias

| Capa       | Tecnología                          |
|------------|-------------------------------------|
| Frontend   | Angular, Bootstrap 5, TypeScript |
| Backend    | Spring Boot, Spring Data JPA, Lombok |
| Base de datos | Oracle DB                        |
| Comunicación | REST API (HTTP JSON)              |

---

## Base de Datos (Oracle)

### Tablas

**`DENOMINACIONES`** — Inventario de billetes y monedas disponibles en el cajero.

| Columna    | Tipo           | Descripción                          |
|------------|----------------|--------------------------------------|
| ID         | NUMBER (PK)    | Identificador único (auto-generado)  |
| TIPO       | VARCHAR2(20)   | `'Billete'` o `'Moneda'`             |
| CANTIDAD   | NUMBER         | Unidades disponibles                 |
| VALOR      | NUMBER(10,2)   | Valor de la denominación             |
| ACTIVO     | NUMBER         | `1` activa / `0` inactiva            |

**`TRANSACIONES`** — Registro de cada operación de retiro.

| Columna           | Tipo              | Descripción                        |
|-------------------|-------------------|------------------------------------|
| ID                | NUMBER (PK)       | Identificador único                |
| MONTO_SOLICITADO  | NUMBER(12,2)      | Monto pedido por el usuario        |
| MONTO_ENTEGADO    | NUMBER(12,2)      | Monto efectivamente dispensado     |
| FECHA_HORA        | TIMESTAMP         | Fecha y hora de la transacción     |
| EXITOSO           | NUMBER            | `1` exitosa / `0` fallida          |

**`TRANSACCION_DETALLE`** — Desglose de denominaciones usadas en cada retiro.

| Columna           | Tipo        | Descripción                              |
|-------------------|-------------|------------------------------------------|
| ID                | NUMBER (PK) | Identificador único                      |
| CANTIDAD_USADA    | NUMBER      | Unidades de esa denominación utilizadas  |
| ID_DENOMINACIONES | NUMBER (FK) | Referencia a `DENOMINACIONES`            |
| ID_TRANSACIONES   | NUMBER (FK) | Referencia a `TRANSACIONES`              |

### Inventario inicial

El cajero arranca con **$12,550** distribuidos de la siguiente manera:

| Tipo    | Denominación | Cantidad |
|---------|-------------|----------|
| Billete | $1,000      | 2        |
| Billete | $500        | 5        |
| Billete | $200        | 10       |
| Billete | $100        | 20       |
| Billete | $50         | 30       |
| Billete | $20         | 40       |
| Moneda  | $10         | 50       |
| Moneda  | $5          | 100      |
| Moneda  | $2          | 200      |
| Moneda  | $1          | 300      |
| Moneda  | $0.50       | 100      |

---

## Backend — Spring Boot

### Estructura de paquetes

```
com.cajero/
├── controller/     
│   └── CajeroController.java
├── service/        
│   ├── CajeroService.java
│   └── CajeroServiceImpl.java
├── repository/     
│   ├── DenominacionRepository.java
│   ├── TransaccionRepository.java
│   └── TransaccionDetalleRepository.java
├── modelo/         
│   ├── Denominacion.java
│   ├── Transaccion.java
│   └── TransaccionDetalle.java
└── dto/            
    └── CajeroDTO.java
```

### Endpoints REST

Base URL: `http://localhost:7575/api/cajero`

| Método | Endpoint   | Descripción                                          |
|--------|------------|------------------------------------------------------|
| GET    | `/estado`  | Devuelve el inventario actual de billetes y monedas  |
| GET    | `/total`   | Devuelve el total de dinero disponible en el cajero  |
| POST   | `/retirar` | Procesa una solicitud de retiro                      |

#### `POST /retirar` — Request body

```json
{
  "monto": 500.00
}
```

#### `POST /retirar` — Response exitosa (`200 OK`)

```json
{
  "exitoso": true,
  "mensaje": "Retiro exitoso.",
  "montoSolicitado": 500.0,
  "montoEntregado": 500.0,
  "detalles": [
    {
      "tipo": "Billete",
      "denominacion": 500.0,
      "cantidad": 1,
      "subtotal": 500.0
    }
  ]
}
```

#### `POST /retirar` — Response fallida (`422 Unprocessable Entity`)

```json
{
  "exitoso": false,
  "mensaje": "Fondos insuficientes en el cajero.",
  "montoSolicitado": 99999.0,
  "montoEntregado": 0.0,
  "detalles": []
}
```

### Lógica de dispensación

El servicio recorre las denominaciones de **mayor a menor valor** y toma la mayor cantidad posible de cada una hasta cubrir el monto solicitado. Si al final queda un residuo mayor a `$0.005` que no puede cubrirse con las denominaciones disponibles, la transacción se rechaza con un error descriptivo.

### Casos de error manejados

- Monto igual o menor a cero.
- Monto superior al total disponible en el cajero.
- Monto que no puede formarse exactamente con las denominaciones existentes.

---

## 🌐 Frontend — Angular

### Estructura de archivos relevantes

```
src/app/
├── components/cajero/
│   ├── cajero.component.ts
│   ├── cajero.component.html
│   └── cajero.component.css
├── services/
│   └── cajero.service.ts
├── Models/
│   └── Cajero.model.ts
├── app.routes.ts
└── app.config.ts
```

### Pantallas

**Formulario de retiro**
- Botones de montos rápidos: `$100`, `$200`, `$500`, `$1,000`.
- Campo de monto manual con validación.
- Botón de retiro deshabilitado mientras se procesa la petición.

**Resultado exitoso**
- Muestra el monto entregado y una tabla con el desglose de denominaciones usadas.

**Resultado fallido**
- Muestra el mensaje de error retornado por el backend.
- Permite reintentar sin recargar la página.

**Inventario** *(colapsable)*
- Tabla con el estado actual de cada denominación en el cajero.

### Comunicación con el backend

El frontend usa un proxy de Angular para evitar problemas de CORS en desarrollo. Toda petición a `/api` se redirige a `http://localhost:7575`.

```json
// proxy.conf.json
{
  "/api": {
    "target": "http://localhost:7575",
    "secure": false,
    "changeOrigin": true
  }
}
```

---

## 🚀 Cómo ejecutar el proyecto

### Requisitos previos

- Java 21+
- Node.js 18+ y Angular CLI
- Oracle Database
- Maven

### 1. Base de datos

Ejecutar el script SQL en Oracle:

```sql
-- Crear tablas e insertar datos iniciales
@Ejercicio_Practico_BancoAzteca.sql
```

### 2. Backend

Configurar la conexión a la base de datos en `application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521:XE
spring.datasource.username=TU_USUARIO
spring.datasource.password=TU_PASSWORD
server.port=7575
```

Ejecutar:

```bash
mvn spring-boot:run
```

### 3. Frontend

```bash
npm install
ng serve
```

La aplicación estará disponible en `http://localhost:4200`.

---

## 📌 Notas

- El campo `ACTIVO` en `DENOMINACIONES` permite deshabilitar una denominación sin eliminarla de la base de datos.
- Las transacciones fallidas **no** se registran en la tabla `TRANSACIONES`; solo se persisten los retiros exitosos.
- El monto mínimo de retiro es **$0.50**.