package apiAdapter.data;

public class MyMethodPair {
    private MyMethod myMethod1,myMethod2;
    private double sim;
    public MyMethodPair(MyMethod myMethod1,MyMethod myMethod2,double sim){
        this.myMethod1 = myMethod1;
        this.myMethod2 = myMethod2;
        this.sim = sim;
    }

    public MyMethod getMyMethod1() {
        return myMethod1;
    }

    public MyMethod getMyMethod2() {
        return myMethod2;
    }

    public double getSim() {
        return sim;
    }
}
