package edu.cmu.andrew.karim.server.managers;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.exceptions.AppException;
import edu.cmu.andrew.karim.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.karim.server.models.Address;
import edu.cmu.andrew.karim.server.models.Order;
import edu.cmu.andrew.karim.server.utils.Calculator;
import edu.cmu.andrew.karim.server.utils.MongoPool;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

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

    public void createOrder(Order order) throws AppException {
        try {
            JSONObject json = new JSONObject(order);
            double fee = Calculator.getInstance().driverFeeinDollar(order);
            String phoneNumber = order.getPhoneNum();
            String startLongitude = order.getStartAddr().getLongitude();
            String startLatitude = order.getStartAddr().getLatitude();

            List<String> helperList = Calculator.getInstance()
                    .helperRecommendation(phoneNumber, startLongitude, startLatitude);

            Document newDoc = new Document()
                    .append("phoneNumber", phoneNumber)
                    .append("fee", fee)
                    .append("helperList", helperList)
                    .append("slotStart", order.getSlotStart())
                    .append("slotEnd", order.getSlotEnd())
                    .append("startAddr", order.getStartAddr().getAddress())
                    .append("startLongitude", startLongitude)
                    .append("startLatitude", startLatitude)
                    .append("endAddr", order.getEndAddr().getAddress())
                    .append("endLongitude", order.getEndAddr().getLongitude())
                    .append("endLatitude", order.getEndAddr().getLatitude())
                    .append("status",order.getStatus());
            if (newDoc != null)
                orderCollection.insertOne(newDoc);
            else
                throw new AppInternalServerException(0, "Failed to create new order");

        } catch (Exception e) {
            throw handleException("Create Order", e);
        }
    }

    public void updateOrder(Order order) throws AppException {
        try {
            Bson filter = new Document("phoneNumber", order.getPhoneNum());
            Bson newValue = new Document()
                    .append("phoneNumber", order.getPhoneNum())
                    .append("fee", order.getFee())
                    .append("helperList", order.getHelperList())
                    .append("slotStart", order.getSlotStart())
                    .append("slotEnd", order.getSlotEnd())
                    .append("startAddr", order.getStartAddr().getAddress())
                    .append("startLongitude", order.getStartAddr().getLongitude())
                    .append("startLatitude", order.getStartAddr().getLatitude())
                    .append("endAddr", order.getEndAddr().getAddress())
                    .append("endLongitude", order.getEndAddr().getLongitude())
                    .append("endLatitude", order.getEndAddr().getLatitude())
                    .append("status",order.getStatus());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                orderCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update order details");
        } catch (Exception e) {
            throw handleException("Update Order", e);
        }
    }
    public void updateHelper(Order order) throws AppException {
        try {
            Bson filter = new Document("phoneNumber", order.getPhoneNum());
            Bson newValue = new Document()
                    .append("phoneNumber", order.getPhoneNum())
                    .append("helperList", order.getHelperList())
                    .append("status",order.getStatus());
            Bson updateOperationDocument = new Document("$set", newValue);

            if (newValue != null)
                orderCollection.updateOne(filter, updateOperationDocument);
            else
                throw new AppInternalServerException(0, "Failed to update order details");
        } catch (Exception e) {
            throw handleException("Update Order helper", e);
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
                order.setHelperList((List<Object>) orderDoc.get("helperList"));
                orderList.add(order);
            }
            return new ArrayList<>(orderList);
        } catch (Exception e) {
            throw handleException("Get Order List", e);
        }
    }

    public ArrayList<Order> getOrderByPhone(String phoneNumber) throws AppException {
        try{
            ArrayList<Order> orderList = new ArrayList<>();
            Bson filter = new Document("phoneNumber", phoneNumber);
            FindIterable<Document> orderDocs = orderCollection.find(filter);
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
                order.setHelperList((List<Object>) orderDoc.get("helperList"));
                orderList.add(order);
            }
            return new ArrayList<>(orderList);
        } catch(Exception e) {
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
