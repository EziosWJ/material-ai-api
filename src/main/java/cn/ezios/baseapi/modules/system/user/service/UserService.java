package cn.ezios.baseapi.modules.system.user.service;

import cn.ezios.baseapi.common.model.BatchIdsRequest;
import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.common.model.StatusUpdateRequest;
import cn.ezios.baseapi.modules.system.user.dto.AvatarUpdateRequest;
import cn.ezios.baseapi.modules.system.user.dto.PasswordChangeRequest;
import cn.ezios.baseapi.modules.system.user.dto.UserPageQuery;
import cn.ezios.baseapi.modules.system.user.dto.UserRoleRequest;
import cn.ezios.baseapi.modules.system.user.dto.UserSaveRequest;
import cn.ezios.baseapi.modules.system.user.dto.UserUpdateRequest;
import cn.ezios.baseapi.modules.system.user.vo.ResetPasswordVO;
import cn.ezios.baseapi.modules.system.user.vo.UserVO;

/**
 * 用户管理服务接口
 * <p>定义用户的增删改查、状态管理、角色分配、密码管理等业务操作</p>
 */
public interface UserService {

    /**
     * 分页查询用户列表
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<UserVO> page(UserPageQuery query);

    /**
     * 获取用户详情（含部门和角色信息）
     *
     * @param id 用户ID
     * @return 用户详情
     */
    UserVO getDetail(Long id);

    /**
     * 新增用户
     *
     * @param request 用户保存请求
     */
    void create(UserSaveRequest request);

    /**
     * 修改用户
     *
     * @param id      用户ID
     * @param request 用户修改请求
     */
    void update(Long id, UserUpdateRequest request);

    /**
     * 删除用户
     *
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 批量删除用户
     *
     * @param request 包含用户ID列表的请求
     */
    void deleteBatch(BatchIdsRequest request);

    /**
     * 修改用户状态
     *
     * @param id      用户ID
     * @param request 状态更新请求
     */
    void updateStatus(Long id, StatusUpdateRequest request);

    /**
     * 分配用户角色
     *
     * @param id      用户ID
     * @param request 用户角色分配请求
     */
    void assignRoles(Long id, UserRoleRequest request);

    /**
     * 重置用户密码（管理员操作）
     *
     * @param id 用户ID
     * @return 包含新密码的响应
     */
    ResetPasswordVO resetPassword(Long id);

    /**
     * 当前用户修改密码
     *
     * @param request 密码修改请求
     */
    void changeCurrentPassword(PasswordChangeRequest request);

    /**
     * 当前用户修改头像
     *
     * @param request 头像更新请求
     */
    void updateCurrentAvatar(AvatarUpdateRequest request);
}
