package org.mengyun.tcctransaction;

import org.apache.log4j.Logger;
import org.mengyun.tcctransaction.api.TransactionContext;
import org.mengyun.tcctransaction.api.TransactionStatus;
import org.mengyun.tcctransaction.common.TransactionType;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;

/**
 * 事务管理器
 * Created by changmingxie on 10/26/15.
 */
public class TransactionManager {

    static final Logger logger = Logger.getLogger(TransactionManager.class.getSimpleName());

    private TransactionRepository transactionRepository;

    /**
     * 当前线程事务队列
     * 支持多个的事务独立存在，后创建的事务先提交。
     */
    private static final ThreadLocal<Deque<Transaction>> CURRENT = new ThreadLocal<Deque<Transaction>>();

    private ExecutorService executorService;

    public void setTransactionRepository(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public TransactionManager() {
    }

    /**
     * 发起根事务
     * 该方法在调用方法类型为 MethodType.ROOT 并且 事务处于 Try 阶段被调用
     * @return 事务
     */
    public Transaction begin() {

        // 创建 根事务
        Transaction transaction = new Transaction(TransactionType.ROOT);
        // 存储 事务
        transactionRepository.create(transaction);
        // 注册事务到当前线程事务队列
        registerTransaction(transaction);
        return transaction;
    }

    /**
     * 传播发起分支事务
     * 该方法在调用方法类型为 MethodType.PROVIDER 并且 事务处于 Try 阶段被调用。
     *
     * @param transactionContext 事务上下文
     * @return 分支事务
     */
    public Transaction propagationNewBegin(TransactionContext transactionContext) {

        Transaction transaction = new Transaction(transactionContext);
        transactionRepository.create(transaction);

        registerTransaction(transaction);
        return transaction;
    }

    /**
     * 传播获取分支事务
     * 该方法在调用方法类型为 MethodType.PROVIDER 并且 事务处于 Confirm / Cancel 阶段被调用。
     *
     * @param transactionContext 事务上下文
     * @return 分支事务
     * @throws NoExistedTransactionException 当事务不存在时
     */
    public Transaction propagationExistBegin(TransactionContext transactionContext) throws NoExistedTransactionException {
        Transaction transaction = transactionRepository.findByXid(transactionContext.getXid());

        if (transaction != null) {
            // 设置事务状态为 CONFIRMING 或 CANCELLING
            transaction.changeStatus(TransactionStatus.valueOf(transactionContext.getStatus()));
            registerTransaction(transaction);
            return transaction;
        } else {
            throw new NoExistedTransactionException();
        }
    }

    /**
     * 提交事务
     * 该方法在事务处于 Confirm / Cancel 阶段被调用。
     * @param asyncCommit
     */
    public void commit(boolean asyncCommit) {

        // 获取 事务
        final Transaction transaction = getCurrentTransaction();

        // 设置 事务状态 为 CONFIRMING
        transaction.changeStatus(TransactionStatus.CONFIRMING);

        // 更新 事务
        transactionRepository.update(transaction);

        if (asyncCommit) {
            try {
                Long statTime = System.currentTimeMillis();

                // 提交、删除 事务
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        commitTransaction(transaction);
                    }
                });
                logger.debug("async submit cost time:" + (System.currentTimeMillis() - statTime));
            } catch (Throwable commitException) {
                logger.warn("compensable transaction async submit confirm failed, recovery job will try to confirm later.", commitException);
                throw new ConfirmingException(commitException);
            }
        } else {
            commitTransaction(transaction);
        }
    }


    /**
     * 回滚事务
     * 该方法在事务处于 Confirm / Cancel 阶段被调用。
     * @param asyncRollback
     */
    public void rollback(boolean asyncRollback) {

        final Transaction transaction = getCurrentTransaction();
        transaction.changeStatus(TransactionStatus.CANCELLING);

        transactionRepository.update(transaction);

        if (asyncRollback) {

            try {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        rollbackTransaction(transaction);
                    }
                });
            } catch (Throwable rollbackException) {
                logger.warn("compensable transaction async rollback failed, recovery job will try to rollback later.", rollbackException);
                throw new CancellingException(rollbackException);
            }
        } else {

            rollbackTransaction(transaction);
        }
    }


    private void commitTransaction(Transaction transaction) {
        try {
            transaction.commit();
            transactionRepository.delete(transaction);
        } catch (Throwable commitException) {
            logger.warn("compensable transaction confirm failed, recovery job will try to confirm later.", commitException);
            throw new ConfirmingException(commitException);
        }
    }

    private void rollbackTransaction(Transaction transaction) {
        try {
            transaction.rollback();
            transactionRepository.delete(transaction);
        } catch (Throwable rollbackException) {
            logger.warn("compensable transaction rollback failed, recovery job will try to rollback later.", rollbackException);
            throw new CancellingException(rollbackException);
        }
    }

    public Transaction getCurrentTransaction() {
        if (isTransactionActive()) {
            // 获得头部元素
            return CURRENT.get().peek();
        }
        return null;
    }

    public boolean isTransactionActive() {
        Deque<Transaction> transactions = CURRENT.get();
        return transactions != null && !transactions.isEmpty();
    }


    /**
     * 注册事务到当前线程事务队列
     * @param transaction 事务
     */
    private void registerTransaction(Transaction transaction) {

        if (CURRENT.get() == null) {
            CURRENT.set(new LinkedList<Transaction>());
        }

        // 添加到头部
        CURRENT.get().push(transaction);
    }

    public void cleanAfterCompletion(Transaction transaction) {
        if (isTransactionActive() && transaction != null) {
            Transaction currentTransaction = getCurrentTransaction();
            if (currentTransaction == transaction) {
                CURRENT.get().pop();
            } else {
                throw new SystemException("Illegal transaction when clean after completion");
            }
        }
    }


    /**
     * 添加参与者到事务
     *
     * @param participant 参与者
     */
    public void enlistParticipant(Participant participant) {
        // 获取 事务
        Transaction transaction = this.getCurrentTransaction();
        // 添加参与者
        transaction.enlistParticipant(participant);
        // 更新 事务
        transactionRepository.update(transaction);
    }
}
