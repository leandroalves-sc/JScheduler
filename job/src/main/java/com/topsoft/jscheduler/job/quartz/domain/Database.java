package com.topsoft.jscheduler.job.quartz.domain;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topsoft.jscheduler.job.quartz.domain.type.DatabaseType;
import com.topsoft.topframework.base.domain.BaseEntity;

@javax.persistence.Entity
@Table(name = "QRTZ_DATABASE")
@AttributeOverride(name = "id", column = @Column(name = "DATABASE_ID") )
public class Database extends BaseEntity<Integer> {

	@Column(name = "NAME")
	private String name;
	
	@Column(name = "URL")
	private String url;
	
	@Column(name = "USERNAME")
	private String username;
	
	@Column(name = "PASSWORD")
	private String password;
	
	@Column(name = "TYPE")
	@Enumerated(EnumType.STRING)
	private DatabaseType type;
	
	@Column(name = "CREATION_DATE")
	private Date creationDate;
	
	public Database() {
		this.creationDate = Calendar.getInstance().getTime();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public DatabaseType getType() {
		return type;
	}

	public void setType(DatabaseType type) {
		this.type = type;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	@Override
	@GeneratedValue(strategy = GenerationType.AUTO)
	public Integer getId() {
		return id;
	}

	@Transient
	@JsonIgnore
	public Connection getConnection() throws SQLException {

		try {

			Class.forName(getType().getDriverClass());
			Connection connection = DriverManager.getConnection(getUrl(), getUsername(), getPassword());

			if (connection == null || connection.isClosed())
				throw new SQLException("Error while openning database connection");

			return connection;
		}
		catch (ClassNotFoundException e) {
			throw new SQLException("Driver not found");
		}
	}

	@Transient
	@JsonIgnore
	public String getConsoleOutput(Connection connection) {

		try {

			return getType().getConsoleOutput(connection);
		}
		catch (SQLException e) {
			return "";
		}
	}

	@Override
	public boolean equals(Object database) {

		if (database == null || getName() == null || !Database.class.isAssignableFrom(database.getClass()))
			return false;

		return getName().equals(((Database) database).getName());
	}

	@Override
	public String toString() {
		return name == null ? "" : name;
	}
}