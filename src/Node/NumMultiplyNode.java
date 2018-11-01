package Node;

public class NumMultiplyNode extends Node {
    private double rate;

    public NumMultiplyNode(){
        super();
    }

    public NumMultiplyNode(Node para, double rate){
        super();
        para.connent(this);
        this.rate=rate;
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=1)
            return false;

        this.value=numMatrix(prevs.get(0).value);
        return true;
    }

    public double[][] numMatrix(double[][] input){
        double[][] result=input.clone();

        for(int i=0; i<result.length; i++){
            for(int j=0; j<result[0].length; j++){
                result[i][j]=rate*input[i][j];
            }
        }

        return result;
    }
}
