package com.sailfish.server.common.annotations;

import java.lang.annotation.*;

/**
 * 为命令分配命令编码
 *
 * @author wangpeixin
 * @since 2025/7/9 16:45
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CommandCode {

    int value() default -1;

    /**
     * 命令编码
     *
     * @author wangpeixin
     * @since 2025/7/9 16:59
     */
    int code() default -1;

    /**
     * 命令描述
     *
     * @author wangpeixin
     * @since 2025/7/9 17:00
     */
    String desc() default "";
}
