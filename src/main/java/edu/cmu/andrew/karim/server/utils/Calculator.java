package edu.cmu.andrew.karim.server.utils;

import edu.cmu.andrew.karim.server.managers.UserManager;
import edu.cmu.andrew.karim.server.models.User;

import java.util.ArrayList;
import java.util.Arrays;
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
    //TODO: In frontEnd, when request, should ask requester to choose currencyType, with a default value as dollar.
    //TODO: backend provide fee in dollar, frontend will call REST API to convert it to count in rupee or Yuan？？
/*    public static double driverFee(double start_lat, double start_lng, double end_lat, double end_lng, String startTime, String currencyType) {
        double feeinDollar = driverFeeinDollar(start_lat,start_lng,end_lat,end_lng,startTime);
        if (currencyType ==
    }*/
    private static double driverFeeinDollar(double start_lat, double start_lng, double end_lat, double end_lng, String startTime) {
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

    //TODO: fee updated
    //TODO: REST API for fee
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
    // 1. Helper has the same language skill with requester (have overlapped language skills as the requester does)
    //And 2. The location of their address is 5 miles less away from the start_addr
    //And 3. Helper has the same currency type with requester (The currency Type is in helper's acceptance list)
    //And 4. The status of helper is "available for service"
    //And 5. The overall list will be ordered by Rating high to low
    //System will have the most 10 or less high rated helpers
    //Parameter requesterId is the phoneNumber of requester, which will be used to get the info of requester

    //TODO: language is a list
    //TODO: the currencyfield of a helper should be a list of currencies that he can accept, separated by ","
    private static List<String> helperRecommendation(String requesterId, String start_lat, String start_lng) {
        List<String> recommendedHelpers = new ArrayList<>();
        List<String> helperList = new ArrayList<>();
        User requester = new User(requesterId);
        //Requester Data
        String requesterLanguage = "Chinese"; //requester.getLanguage();
        String requesterCurrency = requester.getCurrency();

        UserManager um = new UserManager();
        List<User> helpList = um.getHelperList(requesterCurrency,requesterLanguage);

        //TODO: in future when we have more users in DB, the helpList will have a limitation size,like40
        for (User helper:helpList) {
            String helper_lat = helper.getAddress().getlat();
            String helper_lng = helper.getAddress().getlng();
            double helper_distance = distance(Double.parseDouble(start_lat), Double.parseDouble(start_lng), Double.parseDouble(helper_lat), Double.parseDouble(helper_lng));
            if (helper_distance <= 5) {
                recommendedHelpers.add(helper.getId());
                if (recommendedHelpers.size() >= 10)
                    break;
            }
        }

        return recommendedHelpers;
    }

}
