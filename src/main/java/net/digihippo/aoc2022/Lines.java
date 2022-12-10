package net.digihippo.aoc2022;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.function.Consumer;

public class Lines {
    static void processLines(InputStream inputStream, Consumer<String> callback) throws IOException {
        try (final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = bufferedReader.readLine()) != null)
            {
                callback.accept(line);
            }
        }
    }
}
