package com.clause.motovoid;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Base de conocimientos pequeña: Android, teléfonos, RAM, historia y videos. */
final class Knowledge {
    private Knowledge() {}

    // ------------------------------------------------------------------
    // Versiones de Android: {versión, nombre, año, dato curioso, nivel de sorpresa 0-3}
    // ------------------------------------------------------------------

    static final String[][] AV = {
        /* 0 */ {"1.0 / 1.1", "", "2008", "fue la primera versión de Android y llegó en el HTC Dream, también llamado T-Mobile G1", "0"},
        /* 1 */ {"1.5", "Cupcake", "2009", "fue la primera con nombre de postre y estrenó el teclado en pantalla", "0"},
        /* 2 */ {"1.6", "Donut", "2009", "añadió la búsqueda por voz y mejoró la tienda de aplicaciones", "0"},
        /* 3 */ {"2.0 / 2.1", "Eclair", "2009", "trajo los fondos de pantalla animados", "0"},
        /* 4 */ {"2.2", "Froyo", "2010", "permitía usar el teléfono como punto de acceso Wi-Fi para compartir internet", "0"},
        /* 5 */ {"2.3", "Gingerbread", "2010", "fue de las primeras con soporte para NFC y mejoró el teclado y la batería", "0"},
        /* 6 */ {"3.x", "Honeycomb", "2011", "estaba hecha solo para tablets", "0"},
        /* 7 */ {"4.0", "Ice Cream Sandwich", "2011", "unió teléfonos y tablets en una sola versión y estrenó los botones en pantalla", "1"},
        /* 8 */ {"4.1 a 4.3", "Jelly Bean", "2012", "mejoró muchísimo la fluidez de la pantalla (Project Butter) y estrenó Google Now", "1"},
        /* 9 */ {"4.4", "KitKat", "2013", "se llamó así por una alianza con Nestlé y se diseñó para funcionar bien en teléfonos con poca RAM", "1"},
        /* 10 */ {"5.x", "Lollipop", "2014", "estrenó el diseño Material Design", "1"},
        /* 11 */ {"6.0", "Marshmallow", "2015", "empezó a pedir permisos a las apps cuando las usas", "1"},
        /* 12 */ {"7.x", "Nougat", "2016", "permitió usar dos apps a la vez con pantalla dividida", "2"},
        /* 13 */ {"8.x", "Oreo", "2017", "trajo imagen en imagen para ver videos mientras usas otra app", "2"},
        /* 14 */ {"9", "Pie", "2018", "estrenó la navegación por gestos", "2"},
        /* 15 */ {"10", "", "2019", "trajo el modo oscuro en todo el sistema y fue la primera sin nombre de postre", "2"},
        /* 16 */ {"11", "", "2020", "añadió burbujas de conversación y permisos de una sola vez", "3"},
        /* 17 */ {"12", "", "2021", "estrenó el diseño Material You, que adapta los colores a tu fondo de pantalla", "3"},
        /* 18 */ {"13", "", "2022", "permitió elegir el idioma de cada app por separado", "3"},
        /* 19 */ {"14", "", "2023", "mejoró la personalización de la pantalla de bloqueo", "3"},
        /* 20 */ {"15", "", "2024", "trajo el espacio privado para esconder apps", "3"},
        /* 21 */ {"16", "", "2025", "añadió notificaciones de progreso en vivo", "3"},
    };

    static int indexFor(int major, int minor) {
        switch (major) {
            case 1:
                if (minor == 5) return 1;
                if (minor == 6) return 2;
                return 0;
            case 2:
                if (minor == 0 || minor == 1) return 3;
                if (minor == 2) return 4;
                return 5;
            case 3: return 6;
            case 4:
                if (minor == 4) return 9;
                if (minor >= 1 && minor <= 3) return 8;
                return 7;
            case 5: return 10;
            case 6: return 11;
            case 7: return 12;
            case 8: return 13;
            case 9: return 14;
            case 10: return 15;
            case 11: return 16;
            case 12: return 17;
            case 13: return 18;
            case 14: return 19;
            case 15: return 20;
            case 16: return 21;
            default: return -1;
        }
    }

