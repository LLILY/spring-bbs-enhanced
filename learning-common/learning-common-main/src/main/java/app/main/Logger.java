package app.main;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created by lili19289 on 2016/8/29.
 */
public class Logger {

    public static boolean forceJuli = false;
    public static boolean redirectJuli = false;
    public static boolean recordCaller = false;
    public static org.apache.log4j.Logger log4j;
    public static java.util.logging.Logger juli = java.util.logging.Logger
            .getLogger("application");
    public static boolean configuredManually = false;

    public static boolean usesJuli() {
        return forceJuli || log4j == null;
    }

    public static void init() {
        String log4jPath = Configure.configuration.getProperty(
                "application.log.path", "/log4j.xml");
        URL log4jConf = Logger.class.getResource(log4jPath);
        boolean isXMLConfig = log4jPath.endsWith(".xml");
        if (log4jConf == null) {
            isXMLConfig = false;
            log4jPath = Configure.configuration.getProperty("application.log.path",
                    "/log4j.properties");
            log4jConf = Logger.class.getResource(log4jPath);
        }

        if (log4jConf == null) {
            Properties rootLogger = new Properties();
            rootLogger.setProperty("log4j.rootLogger", "OFF");
            PropertyConfigurator.configure(rootLogger);
        } else if (log4j == null) {
            if (log4jConf.getFile().indexOf(
                    Configure.applicationPath.getAbsolutePath()) == 0) {
                configuredManually = true;
            }

            if (isXMLConfig) {
                DOMConfigurator.configure(log4jConf);
            } else {
                PropertyConfigurator.configure(log4jConf);
            }

            log4j = org.apache.log4j.Logger.getLogger("play");

        }

    }

    public static void setUp(String level) {
        if (!forceJuli && log4j != null) {
            log4j.setLevel(Level.toLevel(level));
            if (redirectJuli) {
                java.util.logging.Logger rootLogger = java.util.logging.Logger
                        .getLogger("application");
                Handler[] activeHandler = rootLogger.getHandlers();
                int juliLevel = activeHandler.length;

                for (int arg3 = 0; arg3 < juliLevel; ++arg3) {
                    Handler handler = activeHandler[arg3];
                    rootLogger.removeHandler(handler);
                }

                Logger.JuliToLog4jHandler arg5 = new Logger.JuliToLog4jHandler();
                java.util.logging.Level arg6 = toJuliLevel(level);
                arg5.setLevel(arg6);
                rootLogger.addHandler(arg5);
                rootLogger.setLevel(arg6);
            }
        } else {
            juli.setLevel(toJuliLevel(level));
        }

    }

    static java.util.logging.Level toJuliLevel(String level) {
        java.util.logging.Level juliLevel = java.util.logging.Level.INFO;
        if (level.equals("ERROR") || level.equals("FATAL")) {
            juliLevel = java.util.logging.Level.SEVERE;
        }

        if (level.equals("WARN")) {
            juliLevel = java.util.logging.Level.WARNING;
        }

        if (level.equals("DEBUG")) {
            juliLevel = java.util.logging.Level.FINE;
        }

        if (level.equals("TRACE")) {
            juliLevel = java.util.logging.Level.FINEST;
        }

        if (level.equals("ALL")) {
            juliLevel = java.util.logging.Level.ALL;
        }

        if (level.equals("OFF")) {
            juliLevel = java.util.logging.Level.OFF;
        }

        return juliLevel;
    }

    public static boolean isDebugEnabled() {
        return !forceJuli && log4j != null ? log4j.isDebugEnabled() : juli
                .isLoggable(java.util.logging.Level.FINE);
    }

    public static boolean isTraceEnabled() {
        return !forceJuli && log4j != null ? log4j.isTraceEnabled() : juli
                .isLoggable(java.util.logging.Level.FINEST);
    }

    public static boolean isEnabledFor(String level) {
        Level log4jLevel = Level.toLevel(level);
        return isEnabledFor(log4jLevel);
    }

