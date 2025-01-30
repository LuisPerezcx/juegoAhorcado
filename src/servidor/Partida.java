package servidor;


import java.util.HashSet;
import java.util.Set;

public class Partida {
    private String palabra;
    private StringBuilder palabraOculta;
    private int intentos;
    private static final int MAX_INTENTOS = 6;
    private Set<Character> letrasIntentadas;

    public Partida(String palabra) {
        this.palabra = palabra;
        this.palabraOculta = new StringBuilder("_".repeat(palabra.length()));
        this.intentos = MAX_INTENTOS;
        this.letrasIntentadas = new HashSet<>();
    }

    public void intentarLetra(char letra) {

        if(letrasIntentadas.contains(letra)){
            intentos--;
            return;
        }

        letrasIntentadas.add(letra);

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
