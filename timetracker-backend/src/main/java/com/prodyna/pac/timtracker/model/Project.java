package com.prodyna.pac.timtracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Version;

import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.persistence.BaseEntity;

/**
 * A project could be linked by many {@link Booking}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@Entity
public class Project extends BaseEntity {

    /**
     * default.
     */
    private static final long serialVersionUID = 1L;

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
     * Required by JPA.
     */
    Project() {

    }

    /**
     * 
     * @param name
     *            unique name for project
     * @param description
     *            optional description
     */
    public Project(final String name, final String description) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("Name must not be null or empty.");
        }
        this.name = name;
        setDescription(description);
    }


    /**
     * @return the version
     */
    public final int getVersion() {
        return version;
    }

    /**
     * @return the name
     */
    public final String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public final String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public final void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Project)) {
            return false;
        }
        Project other = (Project) obj;
        if (description == null) {
            if (other.description != null) {
                return false;
            }
        } else if (!description.equals(other.description)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
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