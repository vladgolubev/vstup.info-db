package ua.samosfator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        Parser parser = new Parser(2013, "6.050103");
        parser.parse();
    }
}