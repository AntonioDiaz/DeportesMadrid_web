package com.adiaz.madrid.daos;

import com.adiaz.madrid.entities.ClassificationEntry;
import com.adiaz.madrid.entities.Competition;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Repository
public class ClassificationEntryDAOImpl implements ClassificationDAO {

    @Override
    public int recordsCount() {
        int count = 0;
        QueryKeys<ClassificationEntry> matchesList = ofy().load().type(ClassificationEntry.class).limit(5000).keys();
        while (matchesList.list().size()==5000) {
            count += matchesList.list().size();
            matchesList = ofy().load().type(ClassificationEntry.class).offset(count).limit(5000).keys();
        }
        count += matchesList.list().size();
        return count;
    }

    @Override
    public List<ClassificationEntry> findByCompeticion(String idCompeticion) {
        Key<Competition> key = Key.create(Competition.class, idCompeticion);
        Ref<Competition> competitionRef = Ref.create(key);
        Query<ClassificationEntry> query = ofy().load().type(ClassificationEntry.class)
                .filter("competitionRef", competitionRef)
                .order("position");
        return query.list();
    }

    @Override
    public void create(Collection<ClassificationEntry> values) {
        ofy().save().entities(values);
    }

    @Override
    public Key<ClassificationEntry> create(ClassificationEntry item) throws Exception {
        return ofy().save().entity(item).now();
    }

    @Override
    public boolean update(ClassificationEntry item) throws Exception {
        boolean updateResult;
        if (item == null || item.getId() == null) {
            updateResult = false;
        } else {
            ClassificationEntry c = ofy().load().type(ClassificationEntry.class).id(item.getId()).now();
            if (c != null) {
                ofy().save().entity(item).now();
                updateResult = true;
            } else {
                updateResult = false;
            }
        }
        return updateResult;
    }

    @Override
    public void remove(Long id) throws Exception {
        ofy().delete().type(ClassificationEntry.class).id(id).now();
    }

    @Override
    public ClassificationEntry findById(Long id) {
        return ofy().load().type(ClassificationEntry.class).id(id).now();
    }

    @Override
    public List<ClassificationEntry> findAll() {
       return ofy().load().type(ClassificationEntry.class).list();
    }
}
