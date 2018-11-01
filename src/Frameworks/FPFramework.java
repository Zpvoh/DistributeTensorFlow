package Frameworks;

import Node.*;

public class FPFramework extends Framework{
    private int[] neuronNum;
    private ValueNode in;
    private Node[] outs;
    private ValueNode[] bias;
    private ValueNode[] weights;
    private Node actualOut;

    public FPFramework(int[] neuronNum, ValueNode in){
        super();
        //Node inputNode=this.getParameters().get(0);
        this.neuronNum=neuronNum;
        this.in=in;
        outs=new Node[neuronNum.length];
        bias=new ValueNode[neuronNum.length];
        weights=new ValueNode[neuronNum.length];
        
        outs[0]=in;
        this.addSendVariables(outs[0]);
        for(int i=1; i<neuronNum.length; i++){
            ValueNode b=new ValueNode(neuronNum[i], 1);
            ValueNode W=new ValueNode(neuronNum[i], neuronNum[i-1]);
            bias[i]=b;
            weights[i]=W;
            
            MultiplyNode muti=new MultiplyNode(W, outs[i-1]);
            AddNode addBias=new AddNode(muti, b);
            TriggerNode trigger=new TriggerNode(addBias);
            
            outs[i]=trigger;
            actualOut=trigger;

            this.addParameter(b);
            this.addParameter(W);
            this.addSendVariables(outs[i]);

            this.addServerVariables(muti);
            this.addServerVariables(addBias);
            this.addServerVariables(trigger);
        }
        
        
    }

    public Node[] getOuts() {
        return outs;
    }

    public boolean readBiasAndWeights(BPFramework bp){
        if(bp.getBias().length!=bias.length || bp.getWeights().length!=weights.length)
            return false;

        for(int i=1; i<neuronNum.length; i++){
            bias[i].setValue(bp.getBias()[i].getValue());
            weights[i].setValue(bp.getWeights()[i].getValue());
        }

        return true;
    }

    public Node getActualOut() {
        return actualOut;
    }

    public ValueNode getIn() {
        return in;
    }
}
