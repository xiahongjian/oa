package tech.hongjian.oa.service.impl;

import java.util.Set;

import tech.hongjian.oa.service.CacheService;

/**
 * @author xiahongjian
 * @since  2021-01-16 14:34:58
 */
public class RedisCacheServiceImpl implements CacheService {

    @Override
    public void set(String key, Object value) {
        // TODO Auto-generated method stub

    }

    @Override
    public Object get(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean contains(String key) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object remove(String key) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setSet(String key, Object vlaue) {
    }

    @Override
    public void setSet(String key, Object... values) {
    }

    @Override
    public Object removeSet(String key, Object value) {
        return null;
    }

    @Override
    public Set<Object> removeSet(String key, Object... values) {
        return null;
    }

}