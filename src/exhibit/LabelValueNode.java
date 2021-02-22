package exhibit;

public class LabelValueNode {
    private String label;
    private String value;
    private int index;
    public LabelValueNode(String label,String value){
        this.label = label;
        this.value = value;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getLabel() {
        return label;
    }

    public String getValue() {
        return value;
    }

    public int getIndex() {
        return index;
    }
}
