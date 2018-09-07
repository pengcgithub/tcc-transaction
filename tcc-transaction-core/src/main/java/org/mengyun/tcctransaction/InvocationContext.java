package org.mengyun.tcctransaction;

import java.io.Serializable;

/**
 * 调用上下文的记录
 * Created by changmingxie on 11/9/15.
 */
public class InvocationContext implements Serializable {

    private static final long serialVersionUID = -7969140711432461165L;

    /**
     * 类
     */
    private Class targetClass;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 参数类型数组
     */
    private Class[] parameterTypes;

    /**
     * 参数数组
     */
    private Object[] args;

    public InvocationContext() {

    }

    public InvocationContext(Class targetClass, String methodName, Class[] parameterTypes, Object... args) {
        this.methodName = methodName;
        this.parameterTypes = parameterTypes;
        this.targetClass = targetClass;
        this.args = args;
    }

    public Object[] getArgs() {
        return args;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }
}
