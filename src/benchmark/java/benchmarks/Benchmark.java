package benchmarks;

import org.openjdk.jmh.Main;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;

public class Benchmark {

    public static void main(String[] args) {
        try {
            Main.main(args);
        } catch (RunnerException | IOException e) {
            e.printStackTrace();
        }
    }

}
