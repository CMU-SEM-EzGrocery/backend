package edu.cmu.andrew.karim.server.http.interfaces;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.karim.server.http.responses.AppResponse;
import edu.cmu.andrew.karim.server.http.utils.PATCH;
import edu.cmu.andrew.karim.server.models.User;
import edu.cmu.andrew.karim.server.managers.UserManager;
import edu.cmu.andrew.karim.server.utils.*;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/users")

public class UserHttpInterface extends HttpInterface{

    private ObjectWriter ow;
    private MongoCollection<Document> userCollection = null;

    public UserHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postUsers(Object request){

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));

            // Generate Salt. The generated value can be stored in DB.
            String salt = PasswordUtils.getSalt(30);

            // Protect user's password. The generated value can be stored in DB.
            String mySecurePassword = PasswordUtils.generateSecurePassword(json.getString("password"), salt);

            User newUser = new User(
                    json.getString("firstName"),
                    json.getString("lastName"),
                    json.getString("roleId"),
                    json.getString("phoneNumber"),
                    mySecurePassword,
                    salt
            );
            UserManager.getInstance().createUser(newUser);
            return new AppResponse("Insert new user Successful");

        } catch (Exception e){
            throw handleException("POST users", e);
        }
    }



    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getUsers(@Context HttpHeaders headers){

        try {
            AppLogger.info("Got an API call");
            ArrayList<User> users = UserManager.getInstance().getUserList();

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        } catch (Exception e){
            throw handleException("GET /users", e);
        }


    }

    @GET
    @Path("/{phoneNumber}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleUser(@Context HttpHeaders headers, @PathParam("phoneNumber") String phoneNumber){

        try {
            AppLogger.info("Got an API call");
            ArrayList<User> users = UserManager.getInstance().getUserByPhone(phoneNumber);

            if(users != null)
                return new AppResponse(users);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        } catch (Exception e){
            throw handleException("GET /users/{userId}", e);
        }


    }


    @PATCH
    @Path("/{phoneNumber}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchUsers(Object request, @PathParam("phoneNumber") String phoneNumber){

        JSONObject json = null;

        try {
            json = new JSONObject(ow.writeValueAsString(request));
            User user = new User(
                    phoneNumber
            );

            UserManager.getInstance().updateUser(user);

        } catch (Exception e){
            throw handleException("PATCH users/{phoneNumber}", e);
        }

        return new AppResponse("Update Successful");
    }




    @DELETE
    @Path("/{phoneNumber}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteUsers(@PathParam("phoneNumber") String phoneNumber){

        try {
            UserManager.getInstance().deleteUser(phoneNumber);
            return new AppResponse("Delete Successful");
        } catch (Exception e) {
            throw handleException("DELETE users/{phoneNumber}", e);
        }
    }

    @GET
    @Path("/login")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse checkAuthentication(Object request){
        JSONObject json = null;
        UserManager userManager = UserManager.getInstance();
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            String phoneNumber = json.getString("phoneNumber");
            //If there 0 or more than 1 users in the db, return failed
            ArrayList<User> userArrayList = userManager.getUserByPhone(phoneNumber);
            if(userArrayList.size() != 1) {
                return new AppResponse("Failed");
            }
            boolean result = userManager
                    .checkAuthentication(phoneNumber, json.getString("password"));
            if(result)
                return new AppResponse("Success");
            else
                return new AppResponse("Failed");
        } catch (Exception e){
            throw handleException("GET /users", e);
        }
    }
}
