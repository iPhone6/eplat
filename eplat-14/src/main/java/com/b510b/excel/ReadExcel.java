package com.b510b.excel;

/**
 * 
 */
//package com.b510.excel;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.b510b.common.Common;
import com.b510b.excel.util.Util;
import com.b510b.excel.vo.Employee;
import com.b510b.excel.vo.Student;
import com.cn.eplat.controller.EpAttenController;
import com.cn.eplat.model.EpUser;
import com.cn.eplat.utils.CryptionUtil;
import com.cn.eplat.utils.DateUtil;

/**
 * @author Hongten
 * @created 2014-5-20
 */
public class ReadExcel {
    
    /**
     * read the Excel file
     * @param path the path of the Excel file
     * @return
     * @throws IOException
     */
    public List<Student> readExcel(String path) throws IOException {
        if (path == null || Common.EMPTY.equals(path)) {
            return null;
        } else {
            String postfix = Util.getPostfix(path);
            if (!Common.EMPTY.equals(postfix)) {
                if (Common.OFFICE_EXCEL_2003_POSTFIX.equals(postfix)) {
                    return readXls(path);
                } else if (Common.OFFICE_EXCEL_2010_POSTFIX.equals(postfix)) {
                    return readXlsx(path);
                }
            } else {
                System.out.println(path + Common.NOT_EXCEL_FILE);
            }
        }
        return null;
    }

