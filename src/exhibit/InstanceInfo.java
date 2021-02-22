package exhibit;

import java.util.List;

public class InstanceInfo {
    private String path;
    private List<LabelValueNode> userInput;
    private List<LabelValueNode> userOutput;
    public InstanceInfo(String path,List<LabelValueNode> userInput,List<LabelValueNode> userOutput){
        this.path = path;
        this.userInput = userInput;
        this.userOutput = userOutput;
    }

    public String getPath() {
        return path;
    }

    public List<LabelValueNode> getUserInput() {
        return userInput;
    }

    public List<LabelValueNode> getUserOutput() {
        return userOutput;
    }
}
