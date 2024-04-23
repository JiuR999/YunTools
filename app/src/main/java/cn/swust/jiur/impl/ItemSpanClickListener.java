package cn.swust.jiur.impl;
import cn.swust.jiur.entity.DataBean;

public interface ItemSpanClickListener {
    /**
     * 展开子Item
     * @param bean
     */
    void onExpandChildren(DataBean bean);
    /**
     * 隐藏子Item
     * @param bean
     */
    void onHideChildren(DataBean bean);
}
