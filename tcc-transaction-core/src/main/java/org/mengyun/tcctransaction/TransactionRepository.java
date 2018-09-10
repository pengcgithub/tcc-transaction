package org.mengyun.tcctransaction;

import org.mengyun.tcctransaction.api.TransactionXid;
import java.util.Date;
import java.util.List;

/**
 * 事务存储器接口
 * Created by changmingxie on 11/12/15.
 */
public interface TransactionRepository {

    /**
     * 新增事务
     *
     * @param transaction 事务
     * @return 新增数量
     */
    int create(Transaction transaction);

    /**
     * 更新事务
     *
     * @param transaction 事务
     * @return 更新数量
     */
    int update(Transaction transaction);

    /**
     * 删除事务
     *
     * @param transaction 事务
     * @return 删除数量
     */
    int delete(Transaction transaction);

    /**
     * 获取事务
     *
     * @param xid 事务编号
     * @return 事务
     */
    Transaction findByXid(TransactionXid xid);

    /**
     * 获取超过指定时间的事务集合
     *
     * @param date 指定时间
     * @return 事务集合
     */
    List<Transaction> findAllUnmodifiedSince(Date date);
}
