package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import utils.DoubleField;
import utils.IntField;

import java.io.File;

/**
 * @author Wojciech Pachuta.
 */
public class ContentController {

    private String imageName;
    private ContentUpdater thread;

    @FXML
    private DoubleField pheromoneCoverage;
    @FXML
    private IntField midIterationPeriod;
    @FXML
    private IntField stepsPerIteration;
    @FXML
    private ImageView imageView;
    @FXML
    private DoubleField numberOfAntsMultiplier;
    @FXML
    private DoubleField evaporationRate;
    @FXML
    private DoubleField gradientThreshold;
    @FXML
    private DoubleField alpha;
    @FXML
    private DoubleField beta;
    @FXML
    private Button saveButton;

    public void setImage(Image image) {
        imageView.setImage(image);
    }

    public double getNumberOfAntsMultiplier() {
        return numberOfAntsMultiplier.getValue();
    }

    public double getEvaporationRate() {
        return evaporationRate.getValue();
    }

    public double getGradientThreshold() {
        return gradientThreshold.getValue();
    }

    public double getAlpha() {
        return alpha.getValue();
    }

    public double getBeta() {
        return beta.getValue();
    }

    public int getMidIterationPeriod() {
        return midIterationPeriod.getValue();
    }

    public int getStepsPerIteration() {
        return stepsPerIteration.getValue();
    }

    public double getPheromoneCoverage() {
        return pheromoneCoverage.getValue();
    }

    public void onRestart(ActionEvent actionEvent) throws InterruptedException {
        thread.interrupt();
        thread.join();
        begin(imageName);
        saveButton.setDisable(true);
    }

    public void onPause(ActionEvent actionEvent) {
        thread.pauseExecution();
        saveButton.setDisable(false);
    }

    public void onResume(ActionEvent actionEvent) {
        thread.resumeExecution();
        saveButton.setDisable(true);
    }

    public void onSave(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select result file");
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null)
            thread.saveCurrentState(file);
    }

    public void begin(String imageName) {
        this.imageName = imageName;
        this.thread = new ContentUpdater(imageName, this);
        this.thread.start();
    }
}
