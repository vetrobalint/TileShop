package com.example.tileshop;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.Random;

public class HelperAsyncLoader extends AsyncTaskLoader<String> {
    public HelperAsyncLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();

        forceLoad();
    }

    @Nullable
    @Override
    public String loadInBackground() {
        Random random = new Random();
        int number = random.nextInt(12);
        int ms = number * 800;

        try{
            Thread.sleep(ms);
        }catch (InterruptedException e){
            e.printStackTrace();
        }

        return "Bejelentkezés";
    }
}