    private static final String[] NAMES = {
        "cupcake", "1", "donut", "2", "eclair", "3", "froyo", "4", "gingerbread", "5", "honeycomb", "6",
        "ice cream sandwich|ics", "7", "jelly bean", "8", "kitkat|kit kat", "9", "lollipop", "10",
        "marshmallow", "11", "nougat|nugat", "12", "oreo", "13", "pie", "14"
    };

    /** Devuelve el índice de la versión mencionada en el texto, -1 si no hay, -2 si es demasiado nueva. */
    static int androidMentioned(String t) {
        Matcher m = Pattern.compile("\\bandroid\\s*(\\d{1,2})(?:\\.(\\d{1,2}))?").matcher(t);
        if (!m.find()) {
            m = Pattern.compile("\\bversion\\s*(\\d{1,2})(?:\\.(\\d{1,2}))?\\s*de\\s*android").matcher(t);
            if (!m.find()) m = null;
        }
        if (m != null) {
            int major = Integer.parseInt(m.group(1));
            int minor = m.group(2) == null ? -1 : Integer.parseInt(m.group(2));
            if (major >= 1 && major <= 16) return indexFor(major, minor);
            if (major > 16 && major <= 40) return -2;
            return -1;
        }
        if (Text.has(t, "android|version")) {
            for (int i = 0; i < NAMES.length; i += 2) {
                if (Text.has(t, NAMES[i])) return Integer.parseInt(NAMES[i + 1]);
            }
        }
        return -1;
    }

    static String androidCard(int idx, Random rnd) {
        String[] a = AV[idx];
        int tier = Integer.parseInt(a[4]);
        StringBuilder sb = new StringBuilder();
        if (idx == 5) {
            sb.append("¡No lo puedo creer, esa es mi versión! Yo vivo en Android 2.3, somos de la misma época. ");
        } else if (tier == 0) {
            String[] o = {"¡Wow, qué sorpresa! Esa es una joya antigua. ", "¡Guau! ¿En serio? Esa versión ya es toda una leyenda. "};
            sb.append(o[rnd.nextInt(o.length)]);
        } else if (tier == 1) {
            String[] o = {"¡Qué bonito recuerdo! ", "¡Vaya, qué recuerdos! "};
            sb.append(o[rnd.nextInt(o.length)]);
        } else if (tier == 2) {
            String[] o = {"¡Qué buena versión! ", "¡Vaya, una versión muy querida! "};
            sb.append(o[rnd.nextInt(o.length)]);
        } else {
            sb.append("¡Qué moderno! Seguro tu teléfono tiene mucha más memoria que yo. ");
        }
        sb.append("Android ").append(a[0]);
        if (a[1].length() > 0) sb.append(" (").append(a[1]).append(")");
        sb.append(" salió en ").append(a[2]).append(" y ").append(a[3]).append(".");
        if (tier == 0 && idx != 5) sb.append(" ¡Todo un tesoro!");
        if (idx == 5) sb.append(" ¿Y tú, desde qué teléfono me escribes?");
        return sb.toString();
    }

    static String androidTooNew() {
        return "¡Esa versión es tan nueva que todavía no la conozco! Mi información llega hasta Android 16, "
                + "pero me da mucha curiosidad. Cuéntame, ¿qué trae de nuevo?";
    }

