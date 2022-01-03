package test.ginrummy.game;

import org.junit.jupiter.api.Test;
import edu.maastricht.ginrummy.util.ListUtil;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ListUtilTest {

    @Test
    public void testFindMaxIdx() {
        var a = new ArrayList<>(Arrays.asList(1,2,3,4,5,6));
        assertEquals(5, ListUtil.idxOfMaxValue(a));
        a = new ArrayList<>(Arrays.asList(1,2,3,4,10,6,2,1));
        assertEquals(4, ListUtil.idxOfMaxValue(a));
    }
}
