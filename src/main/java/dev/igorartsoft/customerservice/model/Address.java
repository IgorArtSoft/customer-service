package dev.igorartsoft.customerservice.model;

public class Address {

    private String line1;
    private String line2;
    private String city;
    private String province;
    private String postalCode;
    private String country;

    public Address() {
    }

    public Address(
            String line1,
            String line2,
            String city,
            String province,
            String postalCode,
            String country
    ) {
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.province = province;
        this.postalCode = postalCode;
        this.country = country;
    }

    public String getLine1() {
        return line1;
    }

    public String getLine2() {
        return line2;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setLine1(String line1) {
        this.line1 = line1;
    }

    public void setLine2(String line2) {
        this.line2 = line2;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}