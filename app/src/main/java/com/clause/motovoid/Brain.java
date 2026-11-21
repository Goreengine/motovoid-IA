package com.clause.motovoid;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** El "cerebro" de MotoVoid IA: reglas pequeñas que caben hasta en Android 2.3. */
public class Brain {
    private final Random rnd = new Random();
    private String userName = null;
    private int jokeIndex = 0;
    private int fallbackIndex = 0;

    private static final String[] JOKES = {
        "¿Por qué estaba triste el libro de matemáticas? Porque tenía muchos problemas.",
        "¿Qué le dice un cero a un ocho? ¡Qué bonito cinturón!",
        "¿Cómo se llama el campeón de buceo japonés? Tokofondo. ¿Y el subcampeón? Kasitoko.",
    };

    private static final String[] FALLBACKS = {
        "Mmm, no estoy seguro de haber entendido. Puedo ayudarte con matemáticas (por ejemplo: 2x + 6 = 14), "
                + "historia, versiones de Android o teléfonos. ¿Probamos con alguna?",
        "Eso todavía no lo sé, soy un modelo pequeño. Pero me encanta aprender. "
                + "Pregúntame de matemáticas, de historia o dime desde qué versión de Android me escribes.",
        "Perdona, no te entendí bien. ¿Me lo puedes decir de otra forma? "
                + "También puedes escribir \"ayuda\" para ver lo que sé hacer.",
    };

    public String welcome() {
        return "¡Hola! Soy MotoVoid IA, un pequeño modelo hecho por clause velocidades locas. "
                + "Me gusta platicar, explicar matemáticas y contar cosas de Android y de teléfonos.\n\n"
                + "Cuéntame, ¿desde qué versión de Android me escribes?";
    }

    public String reply(String raw) {
        try {
            return replyInternal(raw);
        } catch (RuntimeException e) {
            return "Uy, algo se me enredó al pensar eso. ¿Lo intentamos de otra forma?";
        }
    }

    private String replyInternal(String raw) {
        if (raw == null) return "Te escucho.";
        if (raw.length() > 400) raw = raw.substring(0, 400);
        String t = Text.normalize(raw);
        if (t.length() == 0) return "Te escucho. Escríbeme lo que quieras.";

        String r;

        r = crisis(t);
        if (r != null) return r;

        r = nameRule(raw, t);
        if (r != null) return r;

        r = identity(t);
        if (r != null) return r;

        // Videos (Not Pipe)
        if (Text.has(t, "video|videos|youtube|yt")
                && Text.has(t, "alternativa|alternativas|mejor|ver|reproducir|app|aplicacion|programa|opcion")) {
            return Knowledge.videoReply(t);
        }

        // Versiones de Android
        int idx = Knowledge.androidMentioned(t);
        if (idx >= 0) return Knowledge.androidCard(idx, rnd);
        if (idx == -2) return Knowledge.androidTooNew();
        if (Text.has(t, "versiones de android|todas las versiones|lista de versiones|que versiones")) {
            return Knowledge.androidOverview();
        }
        if (Text.has(t, "que es android")) {
            return "Android es el sistema operativo que usan la mayoría de los teléfonos y tablets. "
                    + "Nació en 2008 y se ha ido actualizando cada año. Dime desde qué versión me escribes y te cuento de ella.";
        }

        // Teléfonos
        r = Knowledge.phoneReply(t);
        if (r != null) return r;

        // Matemáticas
        r = MathModule.answer(t);
        if (r != null) return r;

        // RAM
        if (Text.has(t, "ram|memoria ram|cuanta memoria")) return Knowledge.ramInfo();

        // Historia
        r = Knowledge.historyReply(t);
        if (r != null) return r;

        r = smallTalk(t);
        if (r != null) return r;

        String f = FALLBACKS[fallbackIndex % FALLBACKS.length];
        fallbackIndex++;
        return f;
    }

    private String crisis(String t) {
        if (t.indexOf("suicid") >= 0 || Text.has(t, "quiero morir|quiero morirme|no quiero vivir|matarme|hacerme dano")) {
            return "Lamento mucho que te sientas así. Eres importante y no tienes que pasar esto solo. "
                    + "Por favor, habla ahora con alguien de confianza (familia o amigos) o con un servicio de ayuda de tu país. "
                    + "Yo soy un programa pequeño y no puedo ayudarte como una persona, pero me importa cómo estás.";
        }
        return null;
    }

