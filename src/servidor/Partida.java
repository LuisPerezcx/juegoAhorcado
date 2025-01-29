package servidor;

import java.util.*;

public class Partida {
    private String palabra;
    private StringBuilder palabraOculta;
    private int intentos;
    private static final int MAX_INTENTOS = 6;

    public Partida() {
        this.palabra = Palabras.obtenerPalabraAleatoria();
        this.palabraOculta = new StringBuilder("_".repeat(palabra.length()));
        this.intentos = MAX_INTENTOS;
    }

    public void intentarLetra(char letra) {
        boolean acierto = false;
        for (int i = 0; i < palabra.length(); i++) {
            if (palabra.charAt(i) == letra) {
                palabraOculta.setCharAt(i, letra);
                acierto = true;
            }
        }
        if (!acierto) intentos--;
    }

    public boolean juegoTerminado() {
        return intentos == 0 || palabraOculta.toString().equals(palabra);
    }

    public String getPalabraOculta() {
        return palabraOculta.toString();
    }

    public int getIntentosRestantes() {
        return intentos;
    }

    public String getPalabra() {
        return palabra;
    }
}
