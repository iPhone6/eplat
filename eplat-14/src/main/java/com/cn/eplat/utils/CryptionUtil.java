 package com.cn.eplat.utils;
 
 /*jadclipse*/// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.

 import java.security.*;

import javax.crypto.*;

import org.apache.shiro.crypto.hash.Md5Hash;

 public class CryptionUtil
 {

     public CryptionUtil()
     {
     }

     private static String encode(byte data[])
     {
         char out[] = new char[((data.length + 2) / 3) * 4];
         int i = 0;
         for(int index = 0; i < data.length; index += 4)
         {
             boolean quad = false;
             boolean trip = false;
             int val = 255 & data[i];
             val <<= 8;
             if(i + 1 < data.length)
             {
                 val |= 255 & data[i + 1];
                 trip = true;
             }
             val <<= 8;
             if(i + 2 < data.length)
             {
                 val |= 255 & data[i + 2];
                 quad = true;
             }
             out[index + 3] = alphabet[quad ? val & 63 : 64];
             val >>= 6;
             out[index + 2] = alphabet[trip ? val & 63 : 64];
             val >>= 6;
             out[index + 1] = alphabet[val & 63];
             val >>= 6;
             out[index + 0] = alphabet[val & 63];
             i += 3;
         }

         return new String(out);
     }

     private static byte[] decode(char data[])
     {
         int len = ((data.length + 3) / 4) * 3;
         if(data.length > 0 && data[data.length - 1] == '=')
             len--;
         if(data.length > 1 && data[data.length - 2] == '=')
             len--;
         byte out[] = new byte[len];
         int shift = 0;
         int accum = 0;
         int index = 0;
         for(int ix = 0; ix < data.length; ix++)
         {
             int value = codes[data[ix] & 255];
             if(value >= 0)
             {
                 accum <<= 6;
                 shift += 6;
                 accum |= value;
                 if(shift >= 8)
                 {
                     shift -= 8;
                     out[index++] = (byte)(accum >> shift & 255);
                 }
             }
         }

         if(index != out.length)
             throw new Error("miscalculated data length!");
         else
             return out;
     }

     public static String encryption(String plainData, String secretKey)
     {
         String result = null;
         try
         {
             Cipher cipher = Cipher.getInstance("DES");
             cipher.init(1, generateKey(secretKey));
             byte buf[] = cipher.doFinal(plainData.getBytes());
             result = encode(buf);
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
         return result;
     }

     public static String decryption(String secretData, String secretKey)
     {
         String result = null;
         try
         {
             Cipher cipher = Cipher.getInstance("DES");
             cipher.init(2, generateKey(secretKey));
             byte buf[] = cipher.doFinal(decode(secretData.toCharArray()));
             result = new String(buf);
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
         return result;
     }

     private static SecretKey generateKey(String secretKey)
         throws NoSuchAlgorithmException
     {
         SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
         secureRandom.setSeed(secretKey.getBytes());
         KeyGenerator kg = null;
         try
         {
             kg = KeyGenerator.getInstance("DES");
         }
         catch(NoSuchAlgorithmException nosuchalgorithmexception) { }
         kg.init(secureRandom);
         return kg.generateKey();
     }

     private static String byteArray2HexStingr(byte bs[])
     {
         StringBuffer sb = new StringBuffer();
         byte abyte0[];
         int j = (abyte0 = bs).length;
         for(int i = 0; i < j; i++)
         {
             byte b = abyte0[i];
             sb.append(byte2HexString(b));
         }

         return sb.toString();
     }

     private static String byte2HexString(byte b)
     {
         String hexStr = null;
         int n = b;
         if(n < 0)
             n = b & 255;
         hexStr = (new StringBuilder(String.valueOf(Integer.toHexString(n / 16)))).append(Integer.toHexString(n % 16)).toString();
         return hexStr.toUpperCase();
     }

     public static String md5Hex(String origString)
     {
         String origMD5 = null;
         try
         {
             MessageDigest md5 = MessageDigest.getInstance("MD5");
             byte result[] = md5.digest(origString.getBytes());
             origMD5 = byteArray2HexStingr(result);
         }
         catch(Exception e)
         {
             e.printStackTrace();
         }
         return origMD5;
     }

     private static byte codes[];
     private static final String DES_ALGORITHM = "DES";
     private static char alphabet[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=".toCharArray();

     static 
     {
         codes = new byte[256];
         for(int i = 0; i < 256; i++)
             codes[i] = -1;

         for(int i = 65; i <= 90; i++)
             codes[i] = (byte)(i - 65);

         for(int i = 97; i <= 122; i++)
             codes[i] = (byte)((26 + i) - 97);

         for(int i = 48; i <= 57; i++)
             codes[i] = (byte)((52 + i) - 48);

         codes[43] = 62;
         codes[47] = 63;
     }
     
     public static void main(String[] args) {
    	String THE_KEY = "eplat2016";
 		String result = encryption("123", THE_KEY);
 		System.out.println("result = " + result);
// 		System.out.println("md5 of result = " + new Md5Hash(result));
 		System.out.println("//////////////////////");
 		System.out.println(encryption(new Md5Hash("123").toString(), THE_KEY));
 		System.out.println("//////////////////////");
 		
 		System.out.println(decryption("+rrlXHOoJAU=", THE_KEY));
 		
 		System.out.println("md5 crypted for `123` = " + md5Hex("123"));
 		System.out.println("md5 crypted for `123456` = " + md5Hex("123456"));
 		System.out.println("md5 crypted for `111111` = " + md5Hex("111111"));
 		System.out.println("md5 crypted for `tpvdml` = " + md5Hex("tpvdml"));
 		System.out.println("md5 crypted for `459828` = " + md5Hex("459828"));
 		System.out.println("md5 crypted for `admin888` = " + md5Hex("admin888") + " | " + new Md5Hash("admin888").toString());
 		
 		/*
 		System.out.println("----------------------------------");
// 		 下面是给项目组成员设置的用于测试的公司系统账号假数据
// 		 任春宇     renchunyu
//		 张洋舜     zhangyangshun
//		徐世船     xushichuan
//		许立强     xuliqiang
//		卢昌昊     luchanghao
//		张兴伟     zhangxingwei
//		陈敬       chenjing
//		蔡廷飞     caitingfei
//		高澜阳     gaolanyang
//		邹华新     zouhuaxin
//		许木军     xumujun
//		陈豪       chenhao
//		佘胜普     sheshengpu
//		李燕山     liyanshan
 		String[] names = {
 				"renchunyu",
 				"zhangyangshun",
 				"xushichuan",
 				"xuliqiang",
 				"luchanghao",
 				"zhangxingwei",
 				"chenjing",
 				"caitingfei",
 				"gaolanyang",
 				"zouhuaxin",
 				"xumujun",
 				"chenhao",
 				"sheshengpu",
 				"liyanshan",
 		};
 		
 		for(String name:names) {
 			System.out.println(name + " : " + md5Hex(name));
 		}
 		*/
 		
 	}
     
 }


 /*
 	DECOMPILATION REPORT

 	Decompiled from: E:\MeSpace\Me2015S3a\IHRBg\lib\agileai_common_1.1.0.jar
 	Total time: 35 ms
 	Jad reported messages/errors:
 	Exit status: 0
 	Caught exceptions:
 */

