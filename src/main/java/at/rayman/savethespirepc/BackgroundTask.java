package at.rayman.savethespirepc;

import javafx.concurrent.Task;

public class BackgroundTask<V> extends Task<V> {

    private final Runnable runnable;

    public BackgroundTask(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    protected V call() {
        runnable.run();
        return null;
    }
}
