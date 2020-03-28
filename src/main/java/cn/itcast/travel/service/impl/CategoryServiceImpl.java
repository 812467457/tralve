package cn.itcast.travel.service.impl;

import cn.itcast.travel.dao.CategoryDao;
import cn.itcast.travel.dao.impl.CategoryDaoImpl;
import cn.itcast.travel.domain.Category;
import cn.itcast.travel.service.CategoryService;
import cn.itcast.travel.util.JedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    private CategoryDao dao = new CategoryDaoImpl();

    /**
     * 查询分类
     *
     * @return
     */
    @Override
    public List<Category> findAll() {
        //从rides中查询
        Jedis jedis = JedisUtil.getJedis();
        //使用sortedset排序查询
        //Set<String> categorys = jedis.zrange("category", 0, -1);
        //查询sortedset中的分数
        Set<Tuple> categorys = jedis.zrangeWithScores("category", 0, -1);
        List<Category> cs = null;
        //判断是否为空
        if (categorys == null || categorys.size() == 0) {
            //缓存中为空，从数据库获取数据
            cs = dao.finAll();
            //存入缓存
            for (int i = 0; i < cs.size(); i++) {
                jedis.zadd("category", cs.get(i).getCid(), cs.get(i).getCname());
            }
        } else {
            //如果不为空，将set的数据存入list
            cs = new ArrayList<Category>();
            for (Tuple tuple : categorys) {
                Category category = new Category();
                category.setCname(tuple.getElement());
                category.setCid((int) tuple.getScore());
                cs.add(category);
            }

        }
        return cs;
    }
}
