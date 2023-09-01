package utils;

import java.util.Random;

public class RandomUtils {

    private static final Random random = new Random();

    private RandomUtils() {

    }

    public static int nextInt(int min, int max) {
        return random.nextInt(min, max + 1);
    }
}