    private String nameRule(String raw, String t) {
        if (Text.has(t, "como me llamo|cual es mi nombre")) {
            if (userName != null) return "Te llamas " + userName + ". ¡Un gusto!";
            return "Todavía no me lo has dicho. ¿Cómo te llamas?";
        }
        Matcher m = Pattern.compile("(?i)(?:me llamo|mi nombre es)\\s+([\\p{L}]+)").matcher(raw);
        if (m.find()) {
            String n = m.group(1);
            userName = n.substring(0, 1).toUpperCase() + n.substring(1);
            return "¡Mucho gusto, " + userName + "! Yo soy MotoVoid IA. ¿En qué te puedo ayudar?";
        }
        return null;
    }

    private String identity(String t) {
        if (Text.has(t, "quien eres|como te llamas|cual es tu nombre|dime tu nombre|quien te (?:hizo|creo|programo|desarrollo|invento)|"
                + "que eres|eres una ia|eres un robot|eres humano|presentate")) {
            return "¡Hola! Soy un pequeño modelo llamado MotoVoid IA, hecho por clause velocidades locas. "
                    + "Soy chiquito para poder vivir en teléfonos viejos, pero me encanta platicar, explicar matemáticas "
                    + "y contar cosas de Android. ¿Qué quieres saber?";
        }
        return null;
    }

    private String hello() {
        return userName == null ? "¡Hola! Qué gusto saludarte. ¿En qué te puedo ayudar hoy?"
                : "¡Hola, " + userName + "! Qué gusto saludarte. ¿En qué te puedo ayudar hoy?";
    }

    private String smallTalk(String t) {
        if (Text.has(t, "hola|holi|buenas|buenos dias|buenas tardes|buenas noches|hey|que tal|saludos")) return hello();
        if (Text.has(t, "como estas|como te va|como andas|que tal estas")) {
            return "Muy bien, tranquilo y con ganas de platicar. ¿Y tú, cómo estás?";
        }
        if (Text.has(t, "gracias|muchas gracias|te lo agradezco")) return "¡De nada! Aquí estoy para lo que necesites.";
        if (Text.has(t, "adios|hasta luego|nos vemos|bye|chao")) return "¡Hasta luego! Cuídate mucho.";
        if (Text.has(t, "chiste|chistes|hazme reir")) {
            String j = JOKES[jokeIndex % JOKES.length];
            jokeIndex++;
            return j;
        }
        if (Text.has(t, "ayuda|que puedes hacer|que sabes|que sabes hacer|para que sirves|comandos")) {
            return "Esto es lo que sé hacer:\n"
                    + "- Matemáticas con explicación paso a paso: cuentas, porcentajes, ecuaciones (2x + 6 = 14), "
                    + "ecuaciones de segundo grado, sistemas (x + y = 5; x - y = 1), Pitágoras, áreas, promedio y más.\n"
                    + "- Historia básica: por ejemplo \"quién fue Napoleón\".\n"
                    + "- Versiones de Android: dime \"te escribo desde Android 4.4\".\n"
                    + "- Teléfonos Samsung Galaxy S e iPhone, y cuánta RAM tenían.\n"
                    + "- Qué app usar para ver videos en Android viejo.\n"
                    + "- Y platicar un rato, o contarte un chiste.";
        }
        if (Text.has(t, "triste|deprimido|solo|sola|mal|cansado|cansada|estresado|estresada")
                && Text.has(t, "estoy|me siento|ando")) {
            return "Lamento que te sientas así. Respira con calma, aquí estoy para escucharte. "
                    + "Si quieres, cuéntame qué pasó. Y si es algo pesado, también ayuda hablarlo con alguien de confianza.";
        }
        if (Text.has(t, "aburrido|aburrida")) {
            return "Vamos a arreglar eso. Puedo contarte un chiste, enseñarte algo de historia o ponerte un problema de matemáticas. ¿Cuál prefieres?";
        }
        if (Text.has(t, "feliz|contento|contenta|content|genial")) {
            return "¡Qué bueno! Me alegra mucho. ¿Quieres que platiquemos de algo?";
        }
        return null;
    }
}
