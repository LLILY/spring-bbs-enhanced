package app.server;


import app.main.Configure;
import app.main.Logger;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.webapp.WebAppContext;


import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by lili19289 on 2016/8/4.
 */
public class JettyServer {


    private static final String PROP_NAME__PREFIX =  "jetty.";

    private static final String PROP_NAME_CONTEXT_PATH = PROP_NAME__PREFIX + "contextPath";

    private static final String PROP_NAME_WEBAPP_PATH = PROP_NAME__PREFIX + "webappPath";

    private static final String PROP_NAME_HOST = PROP_NAME__PREFIX + "host";

    private static final String PROP_NAME_PORT = PROP_NAME__PREFIX + "port";

    private static  int port;

    private String contextPath;

    private String webappPath;

    private Server server;

    private WebAppContext webapp;


    /**
     * 创建用于开发运行调试的Jetty Server, 以src/main/webapp为Web应用目录.
     */



    public static void main(String[] args) {
        String contextPath = System.getProperty(PROP_NAME_CONTEXT_PATH);
        if (StringUtils.isBlank(contextPath)) {
            contextPath = Configure.configuration.getProperty(PROP_NAME_CONTEXT_PATH);
        }
        String webappPath = System.getProperty(PROP_NAME_WEBAPP_PATH);
        if (StringUtils.isBlank(webappPath)) {
            webappPath = Configure.configuration.getProperty(PROP_NAME_WEBAPP_PATH);
        }

        String portString = System.getProperty(PROP_NAME_PORT);
        if (StringUtils.isBlank(portString)) {
            portString = Configure.configuration.getProperty(PROP_NAME_PORT);
        }
        int port = 0;
        try {
            port = Integer.parseInt(portString);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(PROP_NAME_PORT + " should be an integer between 1 and 65535");
        }
        JettyServer jettyServer = new JettyServer(contextPath, webappPath, port);
        jettyServer.start();

    }

    public JettyServer(String contextPath, String webappPath, int port) {
        if (StringUtils.isBlank(contextPath)) {
            this.contextPath = "";
        }else{
            this.contextPath = contextPath;
        }
        if (StringUtils.isBlank(webappPath)) {
            throw new IllegalArgumentException("webappPath is required");
        }else{
            this.webappPath = webappPath;
        }
        this.port = port;
    }


    public void start()  {
        if (null == server || server.isStopped()) {
            doStart();
        } else {
            throw new IllegalStateException("JettyServer already started.");
        }
    }

    private void doStart()  {
        if (!checkServerPortAvailable()) {
            throw new IllegalStateException("Server port already in use: " + port);
        }
        webapp = new WebAppContext(webappPath, contextPath);
        server = new Server(port);
        server.setHandler(webapp);
        try {
            long st = System.currentTimeMillis();
            server.start();
            long sp = System.currentTimeMillis() - st;
            Logger.info("Listening for HTTP on port %s  ...，started %s seconds",new Object[] { Integer.valueOf(port),String.format("%.2f sec", sp / 1000D) });
            server.join();
        }catch (Exception e){
            e.printStackTrace();
            Logger.error("JettyServer started failed!",e);
        }
    }

    private boolean checkServerPortAvailable() {
        if (0 < port) {
            ServerSocket ss = null;
            try {
                ss = new ServerSocket(port, 0, null);
            } catch (Exception e) {
                Logger.error("check serverPort failed", e);
                return false;
            } finally {
                if (null != ss) {
                    try {
                        ss.close();
                    } catch (IOException e) {
                        Logger.error("close ServerSocket failed", e);
                    }
                }
            }
        } else {
            throw new IllegalArgumentException("Invalid port " + port);
        }
        return true;
    }



}


