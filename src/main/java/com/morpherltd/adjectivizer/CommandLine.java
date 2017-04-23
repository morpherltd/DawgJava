package com.morpherltd.adjectivizer;

import java.io.IOException;

public class CommandLine {
    public static void main (String [] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java -jar adjectivizer.jar noun");
            return;
        }

        for (String adj : new Adjectivizer().getAdjectives(args[0])) {
            System.out.println(adj);
        }
    }
}
