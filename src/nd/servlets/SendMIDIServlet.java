package nd.servlets;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SendMIDIServlet extends HttpServlet 
{
	private String dir = null;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5973599874718074586L;
	
	private void setDir()
	{
		if(null==dir)
		{
			ServletContext context=getServletConfig().getServletContext();
			String uploadpath=context.getInitParameter("MusicDir");
			dir=context.getRealPath(uploadpath)+File.separatorChar;
		}
	}
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		setDir();
		log("enter");
		String name=request.getParameter("n");

        String fullFilePath = dir +File.separatorChar+ name;

        File file = new File(fullFilePath);
        if (file.exists()) 
        {
            response.reset();
            response.setContentType("application/x-msdownload");
            response.addHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\"");
            int fileLength = (int) file.length();
            response.setContentLength(fileLength);

            if (fileLength != 0) {

                InputStream inStream = new FileInputStream(file);
                byte[] buf = new byte[4096];

                ServletOutputStream servletOS = response.getOutputStream();
                int readLength;
                while (((readLength = inStream.read(buf)) != -1)) {
                    servletOS.write(buf, 0, readLength);
                }
                inStream.close();
                servletOS.flush();
                servletOS.close();
            }
        }
	}
}
