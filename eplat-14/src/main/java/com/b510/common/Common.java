/**
 * 
 */
package com.b510.common;

/**
 * @author Hongten
 * @created 2014-5-18
 */
public class Common {

	// connect the database
	/*
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_NAME = "test";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "root";
	public static final String IP = "192.168.1.103";
	public static final String PORT = "3306";
	public static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB_NAME;
	*/
	public static final String DRIVER = "com.mysql.jdbc.Driver";
	public static final String DB_NAME = "javenforexcel";
	public static final String USERNAME = "root";
	public static final String PASSWORD = "666666";
	public static final String IP = "192.168.1.98";
	public static final String PORT = "3306";
	public static final String URL = "jdbc:mysql://" + IP + ":" + PORT + "/" + DB_NAME;
	
	// common
//	public static final String EXCEL_PATH = "lib/student_info.xls";
	public static final String EXCEL_PATH = "E:/TempE/14/student_info3.xls";

	// sql
	public static final String INSERT_STUDENT_SQL = "insert into student_info2(no, name, age, score) values(?, ?, ?, ?)";
	public static final String UPDATE_STUDENT_SQL = "update student_info2 set no = ?, name = ?, age= ?, score = ? where id = ? ";
	public static final String SELECT_STUDENT_ALL_SQL = "select id,no,name,age,score from student_info2";
	public static final String SELECT_STUDENT_SQL = "select * from student_info2 where name like ";
}
