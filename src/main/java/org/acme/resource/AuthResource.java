package org.acme.resource;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.acme.entity.User;

import java.util.HashMap;
import java.util.Map;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    public static class LoginRequest {
        public String username;
        public String password;
    }

    @POST
    @Path("/login")
    public Response login(LoginRequest req) {
        if (req.username == null || req.password == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Username dan password wajib diisi").build();
        }

        User user = User.find("username = ?1 and password = ?2", req.username, req.password)
                .firstResult();

        if (user == null) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Username atau password salah").build();
        }

        // Return only relevant info
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.id);
        response.put("username", user.username);
        response.put("role", user.role);

        return Response.ok(response).build();
    }
}
