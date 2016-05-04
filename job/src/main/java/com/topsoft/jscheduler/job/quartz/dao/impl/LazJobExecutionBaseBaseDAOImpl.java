package com.topsoft.jscheduler.job.quartz.dao.impl;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Repository;

import com.topsoft.jscheduler.job.quartz.dao.LazJobExecutionBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazJobExecution;
import com.topsoft.topframework.base.paging.DataPage;
import com.topsoft.topframework.base.paging.Page;

@Repository
public class LazJobExecutionBaseBaseDAOImpl extends QuartzBaseBaseDAOImpl<LazJobExecution, Integer> implements LazJobExecutionBaseDAO {

	public List<LazJobExecution> findAllLastJobExecutions(JobDetail job, int qtde) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<LazJobExecution> query = builder.createQuery(LazJobExecution.class);
		Root<LazJobExecution> root = query.from(LazJobExecution.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("jobName"), job.getKey().getName()));
		predicates.add(builder.equal(root.get("jobGroup"), job.getKey().getGroup()));

		query.where(predicates.toArray(new Predicate[]{}));
		query.orderBy(builder.desc(root.get("firedTime")));

		TypedQuery<LazJobExecution> tQuery = entityManager.createQuery(query);
		tQuery.setMaxResults(qtde);

		return tQuery.getResultList();
	}

	@Override
	public DataPage<LazJobExecution> findPageLastJobExecutions(JobDetail job, Page page) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<LazJobExecution> query = builder.createQuery(LazJobExecution.class);
		Root<LazJobExecution> root = query.from(LazJobExecution.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("jobName"), job.getKey().getName()));
		predicates.add(builder.equal(root.get("jobGroup"), job.getKey().getGroup()));

		query.where(predicates.toArray(new Predicate[]{}));
		query.orderBy(builder.desc(root.get("firedTime")));

		return readAllPagedObjects(query, page);
	}

	@Override
	public void deleteAllHistory(JobKey key) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaDelete<LazJobExecution> query = builder.createCriteriaDelete(LazJobExecution.class);
		Root<LazJobExecution> root = query.from(LazJobExecution.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("jobName"), key.getName()));
		predicates.add(builder.equal(root.get("jobGroup"), key.getGroup()));

		query.where(predicates.toArray(new Predicate[]{}));

		entityManager.createQuery(query).executeUpdate();
	}
}