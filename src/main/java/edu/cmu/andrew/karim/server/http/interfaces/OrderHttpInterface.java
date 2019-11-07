package edu.cmu.andrew.karim.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.karim.server.http.responses.AppResponse;
import edu.cmu.andrew.karim.server.http.utils.PATCH;
import edu.cmu.andrew.karim.server.managers.OrderManager;
import edu.cmu.andrew.karim.server.managers.UserManager;
import edu.cmu.andrew.karim.server.models.Address;
import edu.cmu.andrew.karim.server.models.Order;
import edu.cmu.andrew.karim.server.models.User;
import edu.cmu.andrew.karim.server.utils.AppLogger;
import org.bson.Document;
import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;

@Path("/orders")
public class OrderHttpInterface extends HttpInterface{
    private ObjectWriter ow;
    private MongoCollection<Document> orderCollection = null;

    public OrderHttpInterface() {
        ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse postOrders(Object request){

        try {
            JSONObject json = null;
            json = new JSONObject(ow.writeValueAsString(request));
            ArrayList<Order> orders = OrderManager.getInstance().getOrderByPhone(json.getString("phoneNumber"));
            if(!orders.isEmpty()) {
                return new AppResponse(500,
                        "The order with this phone number already exists.");
            }
            Address startAddr = new Address(
                    json.getString("startAddr"),
                    json.getString("startLongitude"),
                    json.getString("startLatitude")
            );
            Address endAddr = new Address(
                    json.getString("endAddr"),
                    json.getString("endLongitude"),
                    json.getString("endLatitude")
            );
            Order newOrder = new Order(
                    json.getString("phoneNumber"),
                    0.0,
                    json.getString("slotStart"),
                    json.getString("slotEnd"),
                    startAddr,
                    endAddr,
                    1
            );
            OrderManager.getInstance().createOrder(newOrder);
            return new AppResponse("Insert new order Successful");

        } catch (Exception e){
            throw handleException("POST orders", e);
        }
    }

    @PATCH
    @Path("/{phoneNumber}")
    @Consumes({ MediaType.APPLICATION_JSON})
    @Produces({ MediaType.APPLICATION_JSON})
    public AppResponse patchOrders(Object request, @PathParam("phoneNumber") String phoneNumber){

        JSONObject json = null;
        try {
            json = new JSONObject(ow.writeValueAsString(request));
            ArrayList<Order> orders = OrderManager.getInstance().getOrderByPhone(json.getString("phoneNumber"));
            if(orders.isEmpty()) {
                return new AppResponse(500,
                        "The order with this phone number does not exists.");
            }
            Address startAddr = new Address(
                    json.getString("startAddr"),
                    json.getString("startLongitude"),
                    json.getString("startLatitude")
            );
            Address endAddr = new Address(
                    json.getString("endAddr"),
                    json.getString("endLongitude"),
                    json.getString("endLatitude")
            );
            Order newOrder = new Order(
                    json.getString("phoneNumber"),
                    json.getDouble("fee"),
                    json.getString("slotStart"),
                    json.getString("slotEnd"),
                    startAddr,
                    endAddr,
                    1
            );

            OrderManager.getInstance().updateOrder(newOrder);

        } catch (Exception e){
            throw handleException("PATCH orders/{phoneNumber}", e);
        }
        return new AppResponse("Update Successful");
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getOrders(@Context HttpHeaders headers){

        try {
            AppLogger.info("Got an API call");
            ArrayList<Order> orders = OrderManager.getInstance().getOrderList();

            if(orders != null)
                return new AppResponse(orders);
            else
                throw new HttpBadRequestException(0, "Problem with getting orders");
        } catch (Exception e){
            throw handleException("GET /orders", e);
        }
    }

    @GET
    @Path("/{phoneNumber}")
    @Produces({MediaType.APPLICATION_JSON})
    public AppResponse getSingleOrder(@Context HttpHeaders headers, @PathParam("phoneNumber") String phoneNumber){

        try {
            ArrayList<Order> orders = OrderManager.getInstance().getOrderByPhone(phoneNumber);
            AppLogger.info("Got an API call");

            if(orders != null)
                return new AppResponse(orders);
            else
                throw new HttpBadRequestException(0, "Problem with getting users");
        } catch (Exception e) {
            throw handleException("GET /orders/{phoneNumber}", e);
        }
    }

    @DELETE
    @Path("/{phoneNumber}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteOrders(@PathParam("phoneNumber") String phoneNumber){

        try {
            ArrayList<Order> orders = OrderManager.getInstance().getOrderByPhone(phoneNumber);
            if(orders.isEmpty()) {
                return new AppResponse(500,
                        "The order with this phone number does not exists.");
            }
            OrderManager.getInstance().deleteOrder(phoneNumber);
            return new AppResponse("Delete Successful");
        } catch (Exception e) {
            throw handleException("DELETE orders/{phoneNumber}", e);
        }
    }


}
