package com.cn.eplat.utils;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Author:qixiao Time:2016年9月2日23:47:51
 */
public final class Files_Helper_DG {
	/**
	 * 私有构造方法，限制该类不能被实例化
	 */
	private Files_Helper_DG() {
		throw new Error("The class Cannot be instance !");
	}

	/**
	 * spring mvc files Upload method (transferTo method) spring mvc 中的文件上传方法
	 * trasferTo 的方式上传，参数为MultipartFile
	 *
	 * @param request
	 *            HttpServletRequest
	 * @param multipartFile
	 *            MultipartFile(spring)
	 * @param filePath
	 *            filePath example "/files/Upload"
	 * @return
	 */
	public static String FilesUpload_transferTo_spring(HttpServletRequest request, MultipartFile multipartFile, String filePath) {
		// get Date path
		String DatePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
		// get server path (real path)
		String savePath = request.getSession().getServletContext().getRealPath(filePath) + File.separator + DatePath;
		// if dir not exists , mkdir
		System.out.println(savePath);
		File saveDir = new File(savePath);
		if (!saveDir.exists() || !saveDir.isDirectory()) {
			// create dir
			saveDir.mkdir();
		}
		if (multipartFile != null) {
			// get files suffix
			String suffix = multipartFile.getOriginalFilename().substring(multipartFile.getOriginalFilename().lastIndexOf("."));
			// use UUID get uuid string
			String uuidName = UUID.randomUUID().toString() + suffix;// make new
																	// file name
			// filePath+fileName the complex file Name
			String fileName = savePath + File.separator + uuidName;
			// return relative Path
			String relativePath = filePath + File.separator + DatePath + File.separator + uuidName;
			try {
				// save file
				multipartFile.transferTo(new File(fileName));
				// return relative Path
				return fileName;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		} else
			return null;
	}

	/**
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param filePath
	 *            example "/filesOut/Download/mst.txt"
	 * @return
	 */
	public static void FilesDownload_servlet(HttpServletRequest request, HttpServletResponse response, String filePath) {
		// get server path (real path)
		String realPath = request.getSession().getServletContext().getRealPath(filePath);
		File file = new File(realPath);
		String filenames = file.getName();
		InputStream inputStream;
		try {
			inputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] buffer = new byte[inputStream.available()];
			inputStream.read(buffer);
			inputStream.close();
			response.reset();
			// 先去掉文件名称中的空格,然后转换编码格式为utf-8,保证不出现乱码,这个文件名称用于浏览器的下载框中自动显示的文件名
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(filenames.replaceAll(" ", "").getBytes("utf-8"), "iso8859-1"));
			response.addHeader("Content-Length", "" + file.length());
			OutputStream os = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/octet-stream");
			os.write(buffer);// 输出文件
			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
