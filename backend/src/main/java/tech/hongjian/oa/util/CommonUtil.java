package tech.hongjian.oa.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.flowable.common.engine.impl.db.SuspensionState;
import tech.hongjian.oa.entity.BaseEntity;
import tech.hongjian.oa.entity.BaseEntityWithOperator;
import tech.hongjian.oa.entity.User;
import tech.hongjian.oa.model.anno.UserInfo;
import tech.hongjian.oa.service.UserService;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author xiahongjian
 * @time 2021/1/25 23:05
 */
@Slf4j
public class CommonUtil {
    public static final long DAY_IN_SECOND = 24 * 3600;
    public static final long HOUR_IN_SECOND = 3600;
    public static final long MINUTES_IN_SECOND = 60;
    private static final String DURATION_STR_FORMATTER = "%d天%d小时%d分";

    public static String wrapWith(String value, String s) {
        if (value == null) {
            return null;
        }
        return s + value + s;
    }

    public static String wrapWithPercent(String value) {
        return wrapWith(value, "%");
    }

    public static  <T extends BaseEntity> T setEntityDefault(T entity) {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreatedAt(now);
        entity.setUpdatedAt(now);
        return entity;
    }

    public static  <T extends BaseEntityWithOperator> T setEntityDefault(T entity, Integer createdBy) {
        LocalDateTime now = LocalDateTime.now();
        entity = setEntityDefault(entity);
        entity.setCreatedBy(createdBy);
        entity.setUpdatedBy(createdBy);
        return entity;
    }

    public static  <T extends BaseEntityWithOperator> T setUpdateDefault(T entity, Integer updatedBy) {
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setUpdatedBy(updatedBy);
        return entity;
    }

    public static boolean isSuspendState(Integer code) {
        return code != null && code == SuspensionState.SUSPENDED.getStateCode();
    }

    public static boolean isActiveState(Integer code) {
        return code != null && code == SuspensionState.ACTIVE.getStateCode();
    }

    public static Integer toInteger(String value) {
        return value == null || !NumberUtils.isDigits(value) ? null : Integer.valueOf(value);
    }

    public static <T> T fetchUserInfo(T entity) {
        if (entity != null) {
            Field[] fields = FieldUtils.getFieldsWithAnnotation(entity.getClass(), UserInfo.class);
            UserService userService = ApplicationContextHolder.getAppCtx().getBean(UserService.class);
            try {
                for (Field field : fields) {
                    UserInfo annotation = field.getAnnotation(UserInfo.class);
                    String userField = annotation.userField();
                    Object value = FieldUtils.readField(entity, userField, true);
                    if (value != null) {
                        if (value instanceof Integer) {
                            User userBriefInfo = userService.getUserBriefInfo((Integer) value);
                            FieldUtils.writeField(field, entity, userBriefInfo, true);
                        } else if (value instanceof List) {
                            List<Integer> ids = (List<Integer>) value;
                            List<User> userInfos = new ArrayList<>(ids.size());
                            for (Integer id : ids) {
                                User info = userService.getUserBriefInfo(id);
                                if (info != null) {
                                    userInfos.add(info);
                                }
                            }
                            FieldUtils.writeField(field, entity, userInfos, true);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                log.error("反射获取字段失败，信息：{}", e.getMessage(), e);
            }
        }
        return entity;
    }

    public static String durationToStr(long durationInSecond) {
        long days = durationInSecond / DAY_IN_SECOND;
        long hours = durationInSecond % DAY_IN_SECOND / HOUR_IN_SECOND;
        long seconds = durationInSecond % HOUR_IN_SECOND / MINUTES_IN_SECOND;
        return String.format(DURATION_STR_FORMATTER, days, hours, seconds);
    }
}
