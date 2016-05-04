package com.topsoft.jscheduler.job.quartz.domain.type;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

public enum DatabaseType{
		
	ORACLE( 
		"Oracle", 
		"oracle.jdbc.driver.OracleDriver", 
		"jdbc:oracle:thin:@<server>[:<1521>]:<database_name>", 
		"SELECT 1 FROM sys.dual" 
	){

		public String getConsoleOutput( Connection connection ) throws SQLException{
			
			CallableStatement show_stmt = connection.prepareCall( 
				"declare " + 
				"l_line varchar2(255); " + 
				"l_done number; " + 
				"l_buffer long; " + 
				"begin " + 
				"  loop " + 
				"    exit when length(l_buffer)+255 > :maxbytes OR l_done = 1; " + 
				"    dbms_output.get_line( l_line, l_done ); " + 
				"    l_buffer := l_buffer || l_line || chr(10); " + 
				"  end loop; " + 
				" :done := l_done; " + 
				" :buffer := l_buffer; " + 
				"end;" 
			);

			show_stmt.registerOutParameter( 2, java.sql.Types.INTEGER );
			show_stmt.registerOutParameter( 3, java.sql.Types.VARCHAR );
			
			StringBuilder str = new StringBuilder();

			for( ;; ){
				
				show_stmt.setInt( 1, 32000 );
				show_stmt.executeUpdate();
				
				str.append( show_stmt.getString( 3 ) );
				
				if( show_stmt.getInt( 2 ) == 1 )
					break;
			}
			
			return str.toString();
		}
	},
	DB2( 
		"DB2", 
		"com.ibm.db2.jcc.DB2Driver", 
		"jdbc:db2://<server>[:<3700>]/<db-name>", 
		"SELECT 1 FROM sysibm.sysdummy1" 
	){
		
		public String getConsoleOutput( Connection connection ) throws SQLException{
			return "";
		}
	},
	SQLSERVER( 
		"SQL Server", 
		"net.sourceforge.jtds.jdbc.Driver", 
		"jdbc:jtds:sqlserver://<hostname>[:<1433>]/<dbname>", 
		"SELECT 1"
	){
		
		public String getConsoleOutput( Connection connection ) throws SQLException{
			return "";
		}
	};
	
	private String name;
	private String urlExample;
	private String driverClass;
	private String testQuery;
	
	DatabaseType( String name, String driverClass, String urlExample, String testQuery ){
		
		this.name = name;
		this.driverClass = driverClass;
		this.urlExample = urlExample;
		this.testQuery = testQuery;
	}
	
	public String getName(){
		return name;
	}
	
	public String getDriverClass(){
		return driverClass;
	}
	
	public String getUrlExample(){
		return urlExample;
	}
	
	public String getTestQuery(){
		return testQuery;
	}
	
	@Override
	public String toString(){
		return name;
	}
	
	public abstract String getConsoleOutput( Connection connection ) throws SQLException;
}