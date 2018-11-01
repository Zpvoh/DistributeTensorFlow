package Frameworks;

import Node.*;

public class BPFramework extends Framework{
    private int[] neuronNum;
    private ValueNode[] outs;
    private ValueNode[] bias;
    private ValueNode[] weights;
    //private ValueNode rate;
    private ValueNode desireOut;

    public BPFramework(int[] neuronNum, double rate, ValueNode desireOut) {
        super();
        this.neuronNum=neuronNum;
        this.desireOut=desireOut;
        outs=new ValueNode[neuronNum.length];
        bias=new ValueNode[neuronNum.length];
        weights=new ValueNode[neuronNum.length];

        Node[] tmp=new Node[neuronNum.length];
        GradTriggerNode[] gs=new GradTriggerNode[neuronNum.length];

        outs[neuronNum.length-1]=new ValueNode();
        //gs[neuronNum.length-1]=new GradTriggerNode(outs[neuronNum.length-1]);
        LossNode loss=new LossNode(this.desireOut, outs[neuronNum.length-1]);
        tmp[neuronNum.length-1]=loss;
        this.addServerVariables(loss);

        for(int i=neuronNum.length-1; i>0; i--){
            ValueNode b=new ValueNode(neuronNum[i], 1);
            ValueNode W=new ValueNode(neuronNum[i], neuronNum[i-1]);
            bias[i]=b;
            weights[i]=W;
            outs[i-1]=new ValueNode();

            GradTriggerNode g=new GradTriggerNode(outs[i]);
            //MultiplyNode theta=new MultiplyNode(loss, g);
            DirectMultiplyNode muti=new DirectMultiplyNode(tmp[i], g);
            NumMultiplyNode mutiRate=new NumMultiplyNode(muti, rate);

            AddNode biasAdjust=new AddNode(bias[i], mutiRate);
            biasAdjust.connent(bias[i]);

            TransposeNode outT=new TransposeNode(outs[i-1]);
            MultiplyNode weightMultiply=new MultiplyNode(mutiRate, outT);
            AddNode weightAdjust=new AddNode(weights[i], weightMultiply);
            weightAdjust.connent(getWeights()[i]);

            TransposeNode weightT=new TransposeNode(weights[i]);
            tmp[i-1]=new MultiplyNode(weightT, muti);

            this.addServerVariables(g);
            //this.addServerVariables(theta);
            this.addServerVariables(muti);
            this.addServerVariables(mutiRate);

            this.addServerVariables(biasAdjust);
            this.addServerVariables(bias[i]);

            this.addServerVariables(outT);
            this.addServerVariables(weightMultiply);
            //this.addServerVariables(weightMultiplyT);
            this.addServerVariables(weightAdjust);
            this.addServerVariables(weights[i]);

            this.addServerVariables(weightT);
            this.addServerVariables(tmp[i-1]);
        }

        this.addParameter(outs[0]);
        for(int i=1; i<neuronNum.length; i++){
            this.addParameter(outs[i]);
            this.addSendVariables(bias[i]);
            this.addSendVariables(weights[i]);
        }
    }


    public boolean readOuts(FPFramework fp){
        if(fp.getOuts().length!=outs.length)
            return false;

        for(int i=0; i<outs.length; i++){
            outs[i].setValue(fp.getOuts()[i].getValue());
        }

        return true;
    }

    public ValueNode[] getBias() {
        return bias;
    }

    public ValueNode[] getWeights() {
        return weights;
    }

    public boolean initBiasAndWeights(double biasLower, double biasUpper, double weightsLower, double weightsUpper){
        for(int i=1; i<neuronNum.length; i++){
            //double[][] initBias=bias[i].getValue().clone();
            for(int j=0; j<bias[i].getValue().length; j++){
                for(int k=0; k<bias[i].getValue()[j].length; k++){
                    bias[i].getValue()[j][k]=Math.random()*(biasUpper-biasLower)+biasLower;
                }
            }

            for(int j=0; j<weights[i].getValue().length; j++){
                for(int k=0; k<weights[i].getValue()[j].length; k++){
                    weights[i].getValue()[j][k]=Math.random()*(weightsUpper-weightsLower)+weightsLower;
                }
            }
        }

        return true;
    }

    public ValueNode getDesireOut() {
        return desireOut;
    }
}
