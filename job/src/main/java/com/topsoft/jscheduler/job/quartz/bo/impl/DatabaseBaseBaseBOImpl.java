package com.topsoft.jscheduler.job.quartz.bo.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.topsoft.jscheduler.job.quartz.bo.DatabaseBaseBO;
import com.topsoft.jscheduler.job.quartz.dao.DatabaseBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.Database;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecutionDB;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam;
import com.topsoft.jscheduler.job.quartz.domain.LazJobParam.JobParamType;
import com.topsoft.jscheduler.job.quartz.domain.type.DatabaseType;
import com.topsoft.topframework.base.bo.impl.BaseBaseBOImpl;
import com.topsoft.topframework.base.exception.BusinessException;
import com.topsoft.topframework.base.security.SecurityContext;

@Service
@Transactional(rollbackFor = Throwable.class)
public class DatabaseBaseBaseBOImpl extends BaseBaseBOImpl<Database, Integer> implements DatabaseBaseBO {

	@Autowired
	private SecurityContext context;

	@Autowired
	public DatabaseBaseBaseBOImpl(DatabaseBaseDAO dao) {
		super(dao);
	}

	@Override
	public Database insertOrUpdate(Database database) {

		database.setUsername(context.getUser().getUsername());
		return super.insertOrUpdate(database);
	}

	@Override
	public Database insert(Database database) {

		database.setUsername(context.getUser().getUsername());
		return baseDao.insert(database);
	}

	@Override
	public List<String> findAllOwners(Database database) {

		List<String> owners = new ArrayList<String>();

		Connection connection = null;

		try {

			connection = database.getConnection();

			DatabaseMetaData metadata = connection.getMetaData();
			ResultSet rs = metadata.getSchemas();

			while (rs.next())
				owners.add(rs.getString(1));

			Collections.sort(owners);

			return owners;
		}
		catch (Exception e) {
			return owners;
		}
		finally {

			try {

				if (connection != null && !connection.isClosed())
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public List<String> findAllObjects(Database database, String schema) {

		List<String> list = new ArrayList<String>();

		Connection connection = null;

		try {

			connection = database.getConnection();
			DatabaseMetaData metadata = connection.getMetaData();
			ResultSet rs = metadata.getProcedures(null, schema, null);

			while (rs.next()) {

				DatabaseType type = database.getType();

				if (type == DatabaseType.ORACLE) {

					String procedureCat = rs.getString("PROCEDURE_CAT");
					int procedureType = rs.getInt("PROCEDURE_TYPE");

					if (procedureCat != null || procedureType == 1)
						list.add((procedureCat == null ? "" : procedureCat + ".") + rs.getString("PROCEDURE_NAME"));
				}
				else
					list.add(rs.getString("PROCEDURE_NAME"));
			}

			Collections.sort(list);

			return list;
		}
		catch (Exception e) {
			return list;
		}
		finally {

			try {

				if (connection != null && !connection.isClosed())
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public String callObject(LazJobExecutionDB config, JobDataMap jobParams) throws BusinessException {

		Connection connection = null;

		try {

			connection = config.getDatabase().getConnection();

			// Enable DBMSOutput
			if (config.getDatabase().getType() == DatabaseType.ORACLE)
				connection.prepareCall("begin dbms_output.enable( 1000000 ); end;").executeUpdate();

			List<LazJobParam> objParams = config.getJobParams();

			StringBuilder call = new StringBuilder();
			call.append("{call " + config.getOwner() + "." + config.getObject() + "(");

			int index = 1;

			for (LazJobParam param : objParams)
				if (jobParams.containsKey(param.getKey()) && jobParams.get(param.getKey()) != null)
					call.append((index++ == 1 ? "" : ", ") + param.getKey() + " => ?");

			call.append(")}");

			index = 1;

			CallableStatement statement = connection.prepareCall(call.toString());

			for (LazJobParam param : objParams) {
				if (jobParams.containsKey(param.getKey())) {

					if (param.getType() == JobParamType.DATE)
						statement.setDate(index++, new java.sql.Date(((Date) jobParams.get(param.getKey())).getTime()));
					else if (param.getType() == JobParamType.DOUBLE)
						statement.setDouble(index++, Double.parseDouble(jobParams.get(param.getKey()).toString()));
					else if (param.getType() == JobParamType.INTEGER)
						statement.setInt(param.getKey(), Integer.parseInt(jobParams.get(param.getKey()).toString()));
					else
						statement.setString(index++, jobParams.get(param.getKey()).toString());
				}
			}

			statement.executeUpdate();
			statement.close();

			return config.getDatabase().getConsoleOutput(connection);
		}
		catch (Exception e) {
			throw new BusinessException("Error while executing DB Object: " + config.getDatabase() + ":" + config
				.getOwner() + ":" + config.getObject(), e);
		}
		finally {

			try {

				if (connection != null && !connection.isClosed())
					connection.close();
			}
			catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}