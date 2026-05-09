package net.salesianos.client;

import java.io.*;
import java.net.Socket;

import net.salesianos.common.CryptoUtils;
import net.salesianos.common.FileInfo;

public class FileClient {

    private String host;
    private int port;
    private String downloadsFolder;

    public FileClient(String host, int port, String downloadsFolder) {
        this.host = host;
        this.port = port;
        this.downloadsFolder = downloadsFolder;
    }

    public FileInfo requestFile(String fileName, ProgressListener listener) {
        try (
                Socket socket = new Socket(host, port);
                DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
                DataInputStream dis = new DataInputStream(socket.getInputStream());
        ) {
            // 1. Enviar nombre del fichero CIFRADO
            byte[] encName = CryptoUtils.encryptString(fileName);
            dos.writeInt(encName.length);
            dos.write(encName);
            dos.flush();

            // 2. Recibir respuesta cifrada del servidor
            int respLength = dis.readInt();
            byte[] encResp = new byte[respLength];
            dis.readFully(encResp);
            String response = CryptoUtils.decryptToString(encResp);

            if (response.equals("NOT_FOUND")) {
                return new FileInfo(fileName, 0, false);
            }

            // Parsear FOUND:nombre:tamaño
            String[] parts = response.split(":");
            String name = parts[1];
            long size = Long.parseLong(parts[2]);

            // 3. Recibir bytes del fichero CIFRADOS y descifrarlos
            int encFileLength = dis.readInt();
            byte[] encFileBytes = new byte[encFileLength];
            dis.readFully(encFileBytes);

            if (listener != null) listener.onProgress(50);

            byte[] fileBytes = CryptoUtils.decrypt(encFileBytes);

            // 4. Guardar el fichero descifrado
            File folder = new File(downloadsFolder);
            if (!folder.exists()) folder.mkdirs();

            File outputFile = new File(downloadsFolder + File.separator + name);
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                fos.write(fileBytes);
            }

            if (listener != null) listener.onProgress(100);

            System.out.println("Fichero descargado y descifrado: " + outputFile.getAbsolutePath());
            return new FileInfo(name, size, true);

        } catch (Exception e) {
            System.out.println("Error en cliente: " + e.getMessage());
            return null;
        }
    }
}