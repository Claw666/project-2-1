package edu.maastricht.ginrummy.melding.cache;

import edu.maastricht.ginrummy.cards.Card;
import edu.maastricht.ginrummy.cards.CardDeck;
import edu.maastricht.ginrummy.melding.MeldDetector;
import edu.maastricht.ginrummy.melding.MeldGrouping;
import edu.maastricht.ginrummy.melding.advanced.AdvancedMeldDetector;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class PermutationCache {

    public static void main(String[] args) throws IOException {
        new PermutationCache().read();
    }

    private final static byte SEQUENCE_SEPARATOR = (byte) 0xFF;
    private final static byte SET_SEPARATOR = (byte) 0xFE;
    private final static byte TERMINATOR = (byte) 0xFD;

    static String fileName = "test";

    public PermutationCache()
    {
    }

    public void read() throws IOException {

        BufferedInputStream tableReader = new BufferedInputStream(new FileInputStream(String.format("F:\\project2-1\\src\\main\\resources\\permutations\\" +
                "%s.gin", fileName)));

        Map<Integer, Integer> lookupTable = new HashMap<>();

        {
            boolean end = false;
            byte[] buffer = new byte[8];

            do {
                end = tableReader.read(buffer) == -1;
                ByteBuffer byteBuffer = ByteBuffer.wrap(buffer);
                if(!end)
                {
                    lookupTable.put(byteBuffer.getInt(), byteBuffer.getInt());
                }
            }while (!end);
        }

        //"534759370" -> "124"
        RandomAccessFile stream = new RandomAccessFile(String.format("F:\\project2-1\\src\\main\\resources\\permutations\\" +
                "%s.rummy", fileName), "r");

        for(Integer offset : lookupTable.values())
        {
            System.out.println(Arrays.toString(lookup(stream, offset)));
        }



        System.out.println("done");
    }

    private ArrayList[] lookup(RandomAccessFile stream, int key) throws IOException {


        stream.seek(key);



        List<Card> currentSequence = new ArrayList<>();
        ArrayList<List<Card>> currentRuns = new ArrayList<>();
        ArrayList<List<Card>> currentSets = new ArrayList<>();

        boolean addToRuns = true;

        //sets then runs
        while (true) {

            byte current = stream.readByte();

            if (current == TERMINATOR) {
                if(addToRuns)
                {
                    currentRuns.add(currentSequence);
                }
                else
                {
                    currentSets.add(currentSequence);
                }
                break;
            }
            else if(current == SET_SEPARATOR)
            {
                addToRuns = false;
            }
            else if(current == SEQUENCE_SEPARATOR)
            {
                if(addToRuns)
                {
                    currentRuns.add(currentSequence);
                }
                else
                {
                    currentSets.add(currentSequence);
                }
                currentSequence = new ArrayList<>();

            }
            else
            {
                currentSequence.add(bytesToCardIndex(current));
            }

        }

        return new ArrayList[] {currentRuns, currentSets};
    }

    public void generate() throws IOException {

        //gin --> lookup table
        //rummy -> data


        BufferedOutputStream lookupTableWriter = new BufferedOutputStream(new FileOutputStream(String.format("F:\\project2-1\\src\\main\\resources\\permutations\\" +
                "%s.gin", fileName)));
        BufferedOutputStream dataWriter = new BufferedOutputStream(new FileOutputStream(String.format("F:\\project2-1\\src\\main\\resources\\permutations\\" +
                "%s.rummy", fileName)));

        AtomicInteger size = new AtomicInteger();

        //TODO make code threaded
        // - hashmap and simply synchronize on it to keep track of the byte offset


        Map<Integer, Integer> hashSet = new HashMap<>();
        for(int i = 3; i <= 3; i++)
        {
            LinkedList<Card> perm = new LinkedList<>();
            for(int j = 0; j < i; j++) perm.add(null);

            AtomicInteger __counter = new AtomicInteger();

            combination(new CardDeck(CardDeck.DeckType.FRENCH, false), i, permutation -> {

                System.out.println(__counter.get());
                if(__counter.getAndIncrement() > 100) {
                    return null;
                }

                //TODO maybe add a collision check and deal with it... (hash codes)

                CardDeck deck = new CardDeck(CardDeck.DeckType.EMPTY, false);
                deck.addAll(permutation);

                MeldGrouping meldGrouping = AdvancedMeldDetector.find(deck);

                if(meldGrouping.getRuns().size() == 0 && meldGrouping.getSets().size() == 0)
                {
                    System.out.println("nothing to do here");
                    return null;
                }

                // (1 byte) for the TERMINATOR
                int bytes_required = 1;
                bytes_required += sequenceByteCalculator(meldGrouping);

                boolean runsAndSets = false;
                if(meldGrouping.getRuns().size() > 0 && meldGrouping.getSets().size() > 0)
                {
                    // (1 byte) for the SET_SEPARATOR
                    bytes_required += 1;
                    runsAndSets = true;
                }

                ByteBuffer byteBuffer = ByteBuffer.allocate(bytes_required);

                appendSequenceToBuffer(byteBuffer, meldGrouping.getSets());
                if(runsAndSets)
                {
                    byteBuffer.put(SET_SEPARATOR);
                }
                appendSequenceToBuffer(byteBuffer, meldGrouping.getRuns());

                byteBuffer.put(TERMINATOR);

                {
                    try {
                        dataWriter.write(byteBuffer.array());

                        ByteBuffer lookupEntry = ByteBuffer.allocate(4 + 4);
                        lookupEntry.putInt(permutation.hashCode());
                        lookupEntry.putInt(size.get());
                        lookupTableWriter.write(lookupEntry.array());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                size.addAndGet(bytes_required);

                return null;
            });
        }

        dataWriter.flush();
        lookupTableWriter.flush();


        System.out.println(size.toString());
    }

    private int sequenceByteCalculator(MeldGrouping meldGrouping)
    {
        int bytes_required = 0;
        {
            int number_of_sets = meldGrouping.getSets().size();
            int total_amount_of_cards = meldGrouping.getSets().stream().mapToInt(List::size).sum();

            if (number_of_sets > 0) {
                // n - 1 * (1 byte) for the SEQUENCE_SEPARATOR
                bytes_required += (number_of_sets - 1) * 1;
                // n * (1 byte) since each card requires 1 byte to be stored
                bytes_required += total_amount_of_cards * 1;
            }
        }

        {
            int number_of_runs = meldGrouping.getRuns().size();
            int total_amount_of_cards = meldGrouping.getRuns().stream().mapToInt(List::size).sum();

            if(number_of_runs > 0)
            {
                // n - 1 * (1 byte) for the SEQUENCE_SEPARATOR
                bytes_required += (number_of_runs - 1) * 1;
                // n * (1 byte) since each card requires 1 byte to be stored
                bytes_required += total_amount_of_cards * 1;
            }
        }

        return bytes_required;
    }

    private void appendSequenceToBuffer(ByteBuffer byteBuffer, ArrayList<CardDeck> sequences) {
        for (int j = 0; j < sequences.size(); j++) {
            CardDeck set = sequences.get(j);
            for (Card card : set) {
                byteBuffer.put(cardIndexToBytes(card));
            }

            if(j < sequences.size() - 1)
            {
                byteBuffer.put(SEQUENCE_SEPARATOR);
            }
        }
    }

    private static byte cardIndexToBytes(Card card)
    {
        throw new UnsupportedOperationException();
    }

    private static Card bytesToCardIndex(byte card)
    {
        int value = (int) card;
        int rank = value / 10;
        int suit = value % 10;
        return new Card(Card.Suit.byId(suit), Card.Rank.byNumRank(rank));
    }

    public static void combination(List<Card> deck, int k, Function<LinkedList<Card>, Void> callback){

        assert k <= deck.size();

        // init combination index array
        int[] indices = new int[k];


        int r = 0; // index for combination array
        int i = 0; // index for elements array

        while(r >= 0)
        {

            // forward step if i < (N + (r-K))
            if(i <= (deck.size() + (r - k)))
            {
                indices[r] = i;

                // if combination array is full print and increment i;
                if(r == k-1)
                {
                    LinkedList<Card> combination = new LinkedList<>();
                    for(int pointer : indices)
                    {
                        combination.add(deck.get(pointer));
                    }
                    combination.sort(Comparator.comparingLong(Card::getByteIndex));
                    callback.apply(combination);
                    i++;
                }
                else
                {
                    // if combination is not full yet, select next element
                    i = indices[r]+1;
                    r++;
                }
            }
            // backward step
            else
            {
                r--;
                if(r >= 0)
                {
                    i = indices[r]+1;
                }
            }
        }
    }

    private static void permutation(LinkedList<Card> perm, int pos, LinkedList<Card> cards, Function<LinkedList<Card>, Void> callback) {
        if (pos == perm.size()) {
            callback.apply(perm);
        } else {
            for (int i = 0 ; i < cards.size() ; i++) {
                perm.set(pos, cards.get(i));
                permutation(perm, pos+1, cards, callback);
            }
        }
    }


}
