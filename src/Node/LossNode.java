package Node;

public class LossNode extends Node {
    public LossNode(){
        super();
        //compute();
    }

    public LossNode(Node desireOut, Node actualOut){
        super();
        desireOut.connent(this);
        actualOut.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=2)
            return false;

        double[][] v=squareLoss(prevs.get(0).value, prevs.get(1).value);
        if(v==null)
            return false;

        this.value=v;
        return true;
    }

    public double[][] squareLoss(double[][] d, double[][] o){
        int height=d.length;
        int width=d[0].length;

        if(o.length!=height || o[0].length!=width)
            return null;

        double[][] result=new double[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                result[i][j]=d[i][j]-o[i][j];
            }
        }

        return result;
    }

}
