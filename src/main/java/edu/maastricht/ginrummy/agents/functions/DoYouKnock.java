package edu.maastricht.ginrummy.agents.functions;

import java.util.Random;

public class DoYouKnock {
    static Random r = new Random();

    public static boolean random() {
        return r.nextBoolean();
    }
}
