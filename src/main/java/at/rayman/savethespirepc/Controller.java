package at.rayman.savethespirepc;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Controller {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @FXML
    private Label title;

    @FXML
    private ProgressIndicator progressBar;

    @FXML
    public TextArea errors;

    @FXML
    protected void onUploadSaveClick() {
        progressBar.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
        new Thread(new BackgroundTask<>(this::zipAndUploadSave)).start();
    }

    @FXML
    protected void onDownloadSaveClick() {
    }

    private Result zipAndUploadSave() {
        return Zipper.zip()
            .then(this::addLog)
            .success(_ -> NetworkService.getInstance().uploadSave()
                .then(this::addLog))
            .then(this::clearProgressBar);
    }

    private void clearProgressBar(Result result) {
        Platform.runLater(() -> progressBar.setProgress(0));
    }

    private void addLog(Result result) {
        Platform.runLater(() -> errors.appendText("\n" + DATE_FORMAT.format(LocalDateTime.now()) + " | " + result));
    }

}