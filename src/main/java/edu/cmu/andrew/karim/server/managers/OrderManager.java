package edu.cmu.andrew.karim.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.exceptions.AppException;
import edu.cmu.andrew.karim.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.karim.server.models.Address;
import edu.cmu.andrew.karim.server.models.Order;
import edu.cmu.andrew.karim.server.models.User;
import edu.cmu.andrew.karim.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;

public class OrderManager extends Manager{

    public static OrderManager _self;
    private MongoCollection<Document> orderCollection;

    public OrderManager() {
        this.orderCollection = MongoPool.getInstance().getCollection("orders");
    }

    public static OrderManager getInstance(){
        if (_self == null)
            _self = new OrderManager();
        return _self;
    }

    public void createOrder(Order oder) throws AppException {
        try {
            JSONObject json = new JSONObject(oder);

            Document newDoc = new Document()
                    .append("phoneNumber", oder.getPhoneNum())
                    .append("slotStart", oder.getSlotStart())
                    .append("slotEnd", oder.getSlotEnd())
                    .append("startAddr", oder.getStartAddr().getAddress())
                    .append("startLongitude", oder.getStartAddr().getLongitude())
                    .append("startLatitude", oder.getStartAddr().getLatitude())
                    .append("endAddr", oder.getEndAddr().getAddress())
                    .append("endLongitude", oder.getEndAddr().getLongitude())
                    .append("endLatitude", oder.getEndAddr().getLatitude())
                    .append("status",oder.getStatus());
            if (newDoc != null)
                orderCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new order");

        } catch (Exception e) {
            throw handleException("Create Order", e);
        }
    }

    public ArrayList<Order> getOrderList() throws AppException {
        try {
            ArrayList<Order> orderList = new ArrayList<>();
            FindIterable<Document> orderDocs = orderCollection.find();
            for(Document orderDoc: orderDocs) {
                Address startAddr = new Address(
                        orderDoc.getString("startAddr"),
                        orderDoc.getString("startLongitude"),
                        orderDoc.getString("startLatitude")
                );
                Address endAddr = new Address(
                        orderDoc.getString("endAddr"),
                        orderDoc.getString("endLongitude"),
                        orderDoc.getString("endLatitude")
                );

                Order order = new Order(
                        orderDoc.getString("phoneNumber"),
                        orderDoc.getDouble("fee"),
                        orderDoc.getString("slotStart"),
                        orderDoc.getString("slotEnd"),
                        startAddr,
                        endAddr,
                        orderDoc.getInteger("status")
                );
                orderList.add(order);
            }
            return new ArrayList<>(orderList);
        } catch (Exception e) {
            throw handleException("Get Order List", e);
        }
    }

    public void deleteOrder(String phoneNumber) throws AppException {
        try {
            Bson filter = new Document("phoneNumber", phoneNumber);
            orderCollection.deleteOne(filter);
        } catch (Exception e){
            throw handleException("Delete Order", e);
        }
    }
}
