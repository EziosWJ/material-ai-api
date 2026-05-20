package cn.ezios.baseapi.modules.system.log.service;

import cn.ezios.baseapi.common.model.PageResult;
import cn.ezios.baseapi.modules.system.log.dto.LoginLogPageQuery;
import cn.ezios.baseapi.modules.system.log.dto.OperLogPageQuery;
import cn.ezios.baseapi.modules.system.log.vo.LoginLogVO;
import cn.ezios.baseapi.modules.system.log.vo.OperLogVO;

/**
 * 日志管理服务接口
 * <p>定义登录日志和操作日志的查询及清空操作</p>
 */
public interface LogService {

    /**
     * 分页查询登录日志
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<LoginLogVO> loginPage(LoginLogPageQuery query);

    /**
     * 获取登录日志详情
     *
     * @param id 日志ID
     * @return 登录日志详情
     */
    LoginLogVO loginDetail(Long id);

    /**
     * 清空登录日志
     */
    void clearLoginLog();

    /**
     * 分页查询操作日志
     *
     * @param query 分页查询条件
     * @return 分页结果
     */
    PageResult<OperLogVO> operPage(OperLogPageQuery query);

    /**
     * 获取操作日志详情
     *
     * @param id 日志ID
     * @return 操作日志详情
     */
    OperLogVO operDetail(Long id);

    /**
     * 清空操作日志
     */
    void clearOperLog();
}
