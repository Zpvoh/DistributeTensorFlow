package Node;

public class TriggerNode extends Node{

    //private double[][] gradValue;

    public TriggerNode(){
        super();
    }

    public TriggerNode(Node para){
        super();
        para.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=1)
            return false;

        this.value=triggerMatrix(prevs.get(0).value);
        //this.gradValue=gradTriggerMatrix(prevs.get(0).value);
        return true;
    }

    public double[][] triggerMatrix(double[][] input){
        double[][] result=input.clone();

        for(int i=0; i<result.length; i++){
            for(int j=0; j<result[0].length; j++){
                result[i][j]=trigger(input[i][j]);
            }
        }

        return result;
    }

    public double trigger(double input){
        return Math.tanh(input);
    }
}
