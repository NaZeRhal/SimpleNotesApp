package com.maxrzhe.simplenotesapp.data;

import com.maxrzhe.simplenotesapp.pojo.Note;

public abstract class RepositoryFactory {

    public static Repository<Note> getRepository() {
        return new FirebaseRepository();
    }
}
