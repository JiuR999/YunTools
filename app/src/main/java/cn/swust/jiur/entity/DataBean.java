package cn.swust.jiur.entity;

/**
 * Created by hbh on 2017/4/20.
 * 实体类，模拟数据
 */

public class DataBean {
    //父布局
    public static final int PARENT_ITEM = 0;
    //子布局
    public static final int CHILD_ITEM = 1;
    // 显示类型
    private int type;
    // 是否展开
    private boolean isExpand;
    private DataBean childBean;

    private String ID;
    private String parentTxt;
    private String childTxt;

    public DataBean() {
    }

    public DataBean(String ID, String parentTxt, String childTxt) {
        this.ID = ID;
        this.parentTxt = parentTxt;
        this.childTxt = childTxt;
    }

    public String getChildTxt() {
        return childTxt;
    }

    public void setChildTxt(String childTxt) {
        this.childTxt = childTxt;
    }

    public String getParentTxt() {
        return parentTxt;
    }

    public void setParentTxt(String parentTxt) {
        this.parentTxt = parentTxt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public DataBean getChildBean() {
        return childBean;
    }

    public void setChildBean(DataBean childBean) {
        this.childBean = childBean;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}