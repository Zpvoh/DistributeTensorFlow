package Node;

public class DirectMultiplyNode extends Node{
    public DirectMultiplyNode(){
        super();
        //compute();
    }

    public DirectMultiplyNode(Node muti1, Node muti2){
        super();
        muti1.connent(this);
        muti2.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=2)
            return false;

        double[][] v=numMutiplyMatrix(prevs.get(0).value, prevs.get(1).value);
        if(v==null)
            return false;

        this.value=v;
        return true;
    }

    public double[][] numMutiplyMatrix(double[][] a, double[][] b){
        int height=a.length;
        int width=a[0].length;

        if(b.length!=height || b[0].length!=width)
            return null;

        double[][] result=new double[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                result[i][j]=a[i][j]*b[i][j];
            }
        }

        return result;
    }
}
