package com.topsoft.jscheduler.job.quartz.log;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import ch.qos.logback.core.spi.AppenderAttachable;
import ch.qos.logback.core.spi.AppenderAttachableImpl;

public class LogAppenderBase<E extends ILoggingEvent> extends UnsynchronizedAppenderBase<E>implements AppenderAttachable<E> {

	public static final String LAZ_APPENDER = "quartzAppender";

	private Map<Long, StringBuilder> logs;
	private AppenderAttachableImpl<E> aai = new AppenderAttachableImpl<E>();

	public LogAppenderBase() {
		this.logs = new HashMap<Long, StringBuilder>();
	}

	public void initLog(long threadId) {

		StringBuilder str = logs.get(threadId);

		if (str == null)
			str = new StringBuilder();
		else
			str.setLength(0);

		logs.put(threadId, str);
	}

	public StringBuilder finalizeLog(long threadId) {

		StringBuilder str = logs.get(threadId);

		if (str == null)
			return new StringBuilder();

		return logs.remove(threadId);
	}

	public static LazLogAppender getLazAppender() {

		Logger logger = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
		return (LazLogAppender) logger.getAppender(LazLogAppender.LAZ_APPENDER);
	}

	public StringBuilder getThreadLog(long threadId) {

		StringBuilder str = logs.get(threadId);

		if (str == null)
			return new StringBuilder();

		return logs.get(threadId);
	}

	@Override
	public void addAppender(Appender<E> newAppender) {
		aai.addAppender(newAppender);
	}

	@Override
	public Iterator<Appender<E>> iteratorForAppenders() {
		return aai.iteratorForAppenders();
	}

	@Override
	public Appender<E> getAppender(String name) {
		return aai.getAppender(name);
	}

	@Override
	public boolean isAttached(Appender<E> appender) {
		return aai.isAttached(appender);
	}

	@Override
	public void detachAndStopAllAppenders() {
		aai.detachAndStopAllAppenders();
	}

	@Override
	public boolean detachAppender(Appender<E> appender) {
		return aai.detachAppender(appender);
	}

	@Override
	public boolean detachAppender(String name) {
		return aai.detachAppender(name);
	}

	@Override
	protected void append(E eventObject) {
		System.out.println("LOGGER: " + eventObject.getMessage());
	}
}