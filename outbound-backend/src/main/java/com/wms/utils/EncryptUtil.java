package com.wms.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密工具类
 * 用于敏感数据（手机号、邮箱等）的加密和解密
 */
@Slf4j
@Component
public class EncryptUtil {

    /**
     * 加密算法
     */
    private static final String ALGORITHM = "AES";
    
    /**
     * 密钥（16字节）
     * 生产环境应从配置中心或环境变量获取
     */
    private static final String SECRET_KEY = "wms-encrypt-key!";

    /**
     * AES加密
     *
     * @param plainText 明文
     * @return 密文（Base64编码）
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception e) {
            log.error("加密失败: {}", e.getMessage());
            return plainText;
        }
    }

    /**
     * AES解密
     *
     * @param cipherText 密文（Base64编码）
     * @return 明文
     */
    public static String decrypt(String cipherText) {
        if (cipherText == null || cipherText.isEmpty()) {
            return cipherText;
        }
        try {
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decoded = Base64.getDecoder().decode(cipherText);
            byte[] decrypted = cipher.doFinal(decoded);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("解密失败: {}", e.getMessage());
            return cipherText;
        }
    }

    /**
     * 手机号脱敏
     * 示例：13800138000 -> 138****8000
     */
    public static String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     * 示例：example@gmail.com -> ex***@gmail.com
     */
    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, atIndex);
        String suffix = email.substring(atIndex);
        
        if (prefix.length() <= 2) {
            return "*" + suffix;
        }
        
        return prefix.substring(0, 2) + "***" + suffix;
    }

    /**
     * 身份证号脱敏
     * 示例：110101199001011234 -> 110101********1234
     */
    public static String maskIdCard(String idCard) {
        if (idCard == null || (idCard.length() != 15 && idCard.length() != 18)) {
            return idCard;
        }
        return idCard.substring(0, 6) + "********" + idCard.substring(14);
    }

    /**
     * 姓名脱敏
     * 示例：张三 -> *三，欧阳娜娜 -> 欧阳**
     */
    public static String maskName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        if (name.length() == 1) {
            return "*";
        }
        if (name.length() == 2) {
            return "*" + name.substring(1);
        }
        // 复姓处理
        String[] compoundSurnames = {"欧阳", "太史", "端木", "上官", "司马", "东方", "独孤", "南宫", 
                                     "夏侯", "诸葛", "尉迟", "皇甫", "公孙", "慕容", "轩辕"};
        for (String surname : compoundSurnames) {
            if (name.startsWith(surname)) {
                return surname + "**";
            }
        }
        return name.charAt(0) + "**";
    }
}
