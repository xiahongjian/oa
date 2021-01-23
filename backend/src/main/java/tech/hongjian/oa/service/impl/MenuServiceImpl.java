package tech.hongjian.oa.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.hongjian.oa.entity.Menu;
import tech.hongjian.oa.entity.enums.MenuType;
import tech.hongjian.oa.exception.CommonServiceException;
import tech.hongjian.oa.mapper.MenuMapper;
import tech.hongjian.oa.service.MenuService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xiahongjian
 * @since 2021-01-12
 */
@Slf4j
@Setter(onMethod_ = {@Autowired})
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> getRoleInterfaceMenus(Integer roleId) {
        if (roleId == null) {
            return Collections.emptyList();
        }
        return getRoleInterfaceMenus(Arrays.asList(roleId));
    }

    @Override
    public List<Menu> getRoleInterfaceMenus(List<Integer> roleIds) {
        if (roleIds.isEmpty()) {
            return Collections.emptyList();
        }
        return getBaseMapper().findRoleMenus(MenuType.INTERFACE, roleIds);
    }

    @Override
    public List<Menu> getMenuTree() {
        return getMenuTree(null, null);
    }


    @Override
    public List<Menu> getMenuTree(String query, Boolean visible) {
        List<Menu> allMenus = getBaseMapper().findAllMenuWithRole(query == null ? null : ("%" + query + "%"), visible);
        return toMenuTree(allMenus);
    }

    @Override
    public void deleteMenu(Integer id) {
        if (id == null) {
            return;
        }
        log.info("删除ID为[{}]的菜单。", id);
        getBaseMapper().deleteById(id);
    }

    private List<Menu> toMenuTree(List<Menu> allMenus) {
        if (allMenus.isEmpty()) {
            return Collections.emptyList();
        }
        Map<Integer, Menu> idMenuMap = allMenus.stream().collect(Collectors.toMap(Menu::getId, m -> m));
        List<Menu> tree = new ArrayList<>();

        for (Menu menu : allMenus) {
            if (menu.getParentId() == null) { // 只处理顶级菜单
                tree.add(menu);
                continue;
            }
            if (idMenuMap.containsKey(menu.getParentId())){
                idMenuMap.get(menu.getParentId()).getChildren().add(menu);
            }
        }
        // 按sort排序
        return sortMenu(tree);
    }

    private List<Menu> sortMenu(List<Menu> menus) {
        if (menus == null || menus.isEmpty()) {
            return menus;
        }
        return menus.stream().map(m -> {
            m.setChildren(sortMenu(m.getChildren()));
            return m;
        }).sorted(new Comparator<Menu>() {
            @Override
            public int compare(Menu o1, Menu o2) {
                return o1.getSort().compareTo(o2.getSort());
            }
        }).collect(Collectors.toList());
    }


    @Override
    public void updateMenu(Integer id, Menu menu) {
        if (id == null || getById(id) == null) {
            throw new CommonServiceException("ID为[" + id + "]的菜单不存在。");
        }
        menu.setUpdateTime(LocalDateTime.now());
        updateById(menu);
    }



}
