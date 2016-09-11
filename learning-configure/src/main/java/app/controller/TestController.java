package app.controller;

import app.context.RuntimeContext;
import app.curator.ZooKeeperClient;
import app.utils.ZookeeperUtil;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by lili19289 on 2016/8/14.
 */
@Controller
public class TestController {
    private static final Logger LOG = Logger.getLogger(TestController.class);

    @RequestMapping("/update")
    public String update(){
        System.out.println("--------------");
//        ZookeeperUtil.writeData();
        ZooKeeperClient zooKeeperClient= RuntimeContext.getBean(ZooKeeperClient.class);
        zooKeeperClient.setData("/learning","aaaaa");
        if(zooKeeperClient.exists("/learning/config")){
            zooKeeperClient.mkdirs("/learning/config");
        }
        zooKeeperClient.setData("/learning/config","测试qqq");
        LOG.info("--------------");

        return "";
    }
}
