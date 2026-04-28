package net.salesianos.server;

import net.salesianos.common.FileInfo;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

public class ClientHandler implements Runnable {

    private Socket clientSocket;
    private String filesFolder;

    public ClientHandler(Socket clientSocket, String filesFolder, Consumer<String> logger) {
        this.clientSocket = clientSocket;
        this.filesFolder = filesFolder;
    }

    @Override
    public void run() {
        System.out.println("Cliente conectado: " + clientSocket.getInetAddress());

        try (
                ObjectOutputStream oos = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(clientSocket.getInputStream());
        ) {
            String requestedFile = (String) ois.readObject();
            System.out.println("Fichero solicitado: " + requestedFile);

            File file = new File(filesFolder + File.separator + requestedFile);

            if (!file.exists() || !file.isFile()) {
                oos.writeObject(new FileInfo(requestedFile, 0, false));
                oos.flush();
                System.out.println("Fichero no encontrado: " + requestedFile);
                return;
            }

            oos.writeObject(new FileInfo(file.getName(), file.length(), true));
            oos.flush();

            // Enviar los bytes del fichero
            try (FileInputStream fis = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    oos.write(buffer, 0, bytesRead);
                }
                oos.flush();
            }

            System.out.println("Fichero enviado correctamente: " + requestedFile);

        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error con cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
                System.out.println("Conexión cerrada: " + clientSocket.getInetAddress());
            } catch (IOException e) {
                System.out.println("Error cerrando socket: " + e.getMessage());
            }
        }
    }
}
