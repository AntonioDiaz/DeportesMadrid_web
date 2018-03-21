package com.adiaz.madrid.daos;

import com.google.appengine.api.datastore.Entity;
import com.googlecode.objectify.Key;

import java.util.List;

public interface GenericDAO<T, V> {

    Key<T> create(T item) throws Exception;

    boolean update(T item) throws Exception;

    List<T> findAll();

    void remove(V id) throws Exception;

    T findById(V id);
}
