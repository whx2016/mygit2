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
		// ʹ�������Զ������Ӧ��װ������װԭʼ��ServletResponse
		ResponseWrapper wrapper = new ResponseWrapper((HttpServletResponse)response);
		// ��仰�ǳ���Ҫ��ע�⿴���ڶ������������ǵİ�װ��������response
		chain.doFilter(request, wrapper);
		// ����ػ�Ľ�������д��������滻���еġ����ơ�Ϊ����ľ���ӡ�
		String result = wrapper.getResult();
		result = result.replace("����", "��ľ����");
		// ������յĽ��
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
