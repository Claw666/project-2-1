package edu.maastricht.ginrummy.agents.functions;

import edu.maastricht.ginrummy.agents.IAgent;

import java.util.Random;

public class WhatToPick {
    static Random r = new Random();

    public static IAgent.CardPickD random()
    {
        return r.nextBoolean() ? IAgent.CardPickD.DISCARD : IAgent.CardPickD.STACK;
    }
}
