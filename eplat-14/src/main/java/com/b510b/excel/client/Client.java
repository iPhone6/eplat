package com.b510b.excel.client;

/**
 * 
 */
//package com.b510.excel.client;

import java.io.IOException;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.b510b.common.Common;
import com.b510b.excel.ReadExcel;
import com.b510b.excel.vo.Employee;
import com.b510b.excel.vo.Student;
import com.cn.eplat.model.EpUser;

/**
 * @author Hongten
 * @created 2014-5-21
 */
public class Client {

    public static void main(String[] args) throws IOException {
    	/*
        String excel2003_2007 = Common.STUDENT_INFO_XLS_PATH;
        String excel2010 = Common.STUDENT_INFO_XLSX_PATH;
        // read the 2003-2007 excel
        List<Student> list = new ReadExcel().readExcel(excel2003_2007);
        if (list != null) {
            for (Student student : list) {
                System.out.println("No. : " + student.getNo() + ", name : " + student.getName() + ", age : " + student.getAge() + ", score : " + student.getScore());
            }
        }
        System.out.println("======================================");
        // read the 2010 excel
        List<Student> list1 = new ReadExcel().readExcel(excel2010);
        if (list1 != null) {
            for (Student student : list1) {
                System.out.println("No. : " + student.getNo() + ", name : " + student.getName() + ", age : " + student.getAge() + ", score : " + student.getScore());
            }
        }
        */
    	
    	/*
        String excel_emp_info = Common.EMP_INFO_XLS_PATH;
        List<Employee> emp_list = new ReadExcel().readXls_emp(excel_emp_info);
        if(emp_list != null) {
        	for(Employee emp:emp_list) {
        		System.out.println(emp);
//        		JSONObject json = (JSONObject) JSONObject.toJSON(emp);
//        		ja.add(json);
        	}
        	
        	System.out.println("------分割线------");
        	
        	JSONArray ja = new JSONArray();
        	ja.addAll(emp_list);
        	// JSON.toJSONString(jsonObj, SerializerFeature.WriteMapNullValue);
        	System.out.println(JSON.toJSONString(ja, SerializerFeature.WriteMapNullValue));
        	
        }
        */
    	
    	
    	
    	/*
        String excel_ep_emp_info = Common.EP_EMP_INFO_XLS_PATH;
        List<EpUser> epu_list = new ReadExcel().readXls_ep_emp(excel_ep_emp_info);
        if(epu_list != null) {
        	for(EpUser epu:epu_list) {
        		System.out.println(epu);
        	}
        	
        	System.out.println("------分割线------");
        	
//        	JSONArray ja = new JSONArray();
//        	ja.addAll(epu_list);
//        	System.out.println(JSON.toJSONString(ja, SerializerFeature.WriteMapNullValue));
        	
        }
        */
    	
    	
    	
        String excel_ep_emp_info_extra = Common.EP_EMP_INFO_EXTRA_XLS_PATH;
        List<EpUser> epu_list = new ReadExcel().readXls_ep_emp_extra(excel_ep_emp_info_extra);
        if(epu_list != null) {
        	for(EpUser epu:epu_list) {
        		System.out.println(epu);
        	}
        	
        	System.out.println("------分割线------");
        	
//        	JSONArray ja = new JSONArray();
//        	ja.addAll(epu_list);
//        	System.out.println(JSON.toJSONString(ja, SerializerFeature.WriteMapNullValue));
        	
        }
    	 
    	
    	
    	
    	
    }
}
