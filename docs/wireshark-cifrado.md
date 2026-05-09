# Análisis del tráfico — Con cifrado AES

## Procedimiento

1. Abrir Wireshark con el mismo filtro: `tcp.port == 5000`
2. Arrancar servidor y cliente con la versión cifrada
3. Solicitar el mismo fichero de antes
4. Analizar los paquetes capturados

## Resultado esperado

En los paquetes capturados ahora se observa:

- El nombre del fichero es **ilegible** — bytes cifrados sin sentido aparente
- El contenido del fichero es **ilegible** — secuencia de bytes aleatorios

### Ejemplo de lo visible en Wireshark

```
Follow TCP Stream → datos:

3F A2 9C 11 D4 07 BB 3E ...   ← nombre del fichero cifrado
8A 21 FF 04 C7 93 12 5D ...   ← contenido del fichero cifrado
```

> **Conclusión**: Un atacante que capture este tráfico solo obtiene datos inservibles. Sin la clave AES, descifrar el contenido es computacionalmente inviable.

---
📸 *Añadir aquí capturas de pantalla de Wireshark mostrando los datos cifrados*
