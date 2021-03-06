package tech.hongjian.oa.entity;

import com.baomidou.mybatisplus.annotation.TableField;

import lombok.Data;
import lombok.EqualsAndHashCode;
import tech.hongjian.oa.entity.enums.Status;

/**
 * <p>
 *
 * </p>
 *
 * @author xiahongjian
 * @since 2021-01-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Dict extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String name;

    @TableField("`key`")
    private String key;

    private Status status;

    private String remark;


}
