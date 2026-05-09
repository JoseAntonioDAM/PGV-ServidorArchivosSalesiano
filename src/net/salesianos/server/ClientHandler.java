package net.salesianos.server;

import java.io.*;
import java.net.Socket;
import java.util.function.Consumer;

import net.salesianos.common.CryptoUtils;
import net.salesianos.common.FileInfo;

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
                DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream());
                DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
        ) {
            // 1. Leer nombre del fichero CIFRADO
            int encNameLength = dis.readInt();
            byte[] encName = new byte[encNameLength];
            dis.readFully(encName);
            String requestedFile = CryptoUtils.decryptToString(encName);
            System.out.println("Fichero solicitado (descifrado): " + requestedFile);

            File file = new File(filesFolder + File.separator + requestedFile);

            // 2. Enviar si existe o no (cifrado también)
            if (!file.exists() || !file.isFile()) {
                byte[] encResponse = CryptoUtils.encryptString("NOT_FOUND");
                dos.writeInt(encResponse.length);
                dos.write(encResponse);
                dos.flush();
                System.out.println("Fichero no encontrado: " + requestedFile);
                return;
            }

            // 3. Enviar confirmación de que existe
            byte[] encFound = CryptoUtils.encryptString("FOUND:" + file.getName() + ":" + file.length());
            dos.writeInt(encFound.length);
            dos.write(encFound);
            dos.flush();

            // 4. Leer los bytes del fichero, cifrarlos y enviarlos
            byte[] fileBytes = new FileInputStream(file).readAllBytes();
            byte[] encFileBytes = CryptoUtils.encrypt(fileBytes);

            dos.writeInt(encFileBytes.length);
            dos.write(encFileBytes);
            dos.flush();

            System.out.println("Fichero enviado cifrado: " + requestedFile);

        } catch (Exception e) {
            System.out.println("Error con cliente: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.out.println("Error cerrando socket: " + e.getMessage());
            }
        }
    }
}