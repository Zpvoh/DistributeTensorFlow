package Node;

import java.io.Serializable;
import java.util.ArrayList;

public abstract class Node implements Serializable{
    protected double[][] value;
    protected int height;
    protected int width;
    protected ArrayList<Node> nexts;

    public ArrayList<Node> getNext() {
        return nexts;
    }

    public ArrayList<Node> getPrevs() {
        return prevs;
    }

    protected ArrayList<Node> prevs;

    public Node(){
        nexts=new ArrayList<>();
        prevs=new ArrayList<>();
    }

    public Node(double[][] value){
        this();
        this.value=value;
        this.height=value.length;
        this.width=value[0].length;
    }

    public Node(int height, int width){
        this();
        this.height=height;
        this.width=width;
        value=new double[height][width];
    }

    public void connent(Node next){
        this.nexts.add(next);
        next.prevs.add(this);
    }

    public boolean compute(){
        return false;
    }

    public void print(){
        for(int i=0; i<value.length; i++){
            for(int j=0; j<value[i].length; j++){
                System.out.print(value[i][j]+" ");
            }

            System.out.println();
        }
    }

    public double loss(Node desire){
        double loss=0;
        for(int i=0; i<desire.value.length; i++){
            loss+=(desire.value[i][0]-value[i][0])*(desire.value[i][0]-value[i][0]);
        }

        loss/=desire.value.length;
        return loss;
    }

    public double[][] getValue() {
        return value;
    }
}
