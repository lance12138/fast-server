package com.jxust.server;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;

public class App {

    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);
    private static final String LOG_XML="logback.xml";

    public static void main( String[] args ) throws Exception {
        initLog();
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("classpath*:application.xml");
        ctx.start();
        System.out.println( "spring framework is started" );
        //ClientServer server = ctx.getBean(ClientServer.class);
        FastServer server = ctx.getBean(FastServer.class);
        server.start();
        System.out.println( "Hello World!" );
    }


    public static void initLog() throws Exception{
        LoggerContext loggerContext =(LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(loggerContext);
        loggerContext.reset();
        configurator.doConfigure(new ClassPathResource(LOG_XML).getInputStream());
        StatusPrinter.printInCaseOfErrorsOrWarnings(loggerContext);
        LOGGER.info("[wechat-server] log has init success");
    }
}
