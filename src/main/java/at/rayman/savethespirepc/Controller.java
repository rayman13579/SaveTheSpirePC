package at.rayman.savethespirepc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Controller {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    @FXML
    private Label title;

    @FXML
    private Button uploadButton;

    @FXML
    private Button downloadButton;

    @FXML
    private ProgressIndicator progressBar;

    @FXML
    public TextArea errors;

    @FXML
    protected void onUploadSaveClick() {
        doActionWithBackgroundStuff(this::zipAndUploadSave);
    }

    @FXML
    protected void onDownloadSaveClick() {
        doActionWithBackgroundStuff(this::downloadAndUnzipSave);
    }

    private void doActionWithBackgroundStuff(Runnable action) {
        errors.appendText("\n");
        uploadButton.setDisable(true);
        downloadButton.setDisable(true);
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        new Thread(() -> {
            action.run();
            Platform.runLater(() -> {
                uploadButton.setDisable(false);
                downloadButton.setDisable(false);
                progressBar.setProgress(0);
            });
        }).start();
    }

    private void zipAndUploadSave() {
        Zipper.zip()
            .then(this::addLog)
            .success(_ -> NetworkService.getInstance().uploadSave()
                .then(this::addLog))
            .then(_ -> deleteZip()
                .then(this::addLog));
    }

    private void downloadAndUnzipSave() {
        NetworkService.getInstance().downloadSave()
            .then(this::addLog)
            .success(_ ->
                clearSaveDirectory()
                    .then(this::addLog)
                    .then(_ -> Zipper.unzip()
                        .then(this::addLog)))
            .then(_ -> deleteZip()
                .then(this::addLog));
    }

    private Result deleteZip() {
        File file = new File(Constants.ZIP_PATH);
        if (file.delete()) {
            return Result.success("Deleted zip file");
        } else {
            return Result.error("Failed to delete zip file");
        }
    }

    private Result clearSaveDirectory() {
        try {
            FileUtils.deleteDirectory(new File(Constants.PREFERENCES_PATH));
            FileUtils.deleteDirectory(new File(Constants.RUNS_PATH));
            FileUtils.deleteDirectory(new File(Constants.SAVES_PATH));
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error(e.getMessage());
        }
        return Result.success("Cleared directories");
    }

    private void addLog(Result result) {
        Platform.runLater(() -> errors.appendText("\n" + DATE_FORMAT.format(LocalDateTime.now()) + " | " + result));
    }

}