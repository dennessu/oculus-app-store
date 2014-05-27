import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.Cipher
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
    println("key=" + byteArrayToHex(skey.getEncoded())+"\n")
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
        def value = byteArrayToHex(cipher.doFinal(message.getBytes("UTF-8")))
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

String byteArrayToHex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();

    for (int index = 0; index < bytes.length; index ++) {
        sb.append(byteToChar((byte)(bytes[index] & 0x0F)));
        sb.append(byteToChar((byte)((bytes[index] & 0xF0) >> 4)));
    }
    return sb.toString();
}

char byteToChar(byte b) {
    if (b < 0 || b >=16) {
        throw new IllegalArgumentException("byte is too large [0..15].");
    }

    if (b >= 0 && b <= 9) {
        return (char)(((Integer)'0') + (Integer)b)
    }

    if (b >= 10 && b <= 15) {
        return (char)(((Integer)'A') + (Integer)(b - 10))
    }
}

byte extractByte(char c) {
    if (c >= '0' && c <='9') {
        return (byte)(((Integer)c - (Integer)'0') & 0x0F);
    }

    if (c >= 'A' && c <= 'F') {
        return (byte)((((Integer)c - (Integer)'A') & 0x0F) + 0x0A);
    }

    throw new IllegalArgumentException("Incorrect hex character");
}

byte[] hexStringToByteArray(String s) {
    byte[] bytes = new byte[s.length() / 2];
    for (int index = 0; index < s.length(); ) {
        byte tempLow = extractByte(s.charAt(index));
        byte tempHigh = extractByte(s.charAt(index + 1));

        byte value = (byte)(tempHigh << 4 | tempLow);
        bytes[index/2] = value;
        index += 2;
    }

    return bytes;
}

SecretKey stringToKey(String keyStr) {
    byte [] bytes = hexStringToByteArray(keyStr)
    return new SecretKeySpec(bytes, 0, bytes.length, 'AES');
}