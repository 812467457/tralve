package cn.itcast.travel.service;

import cn.itcast.travel.domain.User;

public interface UserService {
    /**
     * 注册用户
     * @param user
     * @return
     */
    Boolean regist(User user);

    /**
     * 激活用户
     * @param code
     * @return
     */
    boolean active(String code);

    User login(User user);
}
