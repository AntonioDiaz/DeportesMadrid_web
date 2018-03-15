package com.adiaz.madrid.daos;

import com.googlecode.objectify.Key;

import java.util.List;

public interface GenericDAO<T> {
    Key<T> create(T item) throws Exception;

    boolean update(T item) throws Exception;

    void remove(Long id) throws Exception;

    T findById(Long id);

    List<T> findAll();
}
