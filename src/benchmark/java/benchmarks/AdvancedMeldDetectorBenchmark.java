package benchmarks;

import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.infra.Blackhole;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode({Mode.AverageTime})
@Warmup(iterations = 10, time = 500, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 100, time = 500, timeUnit = TimeUnit.MILLISECONDS)
public class AdvancedMeldDetectorBenchmark {

    @Param({ /*"52", "51", "50", "51", "50",
            "49", "48", "47", "46", "45", "44", "43", "42", "41", "40",
            "39", "38", "37", "36", "35", "34", "33", "32", "31", "30",
            "29", "28", "27", "26", "25", "24", "23", "22", "21",*/ "20",
            "19", "18", "17", "16", "15", "14", "13", "12", "11", "10",
            "9", "8", "7", "6", "5", "4", "3", "2", "1"})
    private int reduceCards;

    private CardDeck hand;

    @Setup(Level.Invocation)
    public void setup()
    {
        this.hand = new CardDeck(CardDeck.DeckType.FRENCH, true);
        this.hand.shuffle(10);
        for(int i = 0; i < 52-reduceCards; i++)
        {
            this.hand.draw();
        }
    }

    @Fork(value = 1, warmups = 0)
    @Benchmark
    public void benchmark(Blackhole blackhole) {
        blackhole.consume(AdvancedMeldDetector.find(this.hand));
    }


}
