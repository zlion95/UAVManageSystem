package com.zlion.controller;

import com.zlion.model.Uav;
import com.zlion.model.User;
import com.zlion.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zzs on 2016/9/1.
 */

@Controller
@EnableAutoConfiguration
@RequestMapping("/auth")
public class AuthController {

    private AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }



    @RequestMapping(value = "/home")
    public String userHome(Map<String,Object> map){

        //using map to set value by thymeleaf templates
        map.put("userName","233");
        return "/home";

    }

    /*
     * This is a simple test method for return json data
     */
    @ResponseBody
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public Map<String, Object> index(){

        Map<String, Object> map = new HashMap<String, Object>(3);

        map.put("state","OK");
        map.put("data","jklsdjfklajldfjalkjfklajflka");
        map.put("code","200");

        return map;
    }

    //用户注册
    /**
     * @api {post} /auth/registe Add User information
     * @apiName Registe User
     * @apiGroup Auth
     *
     * @apiParam {Number} id Users unique ID.
     *
     * @apiParam {String} username Username of the User.
     * @apiParam {String} password  Password of the User.
     * @apiParam {String} email  Email of the User.
     * @apiParam {String} Phone  Phone of the User.
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     *
     * @apiError UserNotFound The arguments of the User error.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 404 Not Found
     *     {
     *       "error": "Arguments Error"
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/registe", method = RequestMethod.POST)
    public Result registe(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        String regUsername = request.getParameter("username");
        String regPassword = request.getParameter("password");
        String regEmail = request.getParameter("email");
        String regPhone = request.getParameter("phone");

        User user = new User(regUsername, regPassword, regPhone, null, regEmail, null, new Date());
        try{
            authService.registe(session,user);
        }catch (Exception e){
            e.printStackTrace();
            jsonRender.argError();
        }

        return jsonRender;
    }


    /*
    登录
     */
    /**
     * @api {post} /auth/login User login system
     * @apiName User login
     * @apiGroup Auth
     *
     * @apiParam {String} username Username of the User.
     * @apiParam {String} password  Password of the User.
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     *
     * @apiError UserOrPassError The Username or password error.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 103 LoginError
     *     {
     *       "Msg": "User or Password Error",
     *       "Code": 103
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public Result authLogin(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        if ( !authService.loginValidate(session, username, password) ){
            jsonRender.passError();
        }

        return jsonRender;
    }

    /*
    登出
     */
    /**
     * @api {get} /auth/logout User logout from system
     * @apiName User logout
     * @apiGroup Auth
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     *
     * @apiError NeedAuth No user login.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 101 NeedAuth
     *     {
     *       "Msg": "User Not Login",
     *       "Code": 101
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public Result authLogout(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        if (session.getAttribute("auth") == null){
            jsonRender.needAuth();
        }
        else{
            session.removeAttribute("auth");
            session.removeAttribute("authId");
        }

        return jsonRender;
    }

    /*
    修改个人信息，需要添加验证数据类型和长度等的代码
     */
    /**
     * @api {post} /auth/update/detail User update personal information in system
     * @apiName User update，请前端在数据传输之前判断数据格式问题
     * @apiGroup Auth
     *
     * @apiParam {groupName} User belonged group name
     * @apiParam {address} User address
     * @apiParam {email} User email
     * @apiParam {email} User telephone number
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/update/detail", method = RequestMethod.POST)
    public Result authUpdate(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        User user = (User) session.getAttribute("auth");
        user.setGroupName(request.getParameter("groupName"));
        user.setAddress(request.getParameter("address"));
        user.setEmail(request.getParameter("email"));
        user.setPhone(request.getParameter("phone"));

        authService.updateAuth(user);
        session.setAttribute("auth", user);
        return jsonRender;
    }

    /*
    用户修改密码
     */
    /**
     * @api {put} /auth/update/password User update password
     * @apiName User update password
     * @apiGroup Auth
     *
     * @apiParam {String} oldPassword Old password for User
     * @apiParam {String} newPassword  New password for User.
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     *
     * @apiError UserOrPassError The old password error.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 103 OldPwdError
     *     {
     *       "Msg": "Old password error",
     *       "Code": 103
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/update/password", method = RequestMethod.PUT)
    public Result authUpdatePwd(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        User user = (User) session.getAttribute("auth");
        String oldPwd = request.getParameter("oldPassword");
        if (oldPwd.equals(user.getPassword())){
            String newPwd = request.getParameter("newPassword");
            authService.updatePwd(session, newPwd);
        }
        else{
            jsonRender.passError();
            jsonRender.put("Msg", "Old password error!");
        }

        return jsonRender;
    }

    /*
    添加无人机
     */
    /**
     * @api {post} /auth/uav/add User add UAV
     * @apiName User add a new UAV
     * @apiGroup Auth
     *
     * @apiParam {String} uuid uuid of UAV
     * @apiParam {String} groupName  UAV belong to
     * @apiParam {String} info  UAV information
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     *
     *
     */
    @ResponseBody
    @RequestMapping(value = "/uav/add", method = RequestMethod.POST)
    public Result addUav(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        String uuid = request.getParameter("uuid");
        Long userId = (Long) session.getAttribute("authId");
        String groupName = request.getParameter("groupName");
        String info = request.getParameter("info");

        Uav uav = new Uav(uuid,userId,groupName,info,new Date());
        authService.addUav(uav);
        return jsonRender;
    }

    /*
    返回用户的无人列表
     */
    /**
     * @api {get} /auth/uavs List all UAVs belong to user
     * @apiName List UAVs
     * @apiGroup Auth
     *
     *
     * @apiSource {Number} Code Return code of state
     * @apiSource {String} Msg Msg of state
     * @apiSource {List<Uav>}  Return data of Uav list
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *       "data": [{
     *           "id": Long,
     *           "uuid": String,
     *           "groupName": String,
     *           "info": String,
     *           "registDate": Date,
     *           "confirm": int
     *       },{...},
     *       ...]
     *
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/uavs", method = RequestMethod.GET)
    public Result getUavList(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        Long userId = (Long) session.getAttribute("authId");
        List<Uav> uavs = authService.getUavs(userId);
        jsonRender = jsonRender.okForList();

        if (uavs == null)
            jsonRender.put("Msg", "No Uav");

        return jsonRender;
    }

    /*
    删除无人机
     */

    /**
     * @api {delete} /auth/uav/delete Delete uav
     * @apiName delete UAV
     * @apiGroup Auth
     *
     * @apiParam {String} uuid uuid of UAV
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "Code": 100,
     *       "Msg": "ok"
     *     }
     *
     * @apiError NeedAuth User has no ability to delete.
     * @apiError IllegalAction User delete error.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 101 NeedAuth
     *     {
     *       "Msg": "User Not Login",
     *       "Code": 101
     *     }
     *
     *     HTTP/1.1 104 IllegalAction
     *     {
     *       "Msg": "Illegal Action Parameters",
     *       "Code": 104
     *     }
     */
    @ResponseBody
    @RequestMapping(value = "/uav/delete", method = RequestMethod.DELETE)
    public Result delUav(HttpServletRequest request, HttpSession session){
        Result jsonRender = new Result();

        String uuid = request.getParameter("uuid");
        Long userId = (Long) session.getAttribute("authId");
        try{
            if(!authService.delUav(userId, uuid))
                jsonRender.needAuth();
        }catch (Exception e){
            e.printStackTrace();
            jsonRender.illegalMethod();
        }

        return jsonRender;
    }


}
