package da_java;

public class ItineraryCls {
    private double a;
    private double b;
    private double c;
    private CoordinateCls first,last;
    public ItineraryCls(double a, double b, double c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
    public ItineraryCls(CoordinateCls first,CoordinateCls last) {
            this.first = first;
            this.last = last;
            getHanhTrinh(first,last);
    }
    

    public  CoordinateCls getFirst() {
        return first;
    }

    public void setFirst(CoordinateCls firsts) {
        first = firsts;
    }

    public CoordinateCls getLast() {
        return last;
    }

    public void setLast(CoordinateCls lasts) {
        last = lasts;
    }

    public ItineraryCls() {
    }

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getC() {
        return c;
    }

    public void setC(double c) {
        this.c = c;
    }
    //Ham viet pt cua hanh trinh co dang ax+by=c(ket qua la 3 he so a, b, c)
    public void getHanhTrinh(CoordinateCls getFirst,CoordinateCls getLast){
        this.setA(getFirst.getY()-getLast.getY());
        this.setB((getLast.getX()-getFirst.getX()));
        this.setC(-(getFirst.getX()*(getLast.getY()-getFirst.getY())-getFirst.getY()*((getLast.getX()-getFirst.getX()))));
    }
    public String toString(){
        return this.getA() +"\t"+this.getB()+"\t"+this.getC();
    }
}
