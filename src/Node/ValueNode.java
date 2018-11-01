package Node;

public class ValueNode extends Node {

    public ValueNode(){
        super();
    }

    public ValueNode(double[][] value){
        super(value);
    }

    public ValueNode(int height){
        this.value=new double[height][height];
        for(int i=0; i<height; i++){
            for(int j=0; j<height; j++){
                this.value[i][j]=i==j?1:0;
            }
        }
    }

    public ValueNode(double v, int height, int width){
        this.value=new double[height][width];
        for(int i=0; i<height; i++){
            for(int j=0; j<width; j++){
                this.value[i][j]=v;
            }
        }
    }

    public ValueNode(int height, int width){
        super(height, width);
    }

    public boolean setValue(double[][] newValue){
        int newHeight=newValue.length;
        int newWidth=newValue[0].length;

        /*if(height!=newHeight || width!=newWidth){
            return false;
        }*/

        this.value=newValue;
        return true;
    }

    public boolean compute(){
        if(prevs.size()!=1)
            return false;

        this.value=prevs.get(0).value;
        return true;
    }

}
