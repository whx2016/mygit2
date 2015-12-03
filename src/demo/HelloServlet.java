package demo;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
//参考文档  http://www.cnblogs.com/xdp-gacl/p/3760336.html
@WebServlet("/hello")
public class HelloServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(HelloServlet.class);
	private String diverClass;
	private String userName;
	private String passWord;
	private String url;
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			Thread.sleep(20000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.println("hello servlet");
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			connection = DriverManager.getConnection(url, userName, passWord);
			statement = connection.prepareStatement("select count(*) total from user_info_tb");
			ResultSet resultSet = statement.executeQuery();
			Integer total = 0;
			if(resultSet.next()) { 
				total = resultSet.getInt("total");
				logger.error("total>>>:"+total);
			}
			request.setAttribute("total", total);
			request.getRequestDispatcher("/jsp/test1.jsp").forward(request, response);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	@Override
	public void init() throws ServletException {
		/*//如果log4j.properties不在src目录下，就这样加载properties的参数;如果在src目录下就不需要了，web.xml也不需要配置log4jConfigLocation了
		String prefix = getServletContext().getRealPath("/");
		String file = getServletContext().getInitParameter("log4jConfigLocation");
		if(file != null){
			PropertyConfigurator.configure(prefix + file);
		}*/
		
		diverClass = getServletContext().getInitParameter("driver");
		url = getServletContext().getInitParameter("url");
		userName = getServletContext().getInitParameter("username");
		passWord = getServletContext().getInitParameter("password");
		try {
			Class.forName(diverClass);//加载org.postgresql.Driver，在web.xml中配置的
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		super.destroy();
	}
}
