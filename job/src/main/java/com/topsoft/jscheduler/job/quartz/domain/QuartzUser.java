package com.topsoft.jscheduler.job.quartz.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.topsoft.topframework.base.domain.BaseEntity;
import com.topsoft.topframework.base.validator.Validator;
import com.topsoft.topframework.base.validator.impl.LongValidator;

//@BaseEntity
//@Table(name="qrtz_user")
public class QuartzUser extends BaseEntity<Integer> implements User {

	private String username, name, email, cellPhone;

	public QuartzUser() {
	}

	public QuartzUser(String username, String name, String email, String cellPhone) {

		this.username = username;
		this.name = name;
		this.email = email;
		this.cellPhone = cellPhone;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCellPhone() {
		return cellPhone;
	}

	public void setCellPhone(String cellPhone) {
		this.cellPhone = cellPhone;
	}

	@JsonIgnore
	public boolean hasCellPhoneAdded() {
		return cellPhone != null && !cellPhone.trim().isEmpty();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@JsonIgnore
	public boolean hasEmailAdded() {
		return email != null && !email.trim().isEmpty();
	}

	@JsonIgnore
	public String getNameAndCellPhone() {
		return getName()
			.trim() + " - " + (cellPhone == null || cellPhone.trim().equals("") ? "NO MOBILE" : cellPhone.trim());
	}

	@JsonIgnore
	public long getCellularNumberOnlyNumbers() {

		String str = cellPhone.replaceAll("\\D+", "");

		if (Validator.use(LongValidator.class).isValid((str)))
			return Long.parseLong(str);

		return 0;
	}

	@Override
	public boolean equals(Object user) {

		if (user == null || !QuartzUser.class.isAssignableFrom(user.getClass()))
			return false;

		return getUsername().equals(((QuartzUser) user).getUsername());
	}
}
