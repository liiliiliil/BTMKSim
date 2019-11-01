package liiliiliil.btmksim_android_part;

public class Util {

    public static byte[] floatToBytes(float f){
        byte[] b = new byte[4];
        int fbit = Float.floatToIntBits(f);
        for (int i = 3; i > -1; i--){
            b[i] = (byte) fbit;
            fbit = fbit >> 8;
        }
        return b;
    }

    public static byte[] floatArrayToBytes(float[] fs){
        int len_fs = fs.length;
        byte[] b = new byte[len_fs*4];

        for (int i = 0; i < len_fs; i++){
            System.arraycopy(floatToBytes(fs[i]), 0, b, i*4, 4);
        }

        return b;
    }


    public static byte[] mergeTwoBytes(byte[] b1, byte[] b2){
        byte[] b = new byte[b1.length+b2.length];

        System.arraycopy(b1, 0, b, 0, b1.length);
        System.arraycopy(b2, 0, b, b1.length, b2.length);

        return b;
    }

}
