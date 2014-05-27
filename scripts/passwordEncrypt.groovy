@Grab(group='commons-codec', module='commons-codec', version='1.7')
import org.apache.commons.codec.binary.Hex

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.Key

if (args == null || args.length == 0) {
    printUsage()
}

if (args[0] == 'genKey' && args.size() == 1) {
    KeyGenerator kgen = KeyGenerator.getInstance("AES");
    // Generate the secret key specs.
    SecretKey skey = kgen.generateKey();
    println("key=" + new String(Hex.encodeHex(skey.getEncoded(), false)))
} else if (args[0] == 'encrypt' && args.size() == 3) {
    byte [] IV = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0]
    String ALGORITHM = 'AES/CBC/PKCS5Padding'

    Key skey = stringToKey(args[1])
    def message = args[2]
    if (message == null) {
        throw new IllegalArgumentException('message is null')
    }
    if (skey == null) {
        throw new IllegalArgumentException('key is null')
    }

    try {
        Cipher cipher = Cipher.getInstance(ALGORITHM)
        IvParameterSpec ivspec = new IvParameterSpec(IV);
        cipher.init(Cipher.ENCRYPT_MODE, skey, ivspec);
        def value = Hex.encodeHex(cipher.doFinal(message.getBytes("UTF-8")), false)
        println("encryptValue="+value+"\n")
    } catch (Exception e) {
        throw new IllegalStateException('Encrypt error: ' + e.message)
    }
} else {
    printUsage()
}

void printUsage() {
    println("groovy passwordEncrypt.groovy [model] [parameters] \n")
    println("Generate key:              groovy passwordEncrypt.groovy genKey")
    println("Generate encrypt value:    groovy passwordEncrypt.groovy encrypt [keyInput] [messageToEncrypt]")
    System.exit(0)
}

SecretKey stringToKey(String keyStr) {
    byte [] bytes = Hex.decodeHex(keyStr.toCharArray())
    return new SecretKeySpec(bytes, 0, bytes.length, 'AES')
}