package edu.maastricht.ginrummy.bees;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BeeHive {

    private static final List<Bee> bees = new LinkedList<>();
    private static ExecutorService threadPoolExecutor = Executors.newFixedThreadPool(12);

    private BeeHive()
    { }

    public static void add(Bee bee)
    {
        threadPoolExecutor.submit(() -> {
            synchronized (bees)
            {
                bees.add(bee);
            }
            bee.perform();
            synchronized (bees)
            {
                bees.remove(bee);
            }
        });
    }

}
