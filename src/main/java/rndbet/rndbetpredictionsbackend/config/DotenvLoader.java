package rndbet.rndbetpredictionsbackend.config;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

/**
 * Carga {@code .env} en {@link System#setProperty} antes de que arranque Spring, para que no dependa
 * del {@code spring.config.import=optional:file:.env} (en IntelliJ el working directory a veces no es la raíz del repo).
 * Busca {@code .env} en {@code user.dir} y en carpetas padre hasta encontrarlo.
 */
public final class DotenvLoader {

    private DotenvLoader() {
    }

    public static void loadIntoSystemProperties() {
        Path envFile = findDotEnvFile();
        if (envFile == null) {
            return;
        }
        try {
            for (String raw : Files.readAllLines(envFile, StandardCharsets.UTF_8)) {
                String line = raw.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }
                if (line.toLowerCase(Locale.ROOT).startsWith("export ")) {
                    line = line.substring(7).trim();
                }
                int eq = line.indexOf('=');
                if (eq <= 0) {
                    continue;
                }
                String key = line.substring(0, eq).trim();
                String value = line.substring(eq + 1).trim();
                if (value.length() >= 2
                        && ((value.startsWith("\"") && value.endsWith("\""))
                                || (value.startsWith("'") && value.endsWith("'")))) {
                    value = value.substring(1, value.length() - 1);
                }
                if (!key.isEmpty() && !skipDatasourceLocationFromEnv(key)) {
                    System.setProperty(key, value);
                }
            }
        } catch (IOException ignored) {
            // sin .env usable; Spring usará application.properties
        }
    }

    /**
     * URL y usuario del pooler van fijos en {@code application.properties}. Si el .env trae
     * {@code SPRING_DATASOURCE_URL} (p. ej. host directo db.* viejo), Spring lo mezcla con alta
     * prioridad y rompe la conexión.
     */
    private static boolean skipDatasourceLocationFromEnv(String key) {
        return "SPRING_DATASOURCE_URL".equals(key) || "SPRING_DATASOURCE_USERNAME".equals(key);
    }

    private static Path findDotEnvFile() {
        Path start = Path.of(System.getProperty("user.dir", ".")).toAbsolutePath().normalize();
        Path p = start;
        for (int i = 0; i < 14 && p != null; i++) {
            Path candidate = p.resolve(".env");
            if (Files.isRegularFile(candidate)) {
                return candidate;
            }
            p = p.getParent();
        }
        return null;
    }
}
