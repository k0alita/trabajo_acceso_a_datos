import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if (existeMatricula(matricula)) {
            throw new IllegalArgumentException("Error: La matricula " + matricula + " ya existe");
        }

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
            }

            byte[] nuevoRegistro = construirRegistro(matricula, marca, modelo);
            raf.seek((long) posicion * TAMAÑO_TOTAL);
            raf.write(nuevoRegistro);
        }
    }

    public boolean existeMatricula(String matricula) throws IOException {
        File f = new File(this.rutaFichero);
        if (!f.exists()) return false;

        try(RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "r")) {
            long total = raf.length() / TAMAÑO_TOTAL;
            for (long i = 0;  i < total; i++) {
                byte[] buffer = new byte[TAMAÑO_TOTAL];
                raf.read(buffer);
                if (buffer[0] != 0) {
                    String matriculaLeida = new String(buffer, 0, TAMAÑO_MATRICULA, StandardCharsets.UTF_8).trim();
                    if (matriculaLeida.equals(matricula)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void ordenarPorMatricula() throws IOException {
        List<byte[]> registros = new ArrayList<>();

        try (RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "r")){
            long total = raf.length() / TAMAÑO_TOTAL;
            for (int i = 0; i < total; i++) {
                byte[] buffer = new byte[TAMAÑO_TOTAL];
                raf.read(buffer);
                if (buffer[0] != 0) {
                    registros.add(buffer);
                }
            }
        }

        registros.sort((b1, b2) -> {
            String m1 = new String(b1, 0, TAMAÑO_MATRICULA, StandardCharsets.UTF_8).trim();
            String m2 = new String(b2, 0, TAMAÑO_MATRICULA, StandardCharsets.UTF_8).trim();
            return m1.compareTo(m2);
        });

        try (RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "rws")){
            raf.setLength(0);
            for (byte[] reg : registros) {
                raf.write(reg);
            }

        }
    }

    public boolean borrarPorPosicion(int posicion) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "rws")){
            if ((long) posicion * TAMAÑO_TOTAL >= raf.length()) {
                return false;
            }
            marcarComoBorrado(raf, posicion);
            return true;

        }
    }

    public boolean borrarPorMatricula(String matricula) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "rws")){
            long total = raf.length() / TAMAÑO_TOTAL;
            for (int i = 0; i < total; i++) {
                byte[] buffer = new byte[TAMAÑO_TOTAL];
                raf.read(buffer);
                if (buffer[0] != 0) {
                    String matriculaLeida = new String(buffer, 0, TAMAÑO_MATRICULA, StandardCharsets.UTF_8).trim();
                    if (matriculaLeida.equals(matricula)) {
                        marcarComoBorrado(raf, i);
                        return true;
                    }
                }

            }

        }
        return false;
    }

    private void marcarComoBorrado(RandomAccessFile raf, long posicion) throws IOException {
        raf.seek(posicion * TAMAÑO_TOTAL);
        byte[] bufferVacio = new byte[TAMAÑO_TOTAL];
        Arrays.fill(bufferVacio, (byte) 0);
        raf.write(bufferVacio);
    }


    private byte[] construirRegistro(String matricula, String marca, String modelo) {
        byte[] registro = new byte[TAMAÑO_TOTAL];
        byte[] bMatricula = formatearABytesFijos("Matricula", matricula, TAMAÑO_MATRICULA);
        byte[] bMarca = formatearABytesFijos("Marca", marca, TAMAÑO_MARCA);
        byte[] bModelo = formatearABytesFijos("Modelo", modelo, TAMAÑO_MODELO);

        System.arraycopy(bMatricula, 0, registro, 0, TAMAÑO_MATRICULA);
        System.arraycopy(bMarca, 0, registro, TAMAÑO_MATRICULA, TAMAÑO_MARCA);
        System.arraycopy(bModelo, 0, registro, TAMAÑO_MATRICULA + TAMAÑO_MARCA, TAMAÑO_MODELO);

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


    public boolean modificarPorPosicion(int posicion, String nuevaMarca, String nuevoModelo) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(this.rutaFichero, "rws")){
            long posicionFisica = (long) posicion * TAMAÑO_TOTAL;
            if (posicionFisica >= raf.length()) return false;

            raf.seek(posicionFisica);
            byte[] primerByte = new byte[1];
            raf.read(primerByte);

            if (primerByte[0] == 0) return false;

            raf.seek(posicionFisica + TAMAÑO_MATRICULA);

            byte[] bufferMarca = formatearABytesFijos("Marca", nuevaMarca, TAMAÑO_MARCA);
            byte[] bufferModelo = formatearABytesFijos("Modelo", nuevoModelo, TAMAÑO_MODELO);

            raf.write(bufferMarca);
            raf.write(bufferModelo);
            return true;
        }
    }
}