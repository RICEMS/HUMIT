package nd.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nd.hackrice.backend.Controller;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {
	private String dir = null;
	private int fileCount=0;
	/**
	 * 
	 */
	private static final long serialVersionUID = 591204638564697194L;
	private void setDir()
	{
		if(null==dir)
		{
			ServletContext context=getServletConfig().getServletContext();
			String uploadpath=context.getInitParameter("MusicDir");
			dir=context.getRealPath(uploadpath)+File.separatorChar;
		}
	}
	private void setFileName()
	{
		File file=new File(dir);
		fileCount=file.listFiles().length+1;
	}
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		super.doGet(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		setDir();
		setFileName();
		try {
			DiskFileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			upload.setHeaderEncoding("utf-8");

			List<FileItem> list = upload.parseRequest(req);
			String instrument="";
			String filename = dir+fileCount+".wav";
			for (FileItem item : list) 
			{
				if (item.isFormField()) {
					String inputName=item.getFieldName();
					if(inputName.equals("Instrument"));
					{
						instrument=item.getString();
					}
					// System.out.println(inputName+"::"+inputValue);
				} 
				else 
				{
					//filename = filename.substring(filename.lastIndexOf("\\") + 1);
					InputStream is = item.getInputStream();
					FileOutputStream fos = new FileOutputStream(filename);
					byte[] buff = new byte[1024];
					int len = 0;
					while ((len = is.read(buff)) > 0) 
					{
						fos.write(buff);
					}
					is.close();
					fos.close();
				}
			}
			String ndres=getServletContext().getRealPath(getServletContext().getInitParameter("NDResDir"))+File.separatorChar;
			String out=dir+File.separatorChar+fileCount+".midi";
			processWav(filename,out,ndres,instrument);
			req.setAttribute("message", "File uploaded!");
			req.setAttribute("path",fileCount+".midi"); 
		} 
		catch (FileUploadException e)
		{
			e.printStackTrace();
			req.setAttribute("message", "Upload failed");
		}
		//req.getRequestDispatcher("./message.jsp").forward(req, resp);
		resp.sendRedirect("/Humit/getMIDI?n="+fileCount+".midi");
	}
//	private void sendFile()
//	{
//		setDir();
//		log("enter");
//		String name=request.getParameter("n");
//
//        String fullFilePath = dir +File.separatorChar+ name;
//
//        File file = new File(fullFilePath);
//        if (file.exists()) 
//        {
//            response.reset();
//            response.setContentType("application/x-msdownload");
//            response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
//            int fileLength = (int) file.length();
//            response.setContentLength(fileLength);
//
//            if (fileLength != 0) {
//
//                InputStream inStream = new FileInputStream(file);
//                byte[] buf = new byte[4096];
//
//                ServletOutputStream servletOS = response.getOutputStream();
//                int readLength;
//                while (((readLength = inStream.read(buf)) != -1)) {
//                    servletOS.write(buf, 0, readLength);
//                }
//                inStream.close();
//                servletOS.flush();
//                servletOS.close();
//            }
//        }
//	}
	private void processWav(String filepath,String out,String NDResDir,String instr)
	{
		Controller controller=new Controller();
		controller.setFilePath(filepath);
		controller.setOriginal(false);
		controller.setVolume(50);
		controller.getInstrument(instr);
		controller.setNDResDir(NDResDir);
		controller.analyse(out);
	}
}
