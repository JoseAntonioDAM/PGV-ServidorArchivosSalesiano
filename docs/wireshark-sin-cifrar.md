# Análisis del tráfico — Sin cifrado

## Procedimiento

1. Abrir Wireshark y seleccionar la interfaz de red local (`lo` o `Loopback`)
2. Aplicar filtro: `tcp.port == 5000`
3. Arrancar el servidor y el cliente
4. Solicitar un fichero de texto desde el cliente
5. Detener la captura y analizar los paquetes

## Resultado esperado

En los paquetes capturados se puede observar claramente:

- El **nombre del fichero** solicitado por el cliente en texto plano
- El **contenido del fichero** transferido legible en la pestaña "Follow TCP Stream"

### Ejemplo de lo visible en Wireshark

```
Follow TCP Stream → datos:

documento.txt                    ← nombre del fichero visible
Hola, esto es el contenido...   ← contenido del fichero visible
```

> **Conclusión**: Cualquier persona con acceso a la red podría capturar este tráfico con Wireshark e interceptar tanto los nombres de los ficheros como su contenido.

---
📸 *Añadir aquí capturas de pantalla de Wireshark mostrando los datos en claro*
