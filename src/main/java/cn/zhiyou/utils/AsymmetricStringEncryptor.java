package cn.zhiyou.utils;

import org.jasypt.encryption.StringEncryptor;

import javax.crypto.Cipher;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * @author Memory
 * @since 2024/4/9
 */
public class AsymmetricStringEncryptor implements StringEncryptor {
    private static final String PRIVATE_KEY_HEADER = "-----BEGIN PRIVATE KEY-----";
    private static final String PUBLIC_KEY_HEADER = "-----BEGIN PUBLIC KEY-----";
    private static final String PRIVATE_KEY_FOOTER = "-----END PRIVATE KEY-----";
    private static final String PUBLIC_KEY_FOOTER = "-----END PUBLIC KEY-----";

    private String publicKeyStr;
    private String privateKeyStr;
    private KeyFormat keyFormat;
    private PublicKey publicKey;
    private PrivateKey privateKey;

    public AsymmetricStringEncryptor(String publicKeyStr, String privateKeyStr) {
        this.publicKeyStr = publicKeyStr;
        this.privateKeyStr = privateKeyStr;
        this.keyFormat = KeyFormat.DER;
        initKey();
    }

    public AsymmetricStringEncryptor(String publicKeyStr, String privateKeyStr, KeyFormat keyFormat) {
        this.publicKeyStr = publicKeyStr;
        this.privateKeyStr = privateKeyStr;
        this.keyFormat = keyFormat;
        initKey();
    }

    @Override
    public String encrypt(String message) {
        byte[] encryptBytes;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            encryptBytes = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return Base64.getEncoder().encodeToString(encryptBytes);
    }

    @Override
    public String decrypt(String encryptedMessage) {
        byte[] bytes = Base64.getDecoder().decode(encryptedMessage);

        byte[] decryptBytes;
        try {
            final Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            decryptBytes = cipher.doFinal(bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new String(decryptBytes, StandardCharsets.UTF_8);
    }

    private void initKey() {
        // 私钥字节
        byte[] privateKeyBytes = keyFormat == KeyFormat.DER ? Base64.getDecoder().decode(privateKeyStr) : privateKeyStr.getBytes(StandardCharsets.UTF_8);
        // 公钥字节
        byte[] publicKeyBytes = keyFormat == KeyFormat.DER ? Base64.getDecoder().decode(publicKeyStr) : publicKeyStr.getBytes(StandardCharsets.UTF_8);

        try {
            this.privateKey = getPrivateKey(privateKeyBytes, keyFormat);
            this.publicKey = getPublicKey(publicKeyBytes, keyFormat);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public PrivateKey getPrivateKey(byte[] keyBytes, KeyFormat format) throws Exception {
        if (format == KeyFormat.PEM) {
            keyBytes = decodePem(keyBytes, PRIVATE_KEY_HEADER, PRIVATE_KEY_FOOTER);
        }
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePrivate(spec);
    }


    public PublicKey getPublicKey(byte[] keyBytes, KeyFormat format) throws Exception {
        if (format == KeyFormat.PEM) {
            keyBytes = decodePem(keyBytes, PUBLIC_KEY_HEADER, PUBLIC_KEY_FOOTER);
        }
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        return kf.generatePublic(spec);
    }

    private byte[] decodePem(byte[] bytes, String... headers) {
        String pem = new String(bytes, StandardCharsets.UTF_8);
        for (String header : headers) {
            pem = pem.replace(header, "");
        }
        return Base64.getMimeDecoder().decode(pem);
    }

    public static AsymmetricStringEncryptor of(String publicKey, String privateKey) {
        return new AsymmetricStringEncryptor(publicKey, privateKey);
    }

    public static AsymmetricStringEncryptor of(String publicKey, String privateKey, KeyFormat keyFormat) {
        return new AsymmetricStringEncryptor(publicKey, privateKey, keyFormat);
    }

    public String getPublicKeyStr() {
        return publicKeyStr;
    }

    public void setPublicKeyStr(String publicKeyStr) {
        this.publicKeyStr = publicKeyStr;
    }

    public String getPrivateKeyStr() {
        return privateKeyStr;
    }

    public void setPrivateKeyStr(String privateKeyStr) {
        this.privateKeyStr = privateKeyStr;
    }

    public KeyFormat getKeyFormat() {
        return keyFormat;
    }

    public void setKeyFormat(KeyFormat keyFormat) {
        this.keyFormat = keyFormat;
    }

    public enum KeyFormat {
        DER,
        PEM;
    }
}
