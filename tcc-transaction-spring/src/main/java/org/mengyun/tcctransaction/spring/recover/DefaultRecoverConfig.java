package org.mengyun.tcctransaction.spring.recover;

import org.mengyun.tcctransaction.OptimisticLockException;
import org.mengyun.tcctransaction.recover.RecoverConfig;

import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Set;

/**
 * 默认事务恢复配置实现
 * Created by changming.xie on 6/1/16.
 */
public class DefaultRecoverConfig implements RecoverConfig {

    public static final RecoverConfig INSTANCE = new DefaultRecoverConfig();

    /**
     * 最大重试次数
     */
    private int maxRetryCount = 30;

    /**
     * 恢复间隔时间，单位：秒
     */
    private int recoverDuration = 120; //120 seconds

    /**
     * cron 表达式
     */
    private String cronExpression = "0 */1 * * * ?";

    /**
     * 延迟取消异常集合
     */
    private int asyncTerminateThreadPoolSize = 1024;

    private Set<Class<? extends Exception>> delayCancelExceptions = new HashSet<Class<? extends Exception>>();

    public DefaultRecoverConfig() {
        delayCancelExceptions.add(OptimisticLockException.class);
        delayCancelExceptions.add(SocketTimeoutException.class);
    }

    @Override
    public int getMaxRetryCount() {
        return maxRetryCount;
    }

    @Override
    public int getRecoverDuration() {
        return recoverDuration;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }


    public void setMaxRetryCount(int maxRetryCount) {
        this.maxRetryCount = maxRetryCount;
    }

    public void setRecoverDuration(int recoverDuration) {
        this.recoverDuration = recoverDuration;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    @Override
    public void setDelayCancelExceptions(Set<Class<? extends Exception>> delayCancelExceptions) {
        this.delayCancelExceptions.addAll(delayCancelExceptions);
    }

    @Override
    public Set<Class<? extends Exception>> getDelayCancelExceptions() {
        return this.delayCancelExceptions;
    }

    public int getAsyncTerminateThreadPoolSize() {
        return asyncTerminateThreadPoolSize;
    }

    public void setAsyncTerminateThreadPoolSize(int asyncTerminateThreadPoolSize) {
        this.asyncTerminateThreadPoolSize = asyncTerminateThreadPoolSize;
    }
}
