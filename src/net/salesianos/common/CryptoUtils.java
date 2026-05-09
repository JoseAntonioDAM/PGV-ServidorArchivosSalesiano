package net.salesianos.common;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final String ALGORITHM = "AES";
    // Clave fija de 128 bits (16 bytes) compartida entre cliente y servidor
    // En un proyecto real esto vendría de un fichero de config o variable de entorno
    private static final byte[] KEY_BYTES = "MiClaveSecreta16".getBytes(); // exactamente 16 chars

    private static final SecretKey SECRET_KEY = new SecretKeySpec(KEY_BYTES, ALGORITHM);

    // Cifra un array de bytes y devuelve los bytes cifrados
    public static byte[] encrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, SECRET_KEY);
        return cipher.doFinal(data);
    }

    // Descifra un array de bytes y devuelve los bytes originales
    public static byte[] decrypt(byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, SECRET_KEY);
        return cipher.doFinal(data);
    }

    // Versión para Strings (para el nombre del fichero)
    public static byte[] encryptString(String text) throws Exception {
        return encrypt(text.getBytes());
    }

    public static String decryptToString(byte[] data) throws Exception {
        return new String(decrypt(data));
    }
}
