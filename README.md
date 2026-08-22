# Tienda - API REST con seguridad JWT y MongoDB

Proyecto academico de seguridad informatica: aplicacion web de tienda desarrollada con Spring Boot, que implementa autenticacion y autorizacion basada en tokens JWT (JSON Web Tokens), almacenamiento persistente en MongoDB y buenas practicas de seguridad en aplicaciones backend.

## Descripcion general

La aplicacion expone una API REST para la gestion de usuarios (registro y login) y de productos (CRUD completo). El acceso a los endpoints protegidos se controla mediante tokens JWT con tiempo de expiracion configurable y control de inactividad de sesion. Las operaciones de escritura sobre productos registran el usuario responsable y su direccion IP, permitiendo trazabilidad de las acciones.

## Tecnologias

| Tecnologia | Version | Uso |
|---|---|---|
| Java | 17 | Lenguaje principal |
| Spring Boot | 3.5.x | Framework de aplicacion |
| Spring Security | 3.5.x | Autenticacion y autorizacion |
| Spring Data MongoDB | 3.5.x | Persistencia en MongoDB |
| JJWT | 0.12.6 | Generacion y validacion de tokens JWT |
| dotenv-java | 3.0.0 | Gestion de variables de entorno |
| Bean Validation | 3.5.x | Validacion de datos de entrada |

## Estructura del proyecto

```
src/main/java/com/tienda/
├── config/        # Configuracion de esquema de MongoDB
├── controller/    # Controladores REST (Auth, Productos)
├── dto/           # Objetos de transferencia de datos
├── exception/     # Manejo global de excepciones
├── model/         # Documentos de MongoDB (Usuario, Producto)
├── repository/    # Interfaces de Spring Data
├── security/      # Filtro JWT, servicio JWT, configuracion de Spring Security
├── service/       # Logica de negocio
└── TiendaApplication.java
```

## Requisitos previos

- JDK 17 o superior
- MongoDB 6.0 o superior en ejecucion local
- No es necesario instalar Maven: el proyecto incluye Maven Wrapper (`mvnw`)

## Configuracion

1. Copiar la plantilla de variables de entorno:

   ```
   copy .env.example .env
   ```

2. Definir las variables de entorno (directamente en el sistema o en el IDE):

   | Variable | Descripcion |
   |---|---|
   | `MONGO_URI` | Cadena de conexion a MongoDB. Ejemplo sin autenticacion: `mongodb://localhost:27017/tienda_db`. Con usuario: `mongodb://usuario:clave@localhost:27017/tienda_db?authSource=tienda_db` |
   | `JWT_SECRET` | Clave secreta para firmar los tokens. Se recomienda generarla con `openssl rand -base64 48` |
   | `JWT_EXPIRACION_MINUTOS` | Vigencia del token en minutos (por defecto 60) |
   | `JWT_TIMEOUT_INACTIVIDAD_MINUTOS` | Minutos de inactividad antes de invalidar la sesion (por defecto 30) |
   | `CORS_ORIGINS` | Origenes permitidos para CORS |

   Nota: el archivo `.env` esta excluido del repositorio por seguridad. Nunca subir credenciales reales al repositorio.

## Ejecucion

```
mvnw.cmd spring-boot:run
```

En Linux/macOS:

```
./mvnw spring-boot:run
```

La aplicacion queda disponible en `http://localhost:8080`.

## Endpoints principales

### Autenticacion

| Metodo | Ruta | Descripcion | Seguridad |
|---|---|---|---|
| POST | `/api/auth/registro` | Registra un nuevo usuario y devuelve un token JWT | Publica |
| POST | `/api/auth/login` | Autentica al usuario y devuelve un token JWT | Publica |

### Productos

| Metodo | Ruta | Descripcion | Seguridad |
|---|---|---|---|
| GET | `/api/productos` | Lista todos los productos | Requiere token |
| GET | `/api/productos/{id}` | Consulta un producto por identificador | Requiere token |
| GET | `/api/productos/activos` | Lista solo productos activos | Requiere token |
| GET | `/api/productos/categoria/{cat}` | Filtra productos por categoria | Requiere token |
| GET | `/api/productos/precio?min=&max=` | Filtra productos por rango de precio | Requiere token |
| POST | `/api/productos` | Crea un producto (registra usuario e IP) | Requiere token |
| PUT | `/api/productos/{id}` | Actualiza un producto | Requiere token |
| DELETE | `/api/productos/{id}` | Elimina un producto | Requiere token |

Los endpoints protegidos requieren la cabecera:

```
Authorization: Bearer <token>
```

## Pruebas

Ejecutar la suite de pruebas unitarias y de integracion:

```
mvnw.cmd test
```

Las pruebas cubren controladores, servicios, generacion/validacion de JWT y escenarios de seguridad de la API.

## Scripts auxiliares

Ubicados en la carpeta `scripts/`:

| Script | Descripcion |
|---|---|
| `backup.ps1` | Respaldo de la base de datos MongoDB |
| `restore.ps1` | Restauracion de un respaldo |
| `crear-usuarios.mongosh.js` | Creacion de usuarios de base de datos mediante mongosh |
| `firewall.ps1` | Configuracion de reglas de firewall para el entorno local |
| `mongod.cfg.example` | Plantilla de configuracion del servidor MongoDB |

## Medidas de seguridad implementadas

- Autenticacion stateless con JWT firmado (HS256 mediante JJWT).
- Expiracion de tokens y control de inactividad de sesion.
- Codificacion de contrasenas con BCrypt a traves de Spring Security.
- Validacion de datos de entrada con Bean Validation.
- Manejo global de excepciones que evita filtrar informacion interna.
- Configuracion CORS restringida a origenes definidos.
- Credenciales externas al codigo mediante variables de entorno.
- Registro del usuario e IP en operaciones de escritura para auditoria.

## Autores

Trabajo academico de seguridad informatica - SENA (ADSO).
