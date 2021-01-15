package io.github.gaol.examples.smarthome.joketeller;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Random;

public class JokeFetcher {
    private static final String jokes_data_file = "jokes.json";
    private final Joke[] jokes;
    private Random random = new Random();

    public JokeFetcher() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream(jokes_data_file), "UTF-8"));) {
            jokes = new Gson().fromJson(reader, Joke[].class);
        } catch (Exception e) {
            throw new RuntimeException("Cannot read jokes", e);
        }
    }

    Joke randomJoke() {
        return jokes[random.nextInt(jokes.length)];
    }
}
