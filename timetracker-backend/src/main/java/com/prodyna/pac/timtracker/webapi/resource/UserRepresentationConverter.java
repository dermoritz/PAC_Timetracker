package com.prodyna.pac.timtracker.webapi.resource;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.core.UriInfo;

import com.prodyna.pac.timtracker.model.User;
import com.prodyna.pac.timtracker.model.UserRole;
import com.prodyna.pac.timtracker.webapi.RepresentationConverter;

/**
 * Maps between {@link User} and {@link UserRepresentation}.
 * 
 * @author moritz löser (moritz.loeser@prodyna.com)
 *
 */
@RequestScoped
public class UserRepresentationConverter extends RepresentationConverter.Base<UserRepresentation, User> {

    /**
     * Constructs this by using super with {@link UserRepresentation} and
     * {@link User}.
     */
    public UserRepresentationConverter() {
        super(UserRepresentation.class, User.class);
    }

    @Override
    public UserRepresentation from(UriInfo uriInfo, User source) {
        UserRepresentation rep = new UserRepresentation(uriInfo);
        rep.setHandle(source.getId());
        rep.setName(source.getName());
        rep.setRole(roleToString(source.getRole()));
        return rep;
    }

    @Override
    public User to(UriInfo uriInfo, UserRepresentation representation) {
        return update(uriInfo, representation, new User(representation.getName(),
                                                        stringToRole(representation.getRole())));
    }

    @Override
    public User update(UriInfo uriInfo, UserRepresentation representation, User target) {
        target.setRole(stringToRole(representation.getRole()));
        return target;
    }

    private static String roleToString(UserRole role) {
        return role.name();
    }

    private static UserRole stringToRole(String role) {
        // will throw an exception if not matching - but we want it to throw
        return UserRole.valueOf(role.toUpperCase());
    }

}
