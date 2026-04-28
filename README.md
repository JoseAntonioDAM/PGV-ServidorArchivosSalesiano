#   PGV – Cliente/Servidor de Archivos

Aplicación cliente-servidor desarrollada en Java que permite transferir ficheros a través de la red mediante sockets TCP. El servidor gestiona múltiples clientes de forma simultánea usando hilos.
 
---

##  Escenario elegido

El escenario elegido simula un **servidor de archivos en red**, similar a un FTP básico. Un servidor centraliza una colección de ficheros y los clientes pueden solicitarlos por nombre. El servidor los envía byte a byte a través de un socket TCP.

Este escenario es habitual en entornos reales: repositorios de ficheros compartidos, sistemas de distribución de recursos, o transferencia de documentos entre aplicaciones.
 
---

##  Roles cliente y servidor

### Servidor
- Escucha conexiones entrantes en el puerto **5000**
- Por cada cliente que se conecta, lanza un **hilo independiente** (`ClientHandler`) para atenderlo
- Recibe el nombre del fichero solicitado
- Comprueba si el fichero existe en la carpeta `files/`
- Envía un objeto `FileInfo` con los metadatos del fichero (nombre, tamaño, existencia)
- Si el fichero existe, lo envía byte a byte al cliente
### Cliente
- Se conecta al servidor mediante su IP y el puerto 5000
- Solicita ficheros por nombre
- Recibe el `FileInfo` para saber si el fichero existe y su tamaño
- Recibe los bytes del fichero y lo guarda en la carpeta `downloads/`
- Muestra el progreso de la descarga en consola
- Puede pedir múltiples ficheros o escribir `exit` para desconectarse
---

##  Estructura del proyecto

```
PGV-ClienteServidorArchivos/
├── src/
│   └── net/salesianos/
│       ├── App.java                  # Punto de entrada
│       ├── client/
│       │   ├── FileClient.java       # Lógica del cliente
│       │   └── ProgressListener.java # Interfaz de progreso
│       ├── common/
│       │   └── FileInfo.java         # Modelo compartido
│       └── server/
│           ├── FileServer.java       # Lógica del servidor
│           └── ClientHandler.java    # Hilo por cliente
├── files/                            # Ficheros disponibles en el servidor
├── downloads/                        # Ficheros descargados por el cliente
├── bin/                              # Clases compiladas
└── README.md
```
 
---

##  Clases y librerías utilizadas

| Clase | Paquete | Función |
|---|---|---|
| `ServerSocket` | `java.net` | Abre el puerto y acepta conexiones entrantes |
| `Socket` | `java.net` | Representa la conexión entre cliente y servidor |
| `ObjectOutputStream` | `java.io` | Serializa y envía objetos por el socket |
| `ObjectInputStream` | `java.io` | Recibe y deserializa objetos del socket |
| `FileInputStream` | `java.io` | Lee los bytes del fichero en el servidor |
| `FileOutputStream` | `java.io` | Escribe los bytes recibidos en el cliente |
| `Thread` | `java.lang` | Lanza un hilo por cada cliente conectado |
| `Consumer<String>` | `java.util.function` | Permite inyectar el logger en el servidor |
| `FileInfo` | `net.salesianos.common` | Modelo serializable con metadatos del fichero |
| `ClientHandler` | `net.salesianos.server` | Hilo que gestiona la comunicación con un cliente |
 
---

## ▶️ Cómo ejecutar

### Requisitos
- JDK 17 o superior
- IntelliJ IDEA (recomendado) o cualquier IDE con soporte Java
### Paso 1 — Añadir ficheros al servidor
Coloca los ficheros que quieras compartir en la carpeta `files/` de la raíz del proyecto.

### Paso 2 — Arrancar el servidor
Ejecuta `App.java` y selecciona la opción `1`:
```
¿Qué quieres arrancar?
  1 - Servidor
  2 - Cliente
Opción: 1
```

### Paso 3 — Arrancar el cliente
Crea una segunda configuración de ejecución en IntelliJ con la misma clase principal `net.salesianos.App`, ejecútala y selecciona la opción `2`:
```
Opción: 2
[CLIENTE] IP del servidor (Enter para localhost):
```

### Paso 4 — Solicitar un fichero
```
Fichero > prueba.txt
[CLIENTE] Solicitando: prueba.txt
[CLIENTE] Descargando... 100%
[CLIENTE] ✓ Descargado en downloads/prueba.txt
```

El fichero descargado aparecerá en la carpeta `downloads/`.
 
---

##  Prueba de funcionamiento

### Fichero encontrado
```
[SERVIDOR] Cliente conectado: /127.0.0.1
[SERVIDOR] Fichero solicitado: prueba.txt
[SERVIDOR] Fichero enviado correctamente: prueba.txt
```

### Fichero no encontrado
```
[SERVIDOR] Fichero no encontrado: noexiste.txt
[CLIENTE] El fichero no existe en el servidor.
```

### Cierre de conexión
```
Fichero > exit
[CLIENTE] Desconectando...
```
 
---

## ⚠ Control de excepciones

- Si el servidor no está arrancado, el cliente muestra un error de conexión y no se cuelga
- Si el fichero no existe, el servidor responde con `FileInfo` indicando `exists = false`
- El socket siempre se cierra en el bloque `finally`, garantizando que no queden conexiones abiertas
- Si un cliente se desconecta inesperadamente, el hilo correspondiente termina sin afectar al resto
---

##  Autor

Desarrollado por **José Antonio** — 2º DAM  
Salesianos La Cuesta — Programación de Servicios y Procesos