package me.caomr.utils;

public class HttpUtil {


    public static String findPath(byte[] src) {
        int nextLineBreak = findNextLineBreak(src, 0, src.length);
        byte[] bytes = copyBytes(src, 0, nextLineBreak);
        return getPath(bytes);
    }

    public static int findNextLineBreak(byte[] src, int startIndex, int endIndex) {
        for (int index = startIndex; index < endIndex; index++) {
            if (src[index] == '\n') {
                if (src[index - 1] == '\r') {
                    return index;
                }
            }
        }
        return -1;
    }


    public static byte[] copyBytes(byte[] bytes, int start, int end) {
        byte[] newBytes = new byte[end - start];
        System.arraycopy(bytes, 0, newBytes, 0, newBytes.length);
        return newBytes;
    }


    public static String getPath(byte[] bytes) {
        String line = new String(bytes);
        String[] split = line.split(" ");
        if (split.length == 3) {
            return split[1];
        }
        return null;
    }
}
