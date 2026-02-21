package ibd.table.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

public class UtilConversor {

    public static byte[] longToByteArray(long l){
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putLong(l).array();
    }

    public static byte[] intToByteArray(int i){
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putInt(i).array();
    }

    public static byte[] shortToByteArray(short s){
        return ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(s).array();
    }

    public static byte[] byteToByteArray(byte b){
        return new byte[]{b};
    }

    public static byte[] floatToByteArray(float f){
        return ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN).putFloat(f).array();
    }
    public static byte[] doubleToByteArray(double f){
        return ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putDouble(f).array();
    }

    public static byte[] stringToByteArray(String s){
        return s.getBytes(StandardCharsets.UTF_8);
    }


    public static byte byteArrayToByte(byte[] arr){
        return arr[0];
    }
    public static short byteArrayToShort(byte[] arr){
        return ByteBuffer.wrap(arr,0,2).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    public static int byteArrayToInt(byte[] arr){
        return ByteBuffer.wrap(arr,0,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public static long byteArrayToLong(byte[] arr){
        return ByteBuffer.wrap(arr,0,8).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static float byteArrayToFloat(byte[] arr){
        return ByteBuffer.wrap(arr,0,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    public static double byteArrayToDouble(byte[] arr){
        return ByteBuffer.wrap(arr,0,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    public static String byteArrayToString(byte[] arr){
        return new String(arr,StandardCharsets.UTF_8).trim();
    }
    
    
    public static short byteArrayToShort(byte[] arr, int offset){
        return ByteBuffer.wrap(arr,offset,2).order(ByteOrder.LITTLE_ENDIAN).getShort();
    }
    public static int byteArrayToInt(byte[] arr, int offset){
        return ByteBuffer.wrap(arr,offset,4).order(ByteOrder.LITTLE_ENDIAN).getInt();
    }
    public static long byteArrayToLong(byte[] arr, int offset){
        return ByteBuffer.wrap(arr,offset,8).order(ByteOrder.LITTLE_ENDIAN).getLong();
    }

    public static float byteArrayToFloat(byte[] arr, int offset){
        return ByteBuffer.wrap(arr,offset,4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
    }
    public static double byteArrayToDouble(byte[] arr, int offset){
        return ByteBuffer.wrap(arr,offset,8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
    }

    public static String byteArrayToString(byte[] arr, int offset, int length){
        return new String(arr, offset, length, StandardCharsets.UTF_8).trim();
    }

}
