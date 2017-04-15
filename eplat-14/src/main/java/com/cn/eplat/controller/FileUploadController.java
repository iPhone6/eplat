package com.cn.eplat.controller;

import com.cn.eplat.utils.Files_Helper_DG;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Iterator;
import java.util.List;

/**
 * Created by qixiao on 2016/8/30.
 */
@Controller
@RequestMapping(value = "/FileUploadController")
public class FileUploadController {
	
	@RequestMapping(params = "gotoFileUpload")
	public String gotoFileUploadPage(HttpServletRequest request) {
		return "fileUpload/FileUpload";
	}

	/*
	 * 方式一 采用 fileUpload_multipartFile ， file.transferTo 来保存上传的文件
	 */
	@RequestMapping(params = "fileUpload_multipartFile")
	@ResponseBody
	public String fileUpload_multipartFile(HttpServletRequest request, @RequestParam("file_upload") MultipartFile multipartFile) {
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String filePath = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		return "{\"TFMark\":\"true\",\"Msg\":\"upload success !\",\"filePath\":\"" + filePath + "\"}";
	}

	/*
	 * 方式二 采用 fileUpload_multipartRequest file.transferTo 来保存上传文件 参数不写
	 * MultipartFile multipartFile
	 * 在request请求里面直接转成multipartRequest，从multipartRequest中获取到文件流
	 */
	@RequestMapping(params = "fileUpload_multipartRequest")
	@ResponseBody
	public String fileUpload_multipartRequest(HttpServletRequest request) {
		// 将request转成MultipartHttpServletRequest
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		// 页面控件的文件流，对应页面控件 input file_upload
		MultipartFile multipartFile = multipartRequest.getFile("file_upload");
		// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
		String filePath = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
		return "{\"TFMark\":\"true\",\"Msg\":\"upload success !\",\"filePath\":\"" + filePath + "\"}";
	}

	/*
	 * 方式三 采用 CommonsMultipartResolver file.transferTo 来保存上传文件 自动扫描全部的input表单
	 */
	@RequestMapping(params = "fileUpload_CommonsMultipartResolver")
	@ResponseBody
	public String fileUpload_CommonsMultipartResolver(HttpServletRequest request) {
		// 将当前上下文初始化给 CommonsMultipartResolver （多部分解析器）
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(request.getSession().getServletContext());
		// 检查form中是否有enctype="multipart/form-data"
		if (multipartResolver.isMultipart(request)) {
			// 将request变成多部分request
			MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
			// 获取multiRequest 中所有的文件名
			Iterator iter = multipartRequest.getFileNames();
			while (iter.hasNext()) {
				// 一次遍历所有文件
				MultipartFile multipartFile = multipartRequest.getFile(iter.next().toString());
				// 调用保存文件的帮助类进行保存文件，并返回文件的相对路径
				String fileName = Files_Helper_DG.FilesUpload_transferTo_spring(request, multipartFile, "\\filesOut\\Upload");
				System.out.println(fileName);
			}
		}
		return "upload success ! ";
	}

