package app.controller;

import app.utils.JackSonUtil;
import app.zookeeper.ConfigureUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lili19289 on 2016/8/25.
 */
@Controller
public class ConfigureController {


    @ResponseBody
    @RequestMapping("/loadConfigure")
    public String loadConfigure(HttpServletRequest request){
        String catatlog= request.getParameter("catatlog");
        Map<String, Object> configData  = ConfigureUtil.getConfigureData(catatlog);
        return JackSonUtil.toJson(configData);
    }


    @ResponseBody
    @RequestMapping("/saveConfigure")
    public String saveConfigure(HttpServletRequest request){
        String catatlog= request.getParameter("catatlog");
        Map<String, Object> resMap = new HashMap<String, Object>();
        Enumeration<?> nameEnumeration = request.getParameterNames();
        String name = "";
        while( nameEnumeration.hasMoreElements() ) {
            name = (String) nameEnumeration.nextElement();
            resMap.put(name, request.getParameter(name));
        }
        ConfigureUtil.setConfigureData(catatlog,resMap);
        return "true";
    }


}
