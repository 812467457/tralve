package cn.itcast.travel.web.servlet;

import cn.itcast.travel.domain.PageBean;
import cn.itcast.travel.domain.Route;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.FavoriteService;
import cn.itcast.travel.service.RouteService;
import cn.itcast.travel.service.impl.FavoriteServiceImpl;
import cn.itcast.travel.service.impl.RouteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private RouteService service = new RouteServiceImpl();
    private FavoriteService favoriteService = new FavoriteServiceImpl();

    /**
     * 分页查询
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void pageQuery(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接收参数
        String currentPageStr = request.getParameter("currentPage");
        String pageSizeStr = request.getParameter("pageSize");
        String cidStr = request.getParameter("cid");
        String rname = request.getParameter("rname");
        //tomcat7乱码问题
        //rname = new String(rname.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);


        int currentPage = 0;
        int pageSize = 0;
        int cid = 0;
        //处理参数
        //当前页码，默认为1
        if (currentPageStr != null && currentPageStr.length() > 0) {
            currentPage = Integer.parseInt(currentPageStr);
        } else {
            currentPage = 1;
        }
        //每页显示条数，默认为5
        if (pageSizeStr != null && pageSizeStr.length() > 0) {
            pageSize = Integer.parseInt(pageSizeStr);
        } else {
            pageSize = 5;
        }
        //类别id
        if (cidStr != null && cidStr.length() > 0 && !"null".equals(cidStr)) {
            cid = Integer.parseInt(cidStr);
        }

        //调用service查询PageBean对象
        PageBean<Route> pb = service.pageQuery(cid, currentPage, pageSize, rname);
        //序列化返回
        writeValue(pb, response);
    }

    /**
     * 根据id查询一个旅游线路详细信息
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void findOne(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //接收id
        String rid = request.getParameter("rid");
        //调用service查询route对象
        Route route = service.findOne(rid);
        //转为json写回客户端
        writeValue(route, response);
    }

    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取线路id
        String rid = request.getParameter("rid");
        //获取当前登陆的user
        int uid;
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            //用户未登陆
            uid = 0;
        } else {
            uid = user.getUid();
        }

        //调用FavoriteService查询是否收藏
        boolean flag = favoriteService.isFavorite(rid, uid);

        //写回客户端
        writeValue(flag, response);
    }

    /**
     * 添加收藏
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取rid
        String rid = request.getParameter("rid");
        //获取当前登陆的user
        int uid;
        User user = (User) request.getSession().getAttribute("user");
        if (user == null) {
            //用户未登陆
            return;
        } else {
            uid = user.getUid();
        }
        //调用service
        favoriteService.add(rid,uid);
    }
}
