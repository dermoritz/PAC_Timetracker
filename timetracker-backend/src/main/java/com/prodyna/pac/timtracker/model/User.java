package com.prodyna.pac.timtracker.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Version;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.prodyna.pac.timtracker.persistence.BaseEntity;
import com.prodyna.pac.timtracker.persistence.Identifiable;

/**
 * User of time tracking. Has unique name for identification/authentication.
 * 
 * @author moritz
 *
 */
@Entity
public class User extends BaseEntity implements Identifiable {
    /**
     * Default id.
     */
    private static final long serialVersionUID = 1L;

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
     * required by JPA.
     */
    User() {
        
    }
    
    /**
     * 
     * @param name unique user name
     * @param role user's role
     */
    public User(final String name, final UserRole role) {
        if (Strings.isNullOrEmpty(name)) {
            throw new IllegalArgumentException("User name must neither null nor empty.");
        }
        this.name = name;
        this.role = Preconditions.checkNotNull(role, "User's role must not be null");
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

    @Override
    public final int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((role == null) ? 0 : role.hashCode());
        return result;
    }

    @Override
    public final boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        User other = (User) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (role != other.role) {
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