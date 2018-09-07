package org.mengyun.tcctransaction.recover;

import java.util.Set;

/**
 * 事务恢复配置接口
 * Created by changming.xie on 6/1/16.
 */
public interface RecoverConfig {

    /**
     * @return 最大重试次数
     */
    public int getMaxRetryCount();

    /**
     * @return 恢复间隔时间，单位：秒
     */
    public int getRecoverDuration();

    /**
     * @return cron 表达式,定时任务 cron 表达式
     */
    public String getCronExpression();

    /**
     * @return 延迟取消异常集合
     */
    public Set<Class<? extends Exception>> getDelayCancelExceptions();

    /**
     * 设置延迟取消异常集合
     *
     * @param delayRecoverExceptions 延迟取消异常集合
     */
    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayRecoverExceptions);

    public int getAsyncTerminateThreadPoolSize();
}
