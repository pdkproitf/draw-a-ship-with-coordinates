package da_java;

public class CoordinateCls {
    private double x ;
    private double y ;

    public CoordinateCls(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public CoordinateCls() {
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.y = y;
    }
    public String toString(){
        return this.getX()+"\t"+this.getY();
    }
}
