package com.clause.motovoid;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matemáticas hasta 3° de secundaria, explicadas paso a paso:
 * operaciones, porcentajes, ecuaciones de 1er y 2do grado, sistemas 2x2,
 * Pitágoras, trigonometría básica, áreas, volúmenes y estadística sencilla.
 */
final class MathModule {
    private MathModule() {}

    /** No se entendió la operación: el cerebro seguirá con otros temas. */
    static class ParseFail extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    /** Se entendió, pero hay un problema matemático que se explica al usuario. */
    static class MathError extends RuntimeException {
        private static final long serialVersionUID = 1L;
        MathError(String m) { super(m); }
    }

    // ------------------------------------------------------------------
    // Punto de entrada
    // ------------------------------------------------------------------

    /** @param t texto ya normalizado. Devuelve null si no es un tema de matemáticas. */
    static String answer(String t) {
        boolean hasDigit = Pattern.compile("\\d").matcher(t).find();
        if (!hasDigit) {
            if (Text.has(t, "pitagoras|teorema de pitagoras")) {
                return "Teorema de Pitágoras: en un triángulo rectángulo, a² + b² = c², donde a y b son los "
                        + "catetos y c es la hipotenusa (el lado más largo). Si me das dos medidas, calculo la "
                        + "tercera. Por ejemplo: hipotenusa de catetos 3 y 4.";
            }
            return null;
        }
        try {
            String r = stats(t);
            if (r == null) r = shapes(t);
            if (r == null) r = pythagoras(t);
            if (r == null) r = trig(t);
            if (r == null) r = percent(t);
            if (r == null) r = algebra(t);
            return r;
        } catch (ParseFail e) {
            return null;
        } catch (MathError e) {
            return e.getMessage();
        }
    }

    // ------------------------------------------------------------------
    // Formato de números
    // ------------------------------------------------------------------

    static String fmt(double d) {
        if (Double.isNaN(d) || Double.isInfinite(d)) return "no definido";
        if (Math.abs(d) < 1e-12) return "0";
        double r = Math.rint(d);
        if (Math.abs(d - r) < 1e-9 && Math.abs(d) < 1e15) return Long.toString((long) r);
        String s = String.format(Locale.US, "%.4f", d);
        if (s.indexOf('.') >= 0) {
            int end = s.length();
            while (end > 0 && s.charAt(end - 1) == '0') end--;
            if (end > 0 && s.charAt(end - 1) == '.') end--;
            s = s.substring(0, end);
        }
        if (s.equals("-0") || s.length() == 0) s = "0";
        return s;
    }

    private static String par(double d) {
        return d < 0 ? "(" + fmt(d) + ")" : fmt(d);
    }

    private static boolean isInt(double d) {
        return Math.abs(d - Math.rint(d)) < 1e-9;
    }

