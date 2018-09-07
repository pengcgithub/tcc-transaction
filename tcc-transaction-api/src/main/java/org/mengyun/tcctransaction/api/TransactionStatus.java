package org.mengyun.tcctransaction.api;

/**
 * Created by changmingxie on 10/28/15.
 */
public enum TransactionStatus {

    /**
     * 尝试中状态
     */
    TRYING(1),

    /**
     * 确认中状态
     */
    CONFIRMING(2),

    /**
     * 取消中状态
     */
    CANCELLING(3);

    private int id;

    TransactionStatus(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static TransactionStatus valueOf(int id) {

        switch (id) {
            case 1:
                return TRYING;
            case 2:
                return CONFIRMING;
            default:
                return CANCELLING;
        }
    }

}
