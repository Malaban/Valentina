package procurement;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ProcurementTest {
		private static String  driver = "oracle.jdbc.driver.OracleDriver";
		private static String url = "jdbc:oracle:thin:@//10.128.49.65:56928/saleshdb";
		private static String username = "sales_app";
		private static String password = "sales_app";
		
		public static void main(String[] args) throws SQLException {
			try {
				Class.forName(driver);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			//��������
			Connection con = DriverManager.getConnection(url,username,password);
			//���ô洢����
			CallableStatement prepareCall = con.prepareCall("{call p(?,?,?,?)}");
			//����JDBC��Щ���������
			prepareCall.registerOutParameter(3, java.sql.Types.INTEGER);
			prepareCall.registerOutParameter(4, java.sql.Types.INTEGER);
			//����124��ֵ��3���������ֵ
			prepareCall.setInt(1, 6666);
			prepareCall.setInt(2, 7777);
			prepareCall.setInt(4, 8888);
			//ִ��
			prepareCall.execute();
			//ȡֵ
			int three = prepareCall.getInt(3);
			System.out.println(three);
			int four = prepareCall.getInt(4);
			System.out.println(four);
			prepareCall.close();
			con.close();
		}
}
