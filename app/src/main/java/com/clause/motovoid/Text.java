package com.clause.motovoid;

import java.util.Locale;
import java.util.regex.Pattern;

/** Utilidades de texto (sin nada exclusivo de Android, para que funcione en 2.3). */
final class Text {
    private Text() {}

    /** Minúsculas, sin acentos ni ñ, y con los símbolos matemáticos unificados. */
    static String normalize(String s) {
        String low = s.toLowerCase(Locale.US);
        StringBuilder sb = new StringBuilder(low.length());
        for (int i = 0; i < low.length(); i++) {
            char c = low.charAt(i);
            switch (c) {
                case 'á': case 'à': case 'ä': case 'â': sb.append('a'); break;
                case 'é': case 'è': case 'ë': case 'ê': sb.append('e'); break;
                case 'í': case 'ì': case 'ï': case 'î': sb.append('i'); break;
                case 'ó': case 'ò': case 'ö': case 'ô': sb.append('o'); break;
                case 'ú': case 'ù': case 'ü': case 'û': sb.append('u'); break;
                case 'ñ': sb.append('n'); break;
                case '²': sb.append("^2"); break;
                case '³': sb.append("^3"); break;
                case '×': sb.append('*'); break;
                case '÷': sb.append('/'); break;
                case '−': case '–': case '—': sb.append('-'); break;
                case '¿': case '¡': case '\n': case '\r': case '\t': sb.append(' '); break;
                default: sb.append(c);
            }
        }
        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    /** ¿Aparece alguna de estas palabras/frases (separadas por |) como palabra completa? */
    static boolean has(String t, String alternatives) {
        return Pattern.compile("\\b(?:" + alternatives + ")\\b").matcher(t).find();
    }

    /** Deja solo letras y números (para comparar nombres de teléfonos). */
    static String squash(String t) {
        return t.replaceAll("[^a-z0-9]", "");
    }
}