    static String androidOverview() {
        StringBuilder sb = new StringBuilder("Estas son las versiones de Android que conozco:\n");
        for (int i = 0; i < AV.length; i++) {
            sb.append("- Android ").append(AV[i][0]);
            if (AV[i][1].length() > 0) sb.append(" ").append(AV[i][1]);
            sb.append(" (").append(AV[i][2]).append(")\n");
        }
        sb.append("Dime desde cuál me escribes y te cuento un poco de ella.");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // Teléfonos: {claves separadas por |, nombre, año, sistema, RAM, dato}
    // Las claves van sin espacios ni símbolos (por ejemplo "galaxys3mini").
    // ------------------------------------------------------------------

    static final String[][] PHONES = {
        {"galaxys", "Galaxy S", "2010", "Android 2.1 (luego se actualizó a 2.3)", "512 MB",
            "tenía pantalla Super AMOLED de 4 pulgadas y fue el que ayudó a que Samsung se hiciera famoso en Android"},
        {"galaxysmini|galaxymini", "Galaxy Mini (a veces le dicen Galaxy S Mini)", "2011", "Android 2.2 (luego pudo llegar a 2.3)", "384 MB",
            "era chiquito y barato, ideal como primer teléfono Android"},
        {"galaxys2|galaxysii", "Galaxy S II", "2011", "Android 2.3", "1 GB",
            "fue de los primeros con doble núcleo y se vendió muchísimo"},
        {"galaxys3|galaxysiii", "Galaxy S III", "2012", "Android 4.0.4", "1 GB (algunas versiones 2 GB)",
            "tenía pantalla de 4.8 pulgadas y asistente de voz S Voice"},
        {"galaxys3mini|galaxysiiimini", "Galaxy S III mini", "2012", "Android 4.1", "1 GB",
            "era la versión más pequeña del S III, con pantalla de 4 pulgadas"},
        {"galaxys4", "Galaxy S4", "2013", "Android 4.2.2", "2 GB", "tenía pantalla Full HD de 5 pulgadas"},
        {"galaxys4mini", "Galaxy S4 mini", "2013", "Android 4.2.2", "1.5 GB", "tenía pantalla de 4.3 pulgadas"},
        {"galaxys5", "Galaxy S5", "2014", "Android 4.4.2", "2 GB", "era resistente al agua y traía lector de huella"},
        {"galaxys6", "Galaxy S6", "2015", "Android 5.0.2", "3 GB", "estrenó cuerpo de vidrio y metal, pero sin ranura para microSD"},
        {"galaxys7", "Galaxy S7", "2016", "Android 6.0", "4 GB", "regresó la ranura microSD y era resistente al agua"},
        {"galaxys8", "Galaxy S8", "2017", "Android 7.0", "4 GB", "estrenó la pantalla casi sin bordes"},
        {"galaxys9", "Galaxy S9", "2018", "Android 8.0", "4 GB", "tenía cámara de apertura variable"},
        {"galaxys10", "Galaxy S10", "2019", "Android 9", "6 u 8 GB según la versión", "tenía la cámara frontal dentro de un agujerito en la pantalla"},
        {"galaxys20", "Galaxy S20", "2020", "Android 10", "8 a 12 GB según la versión", "traía 5G y pantalla de 120 Hz"},
        {"galaxys21", "Galaxy S21", "2021", "Android 11", "8 GB", "tenía un diseño nuevo en el módulo de cámaras"},
        {"galaxys22", "Galaxy S22", "2022", "Android 12", "8 GB", "tenía un diseño más compacto y cámaras mejoradas"},
        {"galaxys23", "Galaxy S23", "2023", "Android 13", "8 GB", "usó un procesador Snapdragon hecho especialmente para Galaxy"},
        {"galaxys24", "Galaxy S24", "2024", "Android 14", "8 GB (el Ultra, 12 GB)", "estrenó las funciones de inteligencia artificial Galaxy AI"},
        {"galaxys25", "Galaxy S25", "2025", "Android 15", "12 GB", "siguió con la inteligencia artificial integrada"},

        {"iphone1|iphoneoriginal|iphone2g", "iPhone (el original)", "2007", "iPhone OS 1", "128 MB",
            "tenía pantalla multitáctil y, al inicio, todavía no tenía tienda de apps"},
        {"iphone3g", "iPhone 3G", "2008", "iPhone OS 2", "128 MB", "fue el primero con la App Store"},
        {"iphone3gs", "iPhone 3GS", "2009", "iPhone OS 3", "256 MB", "la S significa speed (velocidad) y añadió control por voz"},
        {"iphone4", "iPhone 4", "2010", "iOS 4", "512 MB", "estrenó la pantalla Retina y la cámara frontal"},
        {"iphone4s", "iPhone 4S", "2011", "iOS 5", "512 MB", "estrenó a Siri, el asistente de voz"},
        {"iphone5", "iPhone 5", "2012", "iOS 6", "1 GB", "alargó la pantalla a 4 pulgadas y estrenó el conector Lightning"},
        {"iphone5s", "iPhone 5s", "2013", "iOS 7", "1 GB", "estrenó el lector de huella Touch ID y el primer chip de 64 bits"},
        {"iphone6", "iPhone 6", "2014", "iOS 8", "1 GB", "subió a una pantalla de 4.7 pulgadas"},
        {"iphone6s", "iPhone 6s", "2015", "iOS 9", "2 GB", "estrenó el 3D Touch"},
        {"iphone7", "iPhone 7", "2016", "iOS 10", "2 GB", "quitó la entrada de audífonos y es resistente al agua"},
        {"iphone8", "iPhone 8", "2017", "iOS 11", "2 GB", "añadió la carga inalámbrica"},
        {"iphonex", "iPhone X", "2017", "iOS 11", "3 GB", "estrenó Face ID y la pantalla sin botón de inicio"},
        {"iphonexs", "iPhone XS", "2018", "iOS 12", "4 GB", "mejoró el diseño del iPhone X"},
        {"iphonexr", "iPhone XR", "2018", "iOS 12", "3 GB", "salió en varios colores y con muy buena batería"},
        {"iphone11", "iPhone 11", "2019", "iOS 13", "4 GB", "trajo dos cámaras traseras"},
        {"iphone12", "iPhone 12", "2020", "iOS 14", "4 GB", "traía 5G y MagSafe"},
        {"iphone12mini", "iPhone 12 mini", "2020", "iOS 14", "4 GB", "era un iPhone muy compacto, con pantalla de 5.4 pulgadas"},
        {"iphone13", "iPhone 13", "2021", "iOS 15", "4 GB", "mejoró la batería y estrenó el modo cine"},
        {"iphone13mini", "iPhone 13 mini", "2021", "iOS 15", "4 GB", "fue el último iPhone mini"},
        {"iphone14", "iPhone 14", "2022", "iOS 16", "6 GB", "añadió la detección de choques"},
        {"iphone15", "iPhone 15", "2023", "iOS 17", "6 GB", "cambió al puerto USB-C"},
        {"iphone16", "iPhone 16", "2024", "iOS 18", "8 GB", "añadió el botón de acción y el control de cámara"},
    };

    private static final String[] FORBID = {
        "mini", "plus", "pro", "max", "edge", "ultra", "fe", "active", "duos", "lite", "advance", "mega"
    };

    private static boolean startsWithAny(String s, String[] list) {
        for (int i = 0; i < list.length; i++) {
            if (s.startsWith(list[i])) return true;
        }
        return false;
    }

    /** Respuesta sobre un teléfono, o null si el texto no habla de teléfonos Galaxy o iPhone. */
    static String phoneReply(String t) {
        String sq = Text.squash(t);
        boolean galaxy = sq.indexOf("galaxy") >= 0;
        boolean iphone = sq.indexOf("iphone") >= 0;
        if (!galaxy && !iphone) return null;

        String[] best = null;
        int bestLen = 0;
        for (int i = 0; i < PHONES.length; i++) {
            String[] keys = PHONES[i][0].split("\\|");
            for (int k = 0; k < keys.length; k++) {
                String key = keys[k];
                String[] variants;
                if (key.startsWith("galaxys")) {
                    variants = new String[] { key, "samsung" + key.substring(6) };
                } else {
                    variants = new String[] { key };
                }
                for (int vi = 0; vi < variants.length; vi++) {
                    String kk = variants[vi];
                    int from = 0;
                    while (true) {
                        int pos = sq.indexOf(kk, from);
                        if (pos < 0) break;
                        String next = sq.substring(pos + kk.length());
                        boolean digitNext = next.length() > 0 && Character.isDigit(next.charAt(0));
                        if (!digitNext && !startsWithAny(next, FORBID) && kk.length() > bestLen) {
                            best = PHONES[i];
                            bestLen = kk.length();
                        }
                        from = pos + 1;
                    }
                }
            }
        }

        if (best == null) {
            if (iphone) {
                return "Conozco los iPhone desde el original (2007) hasta el iPhone 16. ¿De cuál quieres saber? "
                        + "Por ejemplo: iPhone 4S, iPhone 6 o iPhone 12 mini.";
            }
            return "Conozco varios Samsung Galaxy S, desde el Galaxy S (2010) hasta el S25, y también el S III mini y el S4 mini. "
                    + "¿De cuál quieres saber? Por ejemplo: Galaxy S2 o Galaxy S4 mini.";
        }

        StringBuilder sb = new StringBuilder();
        if (Text.has(t, "tengo|uso|usaba|tuve|mi")) sb.append("¡Qué buen teléfono! ");
        else sb.append("Te cuento: ");
        sb.append("el ").append(best[1]).append(" salió en ").append(best[2]).append(" con ").append(best[3])
                .append(". Tenía ").append(best[4]).append(" de RAM. Dato curioso: ").append(best[5]).append(".");
        return sb.toString();
    }

    // ------------------------------------------------------------------
    // RAM
    // ------------------------------------------------------------------

    static String ramInfo() {
        return "La RAM es la memoria de trabajo del teléfono: ahí viven las apps que tienes abiertas. "
                + "Mientras menos tienes, menos cosas pueden estar abiertas a la vez.\n\n"
                + "En 2010 un teléfono con Android 2.3 tenía entre 256 y 512 MB. En 2026, hasta donde sé, los de gama media "
                + "suelen traer entre 6 y 8 GB, y los de gama alta 12 GB o más.\n\n"
                + "Las limitaciones hoy: el sistema y las apps ocupan mucho, y aunque tengas varios GB, Android cierra apps "
                + "en segundo plano para no quedarse sin memoria. Además, los modelos de IA grandes necesitan varios GB solo "
                + "para cargarse, por eso en un teléfono viejo solo caben IA muy chicas, como yo. "
                + "También he oído que la memoria se ha encarecido por la demanda de IA, pero de eso no tengo datos seguros.";
    }

    // ------------------------------------------------------------------
    // Video
    // ------------------------------------------------------------------

    static String videoReply(String t) {
        int idx = androidMentioned(t);
        boolean oldRange = idx < 0 || (idx >= 5 && idx <= 7);
        // Android 2.3 a 4.0.4: Gingerbread (5), Honeycomb (6) e Ice Cream Sandwich (7)
        if (oldRange) {
            return "Te recomiendo Not Pipe. Pero te aviso con cariño que en una versión tan antigua (de Android 2.3 a 4.0.4) "
                    + "puede llegar a fallar: a lo mejor no se instala, se cierra sola o el video no carga. "
                    + "Si te pasa, no es culpa tuya, es que tu teléfono ya es un veterano. "
                    + "Descárgala solo de su página oficial.";
        }
        return "Mi recomendación para ver videos en Android 2.3 a 4.0.4 es Not Pipe, aunque puede llegar a fallar. "
                + "Para versiones más nuevas hay más opciones y no estoy seguro de cuál sería la mejor.";
    }

    // ------------------------------------------------------------------
    // Historia: {claves (expresión regular), respuesta}
    // ------------------------------------------------------------------

    static final String[][] HISTORY = {
        {"napoleon|bonaparte",
            "Napoleón Bonaparte (1769-1821) fue un militar y gobernante francés. Llegó al poder tras la Revolución Francesa, "
            + "se coronó emperador en 1804 y conquistó gran parte de Europa. Fue derrotado en Waterloo en 1815 y murió "
            + "desterrado en la isla de Santa Elena."},
        {"hitler",
            "Adolf Hitler (1889-1945) fue el líder de la Alemania nazi desde 1933. Su política de guerra y conquista provocó "
            + "la Segunda Guerra Mundial, y su régimen fue responsable del Holocausto, el asesinato de seis millones de judíos "
            + "y de millones de personas más. Murió en 1945, al final de la guerra. Es un tema muy triste, pero importante "
            + "para que nunca se repita."},
        {"julio cesar",
            "Julio César (100-44 a. C.) fue un general y político romano. Conquistó la Galia, ganó una guerra civil y se "
            + "convirtió en dictador de Roma. Fue asesinado en el año 44 a. C. por un grupo de senadores."},
        {"cleopatra",
            "Cleopatra VII (69-30 a. C.) fue la última reina del antiguo Egipto. Se alió con Julio César y después con "
            + "Marco Antonio, y murió en el año 30 a. C."},
        {"colon|cristobal colon",
            "Cristóbal Colón (1451-1506) fue un navegante genovés que, con el apoyo de los Reyes Católicos de España, "
            + "llegó a América el 12 de octubre de 1492."},
        {"hidalgo",
            "Miguel Hidalgo y Costilla (1753-1811) fue un sacerdote mexicano que inició la lucha por la Independencia de México "
            + "con el Grito de Dolores, el 16 de septiembre de 1810. Fue capturado y fusilado en 1811."},
        {"morelos",
            "José María Morelos (1765-1815) fue un sacerdote y militar insurgente que continuó la lucha por la Independencia. "
            + "Escribió los Sentimientos de la Nación y fue fusilado en 1815."},
        {"juarez",
            "Benito Juárez (1806-1872) nació en Oaxaca y fue presidente de México. Impulsó las Leyes de Reforma y defendió al "
            + "país durante la intervención francesa. Es famosa su frase: \"Entre los individuos, como entre las naciones, "
            + "el respeto al derecho ajeno es la paz\"."},
        {"zapata",
            "Emiliano Zapata (1879-1919) fue un líder campesino de la Revolución Mexicana. Su lema era \"Tierra y Libertad\". "
            + "Fue asesinado en 1919."},
        {"pancho villa|francisco villa",
            "Francisco Villa, conocido como Pancho Villa (1878-1923), fue un jefe militar de la Revolución Mexicana al mando "
            + "de la División del Norte. Fue asesinado en 1923."},
        {"independencia de mexico",
            "La Independencia de México empezó el 16 de septiembre de 1810 con el Grito de Dolores y terminó el 27 de "
            + "septiembre de 1821, cuando el Ejército Trigarante entró a la Ciudad de México."},
        {"revolucion mexicana",
            "La Revolución Mexicana empezó en 1910 contra el gobierno de Porfirio Díaz, con Francisco I. Madero, y siguió con "
            + "líderes como Emiliano Zapata y Francisco Villa. De ella nació la Constitución de 1917."},
        {"revolucion francesa",
            "La Revolución Francesa empezó en 1789 con la toma de la Bastilla (14 de julio). Terminó con la monarquía absoluta "
            + "y proclamó la Declaración de los Derechos del Hombre y del Ciudadano. Sus lemas eran libertad, igualdad y fraternidad."},
        {"segunda guerra mundial|2da guerra mundial|ii guerra mundial",
            "La Segunda Guerra Mundial fue de 1939 a 1945. Enfrentó a los Aliados (como Reino Unido, Estados Unidos y la Unión "
            + "Soviética) contra las potencias del Eje (Alemania, Italia y Japón). Empezó cuando Alemania invadió Polonia y "
            + "terminó con la rendición de Alemania y de Japón."},
        {"primera guerra mundial|1ra guerra mundial|i guerra mundial",
            "La Primera Guerra Mundial fue de 1914 a 1918. Empezó tras el asesinato del archiduque Francisco Fernando en Sarajevo. "
            + "Se enfrentaron la Triple Entente y los Imperios Centrales, y murieron millones de personas."},
        {"guerra fria",
            "La Guerra Fría fue un periodo de tensión entre Estados Unidos y la Unión Soviética, de 1947 a 1991 más o menos, "
            + "sin una guerra directa entre ellos. Incluyó la carrera espacial y la caída del Muro de Berlín en 1989."},
        {"einstein",
            "Albert Einstein (1879-1955) fue un físico alemán famoso por la teoría de la relatividad y la fórmula E = mc². "
            + "Ganó el Premio Nobel de Física en 1921."},
        {"leonardo da vinci|da vinci",
            "Leonardo da Vinci (1452-1519) fue un pintor, inventor y científico italiano del Renacimiento. "
            + "Pintó la Mona Lisa y La Última Cena."},
        {"washington",
            "George Washington (1732-1799) fue el primer presidente de Estados Unidos (1789-1797) y dirigió el ejército "
            + "durante la guerra de Independencia de su país."},
        {"lincoln",
            "Abraham Lincoln (1809-1865) fue el 16.º presidente de Estados Unidos. Dirigió al país en la Guerra de Secesión y "
            + "abolió la esclavitud. Fue asesinado en 1865."},
        {"gandhi",
            "Mahatma Gandhi (1869-1948) fue el líder de la independencia de la India. Defendía la no violencia como forma de "
            + "lucha. Fue asesinado en 1948."},
        {"simon bolivar|bolivar",
            "Simón Bolívar (1783-1830) fue un militar venezolano llamado el Libertador, que lideró la independencia de "
            + "Venezuela, Colombia, Ecuador, Perú y Bolivia."},
    };

    static String historyReply(String t) {
        for (int i = 0; i < HISTORY.length; i++) {
            if (Text.has(t, HISTORY[i][0])) return HISTORY[i][1];
        }
        return null;
    }
}
