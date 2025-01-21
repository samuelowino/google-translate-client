package org.mwangi.desktop.util;

import io.reactivex.annotations.NonNull;
import javafx.application.Platform;

import java.util.concurrent.Executor;

public class FxExecutor implements Executor {
     FxExecutor(){
    }
    private static  class FxHolder{
        private static  final FxExecutor INSTANCE=new FxExecutor();
    }

    public static Executor getInstance(){
        return FxHolder.INSTANCE;
    }

    @Override
    public void execute(@NonNull Runnable runnable) {
        Platform.runLater(runnable);
    }
}
