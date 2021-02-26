package com.iasia.collection;

import java.util.List;
import java.util.Random;

public class ListUtility {

    public static <T> T next(List<T> list, Random random) {
        if (list.isEmpty()) {
            throw new IndexOutOfBoundsException();
        }

        var index = random.nextInt(list.size());
        return list.get(index);
    }
}
