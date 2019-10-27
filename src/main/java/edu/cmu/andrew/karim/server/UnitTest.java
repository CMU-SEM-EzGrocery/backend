package edu.cmu.andrew.karim.server;

import edu.cmu.andrew.karim.server.utils.Calculator;

import java.util.List;

//public static List<String> helperRecommendation(String requesterId, String start_lat, String start_lng)
//public static double driverFeeinDollar(double start_lat, double start_lng, double end_lat, double end_lng, String startTime) {

/*Test Data
Requester
        Lucy, 6502409811,
        Destination: 37.4118032,-122.0997863
Helper
        Sam - 6507709455 - near people + 5 + similar background
        Tim - 9509909455 - near people + 5 + similar background
        Qige - 888888888 - near people + 5 + similar background
        Maria - 9507709455 - near people + 4 + similar background

        Matt - 6502409300 - 5.38far people + 5 + similar background
        Leo - 6502409455 - near people + 5 + diff background

        Windy - 9509913455 - near people + 5 + similar background, but a requester

 */
public class UnitTest {
    public static void main(String[] args) {
        //The requester, Lucy's home is 570 N Shoreline Blvd, Mountain View, CA 94043
        String start_lat = "-122.0707886";
        String start_lng = "37.4091132";
        //Destination is Starbucks, 580 N Rengstorff Ave, Mountain View, CA 94043;
        String end_lat = "-122.0997863";
        String end_lng = "37.4118032";
        String requesterId = "6502409811";
        String startTime = "8";

        try {
            System.out.println("Following ids are phone numbers of recommended helpers");
            List<String> userPhoneIdList = Calculator.helperRecommendation(requesterId, start_lat, start_lng);
            for (String id : userPhoneIdList) {
                System.out.println(id);
            }
        } catch (Exception e) {
            System.out.println("Exception in matching helpers" + e);
        }

        double fee = Calculator.driverFeeinDollar(Double.parseDouble(start_lat), Double.parseDouble(start_lng),
                Double.parseDouble(end_lat), Double.parseDouble(end_lng), startTime);

        System.out.println("The estimation fee in dollar is " + fee);
    }
}
