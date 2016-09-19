package org.uma.jmetal.util.grid.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lenovo on 2015/11/8.
 */
public class ShuffleListBuilder {

    public ShuffleListBuilder() {
    }

    public static List<Integer> getShuffleList(int length) {
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < length; i++) {
            list.add(i, i);
        }
        Collections.shuffle(list);
        return list;

    }
}
