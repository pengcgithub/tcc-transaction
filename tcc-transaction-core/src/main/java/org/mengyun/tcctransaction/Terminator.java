package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.support.FactoryBuilder;
import org.mengyun.tcctransaction.utils.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * 执行器
 * Created by changmingxie on 10/30/15.
 */
public class Terminator implements Serializable {

    private static final long serialVersionUID = -164958655471605778L;


    public Terminator() {

    }

    public Object invoke(TransactionContext transactionContext, InvocationContext invocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {


        if (StringUtils.isNotEmpty(invocationContext.getMethodName())) {

            try {

                /**
                 * 获取参与者对象
                 */
                Object target = FactoryBuilder.factoryOf(invocationContext.getTargetClass()).getInstance();

                Method method = null;

                /**
                 * 获取方法
                 */
                method = target.getClass().getMethod(invocationContext.getMethodName(), invocationContext.getParameterTypes());

                /**
                 * 设置 事务上下文 到方法参数
                 */
                FactoryBuilder.factoryOf(transactionContextEditorClass).getInstance().set(transactionContext, target, method, invocationContext.getArgs());

                /**
                 * 执行方法
                 */
                return method.invoke(target, invocationContext.getArgs());

            } catch (Exception e) {
                throw new SystemException(e);
            }
        }
        return null;
    }
}
