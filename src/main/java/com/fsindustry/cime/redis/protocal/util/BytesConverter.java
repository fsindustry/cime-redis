package com.fsindustry.cime.redis.protocal.util;

/**
 * 将long转换为bytes数组
 *
 * @author fuzhengxin
 */
public class BytesConverter {

    /**
     * 缓存数量
     */
    private static final int CACHE_SIZE = 256;

    private static final int MAX_UINT16 = 65536;

    /**
     * 缓存0~255对应long的byte数组
     */
    private static final byte[][] LONG_CACHE = new byte[CACHE_SIZE][];

    private static final char[] DIGITTENS =
            {'0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1', '1', '1',
                    '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3', '3',
                    '3',
                    '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5', '5',
                    '5',
                    '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7', '7',
                    '7',
                    '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9', '9',
                    '9',};

    private static final char[] DIGITONES =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
                    '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
                    '6',
                    '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
                    '7',
                    '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7',
                    '8',
                    '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8',
                    '9',};

    private static final char[] DIGITS =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f', 'g',
                    'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};

    private static final int[] SIZETABLE =
            {9, 99, 999, 9999, 99999, 999999, 9999999, 99999999, 999999999, Integer.MAX_VALUE};

    public static byte[] convert(long i) {
        // 如果命中缓存，则直接返回
        if (i >= 0 && i < CACHE_SIZE) {
            return LONG_CACHE[(int) i];
        }
        return toChars(i);
    }

    private static byte[] toChars(long i) {
        int size = (i < 0) ? stringSize(-i) + 1 : stringSize(i);
        byte[] buf = new byte[size];
        getChars(i, size, buf);
        return buf;
    }

    /**
     * Requires positive x
     */
    private static int stringSize(long x) {
        for (int i = 0; ; i++) {
            if (x <= SIZETABLE[i]) {
                return i + 1;
            }
        }
    }

    private static void getChars(long i, int index, byte[] buf) {
        long q, r;
        int charPos = index;
        byte sign = 0;

        if (i < 0) {
            sign = '-';
            i = -i;
        }

        // Generate two digits per iteration
        while (i >= MAX_UINT16) {
            q = i / 100;
            // really: r = i - (q * 100);
            r = i - ((q << 6) + (q << 5) + (q << 2));
            i = q;
            buf[--charPos] = (byte) DIGITONES[(int) r];
            buf[--charPos] = (byte) DIGITTENS[(int) r];
        }

        // Fall thru to fast mode for smaller numbers
        // assert(i <= 65536, i);
        for (; ; ) {
            q = (i * 52429) >>> (16 + 3);
            // r = i-(q*10) ...
            r = i - ((q << 3) + (q << 1));
            buf[--charPos] = (byte) DIGITS[(int) r];
            i = q;
            if (i == 0) {
                break;
            }
        }
        if (sign != 0) {
            buf[--charPos] = sign;
        }
    }

    /**
     * 缓存预热
     */
    static {
        for (int i = 0; i < CACHE_SIZE; i++) {
            byte[] value = toChars(i);
            LONG_CACHE[i] = value;
        }
    }
}
