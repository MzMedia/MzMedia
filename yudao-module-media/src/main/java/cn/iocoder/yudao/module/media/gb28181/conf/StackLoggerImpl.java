package cn.iocoder.yudao.module.media.gb28181.conf;

import gov.nist.core.StackLogger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class StackLoggerImpl implements StackLogger {

	/**
	 * 完全限定类名(Fully Qualified Class Name)，用于定位日志位置
	 */
	private static final String FQCN = StackLoggerImpl.class.getName();

	/**
	 * 封装打印日志的位置信息
	 * @param level   日志级别
	 * @param message 日志事件的消息
	 */
	private static void log(int level, String message) {
		log(level, message, null);
	}

	/**
	 * 封装打印日志的位置信息
	 * @param level   日志级别
	 * @param message 日志事件的消息
	 */
	private static void log(int level, String message, Throwable throwable) {
		try {
			StackTraceElement[] stackTrace = new Throwable().getStackTrace();
			String className = (stackTrace.length > 4) ? stackTrace[4].getClassName() : StackLoggerImpl.class.getName();
			org.slf4j.Logger logger = LoggerFactory.getLogger(className);
			if (logger instanceof LocationAwareLogger) {
				((LocationAwareLogger) logger).log(null, FQCN, level, message, null, throwable);
			} else {
				if (level >= LocationAwareLogger.ERROR_INT) {
					logger.error(message, throwable);
				} else if (level >= LocationAwareLogger.WARN_INT) {
					logger.warn(message, throwable);
				} else if (level >= LocationAwareLogger.INFO_INT) {
					logger.info(message, throwable);
				} else {
					logger.debug(message, throwable);
				}
			}
		} catch (Exception ignored) {
		}
	}

	@Override
	public void logStackTrace() {

	}

	@Override
	public void logStackTrace(int traceLevel) {
		System.out.println("traceLevel: " + traceLevel);
	}

	@Override
	public int getLineCount() {
		return 0;
	}

	@Override
	public void logException(Throwable ex) {

	}

	@Override
	public void logDebug(String message) {
		log(LocationAwareLogger.INFO_INT, message);
	}

	@Override
	public void logDebug(String message, Exception ex) {
		log(LocationAwareLogger.INFO_INT, message, ex);
	}

	@Override
	public void logTrace(String message) {
		log(LocationAwareLogger.INFO_INT, message);
	}

	@Override
	public void logFatalError(String message) {
		log(LocationAwareLogger.INFO_INT, message);
	}

	@Override
	public void logError(String message) {
		log(LocationAwareLogger.INFO_INT, message);
	}

	@Override
	public boolean isLoggingEnabled() {
		return true;
	}

	@Override
	public boolean isLoggingEnabled(int logLevel) {
		return true;
	}

	@Override
	public void logError(String message, Exception ex) {
		log(LocationAwareLogger.INFO_INT, message, ex);
	}

	@Override
	public void logWarning(String message) {
		log(LocationAwareLogger.INFO_INT, message);
	}

	@Override
	public void logInfo(String message) {
		log(LocationAwareLogger.INFO_INT, message);
	}

	@Override
	public void disableLogging() {

	}

	@Override
	public void enableLogging() {

	}

	@Override
	public void setBuildTimeStamp(String buildTimeStamp) {

	}

	@Override
	public void setStackProperties(Properties stackProperties) {

	}

	@Override
	public String getLoggerName() {
		return null;
	}
}
