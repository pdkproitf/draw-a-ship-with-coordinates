import java.sql.*;
//C:\Program Files\Java\jdk1.6.0_27\jre\lib\ext
	class JDBCDemo{
		public JDBCDemo(){
			try {
		String user="dajava";
		String pass="dajava";
		String url="jdbc:oracle:thin:@ localhost:1521:orcl";
		
				// load vendor
		     Class.forName("oracle.jdbc.driver.OracleDriver") ;
		     System.out.println ("Load thanh cong!");
		 
		//open connect
		Connection con =DriverManager.getConnection(url, user, pass);
		
		System.out.println ("ket noi thanh cong");
                String lenh = "insert into tbl_thongTin values('tau c','mt03',21.4,22.4	,522.50,520.0,	12.0,	12.0,'02/22/2015 00:12:29',2.32,'127/157/14',55)";
		//PreparedStatement pstm = con.prepareStatement(lenh);
		int cr=con.createStatement().executeUpdate(lenh);
                Statement pstm = con.createStatement();
		ResultSet rs=pstm.executeQuery("select * from tbl_thongTin");
		while(rs.next()){
			System.out.println ("name 1="+ rs.getString(1));
                        System.out.println ("name 1="+ rs.getString(2));
			}
		    }
		    catch (Exception ex) {
		    	ex.printStackTrace();
		    }
			}
			public static void main(String[] a){
		    			new JDBCDemo();
		    		}	
		}