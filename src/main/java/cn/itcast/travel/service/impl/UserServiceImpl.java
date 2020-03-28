package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.UserDao;
import cn.itcast.travel.dao.impl.UserDaoImpl;
import cn.itcast.travel.domain.User;
import cn.itcast.travel.service.UserService;
import cn.itcast.travel.util.MailUtils;
import cn.itcast.travel.util.UuidUtil;

public class UserServiceImpl implements UserService {
    private UserDao dao = new UserDaoImpl();
    /**
     * 注册用户
     * @param user
     * @return
     */
    @Override
    public Boolean regist(User user) {
        //根据用户名查询用户
        User u = dao.findByUsername(user.getUsername());
        //判断用户是否存在
        if (u != null) {
            //用户名存在
            return false;
        }
        //用户不存在 保存信息
        //设置激活码
        user.setCode(UuidUtil.getUuid());
        //设置激活状态
        user.setStatus("N");
        dao.save(user);

        //发送激活邮件
        String content = "<a href=' http://localhost:80/travel/user/active?code="+user.getCode() +"'>点击激活【黑马旅游网】</a>";
        MailUtils.sendMail(user.getEmail(),content,"激活邮件");

        return true;
    }

    /**
     * 激活用户
     * @param code
     * @return
     */
    @Override
    public boolean active(String code) {
        //根据激活码查询用户对象
        User user = dao.findByCode(code);
        if (user != null) {
            //调用dao方法激活用户
            dao.updateStatus(user);
            return true;
        }else {
            return false;
        }
    }


    /**
     * 登陆方法
     * @param user
     * @return
     */
    @Override
    public User login(User user) {
        return dao.findByUsernameAndByPassword(user.getUsername(),user.getPassword());
    }
}
