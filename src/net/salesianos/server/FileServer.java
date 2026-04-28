package net.salesianos.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;

public class FileServer {

    private static final int PORT = 5000;
    private static final String FILES_FOLDER = "files";

    private ServerSocket serverSocket;
    private boolean running = false;
    private Consumer<String> logger;

    public FileServer(Consumer<String> logger) {
        this.logger = logger;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(PORT);
            running = true;
            logger.accept("Servidor escuchando en puerto " + PORT);

            while (running) {
                Socket clientSocket = serverSocket.accept();
                logger.accept("Cliente conectado: " + clientSocket.getInetAddress());
                Thread t = new Thread(new ClientHandler(clientSocket, FILES_FOLDER, logger));
                t.start();
            }

        } catch (IOException e) {
            if (running) logger.accept("Error en el servidor: " + e.getMessage());
        }
    }

    public void stop() {
        running = false;
        try {
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            logger.accept("Error al detener: " + e.getMessage());
        }
    }
}