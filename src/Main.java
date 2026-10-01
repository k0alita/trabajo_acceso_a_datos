import java.io.IOException;
import java.util.Scanner;

public class Main {
    public static Scanner sc = new Scanner(System.in);
    public static void main(String[] args) {

        GestorFichero gestor = new GestorFichero("coches.dat");

        boolean salir = false;

        while (!salir) {
            mostrarMenu();
            try {
                int opcion = leerEntero("Elige una opción: ");

                switch (opcion) {
                    case 1 -> cargarCsv(gestor);
                    case 2 -> insertar(gestor);
                    case 3 -> gestor.ordenarPorMatricula();
                    case 4 -> borrar(gestor);
                    case 5 -> modificar(gestor);
                    case 6 -> mostrarTodos(gestor);
                    case 0 -> salir = true;
                    default -> System.out.println(
                            "Opción no válida.");
                }
            } catch (NumberFormatException e) {
                System.out.printf("Debes introducir un numero");
            } catch (IOException e) {
                System.out.printf(e.getMessage());;
            }
        }
    }

    private static void mostrarMenu() {
        System.out.println();
        System.out.println("===== BASE DE DATOS DE COCHES =====");
        System.out.println("1. Cargar fichero CSV");
        System.out.println("2. Insertar coche");
        System.out.println("3. Ordenar por matrícula");
        System.out.println("4. Borrar coche");
        System.out.println("5. Modificar coche");
        System.out.println("6. Mostrar registros");
        System.out.println("0. Salir");
    }

    private static void cargarCsv(GestorFichero gestor) {

    }

    private static void insertar(GestorFichero gestor) throws IOException {
        int posicion = leerEntero("Introduce la pocision");

        System.out.println("Matricula: ");
        String matricula = sc.nextLine();

        System.out.println("Marca: ");
        String marca = sc.nextLine();

        System.out.printf("Modelo: ");
        String modelo = sc.nextLine();

        gestor.insertar(posicion, matricula, marca, modelo);

        System.out.printf("Registro completado");
    }

    private static void borrar(GestorFichero gestor) {

    }

    private static void modificar(GestorFichero gestor) {

    }

    private static void mostrarTodos(GestorFichero gestor) {

    }

    private static int leerEntero(String mensaje) {
        return Integer.parseInt(leerTexto(mensaje));
    }

    private static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return sc.nextLine().trim();
    }
}