    private static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long tmp = a % b;
            a = b;
            b = tmp;
        }
        return a == 0 ? 1 : a;
    }

    /** Fracción reducida n/d si ambos son enteros y el resultado no es entero; si no, null. */
    private static String frac(double num, double den) {
        if (!isInt(num) || !isInt(den) || Math.abs(den) < 1e-12) return null;
        long n = Math.round(num);
        long d = Math.round(den);
        long g = gcd(n, d);
        n /= g;
        d /= g;
        if (d < 0) {
            n = -n;
            d = -d;
        }
        if (d == 1) return null;
        return n + "/" + d;
    }

    private static List<Double> nums(String t, boolean allowNegative) {
        List<Double> out = new ArrayList<Double>();
        String re = allowNegative ? "(?<![a-z0-9.])-?\\d+(?:\\.\\d+)?" : "(?<![a-z0-9.])\\d+(?:\\.\\d+)?";
        Matcher m = Pattern.compile(re).matcher(t);
        while (m.find()) {
            try {
                out.add(Double.valueOf(m.group()));
            } catch (NumberFormatException e) {
                // se ignora
            }
        }
        return out;
    }

    // ------------------------------------------------------------------
    // Estadística: promedio, mediana y moda
    // ------------------------------------------------------------------

    private static String stats(String t) {
        boolean mean = Text.has(t, "promedio|media aritmetica|la media");
        boolean median = Text.has(t, "mediana");
        boolean mode = Text.has(t, "moda");
        if (!mean && !median && !mode) return null;
        List<Double> v = nums(t, true);
        if (v.size() < 2) return "Dime los números, por ejemplo: promedio de 8, 9 y 10.";
        StringBuilder sb = new StringBuilder();

        if (mean) {
            double sum = 0;
            StringBuilder plus = new StringBuilder();
            for (int i = 0; i < v.size(); i++) {
                sum += v.get(i);
                if (i > 0) plus.append(" + ");
                plus.append(par(v.get(i)));
            }
            sb.append("Promedio: sumo todos los números y divido entre cuántos son.\n");
            sb.append(plus).append(" = ").append(fmt(sum)).append("\n");
            sb.append("Son ").append(v.size()).append(" números: ").append(fmt(sum)).append(" ÷ ")
                    .append(v.size()).append(" = ").append(fmt(sum / v.size()));
        }

        List<Double> sorted = new ArrayList<Double>(v);
        Collections.sort(sorted);

        if (median) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("Mediana: ordeno los números de menor a mayor y tomo el del centro.\n");
            for (int i = 0; i < sorted.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(fmt(sorted.get(i)));
            }
            sb.append("\n");
            int n = sorted.size();
            if (n % 2 == 1) {
                sb.append("Son ").append(n).append(" números (impar), el del centro es ")
                        .append(fmt(sorted.get(n / 2))).append(".");
            } else {
                double a = sorted.get(n / 2 - 1);
                double b = sorted.get(n / 2);
                sb.append("Son ").append(n).append(" números (par): promedio los dos del centro, (")
                        .append(fmt(a)).append(" + ").append(fmt(b)).append(") ÷ 2 = ").append(fmt((a + b) / 2)).append(".");
            }
        }

        if (mode) {
            if (sb.length() > 0) sb.append("\n\n");
            sb.append("Moda: el número que más se repite.\n");
            int best = 0;
            List<Double> modes = new ArrayList<Double>();
            int i = 0;
            while (i < sorted.size()) {
                int j = i;
                while (j < sorted.size() && Math.abs(sorted.get(j) - sorted.get(i)) < 1e-12) j++;
                int count = j - i;
                if (count > best) {
                    best = count;
                    modes.clear();
                    modes.add(sorted.get(i));
                } else if (count == best) {
                    modes.add(sorted.get(i));
                }
                i = j;
            }
            if (best <= 1) {
                sb.append("Ningún número se repite, así que no hay moda.");
            } else {
                sb.append("Se repite ").append(best).append(" veces: ");
                for (int k = 0; k < modes.size(); k++) {
                    if (k > 0) sb.append(" y ");
                    sb.append(fmt(modes.get(k)));
                }
                sb.append(".");
            }
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Áreas, perímetros y volúmenes
    // ------------------------------------------------------------------

    private static String shapes(String t) {
        boolean area = Text.has(t, "area|areas");
        boolean per = Text.has(t, "perimetro|circunferencia");
        boolean vol = Text.has(t, "volumen");
        if (!area && !per && !vol) return null;

        boolean circle = Text.has(t, "circulo|circunferencia|radio|diametro");
        boolean square = Text.has(t, "cuadrado|lado");
        boolean rect = Text.has(t, "rectangulo");
        boolean tri = Text.has(t, "triangulo");
        boolean cube = Text.has(t, "cubo");
        boolean sphere = Text.has(t, "esfera");
        boolean cyl = Text.has(t, "cilindro");
        if (!circle && !square && !rect && !tri && !cube && !sphere && !cyl) return null;

        List<Double> v = nums(t, false);
        StringBuilder sb = new StringBuilder();

        if (vol) {
            if (cube) {
                if (v.size() < 1) return "Dime cuánto mide el lado del cubo.";
                double l = v.get(0);
                return "Volumen del cubo = lado³\n" + par(l) + "³ = " + fmt(l * l * l);
            }
            if (sphere) {
                if (v.size() < 1) return "Dime cuánto mide el radio de la esfera.";
                double r = v.get(0);
                if (Text.has(t, "diametro")) r = r / 2;
                return "Volumen de la esfera = 4/3 × pi × radio³\nRadio = " + fmt(r) + "\n4/3 × 3.1416 × "
                        + fmt(r) + "³ = " + fmt(4.0 / 3.0 * Math.PI * r * r * r) + " (aprox.)";
            }
            if (cyl) {
                if (v.size() < 2) return "Dime el radio y la altura del cilindro.";
                double r = v.get(0);
                double h = v.get(1);
                return "Volumen del cilindro = pi × radio² × altura\n3.1416 × " + fmt(r) + "² × " + fmt(h) + " = "
                        + fmt(Math.PI * r * r * h) + " (aprox.)";
            }
            return null;
        }

        if (tri && !circle) {
            if (area) {
                if (v.size() < 2) return "Para el área de un triángulo dime la base y la altura.";
                double b = v.get(0);
                double h = v.get(1);
                sb.append("Área del triángulo = base × altura ÷ 2\n");
                sb.append(fmt(b)).append(" × ").append(fmt(h)).append(" ÷ 2 = ").append(fmt(b * h / 2));
            }
            if (per) {
                if (sb.length() > 0) sb.append("\n\n");
                if (v.size() < 3) {
                    sb.append("Para el perímetro del triángulo dime sus tres lados.");
                } else {
                    sb.append("Perímetro = suma de los tres lados\n");
                    sb.append(fmt(v.get(0))).append(" + ").append(fmt(v.get(1))).append(" + ").append(fmt(v.get(2)))
                            .append(" = ").append(fmt(v.get(0) + v.get(1) + v.get(2)));
                }
            }
            return sb.length() == 0 ? null : sb.toString();
        }

        if (rect && !circle) {
            if (v.size() < 2) return "Para el rectángulo dime la base y la altura.";
            double b = v.get(0);
            double h = v.get(1);
            if (area) {
                sb.append("Área del rectángulo = base × altura\n");
                sb.append(fmt(b)).append(" × ").append(fmt(h)).append(" = ").append(fmt(b * h));
            }
            if (per) {
                if (sb.length() > 0) sb.append("\n\n");
                sb.append("Perímetro = 2 × (base + altura)\n");
                sb.append("2 × (").append(fmt(b)).append(" + ").append(fmt(h)).append(") = ").append(fmt(2 * (b + h)));
            }
            return sb.length() == 0 ? null : sb.toString();
        }

        if (circle) {
            if (v.size() < 1) return "Dime el radio (o el diámetro) del círculo.";
            double r = v.get(0);
            if (Text.has(t, "diametro") && !Text.has(t, "radio")) {
                sb.append("El radio es la mitad del diámetro: ").append(fmt(r)).append(" ÷ 2 = ").append(fmt(r / 2)).append("\n\n");
                r = r / 2;
            }
            boolean wantArea = area;
            boolean wantPer = per;
            if (!wantArea && !wantPer) wantArea = true;
            if (wantArea) {
                sb.append("Área del círculo = pi × radio²\n");
                sb.append("3.1416 × ").append(fmt(r)).append("² = ").append(fmt(Math.PI * r * r)).append(" (aprox.)");
            }
            if (wantPer) {
                if (wantArea) sb.append("\n\n");
                sb.append("Perímetro (circunferencia) = 2 × pi × radio\n");
                sb.append("2 × 3.1416 × ").append(fmt(r)).append(" = ").append(fmt(2 * Math.PI * r)).append(" (aprox.)");
            }
            return sb.toString();
        }

        if (square) {
            if (v.size() < 1) return "Dime cuánto mide el lado del cuadrado.";
            double l = v.get(0);
            if (area) {
                sb.append("Área del cuadrado = lado × lado\n");
                sb.append(fmt(l)).append(" × ").append(fmt(l)).append(" = ").append(fmt(l * l));
            }
            if (per) {
                if (sb.length() > 0) sb.append("\n\n");
                sb.append("Perímetro = 4 × lado\n");
                sb.append("4 × ").append(fmt(l)).append(" = ").append(fmt(4 * l));
            }
            return sb.length() == 0 ? null : sb.toString();
        }
        return null;
    }

    // ------------------------------------------------------------------
    // Pitágoras
    // ------------------------------------------------------------------

    private static String pythagoras(String t) {
        if (!Text.has(t, "hipotenusa|cateto|catetos|pitagoras|triangulo rectangulo")) return null;
        List<Double> v = nums(t, false);
        if (v.size() < 2) {
            return "Teorema de Pitágoras: en un triángulo rectángulo, a² + b² = c², donde a y b son los catetos "
                    + "y c es la hipotenusa. Dame dos medidas y calculo la tercera. "
                    + "Por ejemplo: hipotenusa de catetos 3 y 4.";
        }
        if (Text.has(t, "cateto|catetos")) {
            Matcher mh = Pattern.compile("hipotenusa\\s*(?:mide|es|=|vale|de)?\\s*(\\d+(?:\\.\\d+)?)").matcher(t);
            if (mh.find()) {
                double h = Double.parseDouble(mh.group(1));
                double other = -1;
                boolean skipped = false;
                for (int i = 0; i < v.size(); i++) {
                    if (!skipped && Math.abs(v.get(i) - h) < 1e-12) {
                        skipped = true;
                        continue;
                    }
                    other = v.get(i);
                    break;
                }
                if (other >= 0) {
                    if (h <= other) throw new MathError("La hipotenusa es siempre el lado más largo, así que tiene que ser mayor que el cateto.");
                    double c = Math.sqrt(h * h - other * other);
                    return "Busco el otro cateto con Pitágoras: cateto² = hipotenusa² - cateto²\n"
                            + "cateto² = " + fmt(h) + "² - " + fmt(other) + "² = " + fmt(h * h) + " - " + fmt(other * other)
                            + " = " + fmt(h * h - other * other) + "\n"
                            + "cateto = raíz de " + fmt(h * h - other * other) + " = " + fmt(c)
                            + (isInt(c) ? "" : " (aprox.)");
                }
            }
        }
        double a = v.get(0);
        double b = v.get(1);
        double sum = a * a + b * b;
        double c = Math.sqrt(sum);
        return "Uso el teorema de Pitágoras: hipotenusa² = cateto² + cateto²\n"
                + "hipotenusa² = " + fmt(a) + "² + " + fmt(b) + "² = " + fmt(a * a) + " + " + fmt(b * b) + " = " + fmt(sum) + "\n"
                + "hipotenusa = raíz de " + fmt(sum) + " = " + fmt(c) + (isInt(c) ? "" : " (aprox.)");
    }

    // ------------------------------------------------------------------
    // Trigonometría básica (ángulos en grados)
    // ------------------------------------------------------------------

    private static String trig(String t) {
        Matcher m = Pattern.compile("\\b(seno|sen|coseno|cos|tangente|tan)\\b\\s*(?:de\\s*)?(\\d+(?:\\.\\d+)?)").matcher(t);
        if (!m.find()) return null;
        String f = m.group(1);
        double deg = Double.parseDouble(m.group(2));
        double rad = Math.toRadians(deg);
        String name;
        String def;
        double res;
        if (f.startsWith("sen")) {
            name = "seno";
            def = "cateto opuesto ÷ hipotenusa";
            res = Math.sin(rad);
        } else if (f.startsWith("cos")) {
            name = "coseno";
            def = "cateto adyacente ÷ hipotenusa";
            res = Math.cos(rad);
        } else {
            name = "tangente";
            def = "cateto opuesto ÷ cateto adyacente";
            double mod = deg % 180;
            if (Math.abs(mod - 90) < 1e-9) {
                return "La tangente de " + fmt(deg) + "° no existe (el cateto adyacente vale 0 y no se puede dividir entre 0).";
            }
            res = Math.tan(rad);
        }
        return "En un triángulo rectángulo, " + name + " = " + def + ".\n"
                + name + " de " + fmt(deg) + "° = " + fmt(res) + (isInt(res) ? "" : " (aprox.)")
                + "\n(Los ángulos se miden en grados.)";
    }

    // ------------------------------------------------------------------
    // Porcentajes
    // ------------------------------------------------------------------

    private static String percent(String t) {
        Matcher m = Pattern.compile("(\\d+(?:\\.\\d+)?)\\s*(?:%|por ciento|porciento)\\s*de\\s*(\\d+(?:\\.\\d+)?)").matcher(t);
        if (!m.find()) return null;
        double p = Double.parseDouble(m.group(1));
        double n = Double.parseDouble(m.group(2));
        double res = n * p / 100;
        return "El " + fmt(p) + "% de " + fmt(n) + ": multiplico el número por el porcentaje y divido entre 100.\n"
                + fmt(n) + " × " + fmt(p) + " ÷ 100 = " + fmt(n * p) + " ÷ 100 = " + fmt(res);
    }

    // ------------------------------------------------------------------
    // Álgebra y aritmética
    // ------------------------------------------------------------------

    /** Convierte la frase en una expresión matemática limpia, o null si no parece una. */
    private static String clean(String t) {
        boolean eq = t.indexOf('=') >= 0;
        String s = " " + t + " ";
        s = s.replace(" por favor ", " ");
        if (!eq) s = s.replaceAll("(\\d),(?=\\d)", "$1.");
        s = s.replaceAll("\\bmultiplicado por\\b", " * ");
        s = s.replaceAll("\\bdividido (?:entre|por)\\b", " / ");
        s = s.replaceAll("\\bdividido\\b", " / ");
        s = s.replaceAll("\\bmas\\b", " + ");
        s = s.replaceAll("\\bmenos\\b", " - ");
        s = s.replaceAll("\\bpor\\b", " * ");
        s = s.replaceAll("\\bentre\\b", " / ");
        s = s.replaceAll("\\belevado a la\\b|\\belevado a\\b", " ^ ");
        s = s.replaceAll("\\bal cuadrado\\b", " ^2 ");
        s = s.replaceAll("\\bal cubo\\b", " ^3 ");
        s = s.replaceAll("\\braiz cuadrada de\\b|\\braiz de\\b|\\braiz\\b", " sqrt ");
        s = s.replaceAll("(?<=\\d)\\s*x\\s*(?=\\d)", " * ");

        Matcher wm = Pattern.compile("\\b[a-z]+\\b").matcher(s);
        StringBuffer out = new StringBuffer();
        int deleted = 0;
        while (wm.find()) {
            String w = wm.group();
            if (w.equals("sqrt") || w.equals("x") || w.equals("y")) {
                wm.appendReplacement(out, w);
            } else {
                deleted++;
                wm.appendReplacement(out, " ");
            }
        }
        wm.appendTail(out);
        if (deleted > 8) return null;

        s = out.toString();
        s = s.replaceAll("[^0-9a-z.+\\-*/^()=,; ]", " ");
        s = s.replaceAll("\\s+", " ").trim();
        s = s.replaceAll("[\\s+\\-*/^=,;]+$", "");
        s = s.replaceAll("^[\\s*/^=,;]+", "");
        if (!Pattern.compile("\\d").matcher(s).find()) return null;
        return s;
    }

    private static String algebra(String t) {
        String s = clean(t);
        if (s == null) return null;
        if (s.indexOf('=') >= 0) return equations(s);
        return arithmetic(s);
    }

    // ---------------- Aritmética con pasos ----------------

    private static String arithmetic(String s) {
        s = s.replaceAll("\\by\\b", " ").replaceAll("\\s+", " ").trim();
        if (s.indexOf('x') >= 0) {
            throw new MathError("Eso parece una expresión con x. Si quieres resolverla, escríbela como ecuación "
                    + "con un signo =, por ejemplo: 2x + 6 = 14");
        }
        boolean hasOp = s.matches(".*[+\\-*/^].*") || s.contains("sqrt");
        if (!hasOp) return null;
        Arith a = new Arith(s);
        double result = a.parseAll();
        if (Double.isNaN(result) || Double.isInfinite(result)) {
            throw new MathError("Ese resultado no se puede calcular (es demasiado grande o no existe).");
        }
        if (a.steps.isEmpty()) return null;
        String shown = s.replace("*", " × ").replace("/", " ÷ ").replaceAll("\\s+", " ").trim();
        StringBuilder sb = new StringBuilder();
        if (a.steps.size() == 1) {
            sb.append(a.steps.get(0));
            return sb.toString();
        }
        sb.append("La cuenta es: ").append(shown).append("\n");
        sb.append("La resuelvo respetando el orden: paréntesis, potencias y raíces, luego multiplicaciones y divisiones, y al final sumas y restas.\n\n");
        for (int i = 0; i < a.steps.size(); i++) {
            sb.append("Paso ").append(i + 1).append(": ").append(a.steps.get(i)).append("\n");
        }
        sb.append("\nResultado: ").append(fmt(result));
        return sb.toString();
    }

    private static class Arith {
        final String s;
        int p = 0;
        final List<String> steps = new ArrayList<String>();

        Arith(String s) { this.s = s; }

        double parseAll() {
            double r = expr();
            skipWs();
            if (p != s.length()) throw new ParseFail();
            return r;
        }

        void skipWs() {
            while (p < s.length() && s.charAt(p) == ' ') p++;
        }

        char peek() {
            skipWs();
            return p < s.length() ? s.charAt(p) : '\0';
        }

        boolean peekSqrt() {
            skipWs();
            return s.startsWith("sqrt", p);
        }

        double expr() {
            double r = term();
            while (true) {
                char ch = peek();
                if (ch == '+' || ch == '-') {
                    p++;
                    double b = term();
                    double res = ch == '+' ? r + b : r - b;
                    steps.add(par(r) + " " + ch + " " + par(b) + " = " + fmt(res));
                    r = res;
                } else {
                    break;
                }
            }
            return r;
        }

        double term() {
            double r = unary();
            while (true) {
                char ch = peek();
                if (ch == '*' || ch == '/') {
                    p++;
                    double b = unary();
                    if (ch == '/' && b == 0) throw new MathError("No se puede dividir entre cero.");
                    double res = ch == '*' ? r * b : r / b;
                    steps.add(par(r) + (ch == '*' ? " × " : " ÷ ") + par(b) + " = " + fmt(res));
                    r = res;
                } else if (ch == '(' || peekSqrt()) {
                    double b = unary();
                    double res = r * b;
                    steps.add(par(r) + " × " + par(b) + " = " + fmt(res));
                    r = res;
                } else {
                    break;
                }
            }
            return r;
        }

        double unary() {
            char ch = peek();
            if (ch == '-') {
                p++;
                return -unary();
            }
            if (ch == '+') {
                p++;
                return unary();
            }
            return power();
        }

        double power() {
            double base = primary();
            if (peek() == '^') {
                p++;
                double e = unary();
                double res = Math.pow(base, e);
                if (Double.isNaN(res) || Double.isInfinite(res)) {
                    throw new MathError("Ese resultado no se puede calcular (es demasiado grande o no existe).");
                }
                String how;
                if (e == 2) how = par(base) + " al cuadrado";
                else if (e == 3) how = par(base) + " al cubo";
                else how = par(base) + " elevado a " + par(e);
                steps.add(how + " = " + fmt(res));
                return res;
            }
            return base;
        }

        double primary() {
            char ch = peek();
            if (ch == '(') {
                p++;
                double v = expr();
                if (peek() != ')') throw new ParseFail();
                p++;
                return v;
            }
            if (peekSqrt()) {
                p += 4;
                double v = unary();
                if (v < 0) throw new MathError("No existe la raíz cuadrada real de un número negativo.");
                double res = Math.sqrt(v);
                steps.add("raíz cuadrada de " + fmt(v) + " = " + fmt(res) + (isInt(res) ? "" : " (aprox.)"));
                return res;
            }
            if (Character.isDigit(ch) || ch == '.') {
                int st = p;
                while (p < s.length() && (Character.isDigit(s.charAt(p)) || s.charAt(p) == '.')) p++;
                try {
                    return Double.parseDouble(s.substring(st, p));
                } catch (NumberFormatException e) {
                    throw new ParseFail();
                }
            }
            throw new ParseFail();
        }
    }

    // ---------------- Polinomios en x e y (grado máximo 2) ----------------

    private static class Poly {
        final double[][] c = new double[3][3]; // c[i][j] = coeficiente de x^i y^j

        static Poly num(double v) {
            Poly p = new Poly();
            p.c[0][0] = v;
            return p;
        }

        static Poly var(int i, int j) {
            Poly p = new Poly();
            p.c[i][j] = 1;
            return p;
        }

        Poly add(Poly o, double sign) {
            Poly r = new Poly();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) r.c[i][j] = c[i][j] + sign * o.c[i][j];
            }
            return r;
        }

        Poly scale(double k) {
            Poly r = new Poly();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) r.c[i][j] = c[i][j] * k;
            }
            return r;
        }

        Poly mul(Poly o) {
            Poly r = new Poly();
            for (int i1 = 0; i1 < 3; i1++) {
                for (int j1 = 0; j1 < 3; j1++) {
                    if (Math.abs(c[i1][j1]) < 1e-15) continue;
                    for (int i2 = 0; i2 < 3; i2++) {
                        for (int j2 = 0; j2 < 3; j2++) {
                            if (Math.abs(o.c[i2][j2]) < 1e-15) continue;
                            int i = i1 + i2;
                            int j = j1 + j2;
                            if (i + j > 2) throw new ParseFail();
                            r.c[i][j] += c[i1][j1] * o.c[i2][j2];
                        }
                    }
                }
            }
            return r;
        }

        boolean isConst() {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if ((i != 0 || j != 0) && Math.abs(c[i][j]) > 1e-15) return false;
                }
            }
            return true;
        }

        boolean usesX() {
            for (int i = 1; i < 3; i++) {
                for (int j = 0; j < 3; j++) if (Math.abs(c[i][j]) > 1e-15) return true;
            }
            return false;
        }

        boolean usesY() {
            for (int i = 0; i < 3; i++) {
                for (int j = 1; j < 3; j++) if (Math.abs(c[i][j]) > 1e-15) return true;
            }
            return false;
        }

        Poly swap() {
            Poly r = new Poly();
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) r.c[j][i] = c[i][j];
            }
            return r;
        }
    }

    private static class PolyParser {
        final String s;
        int p = 0;

        PolyParser(String s) { this.s = s; }

        Poly parseAll() {
            Poly r = expr();
            skipWs();
            if (p != s.length()) throw new ParseFail();
            return r;
        }

        void skipWs() {
            while (p < s.length() && s.charAt(p) == ' ') p++;
        }

        char peek() {
            skipWs();
            return p < s.length() ? s.charAt(p) : '\0';
        }

        Poly expr() {
            Poly r = term();
            while (true) {
                char ch = peek();
                if (ch == '+') {
                    p++;
                    r = r.add(term(), 1);
                } else if (ch == '-') {
                    p++;
                    r = r.add(term(), -1);
                } else {
                    break;
                }
            }
            return r;
        }

        Poly term() {
            Poly r = unary();
            while (true) {
                char ch = peek();
                if (ch == '*') {
                    p++;
                    r = r.mul(unary());
                } else if (ch == '/') {
                    p++;
                    Poly d = unary();
                    if (!d.isConst()) throw new ParseFail();
                    if (Math.abs(d.c[0][0]) < 1e-15) throw new MathError("No se puede dividir entre cero.");
                    r = r.scale(1.0 / d.c[0][0]);
                } else if (ch == 'x' || ch == 'y' || ch == '(') {
                    r = r.mul(unary());
                } else {
                    break;
                }
            }
            return r;
        }

        Poly unary() {
            char ch = peek();
            if (ch == '-') {
                p++;
                return unary().scale(-1);
            }
            if (ch == '+') {
                p++;
                return unary();
            }
            return power();
        }

        Poly power() {
            Poly base = primary();
            if (peek() == '^') {
                p++;
                Poly e = unary();
                if (!e.isConst()) throw new ParseFail();
                double ev = e.c[0][0];
                if (!isInt(ev) || ev < 0 || ev > 2) throw new ParseFail();
                int n = (int) Math.round(ev);
                Poly r = Poly.num(1);
                for (int i = 0; i < n; i++) r = r.mul(base);
                return r;
            }
            return base;
        }

        Poly primary() {
            char ch = peek();
            if (ch == '(') {
                p++;
                Poly v = expr();
                if (peek() != ')') throw new ParseFail();
                p++;
                return v;
            }
            if (ch == 'x') {
                p++;
                return Poly.var(1, 0);
            }
            if (ch == 'y') {
                p++;
                return Poly.var(0, 1);
            }
            if (Character.isDigit(ch) || ch == '.') {
                int st = p;
                while (p < s.length() && (Character.isDigit(s.charAt(p)) || s.charAt(p) == '.')) p++;
                try {
                    return Poly.num(Double.parseDouble(s.substring(st, p)));
                } catch (NumberFormatException e) {
                    throw new ParseFail();
                }
            }
            throw new ParseFail();
        }
    }

    // ---------------- Ecuaciones ----------------

    private static String equations(String s) {
        String[] parts = s.split("[;,]");
        List<String> eqs = new ArrayList<String>();
        for (int i = 0; i < parts.length; i++) {
            String p = parts[i].trim();
            if (p.length() > 0) eqs.add(p);
        }
        if (eqs.size() == 1) return solveOne(eqs.get(0));
        if (eqs.size() == 2) return solveSystem(eqs.get(0), eqs.get(1));
        throw new MathError("Por ahora resuelvo una ecuación o un sistema de dos ecuaciones.");
    }

    private static void appendTerm(StringBuilder sb, double c, String label) {
        if (Math.abs(c) < 1e-12) return;
        boolean neg = c < 0;
        double a = Math.abs(c);
        if (sb.length() == 0) {
            if (neg) sb.append("-");
        } else {
            sb.append(neg ? " - " : " + ");
        }
        if (label.length() == 0) {
            sb.append(fmt(a));
        } else {
            if (Math.abs(a - 1) > 1e-12) sb.append(fmt(a));
            sb.append(label);
        }
    }

    private static String polyStr(double a2, double a1, double a0, String v) {
        StringBuilder sb = new StringBuilder();
        appendTerm(sb, a2, v + "²");
        appendTerm(sb, a1, v);
        appendTerm(sb, a0, "");
        return sb.length() == 0 ? "0" : sb.toString();
    }

    private static String lin2Str(double a, double b) {
        StringBuilder sb = new StringBuilder();
        appendTerm(sb, a, "x");
        appendTerm(sb, b, "y");
        return sb.length() == 0 ? "0" : sb.toString();
    }

    private static String solveOne(String eq) {
        int i = eq.indexOf('=');
        if (i != eq.lastIndexOf('=')) throw new ParseFail();
        String ls = eq.substring(0, i).trim();
        String rs = eq.substring(i + 1).trim();
        if (ls.length() == 0 || rs.length() == 0) throw new ParseFail();
        Poly L = new PolyParser(ls).parseAll();
        Poly R = new PolyParser(rs).parseAll();
        if (Math.abs(L.c[1][1]) > 1e-15 || Math.abs(R.c[1][1]) > 1e-15) throw new ParseFail();
        boolean usesX = L.usesX() || R.usesX();
        boolean usesY = L.usesY() || R.usesY();
        if (usesX && usesY) {
            return "Esa ecuación tiene dos incógnitas (x e y), así que necesito dos ecuaciones. "
                    + "Escríbelas separadas por punto y coma, por ejemplo: x + y = 5; x - y = 1";
        }
        if (!usesX && !usesY) {
            double a = L.c[0][0];
            double b = R.c[0][0];
            if (Math.abs(a - b) < 1e-9) {
                return "Comparo los dos lados: " + fmt(a) + " y " + fmt(b) + ". Sí es verdad, la igualdad se cumple.";
            }
            return "Comparo los dos lados: " + fmt(a) + " y " + fmt(b) + ". No son iguales, así que la igualdad no es verdadera.";
        }
        String v = "x";
        if (!usesX) {
            L = L.swap();
            R = R.swap();
            v = "y";
        }
        double qa = L.c[2][0] - R.c[2][0];
        if (Math.abs(qa) > 1e-12) return quadratic(L, R, v);
        return linear(L, R, v);
    }

    private static String linear(Poly L, Poly R, String v) {
        double aL = L.c[1][0];
        double bL = L.c[0][0];
        double aR = R.c[1][0];
        double bR = R.c[0][0];
        double A = aL - aR;
        double B = bR - bL;
        StringBuilder sb = new StringBuilder();
        sb.append("Es una ecuación de primer grado. La escribo ya desarrollada:\n");
        sb.append(polyStr(L.c[2][0], aL, bL, v)).append(" = ").append(polyStr(R.c[2][0], aR, bR, v)).append("\n");
        if (Math.abs(L.c[2][0]) > 1e-12) {
            sb.append("Los términos con ").append(v).append("² son iguales en los dos lados, así que se cancelan.\n");
        }
        sb.append("\n");
        int n = 1;
        if (Math.abs(aR) > 1e-12 || Math.abs(bL) > 1e-12) {
            sb.append("Paso ").append(n++).append(": dejo las ").append(v).append(" a la izquierda y los números a la derecha "
                    + "(lo que cambia de lado cambia de signo):\n");
            sb.append(polyStr(0, A, 0, v)).append(" = ").append(fmt(B)).append("\n\n");
        }
        if (Math.abs(A) < 1e-12) {
            if (Math.abs(B) < 1e-12) {
                sb.append("Queda 0 = 0, que siempre es verdad: tiene infinitas soluciones (sirve cualquier número).");
            } else {
                sb.append("Queda 0 = ").append(fmt(B)).append(", que nunca es verdad: la ecuación no tiene solución.");
            }
            return sb.toString();
        }
        double r = B / A;
        if (Math.abs(A - 1) > 1e-12) {
            sb.append("Paso ").append(n).append(": divido los dos lados entre ").append(par(A)).append(":\n");
            sb.append(v).append(" = ").append(fmt(B)).append(" ÷ ").append(par(A)).append(" = ").append(fmt(r));
            String fr = frac(B, A);
            if (fr != null) sb.append("  (como fracción: ").append(fr).append(")");
            sb.append("\n\n");
        }
        double lhs = L.c[2][0] * r * r + aL * r + bL;
        double rhs = R.c[2][0] * r * r + aR * r + bR;
        sb.append("Compruebo con ").append(v).append(" = ").append(fmt(r)).append(": izquierda = ").append(fmt(lhs))
                .append(", derecha = ").append(fmt(rhs)).append(Math.abs(lhs - rhs) < 1e-6 ? ". ¡Coincide!" : ".").append("\n");
        sb.append("Resultado: ").append(v).append(" = ").append(fmt(r));
        return sb.toString();
    }

    private static String quadratic(Poly L, Poly R, String v) {
        double A = L.c[2][0] - R.c[2][0];
        double B = L.c[1][0] - R.c[1][0];
        double C = L.c[0][0] - R.c[0][0];
        StringBuilder sb = new StringBuilder();
        sb.append("Es una ecuación de segundo grado.\n\n");
        sb.append("Paso 1: paso todo a la izquierda para que la derecha quede en 0:\n");
        sb.append(polyStr(A, B, C, v)).append(" = 0\n\n");
        sb.append("Paso 2: identifico a = ").append(fmt(A)).append(", b = ").append(fmt(B)).append(", c = ").append(fmt(C))
                .append(" y uso la fórmula general:\n");
        sb.append(v).append(" = (-b ± raíz(b² - 4ac)) ÷ (2a)\n\n");
        double D = B * B - 4 * A * C;
        if (Math.abs(D) < 1e-9) D = 0;
        sb.append("Paso 3: calculo el discriminante D = b² - 4ac:\n");
        sb.append("D = ").append(par(B)).append("² - 4 × ").append(par(A)).append(" × ").append(par(C)).append(" = ")
                .append(fmt(B * B)).append(" - ").append(fmt(4 * A * C)).append(" = ").append(fmt(D)).append("\n\n");
        if (D < 0) {
            sb.append("Paso 4: como D es negativo, no existe su raíz cuadrada entre los números reales.\n");
            sb.append("Resultado: la ecuación no tiene soluciones reales.");
            return sb.toString();
        }
        if (D == 0) {
            double r = -B / (2 * A);
            sb.append("Paso 4: como D = 0, hay una sola solución:\n");
            sb.append(v).append(" = -b ÷ 2a = ").append(fmt(-B)).append(" ÷ ").append(fmt(2 * A)).append(" = ").append(fmt(r)).append("\n");
            sb.append("Resultado: ").append(v).append(" = ").append(fmt(r));
            return sb.toString();
        }
        double sq = Math.sqrt(D);
        double x1 = (-B + sq) / (2 * A);
        double x2 = (-B - sq) / (2 * A);
        sb.append("Paso 4: como D es positivo, hay dos soluciones. La raíz de ").append(fmt(D)).append(" es ")
                .append(fmt(sq)).append(isInt(sq) ? "" : " (aprox.)").append(".\n");
        sb.append(v).append("1 = (").append(fmt(-B)).append(" + ").append(fmt(sq)).append(") ÷ ").append(fmt(2 * A))
                .append(" = ").append(fmt(x1)).append("\n");
        sb.append(v).append("2 = (").append(fmt(-B)).append(" - ").append(fmt(sq)).append(") ÷ ").append(fmt(2 * A))
                .append(" = ").append(fmt(x2)).append("\n");
        sb.append("Resultado: ").append(v).append("1 = ").append(fmt(x1)).append(" y ").append(v).append("2 = ").append(fmt(x2));
        return sb.toString();
    }

    private static double[] linear2(String eq) {
        int i = eq.indexOf('=');
        if (i < 0 || i != eq.lastIndexOf('=')) throw new ParseFail();
        String ls = eq.substring(0, i).trim();
        String rs = eq.substring(i + 1).trim();
        if (ls.length() == 0 || rs.length() == 0) throw new ParseFail();
        Poly P = new PolyParser(ls).parseAll().add(new PolyParser(rs).parseAll(), -1);
        if (Math.abs(P.c[2][0]) > 1e-12 || Math.abs(P.c[0][2]) > 1e-12 || Math.abs(P.c[1][1]) > 1e-12) {
            throw new MathError("Por ahora solo resuelvo sistemas de ecuaciones lineales (sin potencias).");
        }
        return new double[] { P.c[1][0], P.c[0][1], -P.c[0][0] };
    }

    private static String solveSystem(String e1, String e2) {
        double[] f = linear2(e1);
        double[] g = linear2(e2);
        double a1 = f[0], b1 = f[1], c1 = f[2];
        double a2 = g[0], b2 = g[1], c2 = g[2];
        StringBuilder sb = new StringBuilder();
        sb.append("Es un sistema de dos ecuaciones con dos incógnitas:\n");
        sb.append("(1) ").append(lin2Str(a1, b1)).append(" = ").append(fmt(c1)).append("\n");
        sb.append("(2) ").append(lin2Str(a2, b2)).append(" = ").append(fmt(c2)).append("\n\n");
        double D = a1 * b2 - a2 * b1;
        if (Math.abs(D) < 1e-12) {
            boolean same = Math.abs(a1 * c2 - a2 * c1) < 1e-9 && Math.abs(b1 * c2 - b2 * c1) < 1e-9;
            if (same) {
                sb.append("Las dos ecuaciones dicen lo mismo, así que hay infinitas soluciones.");
            } else {
                sb.append("Las dos ecuaciones son rectas paralelas que nunca se cruzan, así que el sistema no tiene solución.");
            }
            return sb.toString();
        }
        double E = c1 * b2 - c2 * b1;
        sb.append("Paso 1: elimino la y (método de reducción). Multiplico la ecuación (1) por ").append(par(b2))
                .append(" y la (2) por ").append(par(b1)).append(":\n");
        sb.append("(1) ").append(lin2Str(a1 * b2, b1 * b2)).append(" = ").append(fmt(c1 * b2)).append("\n");
        sb.append("(2) ").append(lin2Str(a2 * b1, b1 * b2)).append(" = ").append(fmt(c2 * b1)).append("\n\n");
        sb.append("Paso 2: resto las dos ecuaciones y la y desaparece:\n");
        sb.append(polyStr(0, D, 0, "x")).append(" = ").append(fmt(E)).append("\n");
        double x = E / D;
        sb.append("x = ").append(fmt(E)).append(" ÷ ").append(par(D)).append(" = ").append(fmt(x));
        String fx = frac(E, D);
        if (fx != null) sb.append("  (fracción: ").append(fx).append(")");
        sb.append("\n\n");
        double y;
        if (Math.abs(b1) > 1e-12) {
            y = (c1 - a1 * x) / b1;
            sb.append("Paso 3: sustituyo x = ").append(fmt(x)).append(" en la ecuación (1):\n");
            sb.append(fmt(a1)).append(" × ").append(par(x)).append(" + ").append(fmt(b1)).append("y = ").append(fmt(c1)).append("\n");
            sb.append("y = (").append(fmt(c1)).append(" - ").append(fmt(a1 * x)).append(") ÷ ").append(par(b1)).append(" = ").append(fmt(y));
        } else {
            y = (c2 - a2 * x) / b2;
            sb.append("Paso 3: sustituyo x = ").append(fmt(x)).append(" en la ecuación (2):\n");
            sb.append(fmt(a2)).append(" × ").append(par(x)).append(" + ").append(fmt(b2)).append("y = ").append(fmt(c2)).append("\n");
            sb.append("y = (").append(fmt(c2)).append(" - ").append(fmt(a2 * x)).append(") ÷ ").append(par(b2)).append(" = ").append(fmt(y));
        }
        sb.append("\n\nResultado: x = ").append(fmt(x)).append(" e y = ").append(fmt(y));
        return sb.toString();
    }
}
