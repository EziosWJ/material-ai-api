package cn.ezios.baseapi.modules.auth.service;

import cn.ezios.baseapi.modules.auth.dto.LoginRequest;
import cn.ezios.baseapi.modules.auth.vo.AuthMenuVO;
import cn.ezios.baseapi.modules.auth.vo.AuthUserVO;
import cn.ezios.baseapi.modules.auth.vo.LoginTokenVO;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 认证服务接口，定义登录、退出、获取当前用户信息和菜单等能力。
 */
public interface AuthService {

    /**
     * 用户登录。
     *
     * @param request        登录请求参数
     * @param servletRequest 用于获取客户端 IP
     * @return 令牌信息
     */
    LoginTokenVO login(LoginRequest request, HttpServletRequest servletRequest);

    /**
     * 退出登录，清除当前会话。
     */
    void logout();

    /**
     * 获取当前登录用户的详细信息。
     *
     * @return 用户信息，包含部门和角色
     */
    AuthUserVO getCurrentUser();

    /**
     * 获取当前登录用户可见的菜单树。
     *
     * @return 菜单树形列表
     */
    List<AuthMenuVO> getCurrentUserMenus();
}
