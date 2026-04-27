package net.salesianos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileServer {
    private static final int PORT = 5000;
    private static final String FILES_FOLDER = "server-files";

    private ServerSocket serverSocket;
    private boolean running = false;

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            System.out.println("Servidor arrancado en puerto " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                Thread t = new Thread(new ClientHandler(clientSocket, FILES_FOLDER));
                t.start();
            }

        } catch (IOException e) {
            if (running) {
                System.out.println("Error en el servidor: " + e.getMessage());
            }
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
            System.out.println("Servidor detenido.");
        } catch (IOException e) {
            System.out.println("Error al detener el servidor: " + e.getMessage());
        }
    }
}
