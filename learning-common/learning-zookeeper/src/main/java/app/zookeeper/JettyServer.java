package app.server;


import org.eclipse.jdt.internal.compiler.codegen.IntegerCache;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * Created by lili19289 on 2016/8/4.
 */
public class JettyServer {


    private static final String CONTEXT = "/";

    private static final String DEFAULT_WEBAPP_PATH = "src/main/webapp";

    private static  String PORT;


    /**
     * 创建用于开发运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
     */



    public static void main(String[] args) {
        String portString = ServerProperties.getProperty("jetty.port");
        Server server = new Server(Integer.parseInt(portString));

        //new ClassPathResource("webapp").getURI().toString()
        WebAppContext webContext = new WebAppContext(DEFAULT_WEBAPP_PATH, "");
//        webContext.setContextPath("/");
//        webContext.setDescriptor("src/main/webapp");
        // 设置webapp的位置
//        webContext.setResourceBase(DEFAULT_WEBAPP_PATH);
//        webContext.setClassLoader(Thread.currentThread().getContextClassLoader());
        server.setHandler(webContext);
        try {
            server.stop();
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }


}


