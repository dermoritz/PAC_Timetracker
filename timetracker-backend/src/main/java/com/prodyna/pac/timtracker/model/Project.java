package com.prodyna.pac.timtracker.model;

import javax.persistence.Entity;

import java.io.Serializable;

import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Column;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Version;

import java.lang.Override;
import java.util.List;

/**
 * A project could be linked by many {@link Booking}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Entity
public class Project implements Serializable {

    /**
     * 
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
     * Used for optimistic locking.
     */
    @Version
    @Column(name = "version")
    private int version;

    /**
     * Unique name, user friendly id.
     */
    @Column(unique = true)
    private String name;

    /**
     * Textual description of project.
     */
    @Column
    private String description;
    
    
    /**
     * @return the id
     */
    public final Long getId() {
        return id;
    }

    /**
     * @param id
     *            the id to set
     */
    public final void setId(final Long id) {
        this.id = id;
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
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public final void setDescription(final String description) {
        this.description = description;
    }
    
    /**
     * Uses id, name and version to create hash.
     * 
     * @return hashCode
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
     * @param obj
     *            the other object
     * @return true if id,name and version are equal.
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
        Project other = (Project) obj;
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
        if (description != null && !description.trim().isEmpty()) {
            result += ", description: " + description;
        }
        return result;
    }
}