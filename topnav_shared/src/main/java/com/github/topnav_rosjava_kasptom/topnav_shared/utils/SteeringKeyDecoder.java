package com.github.topnav_rosjava_kasptom.topnav_shared.utils;

public class SteeringKeyDecoder {
    private static final short KEY_UP = 1;
    private static final short KEY_DOWN = 2;
    private static final short KEY_LEFT = 4;
    private static final short KEY_RIGHT = 8;

    public static short encode(boolean isUpPressed, boolean isDownPressed, boolean isLeftPressed, boolean isRightPressed) {
        short sum = 0;
        if (isUpPressed) sum += KEY_UP;
        if (isDownPressed) sum += KEY_DOWN;
        if (isLeftPressed) sum += KEY_LEFT;
        if (isRightPressed) sum += KEY_RIGHT;

        return sum;
    }

    public static boolean[] decode(short sum) {
        if (sum == 0) return new boolean[]{false, false, false, false};
        else if (sum == 1) return new boolean[]{true, false, false, false};
        else if (sum == 2) return new boolean[]{false, true, false, false};
        else if (sum == 3) return new boolean[]{true, true, false, false};
        else if (sum == 4) return new boolean[]{false, false, true, false};
        else if (sum == 5) return new boolean[]{true, false, true, false};
        else if (sum == 6) return new boolean[]{false, true, true, false};
        else if (sum == 7) return new boolean[]{true, true, true, false};
        else if (sum == 8) return new boolean[]{false, false, false, true};
        else if (sum == 9) return new boolean[]{true, false, false, true};
        else if (sum == 10) return new boolean[]{false, true, false, true};
        else if (sum == 11) return new boolean[]{true, true, false, true};
        else if (sum == 12) return new boolean[]{false, false, true, true};
        else if (sum == 13) return new boolean[]{true, false, true, true};
        else if (sum == 14) return new boolean[]{false, true, true, true};
        else if (sum == 15) return new boolean[]{true, true, true, true};
        else throw new RuntimeException(String.format("Invalid sum value %d", sum));
    }
}
