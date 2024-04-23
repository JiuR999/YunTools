package cn.swust.jiur.entity;

public class Theme {
    private String themeName;
    private int theme;
    private int color;

    public int getTheme() {
        return theme;
    }

    public Theme(int theme,String themeName, int color) {
        this.theme = theme;
        this.themeName = themeName;
        this.color = color;
    }

    public String getThemeName() {
        return themeName;
    }

    public void setThemeName(String themeName) {
        this.themeName = themeName;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
