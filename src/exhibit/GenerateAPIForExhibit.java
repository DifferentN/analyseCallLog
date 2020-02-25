package exhibit;

import buildModel.StartBuildModel;

import java.util.ArrayList;
import java.util.List;

public class GenerateAPIForExhibit {
    private List<String> instancePaths;
    private List<List<String>> userInputs;
    public GenerateAPIForExhibit(){
        instancePaths = new ArrayList<>();
        userInputs = new ArrayList<>();
    }
    public void addInstance(String path,String userInput){
        instancePaths.add(path);
        List<String> list = new ArrayList<>();
        String strs[] = userInput.split("/");
        for(String item:strs){
            list.add(item);
        }
        userInputs.add(list);
    }
    public String executeGenerateAPI(){
        StartBuildModel startBuildModel = new StartBuildModel();
        String res = startBuildModel.generateAPI(instancePaths,userInputs);
        return res;
    }
}
