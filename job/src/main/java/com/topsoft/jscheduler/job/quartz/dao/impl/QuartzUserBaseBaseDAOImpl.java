package com.topsoft.jscheduler.job.quartz.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.stereotype.Repository;

import com.topsoft.jscheduler.job.quartz.dao.QuartzUserBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.QuartzUser;
import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;

@Repository
public class QuartzUserBaseBaseDAOImpl extends QuartzBaseBaseDAOImpl<QuartzUser, Integer> implements QuartzUserBaseDAO {

	public QuartzUserBaseBaseDAOImpl() {
		super();
	}

	@Override
	public DataPage<QuartzUser> findPageByName(String name, Page page) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<QuartzUser> query = builder.createQuery(QuartzUser.class);
		Root<QuartzUser> root = query.from(QuartzUser.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("current"), "T"));
		predicates.add(builder.like(root.get("name"), name));

		query.where(predicates.toArray(new Predicate[]{}));
		query.orderBy(builder.asc(root.get("name")));

		return readAllPagedObjects(query, page);
	}

	@Override
	public QuartzUser findByUserID(String userId) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<QuartzUser> query = builder.createQuery(QuartzUser.class);
		Root<QuartzUser> root = query.from(QuartzUser.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("current"), "T"));
		predicates.add(builder.equal(root.get("userId"), userId.toUpperCase()));

		query.where(predicates.toArray(new Predicate[]{}));

		return readObject(query);
	}
}