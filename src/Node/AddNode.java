package Node;

public class AddNode extends Node{

    public AddNode(){
        super();
        //compute();
    }

    public AddNode(Node add1, Node add2){
        super();
        add1.connent(this);
        add2.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=2)
            return false;

        double[][] v=addMatrix(prevs.get(0).value, prevs.get(1).value);
        if(v==null)
            return false;

        this.value=v;
        return true;
    }

    public double[][] addMatrix(double[][] a, double[][] b){
        int height=a.length;
        int width=a[0].length;

        double[][] result=new double[height][width];

        if(b.length!=height || b[0].length!=width)
            return null;

        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                result[i][j]=a[i][j]+b[i][j];
            }
        }

        return result;
    }
}
