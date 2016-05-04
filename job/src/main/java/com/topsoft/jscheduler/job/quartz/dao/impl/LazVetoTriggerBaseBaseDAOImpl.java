package com.topsoft.jscheduler.job.quartz.dao.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.quartz.TriggerKey;
import org.springframework.stereotype.Repository;

import com.topsoft.jscheduler.job.quartz.dao.LazVetoTriggerBaseDAO;
import com.topsoft.jscheduler.job.quartz.domain.LazVetoTrigger;

@Repository(value = "vetoDAO")
public class LazVetoTriggerBaseBaseDAOImpl extends QuartzBaseBaseDAOImpl<LazVetoTrigger, Integer> implements LazVetoTriggerBaseDAO {

	@Override
	public List<LazVetoTrigger> findVetoExecutions(TriggerKey key) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<LazVetoTrigger> query = builder.createQuery(LazVetoTrigger.class);
		Root<LazVetoTrigger> root = query.from(LazVetoTrigger.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("name"), key.getName()));
		predicates.add(builder.equal(root.get("group"), key.getGroup()));
		predicates.add(builder.ge(root.get("vetoTime"), Calendar.getInstance().getTimeInMillis()));

		query.where(predicates.toArray(new Predicate[]{}));
		query.orderBy(builder.desc(root.get("vetoTime")));

		return readAllObjects(query);
	}

	@Override
	public LazVetoTrigger findVetoExecution(TriggerKey key, long fireTime) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaQuery<LazVetoTrigger> query = builder.createQuery(LazVetoTrigger.class);
		Root<LazVetoTrigger> root = query.from(LazVetoTrigger.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("name"), key.getName()));
		predicates.add(builder.equal(root.get("group"), key.getGroup()));
		predicates.add(builder.equal(root.get("vetoTime"), fireTime));

		query.where(predicates.toArray(new Predicate[]{}));

		return readObject(query);
	}

	@Override
	public void deleteAllVetos(TriggerKey key) {

		CriteriaBuilder builder = getCriteriaBuilder();
		CriteriaDelete<LazVetoTrigger> query = builder.createCriteriaDelete(LazVetoTrigger.class);
		Root<LazVetoTrigger> root = query.from(LazVetoTrigger.class);

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(builder.equal(root.get("name"), key.getName()));
		predicates.add(builder.equal(root.get("group"), key.getGroup()));

		query.where(predicates.toArray(new Predicate[]{}));

		entityManager.createQuery(query).executeUpdate();
	}
}