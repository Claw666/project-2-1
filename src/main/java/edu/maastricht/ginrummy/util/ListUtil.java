package edu.maastricht.ginrummy.util;

import java.util.ArrayList;
import java.util.List;

public class ListUtil
{
    public static int idxOfMaxValue(ArrayList<Integer> list)
    {
        var idxMaxValue = 0;
        int maxValue = list.get(0);
        for (int i = 1; i < list.size(); i++) {
            var e = list.get(i);
            if (e > maxValue) {
                maxValue = e;
                idxMaxValue = i;
            }
        }
        return idxMaxValue;
    }
}
