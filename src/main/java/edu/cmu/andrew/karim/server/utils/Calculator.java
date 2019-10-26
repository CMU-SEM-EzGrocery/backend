package edu.cmu.andrew.karim.server.utils;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    //Estimate the Service Fee based on the distance and the service time.
    //As of now, the service is paid by cash, so our platform won't charge service fee or say transaction fee.
    //The fee estimation is only based on 3 factors
    // 1. The distance between 2 locations and 2. The time the service is delivered 3. A fixed service fee
    //1. For the distance part, we have a base rate like 0.2 dollar per mile, much lower than uber.
    //2. For the time part, we add an additional 2 dollar pay if it is the rush hour request
    //3. For the service part, since we require the helper companying with the requester, there is additional 2 dollar fee
    //Referring to Ubers' fee estimation as following, we come up our estimation formula.
    //Gross Uber Driver Pay Per Hour	$15.73
    //Gross Uber Driver Pay Per Mile	$0.83
    //Gross Uber Driver Pay Per Trip	$8.90
    //So the calculation will be "fee = 0.2*distance*2 + rushHourFee + service Fee;
    public static double driverFee(double start_lat, double start_lng, double end_lat, double end_lng, String startTime) {
        double mileRate = 0.2;
        int serviceFee = 2;
        int rushHourRate = 2;
        double fee = 0;
        List<String> rushHours = new ArrayList<String>();
        rushHours.add("7");
        rushHours.add("8");
        rushHours.add("17");
        rushHours.add("18");
        double rushHourFee = rushHours.contains(startTime)?rushHourRate:0;
        double distance = distance(start_lat, start_lng, end_lat, end_lng);
        fee = mileRate*distance + rushHourFee + serviceFee;
        return fee;
    }

    //in miles
    private static double distance(double lat1, double lon1, double lat2, double lon2) {
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            return (dist);
        }
    }

    //We recommended helper based on similarities between helper and requester, as of now, we consider language only
    //Other factor taken into consideration is "helper availability, location(<5miles to start_addr), rating)
    //We will constraint the number of recommended helper to less than 10
    //As a summary, we find Helpers who
    // 1. have same language background as the requester
    //And 2. The location of their address is 5 miles less away from the start_addr
    //And 3. The overall list will be ordered by Rating high to low
    //System will have the most 10 or less high rated helpers
    public static List<String> helperRecommendation(String requesterId, String start_addr, String rating) {
        List<String> helperList = new ArrayList<>();

        return helperList;
    }




}
