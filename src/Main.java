import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Clase principal que gestiona la interfaz de usuario en consola.
 */
public class Main {

    private static final String RUTA_BBDD = "coches.txt";

    public static void main(String[] args) {
        GestorFichero gestor = new GestorFichero(RUTA_BBDD);
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            System.out.println("\n--- GESTOR DE VEHÍCULOS ---");
            System.out.println("1. Insertar coche en una posición");
            System.out.println("2. Ordenar fichero por matrícula");
            System.out.println("3. Borrar registro por matrícula");
            System.out.println("4. Borrar registro por posición");
            System.out.println("5. Modificar registro por posición (Marca y Modelo)");
            System.out.println("6. Salir");
            System.out.print("Elige una opción: ");

            try {
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Limpiar buffer

                switch (opcion) {
                    case 1:
                        System.out.print("Posición de inserción: ");
                        int posInsert = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Matrícula (max 7 chars): ");
                        String matricula = scanner.nextLine();
                        System.out.print("Marca (max 32 chars): ");
                        String marca = scanner.nextLine();
                        System.out.print("Modelo (max 32 chars): ");
                        String modelo = scanner.nextLine();

                        gestor.insertarEnPosicion(posInsert, matricula, marca, modelo);
                        System.out.println("Coche insertado correctamente.");
                        break;

                    case 2:
                        gestor.ordenarPorMatricula();
                        System.out.println("Fichero ordenado por matrícula.");
                        break;

                    case 3:
                        System.out.print("Matrícula a borrar: ");
                        String matBorrar = scanner.nextLine();
                        if (gestor.borrarPorMatricula(matBorrar)) {
                            System.out.println("Registro borrado con éxito.");
                        } else {
                            System.out.println("Error: Matrícula no encontrada.");
                        }
                        break;

                    case 4:
                        System.out.print("Posición a borrar: ");
                        int posBorrar = scanner.nextInt();
                        if (gestor.borrarPorPosicion(posBorrar)) {
                            System.out.println("Registro borrado con éxito.");
                        } else {
                            System.out.println("Error: Posición inválida.");
                        }
                        break;

                    case 5:
                        System.out.print("Posición a modificar: ");
                        int posMod = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Nueva Marca (max 32 chars): ");
                        String nuevaMarca = scanner.nextLine();
                        System.out.print("Nuevo Modelo (max 32 chars): ");
                        String nuevoModelo = scanner.nextLine();

                        if (gestor.modificarPorPosicion(posMod, nuevaMarca, nuevoModelo)) {
                            System.out.println("Registro modificado con éxito.");
                        } else {
                            System.out.println("Error: Posición inválida o registro borrado.");
                        }
                        break;

                    case 6:
                        salir = true;
                        System.out.println("Saliendo del programa...");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Entrada inválida. Debes introducir un número.");
                scanner.nextLine(); // Limpiar entrada errónea
            } catch (IllegalArgumentException e) {
                System.out.println("Error de validación: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error en acceso al fichero: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }
}