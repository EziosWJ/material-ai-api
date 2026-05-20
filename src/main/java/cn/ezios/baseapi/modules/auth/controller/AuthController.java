package cn.ezios.baseapi.modules.auth.controller;

import cn.ezios.baseapi.common.model.ApiResponse;
import cn.ezios.baseapi.modules.auth.dto.LoginRequest;
import cn.ezios.baseapi.modules.auth.service.AuthService;
import cn.ezios.baseapi.modules.auth.vo.AuthMenuVO;
import cn.ezios.baseapi.modules.auth.vo.AuthUserVO;
import cn.ezios.baseapi.modules.auth.vo.LoginTokenVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证控制器，提供登录、退出、当前用户信息和菜单等接口。
 */
@Tag(name = "认证")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录，校验用户名和密码，返回令牌信息。
     *
     * @param request        登录请求参数（用户名、密码）
     * @param servletRequest 用于获取客户端 IP
     * @return 令牌信息
     */
    @Operation(summary = "登录")
    @PostMapping("/login")
    public ApiResponse<LoginTokenVO> login(@Valid @RequestBody LoginRequest request,
                                           HttpServletRequest servletRequest) {
        return ApiResponse.success(authService.login(request, servletRequest));
    }

    /**
     * 退出登录，清除当前会话。
     */
    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success();
    }

    /**
     * 获取当前登录用户的详细信息，包括部门和角色。
     *
     * @return 当前用户信息
     */
    @Operation(summary = "当前用户信息")
    @GetMapping("/me")
    public ApiResponse<AuthUserVO> me() {
        return ApiResponse.success(authService.getCurrentUser());
    }

    /**
     * 获取当前登录用户可见的菜单树。
     *
     * @return 菜单树形列表
     */
    @Operation(summary = "当前用户可见菜单")
    @GetMapping("/menus")
    public ApiResponse<List<AuthMenuVO>> menus() {
        return ApiResponse.success(authService.getCurrentUserMenus());
    }
}
