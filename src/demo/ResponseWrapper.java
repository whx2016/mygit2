package demo;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ResponseWrapper extends HttpServletResponseWrapper {
	private PrintWriter cachedWriter;
	private CharArrayWriter bufferedWriter;
	
	public ResponseWrapper(HttpServletResponse response) {
		super(response);
		// TODO Auto-generated constructor stub
		// ��������Ǳ��淵�ؽ���ĵط�
		bufferedWriter = new CharArrayWriter();
		// ����ǰ�װPrintWriter�ģ������н��ͨ�����PrintWriterд�뵽bufferedWriter��
		cachedWriter = new PrintWriter(bufferedWriter);
	}
	
	@Override
	public PrintWriter getWriter() {
		return cachedWriter;
	}
 
	/**
	 * ��ȡԭʼ��HTMLҳ�����ݡ�
	 * @return
	 */
	public String getResult() {
		return bufferedWriter.toString();
	}

}
