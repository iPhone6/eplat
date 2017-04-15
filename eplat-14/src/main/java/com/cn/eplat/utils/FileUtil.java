package com.cn.eplat.utils;

//package cn.outofmemory.util;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.alibaba.fastjson.JSONObject;

/**
 * 文件操作代码
 * 
 * @author cn.outofmemory
 * @date 2013-1-7
 */
public class FileUtil {
    /**
     * 将文本文件中的内容读入到buffer中
     * @param buffer buffer
     * @param filePath 文件路径
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = new FileInputStream(filePath);
        String line; // 用来保存每行读取的内容
        BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
        line = reader.readLine(); // 读取第一行
        while (line != null) { // 如果 line 为空说明读完了
            buffer.append(line); // 将读到的内容添加到 buffer 中
            buffer.append("\n"); // 添加换行符
            line = reader.readLine(); // 读取下一行
        }
        reader.close();
        is.close();
    }

    /**
     * 读取文本文件内容
     * @param filePath 文件所在路径
     * @return 文本内容
     * @throws IOException 异常
     * @author cn.outofmemory
     * @date 2013-1-7
     */
    public static String readFile(String filePath) throws IOException {
        StringBuffer sb = new StringBuffer();
        FileUtil.readToBuffer(sb, filePath);
        return sb.toString();
    }
    
    public static void main(String[] args) {
		String file_path = "D:/TempDataDpan/10.FakeJsonData/data_1.json";
		try {
			String file_content_str = FileUtil.readFile(file_path);
			System.out.println(file_content_str);
			Object json = JSONObject.parse(file_content_str);
			System.out.println(json.getClass());
			System.out.println(json);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		Object json_obj = JSONObject.parse("{'aaa':'123','bbb':'321'}");
//		System.out.println(json_obj.getClass());
//		System.out.println(json_obj);
	}
}

