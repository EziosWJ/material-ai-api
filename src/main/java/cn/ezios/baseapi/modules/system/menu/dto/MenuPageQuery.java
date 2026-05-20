package cn.ezios.baseapi.modules.system.menu.dto;

import cn.ezios.baseapi.common.model.PageQuery;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 菜单分页查询条件
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MenuPageQuery extends PageQuery {

    /** 菜单名称，模糊匹配 */
    private String menuName;

    /** 菜单类型 */
    private String menuType;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 是否可见：1-可见，0-隐藏 */
    private Integer visible;
}
