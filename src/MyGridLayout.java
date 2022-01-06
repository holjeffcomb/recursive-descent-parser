import java.awt.*;

public class MyGridLayout extends GridLayout {
    private int params;

    public MyGridLayout(int x1, int x2, int x3, int x4){
        super(x1, x2, x3, x4);
        this.params = 2;
    }

    public MyGridLayout(int x1, int x2){
        super(x1, x2);
        this.params = 4;
    }

    public int getParams() {
        return params;
    }
}
