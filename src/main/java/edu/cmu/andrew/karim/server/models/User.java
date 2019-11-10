package edu.cmu.andrew.karim.server.models;


public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String roleId;
    private String phoneNumber;
    private String password;
    private String salt;
    private String preferredMarket;
    private String currency;
    private String language;
    private String rating;
    private String email;
    private String userAvatar;
    private Address addr;

    public User(String firstName, String lastName, String roleId,
                String phoneNumber, String password, String salt) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleId = roleId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.salt = salt;
    }

    public User(String id, String firstName, String lastName,
                String roleId, String phoneNumber, String password,
                String salt, String currency, String language,
                String rating, Address addr) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.roleId = roleId;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.salt = salt;
        this.currency = currency;
        this.language = language;
        this.rating = rating;
        this.addr = addr;
    }

    public User(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getPreferredMarket() {
        return preferredMarket;
    }

    public void setPreferredMarket(String preferredMarket) {
        this.preferredMarket = preferredMarket;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public Address getAddr() {
        return addr;
    }

    public void setAddr(Address addr) {
        this.addr = addr;
    }
}
