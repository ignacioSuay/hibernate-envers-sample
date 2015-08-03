package sample.data.jpa.revision;

import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionEntity;
import sample.data.jpa.domain.City;

import javax.persistence.Entity;

/**
 * Created by suay on 7/30/15.
 */
@Entity
@RevisionEntity(UserRevisionListener.class)
public class UserRevEntity extends DefaultRevisionEntity {

    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
