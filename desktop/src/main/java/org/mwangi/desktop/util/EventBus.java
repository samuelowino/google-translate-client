package org.mwangi.desktop.util;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import org.mwangi.desktop.dialogs.NotificationDialog;
import org.mwangi.desktop.payload.TranslationResponse;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;


public class EventBus {


    private final SimpleIntegerProperty activityCount=new SimpleIntegerProperty();
    private final PublishSubject<TranslationResponse> responses=PublishSubject.create();
    private final Executor uiexecutor;
    public EventBus(Executor uiexecutor){
        this.uiexecutor=uiexecutor;
    }
    private void startActivity(){
        if(Platform.isFxApplicationThread()){
            activityCount.set(activityCount.get() +1);
        }else{
            CompletableFuture.runAsync(()->activityCount.set(activityCount.get() +1),uiexecutor);
        }
    }
    private void stopActivity(){
        if(Platform.isFxApplicationThread()){
            activityCount.set(activityCount.get() -1);
        }else{
            CompletableFuture.runAsync(()->activityCount.set(activityCount.get()-1),uiexecutor);
        }
    }


    public SimpleIntegerProperty getActivityCount() {
        return activityCount;
    }

    public   <T> CompletableFuture<T> activity(CompletableFuture<T> completableFuture){
            startActivity();
             return completableFuture.whenComplete((_,throwable)->{
                 stopActivity();
                 if(throwable != null){
                     throwable.printStackTrace();
                     CompletableFuture.runAsync(()->new NotificationDialog(throwable.toString()).show(),uiexecutor);
                 }
        });
    }
    public SimpleIntegerProperty activityCountProperty() {
        return activityCount;
    }
    public  void pub(TranslationResponse res){
        responses.onNext(res);
    }

    public Observable<TranslationResponse> getD(){
       return responses;
    }

}
