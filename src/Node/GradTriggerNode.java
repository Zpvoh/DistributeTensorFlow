package Node;

public class GradTriggerNode extends Node{

    public GradTriggerNode(){
        super();
    }

    public GradTriggerNode(Node para){
        super();
        para.connent(this);
        //compute();
    }

    @Override
    public boolean compute() {
        if(prevs.size()!=1)
            return false;

        this.value=gradTriggerMatrix(prevs.get(0).value);
        return true;
    }

    public double[][] gradTriggerMatrix(double[][] input){
        double[][] result=input.clone();

        for(int i=0; i<result.length; i++){
            for(int j=0; j<result[0].length; j++){
                result[i][j]=gradTrigger(input[i][j]);
            }
        }

        return result;
    }

    public double gradTrigger(double value){
        return (1+value)*(1-value);
    }
}
