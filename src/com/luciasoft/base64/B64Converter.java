package com.luciasoft.base64;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

public class B64Converter
{
    public static byte[] encode(byte[] data, boolean urlSafe)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        for (int i = 0; i < data.length; i += 3)
        {
            int length = Math.min(3, data.length - i);
            byte[] array = Arrays.copyOfRange(data, i, i + length);
            array = convert256to64(array);
            buffer.write(array, 0, array.length);
        }

        data = buffer.toByteArray();
        return convertEncodedDataToText(data, urlSafe);
    }

    public static byte[] decode(byte[] data)
    {
        data = convertEncodedTextToData(data);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        for (int i = 0; i < data.length; i += 4)
        {
            int length = Math.min(4, data.length - i);
            byte[] array = Arrays.copyOfRange(data, i, i + length);
            array = convert64to256(array);
            buffer.write(array, 0, array.length);
        }

        return buffer.toByteArray();
    }

    private static byte[] convert256to64(byte[] array)
    {
        int iVal = 0;
        for (int i = 0; i < array.length; i++)
        {
            int b256 = array[i] & 255;
            iVal |= b256 << ((2 - i) * 8);
        }

        array = new byte[array.length + 1];

        for (int i = 0; i < array.length; i++)
        {
            int b64 = (iVal >> ((3 - i) * 6)) & 63;
            array[i] = (byte)b64;
        }

        return array;
    }

    private static byte[] convert64to256(byte[] array)
    {
        int iVal = 0;
        for (int i = 0; i < array.length; i++)
        {
            int b64 = array[i] & 63;
            iVal |= b64 << ((3 - i) * 6);
        }

        array = new byte[array.length - 1];

        for (int i = 0; i < array.length; i++)
        {
            int b256 = (iVal >> ((2 - i) * 8)) & 255;
            array[i] = (byte)b256;
        }

        return array;
    }

    private static byte[] convertEncodedDataToText(byte[] data, boolean urlSafe)
    {
        char[] table = urlSafe ? B64_CHARS_URLSAFE : B64_CHARS_DEFAULT;
        int length = data.length;
        for (int i = 0; i < length; i++) data[i] = (byte)table[data[i]];
        if (!urlSafe)
        {
            int extra = length % 4;
            if (extra > 0)
            {
                data = Arrays.copyOf(data, length + (4 - extra));
                for (int i = length; i < data.length; i++) data[i] = '=';
            }
        }
        return data;
    }

    private static byte[] convertEncodedTextToData(byte[] data)
    {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        boolean ending = false;
        for (byte b : data)
        {
            char ch = (char)b;
            if (ch == '=')
            {
                ending = true;
                continue;
            }
            int index = REVERSE_LOOKUP[ch];
            if (index == -2) continue; // b = 9 (tab), 10 (lf), 13 (cr) or 32 (spc)
            if (ending || index == -1)
                throw new IllegalArgumentException("Invalid char val=" + (int)ch);
            buffer.write((byte)index);
        }
        return buffer.toByteArray();
    }

    private final static char[] B64_CHARS_DEFAULT = {
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/' };
    private final static char[] B64_CHARS_URLSAFE = {
        'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
        'Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f',
        'g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v',
        'w','x','y','z','0','1','2','3','4','5','6','7','8','9','-','_' };
    private final static int[] REVERSE_LOOKUP = {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -2, -2, -1, -1, -2, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63,
        52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1,
        -1,  0,  1,  2,  3,  4,  5,  6,  7,  8,  9, 10, 11, 12, 13, 14,
        15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63,
        -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40,
        41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
        -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1 };

}
