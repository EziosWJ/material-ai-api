package cn.ezios.baseapi.modules.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

/**
 * 当前用户可见菜单视图对象，支持树形子菜单。
 */
@Data
@Schema(description = "当前用户可见菜单")
public class AuthMenuVO {

    /** 菜单 ID */
    @Schema(description = "菜单 ID")
    private Long id;

    /** 父级菜单 ID，0 表示根菜单 */
    @Schema(description = "父级菜单 ID")
    private Long parentId;

    /** 菜单名称 */
    @Schema(description = "菜单名称")
    private String menuName;

    /** 菜单类型（目录、菜单、按钮等） */
    @Schema(description = "菜单类型")
    private String menuType;

    /** 前端路由路径 */
    @Schema(description = "路由路径")
    private String path;

    /** 前端组件标识 */
    @Schema(description = "前端组件标识")
    private String component;

    /** 菜单图标 */
    @Schema(description = "图标")
    private String icon;

    /** 权限编码，用于按钮级权限控制 */
    @Schema(description = "权限编码")
    private String permissionCode;

    /** 排序值，数值越小越靠前 */
    @Schema(description = "排序值")
    private Integer sortOrder;

    /** 是否显示，1=显示，0=隐藏 */
    @Schema(description = "是否显示，1=显示，0=隐藏")
    private Integer visible;

    /** 子菜单列表 */
    @Schema(description = "子菜单")
    private List<AuthMenuVO> children = new ArrayList<>();
}
