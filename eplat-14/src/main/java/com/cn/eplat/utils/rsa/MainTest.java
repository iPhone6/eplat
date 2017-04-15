package com.cn.eplat.utils.rsa;

public class MainTest {

	public static void main(String[] args) throws Exception {
		/*
		String filepath="E:/TempE/12/";
		filepath = "D:/Java/rsakeys";

//		RSAEncrypt.genKeyPair(filepath);
		
		System.out.println("--------------公钥加密私钥解密过程-------------------");
		String plainText="ihep_公钥加密私钥解密";
		plainText = "123456";
		//公钥加密过程
		byte[] cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)),plainText.getBytes());
		String cipher=Base64.encode(cipherData);
		//私钥解密过程
		byte[] res=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), Base64.decode(cipher));
		String restr=new String(res);
		System.out.println("原文："+plainText);
		System.out.println("加密："+cipher);
		System.out.println("解密："+restr);
		System.out.println();
		
		System.out.println("--------------私钥加密公钥解密过程-------------------");
		plainText="ihep_私钥加密公钥解密";
		plainText = "123456789";
		//私钥加密过程
		cipherData=RSAEncrypt.encrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)),plainText.getBytes());
		cipher=Base64.encode(cipherData);
		//公钥解密过程
		res=RSAEncrypt.decrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)), Base64.decode(cipher));
		restr=new String(res);
		System.out.println("原文2："+plainText);
		System.out.println("加密2："+cipher);
		System.out.println("解密2："+restr);
		System.out.println();
		*/
		
		/*
		System.out.println("---------------私钥签名过程------------------");
		String content="ihep_这是用于签名的原始数据";
		content = "123456789";
		String signstr=RSASignature.sign(content,RSAEncrypt.loadPrivateKeyByFile(filepath));
		System.out.println("签名原串："+content);
		System.out.println("签名串："+signstr);
		System.out.println();
		
		System.out.println("---------------公钥校验签名------------------");
		System.out.println("签名原串："+content);
		System.out.println("签名串："+signstr);
		
		System.out.println("验签结果："+RSASignature.doCheck(content, signstr, RSAEncrypt.loadPublicKeyByFile(filepath)));
		System.out.println();
		*/
		
		
		
		// 公钥加密示例代码
		String filepath2="E:/TempE/13/";
		filepath2 = "D:/Java/rsakeys";
		String plainText2="ihep_公钥加密私钥解密";
		plainText2 = "12345";
		plainText2 = "123";
		plainText2 = "111111";
		plainText2 = "123456";
		plainText2 = "chenjing";
		plainText2 = "123";
//		plainText2 = "8z8735";
		//公钥加密过程
		String pub_key_str = RSAEncrypt.loadPublicKeyByFile(filepath2);
//		System.out.println("public key = " + pub_key_str);
		byte[] cipherData2=RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(pub_key_str),plainText2.getBytes());
		String cipher2=Base64.encode(cipherData2);
		System.out.println("原文2："+plainText2);
		System.out.println("加密2："+cipher2);
		
		
		/*
		// 私钥解密示例代码
		String filepath="E:/TempE/13/";
		filepath = "D:/Java/rsakeys";
		String cipher2 = "jS98TttjDs85YwWCXEEM7nBzxnwsIkBlf7laYdnvH39iC42k9/Sciv//6sEgXz9pUk/AWqobSG6xPf1xW9o8WZ1LtBQ/CeBlqn7dX/yWV4gjRBXICFc5qsS3aXYv2iTXuE7ZBvpKFRtd1kPgIB7VSnk7nhOmJZ/ku1YTJfC5+lg=";
		cipher2 = "IWcacgNSsd8k6JN/bnP/PlZZOsct/XJVTzBVEQuBlfsmKzRIC2oaeLlI+S9aSRd6GktceaDlnEURHhqhOKJTeQq814wQd3CWrdaV7yKYNk5qZsUl/Q1OhUJ5e66yNe98Smf+khIOykem1tho42FbTYr6gY6Lhzg4Kx0HGl3eSyc=";
		cipher2 = "TXDj2V4CxYX+C9xmXoYTsg9DsBqGYRlN9ClckakrXGVfNHco/OuQip3wMABpJP7VQnbSiMyV3cvFmaSvksjUV4m2bleCBi+XoeEWBW2HWPEt4O8pft/GGwsHTsDMOodi5mKh/GsNJgmpKaKm9trNIZnDH0wOYUbdDzEI9v5vwnc=";
		cipher2 = "RSI+8HXi90eP8SpKk1hIBvcgzFfPkAlXAP1QsAgkBCJLN6B46Bnmx7nhKkAP2iJcYYBCpd6PnXf/9foiTdpPPIJ8JjDVxQr8NByKdunzZFvHI3DL3w1+dnZ6Yl+grWxdxdPTR30Yt0BJn/6l2NSSwMrE2+JHODIlCeGqvJWBgzI=";
		cipher2 = "a7iWbeZn+/rBjuSnulVbqY+3zN0QPPUQRekxPf5Un77fRWBpMcG4VRvbPRuv0wbFDmDeOC6CTiiB4Rzf/kVFMP4vxX4tjgipuDUxLUGIS48jyA+/1K5HXBZWhNu9GUrt971G9Ve/AW7pzV7g8D+N+7F2usmpemYelwaSc+Zdfrw=";
		cipher2 = "GcWw8j6B6PqMYtQlEgqv0r7fxt89oQSN+/uk85UEvezAn4vs5QK5eEbEedtacp3qHWmZTe/1HJqIx7bFnqc/YpmAT/AcU8s4gZ73P/YUuX/L7FOQD9i8/vNnL8Hn9mx19VGXHX4cMtXMdREjS5kiGaq5f6XcbS48j889Vz5pDoc=";
//		cipher2 = "lud+NEqOnYGoZmK3/bTegvV8S9MnMbCwWFmcm3vnsdUTGjW3wYAafu0QYrtN2wwpNQadIjVlvpXPA8MDT+5+yLbDHz5bCoGgPRq8IMIK3vLhaSciReI6HBTed892gyYRyHDrVSdWyo05lzcu70wPdDo+6poxO/ij2JBYNQQpVJk=";
//		byte[] res2=RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), Base64.decode(cipher2));
		byte[] res2=RSAEncrypt.decryptAndroid(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), Base64.decode(cipher2));
		String restr2 = new String(res2);
		System.out.println("解密2："+restr2);
		*/
		
		
		
		
		/*
		String filepath="E:/keys/";		// filepath为存放RSA公钥私钥文件的目录路径
		String plainText="abcd1234";	// 要加密的字符串原文
		
		//公钥加密过程
		byte[] cipherData = RSAEncrypt.encrypt(RSAEncrypt.loadPublicKeyByStr(RSAEncrypt.loadPublicKeyByFile(filepath)),plainText.getBytes());
		String cipher = Base64.encode(cipherData);
		System.out.println("原文："+plainText);
		System.out.println("加密："+cipher);
		
		// 私钥解密过程
		byte[] res = RSAEncrypt.decrypt(RSAEncrypt.loadPrivateKeyByStr(RSAEncrypt.loadPrivateKeyByFile(filepath)), Base64.decode(cipher));
		String restr = new String(res);
		System.out.println("解密："+restr);
		*/
		
		
	}
}
