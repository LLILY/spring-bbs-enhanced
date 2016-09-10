package app.controller;

import app.models.User;
import app.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;

/**
 * Created by lili19289 on 2016/8/4.
 */
@Controller
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("/index")
    public ModelAndView findUser(){
        User user = userService.getUserById(1);
        System.err.println("--------------************--------------------"+user.getUser_name());
        return new ModelAndView("index");
    }
}
