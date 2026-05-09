# Servidor de Archivos PGV — Seguridad
## Aplicación cliente-servidor en Java para transferencia de ficheros con comunicación cifrada mediante AES.

# Ejercicios: 
- [Análisis del tráfico sin cifrar (Wireshark)](docs/wireshark-sin-cifrar.md)
- [Implementación del cifrado AES](docs/cifrado-aes.md)
- [Análisis del tráfico cifrado (Wireshark)](docs/wireshark-cifrado.md)
- [Esquema de seguridad basado en roles](docs/roles.md)

# Tecnologías de seguridad utilizadas

## Algoritmo: AES (Advanced Encryption Standard) — cifrado simétrico de 128 bits
## Clase responsable: CryptoUtils.java
## Datos cifrados: nombre del fichero solicitado + bytes del fichero transferido