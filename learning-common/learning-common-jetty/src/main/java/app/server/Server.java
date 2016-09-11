package app.server;

import app.main.Configure;
import app.main.Logger;
import app.utils.EnvironmentUtil;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.webapp.WebAppContext;

import java.io.File;
import java.net.InetAddress;
import java.util.Properties;

/**
 * Created by lili19289 on 2016/8/29.
 */
public class Server {

    private static int httpPort;

    private String contextPath;

    private String webappPath;

    private org.eclipse.jetty.server.Server server;

    private WebAppContext webapp;

    public Server(String[] args){
        System.setProperty("file.encoding", "utf-8");
        Properties p = Configure.configuration;
        httpPort = Integer.parseInt(this.getOpt(args, "http.port",
                p.getProperty("http.port", "-1")));
        if (httpPort == -1 ) {
            httpPort = 9000;
        }
        InetAddress address = null;
        InetAddress secureAddress = null;
        try {
            if (p.getProperty("http.address") != null) {
                address = InetAddress.getByName(p.getProperty("http.address"));
            } else if (System.getProperties().containsKey("http.address")) {
                address = InetAddress.getByName(System
                        .getProperty("http.address"));
            }
        } catch (Exception arg9) {
            Logger.error(arg9, "Could not understand http.address",
                    new Object[0]);
        }
        String webappPath = System.getProperty("jetty.webappPath");
        if (StringUtils.isBlank(webappPath)) {
            webappPath = Configure.configuration.getProperty("jetty.webappPath");
        }
        JettyServer server = new JettyServer(contextPath,webappPath,httpPort);
        server.start();

    }

    private String getOpt(String[] args, String arg, String defaultValue) {
        String s = "--" + arg + "=";
        String[] arg4 = args;
        int arg5 = args.length;
        for (int arg6 = 0; arg6 < arg5; ++arg6) {
            String a = arg4[arg6];
            if (a.startsWith(s)) {
                return a.substring(s.length());
            }
        }
        return defaultValue;
    }


    public static void main(String[] args) throws Exception {
        File root = new File(EnvironmentUtil.getSystemPath());
        Configure.init(root);
        if (System.getProperty("precompile") == null) {
            new Server(args);
        } else {
            Logger.info("Done precompiling.", new Object[0]);
        }

    }
}
