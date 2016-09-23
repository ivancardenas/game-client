
package co.edu.eafit.dis.checksum;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1 {

    public static String checksum(String input) {

        StringBuilder key = new StringBuilder();
        
        try {
            MessageDigest string = MessageDigest.getInstance("SHA1");

            byte[] result = string.digest(input.getBytes());
            
            for (int i = 0; i < result.length; i++) {
                key.append(Integer.toString(
                        (result[i] & 0xff) + 0x100, 16).substring(1));
            }

        } catch (NoSuchAlgorithmException e) {
            System.out.println("SHA1 algorithm exception: " + e);
        }

        return key.toString();
    }
}