package app.controller;

import app.models.User;
import app.service.UserService;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;

/**
 * Created by lili19289 on 2016/8/4.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping(value="/info",method = RequestMethod.GET)
    public void findUser(){
        User user = userService.getUserById(1);
        System.err.println("--------------************--------------------"+user.getUser_name());
    }

    /**
     * 根据用户名获取用户对象
     * @param name
     * @return
     */
    @RequestMapping(value = "updateStudent", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    @ApiOperation(value = "根据用户名获取用户对象", httpMethod = "GET", response = String.class, notes = "根据用户名获取用户对象")
    public String getUserByName(@ApiParam(required = true, name = "name", value = "用户名") @PathVariable String name) throws Exception{
        User ucUser = userService.getUserById(1);
        return ucUser.getUser_name();


    }

    @ApiOperation("获取用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="header",name="username",dataType="String",required=true,value="用户的姓名",defaultValue="zhaojigang"),
            @ApiImplicitParam(paramType="query",name="password",dataType="String",required=true,value="用户的密码",defaultValue="wangna")
    })
    @ApiResponses({
            @ApiResponse(code=400,message="请求参数没填好"),
            @ApiResponse(code=404,message="请求路径没有或页面跳转路径不对")
    })
    @RequestMapping(value="/getUser",method=RequestMethod.GET)
    public void getUser(@RequestHeader("username") String username, @RequestParam("password") String password) {
    }
}
