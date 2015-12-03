package demo;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class ResponseFilter implements Filter {

	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		// 使用我们自定义的响应包装器来包装原始的ServletResponse
		ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse)response);
		// 这句话非常重要，注意看到第二个参数是我们的包装器而不是response
		chain.doFilter(request, wrapper);
		// 处理截获的结果并进行处理，比如替换所有的“名称”为“铁木箱子”
		String result = wrapper.getResult();
		result = result.replace("名称", "铁木箱子");
		// 输出最终的结果
		PrintWriter out = response.getWriter();
		out.write(result);
		out.flush();
		out.close();
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

}
