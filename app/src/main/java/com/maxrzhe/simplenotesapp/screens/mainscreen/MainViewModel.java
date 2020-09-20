package com.maxrzhe.simplenotesapp.screens.mainscreen;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.maxrzhe.simplenotesapp.data.Repository;
import com.maxrzhe.simplenotesapp.data.RepositoryFactory;
import com.maxrzhe.simplenotesapp.pojo.Note;

public class MainViewModel extends AndroidViewModel {

    private Repository<Note> repository;
    private LiveData<Note> noteLiveData;

    public MainViewModel(@NonNull Application application) {
        super(application);
        repository = RepositoryFactory.getRepository();
        noteLiveData = new MutableLiveData<>();
    }

    public void createNote(Note note) {
        repository.create(note);
    }



}
