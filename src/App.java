
import net.salesianos.client.FileClient;
import net.salesianos.server.FileServer;

import java.util.Scanner;


public class App {

    public static void main(String[] args) {

        System.out.println("=================================");
        System.out.println("   SERVIDOR DE ARCHIVOS - PGV   ");
        System.out.println("=================================");
        System.out.println("¿Qué quieres arrancar?");
        System.out.println("  1 - Servidor");
        System.out.println("  2 - Cliente");
        System.out.print("Opción: ");

        Scanner scanner = new Scanner(System.in);
        String opcion = scanner.nextLine().trim();

        if (opcion.equals("1")) {
            arrancarServidor();
        } else if (opcion.equals("2")) {
            arrancarCliente(scanner);
        } else {
            System.out.println("Opción no válida.");
        }

        scanner.close();
    }

    private static void arrancarServidor() {
        System.out.println("\n[SERVIDOR] Arrancando...");
        System.out.println("[SERVIDOR] Carpeta de ficheros: server-files/");
        System.out.println("[SERVIDOR] Escuchando en puerto 5000...");
        System.out.println("[SERVIDOR] Ctrl+C para detener\n");

        FileServer server = new FileServer(msg -> System.out.println("[SERVIDOR] " + msg));
        server.start();
    }

    private static void arrancarCliente(Scanner scanner) {
        System.out.print("\n[CLIENTE] IP del servidor (Enter para localhost): ");
        String host = scanner.nextLine().trim();
        if (host.isEmpty()) host = "localhost";

        FileClient client = new FileClient(host, 5000, "downloads");

        System.out.println("[CLIENTE] Conectado. Escribe el nombre del fichero o 'exit' para salir.\n");

        while (true) {
            System.out.print("Fichero > ");
            String fileName = scanner.nextLine().trim();

            if (fileName.equalsIgnoreCase("exit")) {
                System.out.println("[CLIENTE] Desconectando...");
                break;
            }

            if (fileName.isEmpty()) continue;

            System.out.println("[CLIENTE] Solicitando: " + fileName);
            var fileInfo = client.requestFile(fileName, progress ->
                System.out.print("\r[CLIENTE] Descargando... " + progress + "%")
            );

            if (fileInfo == null) {
                System.out.println("\n[CLIENTE] Error de conexión.");
            } else if (!fileInfo.isExists()) {
                System.out.println("\n[CLIENTE] El fichero no existe en el servidor.");
            } else {
                System.out.println("\n[CLIENTE] ✓ Descargado en downloads/" + fileInfo.getFileName());
            }
        }
    }
}