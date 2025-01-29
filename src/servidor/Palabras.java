package servidor;

import java.io.*;
import java.util.*;

public class Palabras {
    private static final String ARCHIVO = "src/palabras.txt";
    private static List<String> listaPalabras = new ArrayList<>();

    static {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                listaPalabras.add(linea.trim());
            }
        } catch (IOException e) {
            System.out.println("⚠️ No se pudo cargar el archivo de palabras.");
        }
    }

    public static String obtenerPalabraAleatoria() {
        if (listaPalabras.isEmpty()) return "ERROR";
        return listaPalabras.get(new Random().nextInt(listaPalabras.size()));
    }
}
