import java.awt.*;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

class GestorFichero {

    private static final int TAMAÑO_MATRICULA = 7;
    private static final int TAMAÑO_MARCA = 32;
    private static final int TAMAÑO_MODELO = 32;
    private static final int TAMAÑO_TOTAL = TAMAÑO_MATRICULA + TAMAÑO_MARCA + TAMAÑO_MODELO;
    private static final byte BYTE_ESPACIO = (byte) ' ';

    private final String rutaFichero;

    GestorFichero(String rutaFichero) {
        this.rutaFichero = rutaFichero;
    }

    public void insertarEnPosicion(int posicion, String matricula, String marca, String modelo) throws IOException {
        try(RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "rws")) {
            long totalRegistros = raf.length() / TAMAÑO_TOTAL;
            if (posicion > totalRegistros) {
                posicion = (int) totalRegistros;
            }

            byte[] buffer = new byte[TAMAÑO_TOTAL];
            for (long i = totalRegistros - 1; i>= posicion ; i--) {
                raf.seek(i * TAMAÑO_TOTAL);
                raf.read(buffer);
                raf.seek((i + 1) * TAMAÑO_TOTAL);
                raf.write(buffer);

                byte[] nuevoRegistro = construirRegistro(matricula, marca, modelo);
                raf.seek((long) posicion * TAMAÑO_TOTAL);
                raf.write(nuevoRegistro);

            }
        }
    }

    private byte[] construirRegistro(String matricula, String marca, String modelo) {
        byte[] registro = new byte[TAMAÑO_TOTAL];
        byte[] bMatricula = formatearABytesFijos("Matricula", matricula, TAMAÑO_MATRICULA);
        byte[] bMarca = formatearABytesFijos("Marca", marca, TAMAÑO_MARCA);
        byte[] bModelo = formatearABytesFijos("Modelo", marca, TAMAÑO_MODELO);

        return registro;
    }

    private byte[] formatearABytesFijos(String nombreCampo, String texto, int longitudFija) {
        byte[] byteOriginales = texto.getBytes(StandardCharsets.UTF_8);
        if (byteOriginales.length > longitudFija) {
            throw new IllegalArgumentException(
                    "El campo " + nombreCampo + " excede el maximo de " + longitudFija + " bytes"
            );
        }
        byte[] bytesResultantes = new byte[longitudFija];
        Arrays.fill(bytesResultantes, BYTE_ESPACIO);
        System.arraycopy(byteOriginales, 0, bytesResultantes, 0, byteOriginales.length);
        return bytesResultantes;
    }


}