	/*
	 * 方式四 通过流的方式上传文件
	 * 
	 * @RequestParam("file") 将name=file控件得到的文件封装成CommonsMultipartFile 对象
	 */
	@RequestMapping(params = "fileUpload_stream")
	@ResponseBody
	public String fileUpload_stream(HttpServletRequest request) {
		// 得到上传文件的保存目录，将上传的文件存放于WEB-INF目录下，不允许外界直接访问，保证上传文件的安全
		String savePath = request.getSession().getServletContext().getRealPath("\\filesOut\\Upload");
		File file = new File(savePath);
		// 判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
			// 创建目录
			file.mkdir();
		}
		try {
			// 使用Apache文件上传组件处理文件上传步骤：
			// 1、创建一个DiskFileItemFactory工厂
			DiskFileItemFactory factory = new DiskFileItemFactory();
			// 2、创建一个文件上传解析器
			ServletFileUpload upload = new ServletFileUpload(factory);
			// 解决上传文件名的中文乱码
			upload.setHeaderEncoding("UTF-8");
			// 3、判断提交上来的数据是否是上传表单的数据
			if (!ServletFileUpload.isMultipartContent(request)) {
				// 按照传统方式获取数据
				return "is not form upload data ";
			}
			// 4、使用ServletFileUpload解析器解析上传数据，解析结果返回的是一个List<FileItem>集合，每一个FileItem对应一个Form表单的输入项
			List<FileItem> list = upload.parseRequest(request);
			System.out.println("start---------");
			System.out.println(list);
			for (FileItem item : list) {
				System.out.println("begin ----");
				// 如果fileitem中封装的是普通输入项的数据
				if (item.isFormField()) {
					String name = item.getFieldName();
					// 解决普通输入项的数据的中文乱码问题
					String value = item.getString("UTF-8");
					// value = new String(value.getBytes("iso8859-1"),"UTF-8");
					System.out.println(name + "=" + value);
				} else {// 如果fileitem中封装的是上传文件
					// 得到上传的文件名称，
					String filename = item.getName();
					System.out.println(filename);
					if (filename == null || filename.trim().equals("")) {
						continue;
					}
					// 注意：不同的浏览器提交的文件名是不一样的，有些浏览器提交上来的文件名是带有路径的，如：
					// c:\a\b\1.txt，而有些只是单纯的文件名，如：1.txt
					// 处理获取到的上传文件的文件名的路径部分，只保留文件名部分
					String suffix = item.getName().substring(item.getName().lastIndexOf("."));
					// 获取item中的上传文件的输入流
					InputStream in = item.getInputStream();
					// 创建一个文件输出流
					FileOutputStream out = new FileOutputStream(savePath + "/123" + suffix);
					// 创建一个缓冲区
					byte buffer[] = new byte[1024];
					// 判断输入流中的数据是否已经读完的标识
					int len = 0;
					// 循环将输入流读入到缓冲区当中，(len=in.read(buffer))>0就表示in里面还有数据
					while ((len = in.read(buffer)) > 0) {
						// 使用FileOutputStream输出流将缓冲区的数据写入到指定的目录(savePath + "\\"
						// + filename)当中
						out.write(buffer, 0, len);
					}
					// 关闭输入流
					in.close();
					// 关闭输出流
					out.close();
					// 删除处理文件上传时生成的临时文件
					item.delete();
				}
			}
			return "upload success !";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "upload fail";
	}

	/*
	 * 多文件上传 采用 MultipartFile[] multipartFile 上传文件方法
	 */
	@RequestMapping(params = "fileUpload_spring_list")
	@ResponseBody
	public String fileUpload_spring_list(HttpServletRequest request, @RequestParam("file_upload") MultipartFile[] multipartFile) {
		// 判断file数组不能为空并且长度大于0
		if (multipartFile != null && multipartFile.length > 0) {
			// 循环获取file数组中得文件
			try {
				for (int i = 0; i < multipartFile.length; i++) {
					MultipartFile file = multipartFile[i];
					// 保存文件
					String fileName = Files_Helper_DG.FilesUpload_transferTo_spring(request, file, "\\filesOut\\Upload");
					System.out.println(fileName);
				}
				return "{\"TFMark\":\"true\",\"Msg\":\"upload success !\"}";
			} catch (Exception ee) {
				return "{\"TFMark\":\"false\",\"Msg\":\"参数传递有误！\"}";
			}
		}
		return "{\"TFMark\":\"false\",\"Msg\":\"参数传递有误！\"}";
	}

	/**
	 * 文件下载
	 *
	 * @param response
	 */
	@RequestMapping(params = "fileDownload_servlet")
	public void fileDownload_servlet(HttpServletRequest request, HttpServletResponse response) {
		Files_Helper_DG.FilesDownload_servlet(request, response, "\\filesOut\\Download\\mst.txt");
	}
}
