package sample.data.jpa.revision;

import org.hibernate.envers.RevisionListener;

/**
 * Created by suay on 7/30/15.
 */
public class UserRevisionListener implements RevisionListener {

    public final static String USERNAME = "Suay";

    @Override
    public void newRevision(Object revisionEntity) {
        UserRevEntity exampleRevEntity = (UserRevEntity) revisionEntity;
        exampleRevEntity.setUsername(USERNAME);
    }
}
