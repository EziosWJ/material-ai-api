package cn.ezios.baseapi.modules.system.menu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 菜单保存请求
 */
@Data
public class MenuSaveRequest {

    /** 父级菜单ID，0表示顶级菜单 */
    @NotNull(message = "父级菜单不能为空")
    private Long parentId;

    /** 菜单名称 */
    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 50, message = "菜单名称长度不能超过 50")
    private String menuName;

    /** 菜单类型：目录、菜单、按钮 */
    @NotBlank(message = "菜单类型不能为空")
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

    /** 备注 */
    private String remark;
}
