package libs.crypto.modules;

import android.util.Log;

import com.facebook.react.bridge.WritableMap;

import java.lang.reflect.Method;

import junit.framework.TestCase;

public class CryptoTest extends TestCase {

    private static String TAG = "CryptoTest";
    private CryptoModule cryptoModule = null;

    protected void setUp() throws Exception {
        cryptoModule = new CryptoModule(null);
    }

    protected void tearDown() throws Exception {
        cryptoModule = null;
    }

    public void randomKeyTest(){
        try {
            Method method = CryptoModule.class.getDeclaredMethod("random",  Integer.class);
            method.setAccessible(true);
            String result = (String) method.invoke(cryptoModule, new Object[] {16});
            assertEquals("the length is not same", result.length(), 32);
        }catch (Exception e){
            Log.e(TAG, e.toString());
            assertTrue(false);
        }
    }

    
    public void SHA1Test(){
        try {
            Method method = CryptoModule.class.getDeclaredMethod("shaX", String.class, String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(cryptoModule, new Object[] {"thisisatest", "SHA-1"});

            assertEquals("not got expected result", result, "42d4a62c53350993ea41069e9f2cfdefb0df097d");
        }catch (Exception e){
            Log.e(TAG, e.toString());
            assertTrue(false);
        }
    }

    
    public void SHA256Test(){
        try {
            Method method = CryptoModule.class.getDeclaredMethod("shaX", String.class, String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(cryptoModule, new Object[] {"thisisatest", "SHA-256"});

            assertEquals("not got expected result", result, "a7c96262c21db9a06fd49e307d694fd95f624569f9b35bb3ffacd880440f9787");
        }catch (Exception e){
            Log.e(TAG, e.toString());
            assertTrue(false);
        }
    }

    
    public void SHA512Test(){
        try {
            Method method = CryptoModule.class.getDeclaredMethod("shaX", String.class, String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(cryptoModule, new Object[] {"thisisatest", "SHA-512"});

            assertEquals("not got expected result", result, "d44edf261feb71975ee9275259b2eab75920d312cb1481a024306002dc57bf680e0c3b5a00edb6ffd15969369d8a714ccce1396937a57fd057ab312cb6c6d8b6");
        }catch (Exception e){
            Log.e(TAG, e.toString());
            assertTrue(false);
        }
    }

    
    public void HMAC256Test(){
        try {
            Method method = CryptoModule.class.getDeclaredMethod("hmac256", String.class, String.class);
            method.setAccessible(true);
            String result = (String) method.invoke(cryptoModule, new Object[] {"thisisatest", "b1ebcf12f5ff0a48b8f76604156a8d52e748"});

            assertEquals("not got expected result", result, "2c5808c4833446895070b2946e6db446fc337a916730b63f46213684e38b4415");
        }catch (Exception e){
            Log.e(TAG, e.toString());
            assertTrue(false);
        }
    }

    
    public void AESTest(){
        String entry = "somemessage";
        String key = "somekey";

        try {
            // encrypt
            Method encryptMethod = CryptoModule.class.getDeclaredMethod("encrypt", String.class, String.class);
            encryptMethod.setAccessible(true);
            WritableMap encResult = (WritableMap) encryptMethod.invoke(cryptoModule, new Object[] {entry, key});


            String cipher = encResult.getString("cipher");
            String iv = encResult.getString("iv");

            assertNotNull("result is null", encResult);
            assertNotNull("iv is not defined", iv);
            assertNotNull("cipher is not defined", cipher);


            //decrypt
            Method decryptMethod = CryptoModule.class.getDeclaredMethod("decrypt", String.class, String.class, String.class);
            decryptMethod.setAccessible(true);
            String decResult = (String) decryptMethod.invoke(cryptoModule, new Object[] {cipher, key, iv });


            assertNotNull("decrypt result is not defined", decResult);
            assertEquals("decrypt result is not as expected", decResult, entry);

        }catch (Exception e){
            Log.e(TAG, e.toString());
            assertTrue(false);
        }
    }
}