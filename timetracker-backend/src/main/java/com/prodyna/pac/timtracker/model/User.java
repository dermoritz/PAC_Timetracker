package com.prodyna.pac.timtracker.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Version;

/**
 * User of time tracking. Has unique name for identification/authentication.
 * 
 * @author moritz
 *
 */
@Entity
public class User implements Serializable {
    /**
     * Default id.
     */
    private static final long serialVersionUID = 1L;

    /**
     * internal id.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    /**
     * version for optimistic locking.
     */
    @Version
    @Column(name = "version")
    private int version;
    
    /**
     * A user has a unique name.
     */
    @Column(unique = true)
    private String name;
    
    /**
     * Role of this user.
     */
    @Enumerated(EnumType.STRING)
    private UserRole role;

    /**
     * 
     * @return id
     */
    public final Long getId() {
        return this.id;
    }
    
    /**
     * @return the version
     */
    public final int getVersion() {
        return version;
    }

    /**
     * @param version
     *            the version to set
     */
    public final void setVersion(final int version) {
        this.version = version;
    }

    /**
     * @param id
     *            the id to set
     */
    public final void setId(final Long id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public final void setName(final String name) {
        this.name = name;
    }

    /**
     * @return hash code using id, name and version
     */
    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + version;
        return result;
    }

    /**
     * 
     * @param obj
     *            object to be compared
     * @return if true id, name and version are equal. false otherwise.
     */
    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        User other = (User) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (version != other.version) {
            return false;
        }
        return true;
    }

    @Override
    public final String toString() {
        String result = getClass().getSimpleName() + " ";
        if (name != null && !name.trim().isEmpty()) {
            result += "name: " + name;
        }
        return result;
    }
}