package com.example.balanceview.volumebalance;

public class PointInfo {

    private int x;

    private int y;

    /**
     * X网格(-7~7)
     */
    private int balanceX;

    /**
     * X网格(-7~7)
     */
    private int balanceY;

    public PointInfo() {

    }

    public PointInfo(int x, int y, int balanceX, int balanceY) {
        this.x = x;
        this.y = y;
        this.balanceX = balanceX;
        this.balanceY = balanceY;
    }

    public int getBalanceX() {
        return balanceX;
    }

    public void setBalanceX(int balanceX) {
        this.balanceX = balanceX;
    }

    public int getBalanceY() {
        return balanceY;
    }

    public void setBalanceY(int balanceY) {
        this.balanceY = balanceY;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        return "x=" + x + ";y=" + y + ";balanceX=" + this.balanceX
                + ";balanceY=" + this.balanceY;
    }
}
