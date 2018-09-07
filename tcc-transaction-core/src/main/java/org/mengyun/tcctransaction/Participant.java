package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionContextEditor;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;

import java.io.Serializable;

/**
 * 参与者事务
 * Created by changmingxie on 10/27/15.
 */
public class Participant implements Serializable {

    private static final long serialVersionUID = 4127729421281425247L;

    /**
     * 事务编号
     *
     * 通过 TransactionXid.globalTransactionId 属性，关联上其所属的事务。
     * 当参与者进行远程调用时，远程的分支事务的事务编号等于该参与者的事务编号。
     * 通过事务编号的关联，TCC Confirm / Cancel 阶段，使用参与者的事务编号和远程的分支事务进行关联，从而实现事务的提交和回滚。
     */
    private TransactionXid xid;

    /**
     * 确认执行业务方法调用上下文
     */
    private InvocationContext confirmInvocationContext;

    /**
     * 取消执行业务方法
     */
    private InvocationContext cancelInvocationContext;

    /**
     * 执行器
     */
    private Terminator terminator = new Terminator();

    /**
     * 事务上下文编辑
     */
    Class<? extends TransactionContextEditor> transactionContextEditorClass;

    public Participant() {

    }

    public Participant(TransactionXid xid, InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.xid = xid;
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public Participant(InvocationContext confirmInvocationContext, InvocationContext cancelInvocationContext, Class<? extends TransactionContextEditor> transactionContextEditorClass) {
        this.confirmInvocationContext = confirmInvocationContext;
        this.cancelInvocationContext = cancelInvocationContext;
        this.transactionContextEditorClass = transactionContextEditorClass;
    }

    public void setXid(TransactionXid xid) {
        this.xid = xid;
    }

    /**
     * 回滚事务
     */
    public void rollback() {
        terminator.invoke(new TransactionContext(xid, TransactionStatus.CANCELLING.getId()), cancelInvocationContext, transactionContextEditorClass);
    }

    /**
     * 提交事务
     */
    public void commit() {
        terminator.invoke(new TransactionContext(xid, TransactionStatus.CONFIRMING.getId()), confirmInvocationContext, transactionContextEditorClass);
    }

    public Terminator getTerminator() {
        return terminator;
    }

    public TransactionXid getXid() {
        return xid;
    }

    public InvocationContext getConfirmInvocationContext() {
        return confirmInvocationContext;
    }

    public InvocationContext getCancelInvocationContext() {
        return cancelInvocationContext;
    }

}
