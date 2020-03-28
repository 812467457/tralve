package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.ResultInfo;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.beanutils.BeanUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    //声明userService业务对象
    private UserService service = new UserServiceImpl();

    /**
     * 注册方法
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void regist(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //验证码校验
        String check = request.getParameter("check");
        //从session中获取验证码
        HttpSession session = request.getSession();
        String checkcode_server = (String) session.getAttribute("CHECKCODE_SERVER");
        //删除验证码
        session.removeAttribute("CHECKCODE_SERVER");
        //比较
        if (checkcode_server == null || !checkcode_server.equalsIgnoreCase(check)) {
            //验证码错误
            ResultInfo info = new ResultInfo();
            info.setFlag(false);
            info.setErrorMsg("注册失败，验证码错误");
            //将info对象序列化为json写回客户端
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(info);
            //设置content-type
            response.setContentType("application/json;charset=utf-8");
            //将json写回客户端
            response.getWriter().write(json);
            return;
        }
        Map<String, String[]> map = request.getParameterMap();
        //封装对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Boolean flag = service.regist(user);
        ResultInfo info = new ResultInfo();
        //响应结果
        if (flag) {
            //注册成功
            info.setFlag(true);
        } else {
            //注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败！");
        }

        //将info对象序列化为json写回客户端
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(info);

        System.out.println(user.getSex());
        //设置content-type
        response.setContentType("application/json;charset=utf-8");
        //将json写回客户端
        response.getWriter().write(json);
    }

    /**
     * 登陆方法
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //1.获取用户名和密码数据
        Map<String, String[]> map = request.getParameterMap();
        //2.封装User对象
        User user = new User();
        try {
            BeanUtils.populate(user, map);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        //3.调用Service查询
        User u = service.login(user);

        ResultInfo info = new ResultInfo();

        //4.判断用户对象是否为null
        if (u == null) {
            //用户名密码或错误
            info.setFlag(false);
            info.setErrorMsg("用户名密码或错误");
        }
        //5.判断用户是否激活
        if (u != null && !"Y".equals(u.getStatus())) {
            //用户尚未激活
            info.setFlag(false);
            info.setErrorMsg("您尚未激活，请激活");
        }
        //6.判断登录成功
        if (u != null && "Y".equals(u.getStatus())) {
            request.getSession().setAttribute("user", u);//登录成功标记
            //登录成功
            info.setFlag(true);
        }

        //响应数据
        writeValue(info, response);
    }

    /**
     * 查询一个对象
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //从session中获取登录用户
        Object user = request.getSession().getAttribute("user");
        //序列化，将user写回客户端
        writeValue(user, response);
    }

    /**
     * 退出方法
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void exit(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //销毁session
        request.getSession().invalidate();

        //跳转页面
        response.sendRedirect(request.getContextPath() + "/login.html");
    }

    /**
     * 激活方法
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void active(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String code = request.getParameter("code");
        if (code != null) {
            //调用service激活
            boolean flag = service.active(code);

            //判断标记
            String msg = null;
            if (flag) {
                //激活成功
                msg = "激活成功，请<a href='login.html'>登陆</a>";
            } else {
                //激活失败
                msg = "激活失败";
            }

            response.setContentType("text/html;charset=utf-8");
            response.getWriter().write(msg);
        }
    }
}
