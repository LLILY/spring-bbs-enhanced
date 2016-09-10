package app.dao;

import app.models.User;

/**
 * Created by lili19289 on 2016/8/4.
 */
public interface UserDao extends DAOBase{

    public User findById(Long userId);
}
