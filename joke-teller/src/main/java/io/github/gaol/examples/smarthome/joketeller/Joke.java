package io.github.gaol.examples.smarthome.joketeller;

public class Joke {
    private int id;
    private String type;
    private String setup;
    private String punchline;

    public int getId() {
        return id;
    }

    public Joke setId(int id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public Joke setType(String type) {
        this.type = type;
        return this;
    }

    public String getSetup() {
        return setup;
    }

    public Joke setSetup(String setup) {
        this.setup = setup;
        return this;
    }

    public String getPunchline() {
        return punchline;
    }

    public Joke setPunchline(String punchline) {
        this.punchline = punchline;
        return this;
    }
}
