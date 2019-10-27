package edu.cmu.andrew.karim.server.http.interfaces;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.http.exceptions.HttpBadRequestException;
import edu.cmu.andrew.karim.server.http.responses.AppResponse;
import edu.cmu.andrew.karim.server.managers.OrderManager;
import edu.cmu.andrew.karim.server.models.Address;
import edu.cmu.andrew.karim.server.models.Order;
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

    @DELETE
    @Path("/{phoneNumber}")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public AppResponse deleteUsers(@PathParam("phoneNumber") String phoneNumber){

        try {
            OrderManager.getInstance().deleteOrder(phoneNumber);
            return new AppResponse("Delete Successful");
        } catch (Exception e) {
            throw handleException("DELETE orders/{phoneNumber}", e);
        }
    }


}
