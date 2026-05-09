# Esquema de seguridad basado en roles

Si la aplicación escalara a un proyecto más grande, se implementaría un sistema de control de acceso basado en roles (RBAC — Role-Based Access Control).

## Roles definidos

| Rol | Descripción |
|---|---|
| `ADMIN` | Acceso total: leer, escribir, borrar ficheros y gestionar usuarios |
| `EDITOR` | Puede subir y descargar ficheros |
| `VIEWER` | Solo puede descargar ficheros, nunca modificarlos |
| `GUEST` | Solo puede ver el listado de ficheros disponibles, no descargar |

## Diagrama de permisos

```
Operación          ADMIN   EDITOR   VIEWER   GUEST
─────────────────────────────────────────────────
Listar ficheros      ✓       ✓        ✓        ✓
Descargar fichero    ✓       ✓        ✓        ✗
Subir fichero        ✓       ✓        ✗        ✗
Borrar fichero       ✓       ✗        ✗        ✗
Gestionar usuarios   ✓       ✗        ✗        ✗
```

## Implementación propuesta

### 1. Autenticación en la conexión

Al conectarse, el cliente enviaría credenciales cifradas:

```java
// Cliente envía (cifrado con AES):
{ "user": "jose", "password": "hash_bcrypt" }

// Servidor responde con token de sesión:
{ "token": "abc123...", "role": "EDITOR" }
```

### 2. Clase User y Role

```java
public enum Role { ADMIN, EDITOR, VIEWER, GUEST }

public class User {
    private String username;
    private String passwordHash; // bcrypt, nunca en plano
    private Role role;
}
```

### 3. Comprobación en ClientHandler

```java
// Al inicio del handler, verificar permisos
if (!user.getRole().canDownload()) {
    sendError("Acceso denegado: sin permisos de descarga");
    return;
}
```

### 4. Almacenamiento de usuarios

Los usuarios se guardarían en base de datos con contraseñas hasheadas con **bcrypt** (nunca en texto plano ni con MD5/SHA1 sin salt).

```sql
CREATE TABLE users (
    id       INTEGER PRIMARY KEY,
    username TEXT UNIQUE NOT NULL,
    password TEXT NOT NULL,  -- hash bcrypt
    role     TEXT NOT NULL   -- ADMIN, EDITOR, VIEWER, GUEST
);
```

## Principios de seguridad aplicados

- **Mínimo privilegio**: cada rol solo tiene los permisos estrictamente necesarios
- **Separación de funciones**: los editores no pueden borrar, los viewers no pueden subir
- **Contraseñas hasheadas**: bcrypt con salt, nunca almacenadas en plano
- **Tokens de sesión**: la contraseña solo viaja una vez (en el login), después se usa un token temporal
- **Comunicación cifrada**: todas las operaciones siguen usando AES para proteger el tránsito
