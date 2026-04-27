package net.salesianos.client;


import java.io.*;
import java.net.Socket;

import net.salesianos.common.*;

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
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
        ) {
            // 1. Enviar nombre del fichero al servidor
            oos.writeObject(fileName);
            oos.flush();

            // 2. Recibir FileInfo del servidor
            FileInfo fileInfo = (FileInfo) ois.readObject();

            if (!fileInfo.isExists()) {
                System.out.println("El fichero no existe en el servidor.");
                return fileInfo;
            }

            // 3. Crear carpeta downloads si no existe
            File folder = new File(downloadsFolder);
            if (!folder.exists()) folder.mkdirs();

            // 4. Recibir bytes y guardar el fichero
            File outputFile = new File(downloadsFolder + File.separator + fileInfo.getFileName());
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                long totalReceived = 0;

                while (totalReceived < fileInfo.getFileSize()) {
                    bytesRead = ois.read(buffer);
                    if (bytesRead == -1) break;
                    fos.write(buffer, 0, bytesRead);
                    totalReceived += bytesRead;

                    // Notificar progreso a la GUI
                    if (listener != null) {
                        int progress = (int) ((totalReceived * 100) / fileInfo.getFileSize());
                        listener.onProgress(progress);
                    }
                }
            }

            System.out.println("Fichero descargado: " + outputFile.getAbsolutePath());
            return fileInfo;

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error en cliente: " + e.getMessage());
            return null;
        }
    }
}