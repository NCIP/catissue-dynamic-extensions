package edu.common.dynamicextensions.nui.action;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import edu.common.dynamicextensions.nutility.IoUtil;
import edu.wustl.dao.newdao.ActionStatus;

public class ExportPermissibleValueAction  extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static AtomicInteger formCnt = new AtomicInteger();

	private static Logger logger = Logger.getLogger(ExportPermissibleValueAction.class);

	public void doPost(HttpServletRequest httpReq, HttpServletResponse httpResp) {
		String tmpDir = getTmpDirName();
		String zipFileName = null;

		try {
			String pvs = String.valueOf(httpReq.getParameter("pvs"));
			if(pvs!=null && !"{}".equals(pvs.trim()))
			{
				String pvControlName = String.valueOf(httpReq.getParameter("pvControlName"));
				if("".equals(pvControlName.trim()))
				{
					pvControlName="Control";
				}
				String[] fields={"value","numericCode","definition","definitionSource","conceptCode"};

				String fileName=pvControlName+"_PV.csv";

				String downloadFileLocation = new StringBuilder(System.getProperty("java.io.tmpdir"))
				.append(File.separator).append(fileName).toString();

				File pvFile = new File(downloadFileLocation);
				FileOutputStream fos = new FileOutputStream(pvFile);
				BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

				JSONObject jsonObject = new JSONObject(pvs);
				Iterator<String> keys = jsonObject.keys();
				while(keys.hasNext() ) 
				{
					String key = keys.next();
					JSONObject json=jsonObject.getJSONObject(key);
					for(int i=0;i<fields.length;i++)
					{
						bw.write("\"");
						bw.write(json.getString(fields[i]).replace("&nbsp;", ""));
						bw.write("\"");
						if(i!=4)
						{
							bw.write(",");
						}
					}
					bw.newLine();
				}
				bw.close();

				sendFile(httpResp, downloadFileLocation);

				httpReq.setAttribute(ActionStatus.ACTIONSTAUS, ActionStatus.SUCCESSFUL);
			}
		} catch (Exception e) {
			throw new RuntimeException("Error occurred when exporting permissible values", e);
		} finally {
			IoUtil.delete(tmpDir);
			IoUtil.delete(zipFileName);
		}
	}

	public void doGet (HttpServletRequest request, HttpServletResponse response) { 
		doPost(request, response);
	}


	private void sendFile(HttpServletResponse response, String filePath) 
			throws Exception {
		response.setContentType("text/csv");
		String fileName=filePath.substring(filePath.lastIndexOf('\\')+1, filePath.length());
		response.setHeader("Content-Disposition", "attachment; filename="+fileName);

		OutputStream out = response.getOutputStream();
		FileInputStream fin = null;

		try {
			fin = new FileInputStream(filePath);
			IoUtil.copy(fin, out);
		} finally {
			IoUtil.close(fin);
		}
	}

	private String getTmpDirName() {
		return new StringBuilder()
		.append(System.getProperty("java.io.tmpdir"))
		.append(File.separator)
		.append(System.currentTimeMillis())
		.append(formCnt.incrementAndGet())
		.append("pv")
		.toString();
	}
}