    /**
     * Read the Excel 2010
     * @param path the path of the excel file
     * @return
     * @throws IOException
     */
    public List<Student> readXlsx(String path) throws IOException {
        System.out.println(Common.PROCESSING + path);
        InputStream is = new FileInputStream(path);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
        Student student = null;
        List<Student> list = new ArrayList<Student>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    student = new Student();
                    XSSFCell no = xssfRow.getCell(0);
                    XSSFCell name = xssfRow.getCell(1);
                    XSSFCell age = xssfRow.getCell(2);
                    XSSFCell score = xssfRow.getCell(3);
                    
                    no.setCellType(XSSFCell.CELL_TYPE_STRING);
                    age.setCellType(XSSFCell.CELL_TYPE_STRING);
                    
                    student.setNo(getValue(no));
                    student.setName(getValue(name));
                    student.setAge(getValue(age));
                    student.setScore(Float.valueOf(getValue(score)));
                    list.add(student);
                }
            }
        }
        return list;
    }

    /**
     * Read the Excel 2003-2007
     * @param path the path of the Excel
     * @return
     * @throws IOException
     */
    public List<Student> readXls(String path) throws IOException {
        System.out.println(Common.PROCESSING + path);
        InputStream is = new FileInputStream(path);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
        Student student = null;
        List<Student> list = new ArrayList<Student>();
        // Read the Sheet
        for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
            HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
            if (hssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
                HSSFRow hssfRow = hssfSheet.getRow(rowNum);
                if (hssfRow != null) {
                    student = new Student();
                    HSSFCell no = hssfRow.getCell(0);
                    HSSFCell name = hssfRow.getCell(1);
                    HSSFCell age = hssfRow.getCell(2);
                    HSSFCell score = hssfRow.getCell(3);
                    
                    no.setCellType(HSSFCell.CELL_TYPE_STRING);
                    age.setCellType(HSSFCell.CELL_TYPE_STRING);
                    
                    student.setNo(getValue(no));
                    student.setName(getValue(name));
                    student.setAge(getValue(age));
                    student.setScore(Float.valueOf(getValue(score)));
                    list.add(student);
                }
            }
        }
        return list;
    }
    
    
    /**
     * 读取将要推送HW考勤系统的员工信息表
     * @param path
     * @return
     * @throws IOException
     */
    public List<EpUser> readXls_push2hw_emp(String path) throws IOException {
    	System.out.println(Common.PROCESSING + path);
    	
    	long read_start = System.currentTimeMillis();
    	
    	InputStream is = new FileInputStream(path);
    	XSSFWorkbook xssfWorkbook = new XSSFWorkbook(is);
    	
    	long read_end = System.currentTimeMillis();
    	
    	System.out.println("读取xlsm表格耗时：" + DateUtil.timeMills2ReadableStr(read_end - read_start));
    	
    	EpUser epu = null;
    	List<EpUser> all_epu_list = new ArrayList<EpUser>();
//    	List<EpUser> valid_epu_list = new ArrayList<EpUser>();
//    	List<EpUser> invalid_epu_list = new ArrayList<EpUser>();
    	
    	 // Read the Sheet
        for (int numSheet = 0; numSheet < xssfWorkbook.getNumberOfSheets(); numSheet++) {
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(numSheet);
            if (xssfSheet == null) {
                continue;
            }
            // Read the Row
            for (int rowNum = 1; rowNum <= xssfSheet.getLastRowNum(); rowNum++) {
                XSSFRow xssfRow = xssfSheet.getRow(rowNum);
                if (xssfRow != null) {
                    epu = new EpUser();
                    XSSFCell name = xssfRow.getCell(0);	// 姓名
                    XSSFCell work_no = xssfRow.getCell(1);	// 工号
                    XSSFCell id_no = xssfRow.getCell(2);	// 身份证号
                    XSSFCell base_place = xssfRow.getCell(3);	// Base地
                    
                    name.setCellType(XSSFCell.CELL_TYPE_STRING);
                    work_no.setCellType(XSSFCell.CELL_TYPE_STRING);
                    id_no.setCellType(XSSFCell.CELL_TYPE_STRING);
                    base_place.setCellType(XSSFCell.CELL_TYPE_STRING);
                    
//                    no.setCellType(XSSFCell.CELL_TYPE_STRING);
//                    age.setCellType(XSSFCell.CELL_TYPE_STRING);
                    
//                    student.setNo(getValue(no));
//                    student.setName(getValue(name));
//                    student.setAge(getValue(age));
//                    student.setScore(Float.valueOf(getValue(score)));
//                    list.add(student);
                    
//                    System.out.print("行号：" + rowNum + "\t" + getValue(name) + "\t" + getValue(work_no) + "\t" + getValue(id_no) + "\t" + getValue(base_place) + "\n");
                    epu.setName(getValue(name));
                    epu.setWork_no(getValue(work_no));
                    epu.setIdentity_no(getValue(id_no));
                    epu.setBase_place(getValue(base_place));
                    epu.setPush2hw_atten(true);
                    epu.setNotes("row_no=" + rowNum);
                    
                    all_epu_list.add(epu);
                    
                    /*
                    EpUser epu_by_wn = EpAttenController.getEpUserByWorkNoMach(epu.getWork_no());
                    if(epu_by_wn != null) {
                    	String epu_notes = epu_by_wn.getNotes();
                    	if(!StringUtils.isBlank(epu_notes) && epu_notes.startsWith("query_result=")) {
                    		invalid_epu_list.add(epu_by_wn);
                    		System.out.println("根据工号查询用户信息出现异常：work_no = " + epu_by_wn.getWork_no() + ", " + epu_notes);
                    	} else {
                    		// 正常获取到用户信息。。。
                    		valid_epu_list.add(epu_by_wn);
//                    		epu.setId(epu_by_wn.getId());
//                    		epu.setWork_no(null);	// 设为null表示不更新工号字段的值
                    		EpUser epu_upd = new EpUser();
                    		epu_upd.setId(epu_by_wn.getId());
                    		epu_upd.setIdentity_no(epu.getIdentity_no());
                    		epu_upd.setBase_place(epu.getBase_place());
                    		epu_upd.setPush2hw_atten(true);
//                    		epUser
                    	}
                    } else {
                    	invalid_epu_list.add(epu);
                    }
                    
//                    System.out.println(epu);
                    */
                }
            }
        }
    	
        long process_end = System.currentTimeMillis();
        
        System.out.println("整个处理表格数据过程耗时：" + DateUtil.timeMills2ReadableStr(process_end - read_start));
        
    	return all_epu_list;
    }
    
    
    public static void main(String[] args) {
    	String path = "D:/TempDataDpan/20.HW_emps/2017.1.12-.xlsm";
    	
		ReadExcel re = new ReadExcel();
		try {
			List<EpUser> p2hw_emps = re.readXls_push2hw_emp(path);
//			for(EpUser epu : p2hw_emps) {
//				System.out.println(epu);
//			}
			System.out.println("count = " + p2hw_emps.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
    /**
     * 读取员工信息表（钉钉管理后台导出的员工信息Excel表）
     * @param path
     * @return
     * @throws IOException
     */
    public List<Employee> readXls_emp(String path) throws IOException {
    	System.out.println(Common.PROCESSING + path);
    	InputStream is = new FileInputStream(path);
    	HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
//    	Student student = null;
//    	List<Student> list = new ArrayList<Student>();
    	Employee emp = null;
    	List<Employee> emp_list = new ArrayList<Employee>();
    	// Read the Sheet
    	for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
    		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
    		if (hssfSheet == null) {
    			continue;
    		}
    		
    		// 读取Excel表格中第2行第1列的数据（该单元格内容中包含有公司名称，也即是所有部门中最高层的部门，用树形组织架构的方式来看，就是所谓的“根部门”。）
    		HSSFRow second_row = hssfSheet.getRow(1);
    		HSSFCell tmp_cell = second_row.getCell(0);
    		System.out.println("第2行数据：+ tmp_cell = " + getValue(tmp_cell));
    		String tmp_cell_data = getValue(tmp_cell);
    		Employee tmp_emp = new Employee();
    		tmp_emp.setRow_no(2);	// 设置这条特殊员工信息数据所在行号为2
    		if(tmp_cell_data == null) {	// 如果第2行第1列单元格的数据为空，则在返回数据中做相应标记
    			tmp_emp.setDept("<empty>");
    			tmp_emp.setNotes("flag=null_cell");	// 标识第2行第1列单元格的数据为空
    		} else {
    			String fixed_data = "员工信息表";
    			if(tmp_cell_data.contains(fixed_data)) {
    				if(tmp_cell_data.endsWith(fixed_data)) {
    					int company_name_end = tmp_cell_data.lastIndexOf(fixed_data);
    					String company_name = tmp_cell_data.substring(0, company_name_end);
    					if(!StringUtils.isBlank(company_name)) {
    						tmp_emp.setDept(company_name);
    						tmp_emp.setNotes("flag=company_name");	// 标识从第2行第1列单元格中成功取到了公司名称（顶级部门名称）
    					} else {
    						tmp_emp.setDept("<empty>");
    						tmp_emp.setNotes("flag=empty_company_name");	// 标识取到的公司名称为空
    					}
    				} else {
    					tmp_emp.setDept("<empty>");
    					tmp_emp.setNotes("flag=invalid_data");	// 标识Excel表格数据不规范
    				}
    			} else {
    				tmp_emp.setDept("<empty>");
					tmp_emp.setNotes("flag=invalid_data");	// 标识Excel表格数据不规范
    			}
    		}
    		
    		// 将带有公司名称的特殊员工信息添加到员工信息数组列表中的第一个位置上
//    		emp_list.add(0, tmp_emp);
    		emp_list.add(tmp_emp);
    		
    		if("<empty>".equals(tmp_emp.getDept())) {	// 如果获取到的公司名称为空，则直接返回带有错误标识的员工信息，不再继续处理表格后面的数据
    			return emp_list;
    		}
    		
    		// Read the Row
    		for (int rowNum = 3; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
    			HSSFRow hssfRow = hssfSheet.getRow(rowNum);
    			if (hssfRow != null) {
//    				student = new Student();
    				emp = new Employee();
    				emp.setRow_no(rowNum+1);	// 设置当前员工信息数据在Excel表格中所在行号为“rowNum+1”
//    				HSSFCell no = hssfRow.getCell(0);
//    				HSSFCell name = hssfRow.getCell(1);
//    				HSSFCell age = hssfRow.getCell(2);
//    				HSSFCell score = hssfRow.getCell(3);
    				
    				HSSFCell userid = hssfRow.getCell(0);
    				HSSFCell dept = hssfRow.getCell(1);
    				HSSFCell job_title = hssfRow.getCell(2);
    				HSSFCell name = hssfRow.getCell(3);
    				HSSFCell gender = hssfRow.getCell(4);
    				HSSFCell work_no = hssfRow.getCell(5);
    				HSSFCell is_managers = hssfRow.getCell(6);
    				HSSFCell mobile_phone = hssfRow.getCell(7);
    				HSSFCell email = hssfRow.getCell(8);
    				HSSFCell fixed_phone = hssfRow.getCell(9);
    				HSSFCell work_place = hssfRow.getCell(10);
    				HSSFCell notes = hssfRow.getCell(11);
    				
//    				no.setCellType(HSSFCell.CELL_TYPE_STRING);
//    				age.setCellType(HSSFCell.CELL_TYPE_STRING);
    				
//    				student.setNo(getValue(no));
//    				student.setName(getValue(name));
//    				student.setAge(getValue(age));
//    				student.setScore(Float.valueOf(getValue(score)));
//    				list.add(student);
    				
    				emp.setUserid(getValue(userid));
    				emp.setDept(getValue(dept));
    				emp.setJob_title(getValue(job_title));
    				emp.setName(getValue(name));
    				emp.setGender(getValue(gender));
    				emp.setWork_no(getValue(work_no));
    				emp.setIs_managers(getValue(is_managers));
    				emp.setMobile_phone(getValue(mobile_phone));
    				emp.setEmail(getValue(email));
    				emp.setFixed_phone(getValue(fixed_phone));
    				emp.setWork_place(getValue(work_place));
    				emp.setNotes(getValue(notes));
    				
    				emp_list.add(emp);
    			}
    		}
    	}
    	return emp_list;
    }
    
    /**
     * 读取员工信息表（公司自己的员工信息Excel表）
     * @param xssfRow
     * @return
     */
    public List<EpUser> readXls_ep_emp(String path) throws IOException {
    	System.out.println(Common.PROCESSING + path);
    	InputStream is = new FileInputStream(path);
    	HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
    	
//    	EpUser epu = null;
    	List<EpUser> epu_list = new ArrayList<EpUser>();
    	// Read the Sheet
    	for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
    		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
    		if (hssfSheet == null) {
    			continue;
    		}
    		
    		for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
    			HSSFRow hssfRow = hssfSheet.getRow(rowNum);
//    			System.out.println("当前行号：" + rowNum);
    			if (hssfRow != null) {
    				EpUser epu = new EpUser();
    				
    				epu.setNotes("row_no=" + rowNum);
    				
    				HSSFCell work_no = hssfRow.getCell(0);	// 工号
    				HSSFCell name = hssfRow.getCell(1);		// 姓名
    				HSSFCell id_no = hssfRow.getCell(2);	// 身份证号
    				HSSFCell base_place = hssfRow.getCell(3);	// base地
    				HSSFCell email = hssfRow.getCell(4);	// 邮箱
    				HSSFCell dept_name = hssfRow.getCell(5);	// 部门
    				HSSFCell project_name = hssfRow.getCell(6);	// 项目组
    				
    				epu.setWork_no(getValue_str(work_no));
    				String name_str = getValue_str(name);
    				if(StringUtils.isNotBlank(name_str)) {
    					epu.setName(name_str);
    				} else {
    					epu.setName("<EMPTY>");	// 名字为空时，用“<EMPTY>”标记填充（因为要求name字段非空）
    				}
    				epu.setDept_name(getValue_str(dept_name));
    				epu.setProject_name(getValue_str(project_name));
    				epu.setEmail(getValue_str(email));
    				epu.setIdentity_no(getValue_str(id_no));	// 身份证号
    				epu.setBase_place(getValue_str(base_place));	// base地
    				epu.setMobile_phone("<NONE>");	// TODO: Excel表格数据中暂未提供手机号字段的值，用“<NONE>”标记标记
    				// 设置默认初始密码为111111
    				epu.setPwd("96E79218965EB72C92A549DD5A330112");
    				epu.setMima("111111");
    				
    				epu_list.add(epu);
    			}
    		}
    	}
    	
    	return epu_list;
    }
    
    
    /**
     * 读取导入excel表格的员工信息
     * @param xssfRow
     * @return 所有员工的邮箱集合
     * @author zhangshun
     */
    public List<String> readEmailsFromExcel(String path) throws IOException {
    	System.out.println(Common.PROCESSING + path);
    	InputStream is = new FileInputStream(path);
    	Workbook hssfWorkbook = null;
    	 if (path.endsWith("xlsx")) {
 			hssfWorkbook = new XSSFWorkbook(is);
 		} else if (path.endsWith("xls")) {
 			hssfWorkbook = new HSSFWorkbook(is);
 		}
    	 
    	List<String> emails = new ArrayList<String>();
    	for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
    		 Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
    		if (hssfSheet == null) {
    			continue;
    		}
    		for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
    			 Row hssfRow = hssfSheet.getRow(rowNum);
    			if (hssfRow != null) {
    				Cell cell = hssfRow.getCell(2);
    				if(cell.getStringCellValue() == null)continue;    				
    				emails.add(cell.getStringCellValue());
    			}
    		}
    	}
    	hssfWorkbook.close();
    	return emails;
    }
    
    /**
     * 读取表格中指定行的日期
     * @param 数据源
     * @return 
     * @author zhangshun
     */
    public TreeMap<Date,String> readDatesFromExcel(String path){
    	Workbook hssfWorkbook = null;
    	TreeMap<Date,String> datas = new TreeMap<Date,String>();
		try {
			InputStream is = new FileInputStream(path);
			if (path.endsWith("xlsx")) {
				hssfWorkbook = new XSSFWorkbook(is);
			} else if (path.endsWith("xls")) {
				hssfWorkbook = new HSSFWorkbook(is);
			}
			
			for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
				Sheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
				if (hssfSheet == null) {
					continue;
				}
				for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
					Row hssfRow = hssfSheet.getRow(rowNum);
					if (hssfRow != null) {
						Cell cell1 = hssfRow.getCell(1);
						if(cell1.getDateCellValue() == null)continue;
						Cell cell2 = hssfRow.getCell(2);
						datas.put(cell1.getDateCellValue(), cell2 == null ? "1" : getWorkbookCellValue(cell2));
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			if(hssfWorkbook != null){
				try {
					hssfWorkbook.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
    	return datas;
    }
  
    /**
     * 读取员工信息表（移动考勤试用人员信息Excel表）
     * @param xssfRow
     * @return
     */
    public List<EpUser> readXls_ep_emp_extra(String path) throws IOException {
    	System.out.println(Common.PROCESSING + path);
    	InputStream is = new FileInputStream(path);
    	HSSFWorkbook hssfWorkbook = new HSSFWorkbook(is);
    	
    	List<EpUser> epu_list = new ArrayList<EpUser>();
    	// Read the Sheet
    	for (int numSheet = 0; numSheet < hssfWorkbook.getNumberOfSheets(); numSheet++) {
    		HSSFSheet hssfSheet = hssfWorkbook.getSheetAt(numSheet);
    		if (hssfSheet == null) {
    			continue;
    		}
    		
    		for (int rowNum = 1; rowNum <= hssfSheet.getLastRowNum(); rowNum++) {
    			HSSFRow hssfRow = hssfSheet.getRow(rowNum);
    			System.out.println("当前行号：" + rowNum);
    			if (hssfRow != null) {
    				EpUser epu = new EpUser();
    				
//    				HSSFCell work_no = hssfRow.getCell(0);
    				HSSFCell name = hssfRow.getCell(1);
    				HSSFCell identity_no = hssfRow.getCell(2);
    				HSSFCell original_pwd = hssfRow.getCell(3);
//    				HSSFCell email = hssfRow.getCell(4);
    				
//    				epu.setWork_no(getValue_str(work_no));
    				String name_str = getValue_str(name);
    				if(StringUtils.isNotBlank(name_str)) {
    					epu.setName(name_str);
    				} else {
    					epu.setName("<EMPTY>");	// 名字为空时，用“<EMPTY>”标记填充（因为要求name字段非空）
    				}
//    				epu.setDept_name(getValue_str(dept_name));
//    				epu.setProject_name(getValue_str(project_name));
//    				epu.setEmail(getValue_str(email));
//    				epu.setMobile_phone("<NONE>");	// TODO: Excel表格数据中暂未提供手机号字段的值，用“<NONE>”标记标记
    				
    				epu.setIdentity_no(getValue_str(identity_no));
//    				epu.setPwd(getValue_str(original_pwd));
    				
    				String original_pwd_str = getValue_str(original_pwd);
    				// 设置原始密码
    				epu.setOrigin_pwd(CryptionUtil.md5Hex(original_pwd_str));
    				
    				// 设置默认初始密码为111111
//    				epu.setPwd("96E79218965EB72C92A549DD5A330112");
    				epu.setPwd(CryptionUtil.md5Hex(original_pwd_str));
    				epu.setMima(original_pwd_str);
    				
    				epu_list.add(epu);
    			}
    		}
    	}
    	
    	return epu_list;
    }
    
    @SuppressWarnings("static-access")
    private String getValue(XSSFCell xssfRow) {
    	if(xssfRow == null) {
    		return null;
    	}
        if (xssfRow.getCellType() == xssfRow.CELL_TYPE_BOOLEAN) {
            return String.valueOf(xssfRow.getBooleanCellValue());
        } else if (xssfRow.getCellType() == xssfRow.CELL_TYPE_NUMERIC) {
            return String.valueOf(xssfRow.getNumericCellValue());
        } else {
            return String.valueOf(xssfRow.getStringCellValue());
        }
    }

    @SuppressWarnings("static-access")
    private String getValue(HSSFCell hssfCell) {
    	if(hssfCell == null) {
    		return null;
    	}
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }
    
    private String getValue_str(HSSFCell hssfCell) {
    	if(hssfCell == null) {
    		return null;
    	}
    	if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
    		return String.valueOf(hssfCell.getBooleanCellValue());
    	} else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
    		return String.valueOf(hssfCell.getNumericCellValue());
    	} else {
    		hssfCell.setCellType(hssfCell.CELL_TYPE_STRING);
    		return String.valueOf(hssfCell.getStringCellValue());
    	}
    }
    
    private String getWorkbookCellValue(Cell cell) {
    	if(cell == null) return "";
    	
    	if (cell.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
    		return String.valueOf(cell.getBooleanCellValue());
    	} else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
    		return String.valueOf((int)cell.getNumericCellValue());
    	} else if(cell.getCellType() == Cell.CELL_TYPE_STRING){
    		return cell.getStringCellValue();
    	}
    	return "";
    }
}
