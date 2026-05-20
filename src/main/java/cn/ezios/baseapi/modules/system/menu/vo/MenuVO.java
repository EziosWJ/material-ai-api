package cn.ezios.baseapi.modules.system.menu.vo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 菜单视图对象
 * <p>支持树形结构展示，包含子菜单列表</p>
 */
@Data
public class MenuVO {

    /** 主键ID */
    private Long id;

    /** 父级菜单ID */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型 */
    private String menuType;

    /** 路由路径 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 外部链接URL */
    private String externalUrl;

    /** 菜单图标 */
    private String icon;

    /** 权限编码 */
    private String permissionCode;

    /** 排序序号 */
    private Integer sortOrder;

    /** 是否可见 */
    private Integer visible;

    /** 状态 */
    private Integer status;

    /** 是否内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 子菜单列表 */
    private List<MenuVO> children = new ArrayList<>();
}
