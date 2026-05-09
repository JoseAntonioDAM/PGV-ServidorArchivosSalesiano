# Implementación del cifrado AES

## ¿Por qué AES?

AES (Advanced Encryption Standard) es el estándar de cifrado simétrico más usado en el mundo.
Se eligió frente a RSA porque:

- Es mucho más rápido para cifrar grandes volúmenes de datos (como ficheros)
- RSA está diseñado para cifrar claves pequeñas, no ficheros enteros
- Con 128 bits, la clave tiene `2^128` combinaciones posibles — imposible de romper por fuerza bruta

## Clase CryptoUtils

La clase `CryptoUtils` centraliza toda la lógica criptográfica. Tiene cuatro métodos:

```java
encrypt(byte[] data)        // cifra bytes → devuelve bytes cifrados
decrypt(byte[] data)        // descifra bytes → devuelve bytes originales
encryptString(String text)  // cifra un String → devuelve bytes cifrados
decryptToString(byte[] data)// descifra bytes → devuelve String
```

## Clave compartida

Cliente y servidor usan la misma clave (`MiClaveSecreta16` — exactamente 16 bytes para AES-128).

```
"MiClaveSecreta16"
 M  i  C  l  a  v  e  S  e  c  r  e  t  a  1  6
[4D 69 43 6C 61 76 65 53 65 63 72 65 74 61 31 36]
```

> En un sistema real, esta clave se almacenaría en una variable de entorno o fichero de configuración protegido, nunca en el código fuente.

## Qué se cifra

| Dato | Dirección | ¿Cifrado? |
|---|---|---|
| Nombre del fichero solicitado | Cliente → Servidor | Sí |
| Respuesta del servidor (FOUND/NOT_FOUND) | Servidor → Cliente | Sí |
| Bytes del fichero | Servidor → Cliente | Sí |

## Cómo funciona AES por dentro

1. Los datos se dividen en bloques de 16 bytes
2. Se aplican 10 rondas de transformaciones matemáticas a cada bloque
3. La salida es una secuencia de bytes aparentemente aleatoria
4. Sin la clave, revertir el proceso es computacionalmente inviable

## Modificaciones realizadas

### ClientHandler.java (servidor)
- Se sustituye `ObjectInputStream/ObjectOutputStream` por `DataInputStream/DataOutputStream`
- El nombre del fichero se descifra con `CryptoUtils.decryptToString()` al recibirlo
- Los bytes del fichero se cifran con `CryptoUtils.encrypt()` antes de enviarlos

### FileClient.java (cliente)
- El nombre del fichero se cifra con `CryptoUtils.encryptString()` antes de enviarlo
- Los bytes recibidos se descifran con `CryptoUtils.decrypt()` antes de guardarlos
