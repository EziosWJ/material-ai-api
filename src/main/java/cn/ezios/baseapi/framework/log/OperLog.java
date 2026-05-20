package cn.ezios.baseapi.framework.log;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解。
 * <p>标注在 Controller 方法上，由 {@link OperLogAspect} 切面拦截并记录操作日志。</p>
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface OperLog {

    /** 操作模块/功能名称 */
    String title();

    /** 操作类型，如：新增、修改、删除、查询 */
    String type();
}
