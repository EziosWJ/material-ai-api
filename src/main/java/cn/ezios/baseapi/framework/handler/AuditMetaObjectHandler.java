package cn.ezios.baseapi.framework.handler;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

/**
 * MyBatis-Plus 审计字段自动填充处理器。
 * <p>在实体插入时自动填充 createTime、updateTime、createBy、updateBy；更新时自动填充 updateTime、updateBy。</p>
 */
@Component
public class AuditMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时自动填充创建时间和创建人。
     *
     * @param metaObject 元对象，用于访问实体属性
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
        Long loginId = currentLoginId();
        if (loginId != null) {
            strictInsertFill(metaObject, "createBy", Long.class, loginId);
            strictInsertFill(metaObject, "updateBy", Long.class, loginId);
        }
    }

    /**
     * 更新操作时自动填充更新时间和更新人。
     *
     * @param metaObject 元对象，用于访问实体属性
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        Long loginId = currentLoginId();
        if (loginId != null) {
            strictUpdateFill(metaObject, "updateBy", Long.class, loginId);
        }
    }

    /**
     * 获取当前登录用户 ID，未登录或异常时返回 null。
     *
     * @return 当前登录用户 ID，或 null
     */
    private Long currentLoginId() {
        try {
            return StpUtil.isLogin() ? StpUtil.getLoginIdAsLong() : null;
        } catch (RuntimeException ex) {
            return null;
        }
    }
}
