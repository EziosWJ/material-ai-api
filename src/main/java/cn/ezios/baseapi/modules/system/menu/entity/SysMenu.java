package cn.ezios.baseapi.modules.system.menu.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;
import lombok.Data;

/**
 * 菜单实体
 * <p>对应数据库表 sys_menu，支持树形层级结构</p>
 */
@Data
@TableName("sys_menu")
public class SysMenu {

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 父级菜单ID，0表示顶级菜单 */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型：目录、菜单、按钮 */
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

    /** 是否可见：1-可见，0-隐藏 */
    private Integer visible;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 是否内置：1-内置，0-非内置 */
    private Integer isBuiltin;

    /** 备注 */
    private String remark;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 创建人ID */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /** 更新人ID */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /** 逻辑删除标志：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
