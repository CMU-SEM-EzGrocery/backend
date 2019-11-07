package edu.cmu.andrew.karim.server.models;

public class Order {
    private String phoneNum;
    private String slotStart;
    private String slotEnd;
    private Address startAddr;
    private Address endAddr;
    private int status;
    private String helperNum;
    private double fee;

    public Order(String phoneNum, double fee, String slotStart, String slotEnd,
                 Address startAddr, Address endAddr, int status) {
        this.phoneNum = phoneNum;
        this.fee = fee;
        this.slotStart = slotStart;
        this.slotEnd = slotEnd;
        this.startAddr = startAddr;
        this.endAddr = endAddr;
        this.status = status;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getSlotStart() {
        return slotStart;
    }

    public void setSlotStart(String slotStart) {
        this.slotStart = slotStart;
    }

    public String getSlotEnd() {
        return slotEnd;
    }

    public void setSlotEnd(String slotEnd) {
        this.slotEnd = slotEnd;
    }

    public Address getStartAddr() {
        return startAddr;
    }

    public void setStartAddr(Address startAddr) {
        this.startAddr = startAddr;
    }

    public Address getEndAddr() {
        return endAddr;
    }

    public void setEndAddr(Address endAddr) {
        this.endAddr = endAddr;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getHelperNum() {
        return helperNum;
    }

    public void setHelperNum(String helperNum) {
        this.helperNum = helperNum;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }
}
