package org.mengyun.tcctransaction.api;

import java.lang.reflect.Method;

/**
 * 事务上下文编辑器( TransactionContextEditor )，用于设置和获得事务上下文( TransactionContext )
 * 1、DubboTransactionContextEditor Dubbo 事务上下文编辑器实现。
 * 2、DefaultTransactionContextEditor，默认事务上下文编辑器实现。
 * Created by changming.xie on 1/18/17.
 */
public interface TransactionContextEditor {

    /**
     * 从参数中获得事务上下文
     *
     * @param target 对象
     * @param method 方法
     * @param args 参数
     * @return 事务上下文
     */
    public TransactionContext get(Object target, Method method, Object[] args);

    /**
     * 设置事务上下文到参数中
     *
     * @param transactionContext 事务上下文
     * @param target 对象
     * @param method 方法
     * @param args 参数
     */
    public void set(TransactionContext transactionContext, Object target, Method method, Object[] args);

}
