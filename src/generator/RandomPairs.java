package generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RandomPairs {

    public List<int[]> generateDistinctPairs(int count) {
        List<int[]> allPairs = new ArrayList<>();

        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                allPairs.add(new int[]{r, c});
            }
        }

        Collections.shuffle(allPairs);
        return allPairs.subList(0, count);
    }
}
