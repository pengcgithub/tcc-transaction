package org.mengyun.tcctransaction;


import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.api.TransactionXid;
import org.mengyun.tcctransaction.common.TransactionType;

import javax.transaction.xa.Xid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事务对象
 * Created by changmingxie on 10/26/15.
 */
public class Transaction implements Serializable {

    private static final long serialVersionUID = 7291423944314337931L;

    /**
     * 事务编号
     */
    private TransactionXid xid;

    /**
     * 事务状态
     */
    private TransactionStatus status;

    /**
     * 事务类型
     */
    private TransactionType transactionType;

    /**
     * 重试次数
     */
    private volatile int retriedCount = 0;

    /**
     * 创建时间
     */
    private Date createTime = new Date();

    /**
     * 最后更新时间
     */
    private Date lastUpdateTime = new Date();

    /**
     * 版本号
     */
    private long version = 1;

    /**
     * 参与者集合
     */
    private List<Participant> participants = new ArrayList<Participant>();

    /**
     * 附带属性映射
     */
    private Map<String, Object> attachments = new ConcurrentHashMap<String, Object>();

    public Transaction() {

    }

    /**
     * 创建分支事务
     *
     * @param transactionContext 事务上下文
     */
    public Transaction(TransactionContext transactionContext) {
        // 事务上下文的 xid
        this.xid = transactionContext.getXid();
        // 尝试中状态
        this.status = TransactionStatus.TRYING;
        // 分支事务
        this.transactionType = TransactionType.BRANCH;
    }

    /**
     * 创建指定类型的事务
     *
     * @param transactionType 事务类型
     */
    public Transaction(TransactionType transactionType) {
        this.xid = new TransactionXid();
        this.status = TransactionStatus.TRYING;
        this.transactionType = transactionType;
    }

    /**
     * 添加参与者
     *
     * @param participant 参与者
     */
    public void enlistParticipant(Participant participant) {
        participants.add(participant);
    }


    public Xid getXid() {
        return xid.clone();
    }

    public TransactionStatus getStatus() {
        return status;
    }


    public List<Participant> getParticipants() {
        return participants;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void changeStatus(TransactionStatus status) {
        this.status = status;
    }


    /**
     * 提交 TCC 事务
     */
    public void commit() {

        for (Participant participant : participants) {
            participant.commit();
        }
    }

    /**
     * 回滚 TCC 事务
     */
    public void rollback() {
        for (Participant participant : participants) {
            participant.rollback();
        }
    }

    public int getRetriedCount() {
        return retriedCount;
    }

    public void addRetriedCount() {
        this.retriedCount++;
    }

    public void resetRetriedCount(int retriedCount) {
        this.retriedCount = retriedCount;
    }

    public Map<String, Object> getAttachments() {
        return attachments;
    }

    public long getVersion() {
        return version;
    }

    public void updateVersion() {
        this.version++;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date date) {
        this.lastUpdateTime = date;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void updateTime() {
        this.lastUpdateTime = new Date();
    }


}
