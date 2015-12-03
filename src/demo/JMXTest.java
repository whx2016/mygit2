package demo;

import java.lang.management.MemoryUsage;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

public class JMXTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {

			String jmxURL = "service:jmx:rmi:///jndi/rmi://127.0.0.1:8999/jmxrmi";

			JMXServiceURL serviceURL = new JMXServiceURL(jmxURL);

			Map map = new HashMap();
			String[] credentials = new String[] { "monitorRole", "tomcat" };
			map.put("jmx.remote.credentials", credentials);
			JMXConnector connector = JMXConnectorFactory.connect(serviceURL,
					map);
			MBeanServerConnection mbsc = connector.getMBeanServerConnection();

			// �˿�����Ƕ�̬ȡ��
			ObjectName threadObjName = new ObjectName(
					"Catalina:type=ThreadPool,name=http-8080");
			MBeanInfo mbInfo = mbsc.getMBeanInfo(threadObjName);

			String attrName = "currentThreadCount";// tomcat���߳�����Ӧ������ֵ
			MBeanAttributeInfo[] mbAttributes = mbInfo.getAttributes();
			System.out.println("currentThreadCount:"
					+ mbsc.getAttribute(threadObjName, attrName));

			// heap
			for (int j = 0; j < mbsc.getDomains().length; j++) {
				System.out.println("###########" + mbsc.getDomains()[j]);
			}
			Set MBeanset = mbsc.queryMBeans(null, null);
			System.out.println("MBeanset.size() : " + MBeanset.size());
			Iterator MBeansetIterator = MBeanset.iterator();
			while (MBeansetIterator.hasNext()) {
				ObjectInstance objectInstance = (ObjectInstance) MBeansetIterator
						.next();
				ObjectName objectName = objectInstance.getObjectName();
				String canonicalName = objectName.getCanonicalName();
				System.out.println("canonicalName : " + canonicalName);
				if (canonicalName
						.equals("Catalina:host=localhost,type=Cluster")) {
					// Get details of cluster MBeans
					System.out.println("Cluster MBeans Details:");
					System.out
							.println("=========================================");
					// getMBeansDetails(canonicalName);
					String canonicalKeyPropList = objectName
							.getCanonicalKeyPropertyListString();
				}
			}
			// ------------------------- system ----------------------
			ObjectName runtimeObjName = new ObjectName("java.lang:type=Runtime");
			System.out.println("����:"
					+ (String) mbsc.getAttribute(runtimeObjName, "VmVendor"));
			System.out.println("����:"
					+ (String) mbsc.getAttribute(runtimeObjName, "VmName"));
			System.out.println("�汾:"
					+ (String) mbsc.getAttribute(runtimeObjName, "VmVersion"));
			Date starttime = new Date((Long) mbsc.getAttribute(runtimeObjName,
					"StartTime"));
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("����ʱ��:" + df.format(starttime));

			Long timespan = (Long) mbsc.getAttribute(runtimeObjName, "Uptime");
			System.out.println("��������ʱ��:" + JMXTest.formatTimeSpan(timespan));
			// ------------------------ JVM -------------------------
			// ��ʹ����
			ObjectName heapObjName = new ObjectName("java.lang:type=Memory");
			MemoryUsage heapMemoryUsage = MemoryUsage
					.from((CompositeDataSupport) mbsc.getAttribute(heapObjName,
							"HeapMemoryUsage"));
			long maxMemory = heapMemoryUsage.getMax();// �����
			long commitMemory = heapMemoryUsage.getCommitted();// �ѵ�ǰ����
			long usedMemory = heapMemoryUsage.getUsed();
			System.out.println("heap:" + (double) usedMemory * 100
					/ commitMemory + "%");// ��ʹ����

			MemoryUsage nonheapMemoryUsage = MemoryUsage
					.from((CompositeDataSupport) mbsc.getAttribute(heapObjName,
							"NonHeapMemoryUsage"));
			long noncommitMemory = nonheapMemoryUsage.getCommitted();
			long nonusedMemory = heapMemoryUsage.getUsed();
			System.out.println("nonheap:" + (double) nonusedMemory * 100
					/ noncommitMemory + "%");

			ObjectName permObjName = new ObjectName(
					"java.lang:type=MemoryPool,name=Perm Gen");
			MemoryUsage permGenUsage = MemoryUsage
					.from((CompositeDataSupport) mbsc.getAttribute(permObjName,
							"Usage"));
			long committed = permGenUsage.getCommitted();// �־öѴ�С
			long used = heapMemoryUsage.getUsed();//
			System.out.println("perm gen:" + (double) used * 100 / committed
					+ "%");// �־ö�ʹ����

			// -------------------- Session ---------------
			ObjectName managerObjName = new ObjectName(
					"Catalina:type=Manager,*");
			Set<ObjectName> s = mbsc.queryNames(managerObjName, null);
			for (ObjectName obj : s) {
				System.out.println("Ӧ����:" + obj.getKeyProperty("path"));
				ObjectName objname = new ObjectName(obj.getCanonicalName());
				System.out.println("���Ự��:"
						+ mbsc.getAttribute(objname, "maxActiveSessions"));
				System.out.println("�Ự��:"
						+ mbsc.getAttribute(objname, "activeSessions"));
				System.out.println("��Ự��:"
						+ mbsc.getAttribute(objname, "sessionCounter"));
			}

			// ----------------- Thread Pool ----------------
			ObjectName threadpoolObjName = new ObjectName(
					"Catalina:type=ThreadPool,*");
			Set<ObjectName> s2 = mbsc.queryNames(threadpoolObjName, null);
			for (ObjectName obj : s2) {
				System.out.println("�˿���:" + obj.getKeyProperty("name"));
				ObjectName objname = new ObjectName(obj.getCanonicalName());
				System.out.println("����߳���:"
						+ mbsc.getAttribute(objname, "maxThreads"));
				System.out.println("��ǰ�߳���:"
						+ mbsc.getAttribute(objname, "currentThreadCount"));
				System.out.println("��æ�߳���:"
						+ mbsc.getAttribute(objname, "currentThreadsBusy"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String formatTimeSpan(long span) {
		long minseconds = span % 1000;

		span = span / 1000;
		long seconds = span % 60;

		span = span / 60;
		long mins = span % 60;

		span = span / 60;
		long hours = span % 24;

		span = span / 24;
		long days = span;
		return (new Formatter()).format("%1$d�� %2$02d:%3$02d:%4$02d.%5$03d",
				days, hours, mins, seconds, minseconds).toString();
	}
}

