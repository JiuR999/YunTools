package cn.swust.jiur.entity;

public class EventMsg {
    public enum MSG_TYPE{
        /**
         * INSERT 插入数据
         * SELECT 查找
         * DELETE 删除
         * ALTER 更改
         */
        INSERT,SELECT,DELETE,ALTER
    }
    private Formation formation;
    private MSG_TYPE msgType;

    public EventMsg(Formation formation, MSG_TYPE msgType) {
        this.formation = formation;
        this.msgType = msgType;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public MSG_TYPE getMsgType() {
        return msgType;
    }

    public void setMsgType(MSG_TYPE msgType) {
        this.msgType = msgType;
    }
}