    public static boolean isEnabledFor(Level log4jLevel) {
        if (!forceJuli && log4j != null) {
            return log4j.isEnabledFor(log4jLevel);
        } else {
            java.util.logging.Level julLevel = toJuliLevel(log4jLevel
                    .toString());
            return juli.isLoggable(julLevel);
        }
    }

    public static void trace(String message, Object... args) {
        if (isEnabledFor(Level.TRACE)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (recordCaller) {
                        org.apache.log4j.Logger.getLogger(getCallerClassName())
                                .trace(format(message, args));
                    } else {
                        log4j.trace(format(message, args));
                    }
                } catch (Throwable arg2) {
                    log4j.error("Oops. Error in Logger !", arg2);
                }
            } else {
                try {
                    juli.finest(format(message, args));
                } catch (Throwable arg3) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg3);
                }
            }
        }

    }

    public static void debug(String message, Object... args) {
        if (isDebugEnabled()) {
            if (!forceJuli && log4j != null) {
                try {
                    if (recordCaller) {
                        org.apache.log4j.Logger.getLogger(getCallerClassName())
                                .debug(format(message, args));
                    } else {
                        log4j.debug(format(message, args));
                    }
                } catch (Throwable arg2) {
                    log4j.error("Oops. Error in Logger !", arg2);
                }
            } else {
                try {
                    juli.fine(format(message, args));
                } catch (Throwable arg3) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg3);
                }
            }
        }

    }

    public static void debug(Throwable e, String message, Object... args) {
        if (isDebugEnabled()) {
            if (!forceJuli && log4j != null) {
                try {
                    if (!niceThrowable(Level.DEBUG, e, message, args)) {
                        if (recordCaller) {
                            org.apache.log4j.Logger.getLogger(
                                    getCallerClassName()).debug(
                                    format(message, args), e);
                        } else {
                            log4j.debug(format(message, args), e);
                        }
                    }
                } catch (Throwable arg3) {
                    log4j.error("Oops. Error in Logger !", arg3);
                }
            } else {
                try {
                    if (!niceThrowable(Level.DEBUG, e, message, args)) {
                        juli.log(java.util.logging.Level.CONFIG,
                                format(message, args), e);
                    }
                } catch (Throwable arg4) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg4);
                }
            }
        }

    }

    public static void info(String message, Object... args) {
        if (isEnabledFor(Level.INFO)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (recordCaller) {
                        org.apache.log4j.Logger.getLogger(getCallerClassName())
                                .info(format(message, args));
                    } else {
                        log4j.info(format(message, args));
                    }
                } catch (Throwable arg2) {
                    log4j.error("Oops. Error in Logger !", arg2);
                }
            } else {
                try {
                    juli.info(format(message, args));
                } catch (Throwable arg3) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg3);
                }
            }
        }

    }

    public static void info(Throwable e, String message, Object... args) {
        if (isEnabledFor(Level.INFO)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (!niceThrowable(Level.INFO, e, message, args)) {
                        if (recordCaller) {
                            org.apache.log4j.Logger.getLogger(
                                    getCallerClassName()).info(
                                    format(message, args), e);
                        } else {
                            log4j.info(format(message, args), e);
                        }
                    }
                } catch (Throwable arg3) {
                    log4j.error("Oops. Error in Logger !", arg3);
                }
            } else {
                try {
                    if (!niceThrowable(Level.INFO, e, message, args)) {
                        juli.log(java.util.logging.Level.INFO,
                                format(message, args), e);
                    }
                } catch (Throwable arg4) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg4);
                }
            }
        }

    }

    public static void warn(String message, Object... args) {
        if (isEnabledFor(Level.WARN)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (recordCaller) {
                        org.apache.log4j.Logger.getLogger(getCallerClassName())
                                .warn(format(message, args));
                    } else {
                        log4j.warn(format(message, args));
                    }
                } catch (Throwable arg2) {
                    log4j.error("Oops. Error in Logger !", arg2);
                }
            } else {
                try {
                    juli.warning(format(message, args));
                } catch (Throwable arg3) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg3);
                }
            }
        }

    }

    public static void warn(Throwable e, String message, Object... args) {
        if (isEnabledFor(Level.WARN)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (!niceThrowable(Level.WARN, e, message, args)) {
                        if (recordCaller) {
                            org.apache.log4j.Logger.getLogger(
                                    getCallerClassName()).warn(
                                    format(message, args), e);
                        } else {
                            log4j.warn(format(message, args), e);
                        }
                    }
                } catch (Throwable arg3) {
                    log4j.error("Oops. Error in Logger !", arg3);
                }
            } else {
                try {
                    if (!niceThrowable(Level.WARN, e, message, args)) {
                        juli.log(java.util.logging.Level.WARNING,
                                format(message, args), e);
                    }
                } catch (Throwable arg4) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg4);
                }
            }
        }

    }

    public static void error(String message, Object... args) {
        if (isEnabledFor(Level.ERROR)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (recordCaller) {
                        org.apache.log4j.Logger.getLogger(getCallerClassName())
                                .error(format(message, args));
                    } else {
                        log4j.error(format(message, args));
                    }
                } catch (Throwable arg2) {
                    log4j.error("Oops. Error in Logger !", arg2);
                }
            } else {
                try {
                    juli.severe(format(message, args));
                } catch (Throwable arg3) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg3);
                }
            }
        }

    }

    public static void error(Throwable e, String message, Object... args) {
        if (isEnabledFor(Level.ERROR)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (!niceThrowable(Level.ERROR, e, message, args)) {
                        if (recordCaller) {
                            org.apache.log4j.Logger.getLogger(
                                    getCallerClassName()).error(
                                    format(message, args), e);
                        } else {
                            log4j.error(format(message, args), e);
                        }
                    }
                } catch (Throwable arg3) {
                    log4j.error("Oops. Error in Logger !", arg3);
                }
            } else {
                try {
                    if (!niceThrowable(Level.ERROR, e, message, args)) {
                        juli.log(java.util.logging.Level.SEVERE,
                                format(message, args), e);
                    }
                } catch (Throwable arg4) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg4);
                }
            }
        }

    }

    public static void fatal(String message, Object... args) {
        if (isEnabledFor(Level.FATAL)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (recordCaller) {
                        org.apache.log4j.Logger.getLogger(getCallerClassName())
                                .fatal(format(message, args));
                    } else {
                        log4j.fatal(format(message, args));
                    }
                } catch (Throwable arg2) {
                    log4j.error("Oops. Error in Logger !", arg2);
                }
            } else {
                try {
                    juli.severe(format(message, args));
                } catch (Throwable arg3) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg3);
                }
            }
        }

    }

    public static void fatal(Throwable e, String message, Object... args) {
        if (isEnabledFor(Level.FATAL)) {
            if (!forceJuli && log4j != null) {
                try {
                    if (!niceThrowable(Level.FATAL, e, message, args)) {
                        if (recordCaller) {
                            org.apache.log4j.Logger.getLogger(
                                    getCallerClassName()).fatal(
                                    format(message, args), e);
                        } else {
                            log4j.fatal(format(message, args), e);
                        }
                    }
                } catch (Throwable arg3) {
                    log4j.error("Oops. Error in Logger !", arg3);
                }
            } else {
                try {
                    if (!niceThrowable(Level.FATAL, e, message, args)) {
                        juli.log(java.util.logging.Level.SEVERE,
                                format(message, args), e);
                    }
                } catch (Throwable arg4) {
                    juli.log(java.util.logging.Level.SEVERE,
                            "Oops. Error in Logger !", arg4);
                }
            }
        }

    }

    static boolean niceThrowable(Level level, Throwable e, String message,
                                 Object... args) {
        if (!(e instanceof Exception)) {
            return false;
        } else {
            Throwable toClean = e;

            for (int sw = 0; sw < 5; ++sw) {
                ArrayList e1 = new ArrayList();
                StackTraceElement[] errorOut = toClean.getStackTrace();
                int arg7 = errorOut.length;

                for (int arg8 = 0; arg8 < arg7; ++arg8) {
                    StackTraceElement se = errorOut[arg8];

                    toClean.setStackTrace((StackTraceElement[]) e1
                            .toArray(new StackTraceElement[e1.size()]));
                    toClean = toClean.getCause();
                    if (toClean == null) {
                        break;
                    }
                }
            }
            StringWriter arg11 = new StringWriter();


            try {
                if (!forceJuli && log4j != null) {
                    if (recordCaller) {
                        org.apache.log4j.Logger
                                .getLogger(getCallerClassName(5)).log(level,
                                arg11.toString(), (Throwable) null);
                    } else {
                        log4j.log(level, arg11.toString(), e);
                    }
                } else {
                    juli.log(toJuliLevel(level.toString()), arg11.toString(), e);
                }
            } catch (Exception arg10) {
                log4j.error("Oops. Error in Logger !", arg10);
            }

            return true;
        }
    }

    static String format(String msg, Object... args) {
        try {
            return args != null && args.length > 0 ? String.format(msg, args)
                    : msg;
        } catch (Exception arg2) {
            return msg;
        }
    }

    static String getCallerClassName() {
        boolean level = true;
        return getCallerClassName(5);
    }

    static String getCallerClassName(int level) {
        Logger.CallInfo ci = getCallerInformations(level);
        return ci.className;
    }

    static Logger.CallInfo getCallerInformations(int level) {
        StackTraceElement[] callStack = Thread.currentThread().getStackTrace();
        StackTraceElement caller = callStack[level];
        return new Logger.CallInfo(caller.getClassName(),
                caller.getMethodName());
    }

    public static class JuliToLog4jHandler extends Handler {
        public void publish(LogRecord record) {
            org.apache.log4j.Logger log4j = getTargetLogger(record
                    .getLoggerName());
            Level priority = this.toLog4j(record.getLevel());
            log4j.log(priority, this.toLog4jMessage(record), record.getThrown());
        }

        static org.apache.log4j.Logger getTargetLogger(String loggerName) {
            return loggerName != null ? org.apache.log4j.Logger
                    .getLogger(loggerName) : org.apache.log4j.Logger
                    .getRootLogger();
        }

        public static org.apache.log4j.Logger getTargetLogger(Class<?> clazz) {
            return getTargetLogger(clazz.getName());
        }

        private String toLog4jMessage(LogRecord record) {
            String message = record.getMessage();

            try {
                Object[] ex = record.getParameters();
                if (ex != null
                        && ex.length != 0
                        && (message.indexOf("{0}") >= 0
                        || message.indexOf("{1}") >= 0
                        || message.indexOf("{2}") >= 0 || message
                        .indexOf("{3}") >= 0)) {
                    message = MessageFormat.format(message, ex);
                }
            } catch (Exception arg3) {
                ;
            }

            return message;
        }

        private Level toLog4j(java.util.logging.Level level) {
            return java.util.logging.Level.SEVERE == level ? Level.ERROR
                    : (java.util.logging.Level.WARNING == level ? Level.WARN
                    : (java.util.logging.Level.INFO == level ? Level.INFO
                    : (java.util.logging.Level.OFF == level ? Level.TRACE
                    : Level.TRACE)));
        }

        public void flush() {
        }

        public void close() {
        }
    }

    static class CallInfo {
        public String className;
        public String methodName;

        public CallInfo() {
        }

        public CallInfo(String className, String methodName) {
            this.className = className;
            this.methodName = methodName;
        }
    }
}
