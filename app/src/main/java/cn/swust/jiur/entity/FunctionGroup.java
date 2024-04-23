package cn.swust.jiur.entity;

import java.util.List;
import java.util.Map;

import cn.swust.jiur.R;

public class FunctionGroup {
    private String groupName;
    private List<FunctionItem> functionItems;

    public FunctionGroup() {
    }

    public FunctionGroup(String groupName, List<FunctionItem> functionItems) {
        this.groupName = groupName;
        this.functionItems = functionItems;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<FunctionItem> getFunctionItems() {
        return functionItems;
    }

    public void setFunctionItems(List<FunctionItem> functionItems) {
        this.functionItems = functionItems;
    }

    public static class FunctionItem {
        private int icon;
        private String name;
        private int fragmentId;
        //TODO 多参数
        private String params;
        private Map<String,Object> arguments;
        public FunctionItem() {
        }

        public FunctionItem(int icon, String name, int fragmentId) {
            this.icon = icon;
            this.name = name;
            this.fragmentId = fragmentId;
        }

        public Map<String, Object> getArguments() {
            return arguments;
        }

        public void setArguments(Map<String, Object> arguments) {
            this.arguments = arguments;
        }

        public int getIcon() {
            return icon;
        }

        public void setIcon(int icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFragmentId() {
            return fragmentId;
        }

        public void setFragmentId(int fragmentId) {
            this.fragmentId = fragmentId;
        }
    }
}
