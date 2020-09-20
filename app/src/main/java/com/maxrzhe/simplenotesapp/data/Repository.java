package com.maxrzhe.simplenotesapp.data;

public interface Repository<T> {

    void create(T entity);

    void read(T entity);

    void update(T entity);

    void delete(T entity);

    void readAll();

    void readAllInRealTime();
}
