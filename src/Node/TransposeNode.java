package Node;

public class TransposeNode extends Node {
    public TransposeNode(){
        super();
    }

    public TransposeNode(Node para){
        super();
        para.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=1)
            return false;

        this.value=transpose(prevs.get(0).value);
        //this.gradValue=gradTriggerMatrix(prevs.get(0).value);
        return true;
    }

    public double[][] transpose(double[][] a){
        double[][] newValue=new double[a[0].length][a.length];
        for(int i=0; i<a.length; i++){
            for(int j=0; j<a[i].length; j++){
                newValue[j][i]=a[i][j];
            }
        }

        return newValue;

    }
}
