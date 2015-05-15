package com.prodyna.pac.timtracker.webapi.resource.user;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

import com.prodyna.pac.timtracker.persistence.Identifiable;

/**
 * Users data representation for rest side.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@XmlRootElement(name = "user", namespace = "urn:timetracker:user")
public class UserRepresentation implements Identifiable {

    @NotNull
    private Long id;
    @NotNull
    private String name;
    @NotNull
    private String role;
    
    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}
