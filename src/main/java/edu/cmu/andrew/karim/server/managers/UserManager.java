package edu.cmu.andrew.karim.server.managers;

import com.mongodb.BasicDBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.exceptions.AppException;
import edu.cmu.andrew.karim.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.karim.server.models.Address;
import edu.cmu.andrew.karim.server.models.User;
import edu.cmu.andrew.karim.server.utils.MongoPool;
import edu.cmu.andrew.karim.server.utils.AppLogger;
import edu.cmu.andrew.karim.server.utils.PasswordUtils;
import org.bson.BSON;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.json.JSONObject;

import java.lang.String;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserManager extends Manager {
    public static UserManager _self;
    private MongoCollection<Document> userCollection;


    public UserManager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    public static UserManager getInstance(){
        if (_self == null)
            _self = new UserManager();
        return _self;
    }

    public void createUser(User user) throws AppException {
        try {
            JSONObject json = new JSONObject(user);

            Document newDoc = new Document()
                    .append("firstName", user.getFirstName())
                    .append("lastName", user.getLastName())
                    .append("roleId", user.getRoleId())
                    .append("phoneNumber", user.getPhoneNumber())
                    .append("password", user.getPassword())
                    .append("salt", user.getSalt())
                    .append("currency", user.getCurrency())
                    .append("language", user.getLanguage())
                    .append("rating", user.getRating())
                    .append("address", user.getAddr().getAddress())
                    .append("longitude", user.getAddr().getLongitude())
                    .append("latitude", user.getAddr().getLatitude());

            if (newDoc != null)
                userCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new user");

        } catch (Exception e) {
            throw handleException("Create User", e);
        }
    }

    public void updateUser( User user) throws AppException {
        try {
            Bson filter = new Document("_id", new ObjectId(user.getId()));
            Bson newValue = new Document()
                    .append("firstName", user.getFirstName())
                    .append("password", user.getPassword())
                    .append("email",user.getEmail());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                userCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update user details");
        } catch (Exception e) {
            throw handleException("Update User", e);
        }
    }

    public void deleteUser(String phoneNumber) throws AppException {
        try {
            Bson filter = new Document("phoneNumber", phoneNumber);
            userCollection.deleteOne(filter);
        } catch (Exception e){
            throw handleException("Delete User", e);
        }
    }

    public ArrayList<User> getUserList() throws AppException {
        try {
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                Address addr = new Address(
                        userDoc.getString("address"),
                        userDoc.getString("longitude"),
                        userDoc.getString("latitude")
                );
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("firstName"),
                        userDoc.getString("lastName"),
                        userDoc.getString("roleId"),
                        userDoc.getString("phoneNumber"),
                        userDoc.getString("password"),
                        userDoc.getString("salt"),
                        userDoc.getString("currency"),
                        userDoc.getString("language"),
                        userDoc.getString("rating"),
                        addr
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch (Exception e) {
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getUserByPhone(String phoneNumber) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            Bson filter = new Document("phoneNumber", phoneNumber);
            FindIterable<Document> userDocs = userCollection.find(filter);
            for(Document userDoc: userDocs) {
                Address addr = new Address(
                        userDoc.getString("address"),
                        userDoc.getString("longitude"),
                        userDoc.getString("latitude")
                );
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("firstName"),
                        userDoc.getString("lastName"),
                        userDoc.getString("roleId"),
                        userDoc.getString("phoneNumber"),
                        userDoc.getString("password"),
                        userDoc.getString("salt"),
                        userDoc.getString("currency"),
                        userDoc.getString("language"),
                        userDoc.getString("rating"),
                        addr
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch(Exception e) {
            throw handleException("Get User List", e);
        }
    }

    public ArrayList<User> getHelperList(String currency, String language) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            Map<String, Object> filterMap = new HashMap<>();
            filterMap.put("roleId", "2");
            filterMap.put("currency", currency);
            filterMap.put("language", language);
            Bson filter = new Document(filterMap);
            BasicDBObject sortParams = new BasicDBObject();
            sortParams.put("rating", -1);
            FindIterable<Document> userDocs = userCollection.find(filter).sort(sortParams);
            for(Document userDoc: userDocs) {
                Address addr = new Address(
                        userDoc.getString("address"),
                        userDoc.getString("longitude"),
                        userDoc.getString("latitude")
                );
                User user = new User(
                        userDoc.getObjectId("_id").toString(),
                        userDoc.getString("firstName"),
                        userDoc.getString("lastName"),
                        userDoc.getString("roleId"),
                        userDoc.getString("phoneNumber"),
                        userDoc.getString("password"),
                        userDoc.getString("salt"),
                        userDoc.getString("currency"),
                        userDoc.getString("language"),
                        userDoc.getString("rating"),
                        addr
                );
                userList.add(user);
            }
            return new ArrayList<>(userList);
        } catch (Exception e) {
            throw handleException("Get Helper List", e);
        }
    }
    public boolean checkAuthentication(String phoneNumber, String password) throws AppException {
        try{
            ArrayList<User> userList = new ArrayList<>();
            FindIterable<Document> userDocs = userCollection.find();
            for(Document userDoc: userDocs) {
                if(userDoc.getString("phoneNumber").equals(phoneNumber)) {
                    User user = new User(
                            userDoc.getString("firstName"),
                            userDoc.getString("lastName"),
                            userDoc.getString("roleId"),
                            userDoc.getString("phoneNumber"),
                            userDoc.getString("password"),
                            userDoc.getString("salt")
                    );
                    userList.add(user);
                }
            }
            return PasswordUtils.verifyUserPassword(password, userList.get(0).getPassword(), userList.get(0).getSalt());
        } catch (Exception e) {
            throw handleException("Get User List", e);
        }
    }

}
