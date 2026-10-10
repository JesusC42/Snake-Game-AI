public class SquareUnit {
    private final int x;
    private final int y;
    private int n;

    public SquareUnit(int x, int y, int n) {
        this.x = x;
        this.y = y;
        this.n = n;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }
}
