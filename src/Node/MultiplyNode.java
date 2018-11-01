package Node;

public class MultiplyNode extends Node{

    public MultiplyNode(){
        super();
        //compute();
    }

    public MultiplyNode(Node muti1, Node muti2){
        super();
        muti1.connent(this);
        muti2.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=2)
            return false;

        double[][] v=mutiplyMatrix(prevs.get(0).value, prevs.get(1).value);
        if(v==null)
            return false;

        this.value=v;
        return true;
    }

    public double[][] mutiplyMatrix(double[][] a, double[][] b){
        int height=a.length;
        int width=b[0].length;
        int temp=b.length;

        if(a[0].length!=b.length)
            return null;

        double[][] result=new double[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                double sum=0;
                for(int k=0; k<temp; k++){
                    sum+=a[i][k]*b[k][j];
                }
                result[i][j]=sum;
            }
        }

        return result;
    }
}
