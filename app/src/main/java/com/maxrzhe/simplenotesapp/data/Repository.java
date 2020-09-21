package com.maxrzhe.simplenotesapp.data;

import androidx.lifecycle.LiveData;

import java.util.List;

public interface Repository<T> {

    LiveData<T> create(T entity);

    LiveData<T> read(T entity);

    void update(T entity);

    void delete(T entity);

    LiveData<List<T>> readAll();

    LiveData<List<T>> readAllInRealTime(String currentUserId);
}
