package com.wms.utils;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 加密工具单元测试
 */
@DisplayName("加密工具测试")
public class EncryptUtilTest {

    @Test
    @DisplayName("AES加密解密测试")
    void testEncryptDecrypt() {
        String plainText = "13800138000";
        
        // 加密
        String encrypted = EncryptUtil.encrypt(plainText);
        assertNotNull(encrypted);
        assertNotEquals(plainText, encrypted);
        
        // 解密
        String decrypted = EncryptUtil.decrypt(encrypted);
        assertEquals(plainText, decrypted);
    }

    @Test
    @DisplayName("加密空字符串")
    void testEncryptEmpty() {
        assertEquals("", EncryptUtil.encrypt(""));
        assertNull(EncryptUtil.encrypt(null));
    }

    @Test
    @DisplayName("手机号脱敏测试")
    void testMaskPhone() {
        assertEquals("138****8000", EncryptUtil.maskPhone("13800138000"));
        assertEquals("150****1234", EncryptUtil.maskPhone("15012341234"));
        assertNull(EncryptUtil.maskPhone(null));
        assertEquals("123", EncryptUtil.maskPhone("123")); // 非11位不处理
    }

    @Test
    @DisplayName("邮箱脱敏测试")
    void testMaskEmail() {
        assertEquals("ex***@gmail.com", EncryptUtil.maskEmail("example@gmail.com"));
        assertEquals("ab***@qq.com", EncryptUtil.maskEmail("abc@qq.com"));
        assertEquals("*@test.com", EncryptUtil.maskEmail("a@test.com"));
        assertNull(EncryptUtil.maskEmail(null));
        assertEquals("invalid", EncryptUtil.maskEmail("invalid"));
    }

    @Test
    @DisplayName("身份证号脱敏测试")
    void testMaskIdCard() {
        assertEquals("110101********1234", EncryptUtil.maskIdCard("110101199001011234"));
        assertEquals("110101********1234", EncryptUtil.maskIdCard("110101900101123")); // 15位
        assertNull(EncryptUtil.maskIdCard(null));
    }

    @Test
    @DisplayName("姓名脱敏测试")
    void testMaskName() {
        assertEquals("*", EncryptUtil.maskName("张"));
        assertEquals("*三", EncryptUtil.maskName("张三"));
        assertEquals("李**", EncryptUtil.maskName("李小龙"));
        assertEquals("欧阳**", EncryptUtil.maskName("欧阳娜娜"));
        assertNull(EncryptUtil.maskName(null));
    }
}
