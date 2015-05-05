package com.prodyna.pac.timtracker.persistence;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Base entity that implements basic stuff like timestamping.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@MappedSuperclass
public abstract class BaseEntity implements Timestampable, Serializable, Identifiable {
    
    /**
     * default.
     */
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    /**
     * Creation date of entity.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date created = new Date();
    
    /**
     * Date of last update.
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date updated;

    /**
     * 
     * @return date of last update or null if no update occurred yet.
     */
    public final Date getLastUpdated() {
        return updated == null ? null : (Date) updated.clone();
    }

    @Override
    public final Date getCreated() {
        return created == null ? null : (Date) created.clone();
    }

    @Override
    public final Date getLastModified() {
        return getLastUpdated() == null ? getCreated() : getLastUpdated();
    }
    
    
    
    @Override
    public Long getId() {
        return id;
    }

    /**
     * Called before perist to set creation date.
     */
    @PrePersist
    protected final void onCreate() {
        created = new Date();
    }
    
    /**
     * Called before update to set/update last modified date.
     */
    @PreUpdate
    protected final void onUpdate() {
        updated = new Date();
    }

}
