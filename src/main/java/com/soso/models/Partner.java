package com.soso.models;

/**
 * Created by Garik Kalashyan on 3/8/2017.
 */
public class Partner {
    private Integer id;
    private String partner_name;
    private String  telephone;
    private boolean isAdmin;
    private Integer serviceId;
    private String partner_username;
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPartner_name() {
        return partner_name;
    }

    public void setPartner_name(String partner_name) {
        this.partner_name = partner_name;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getPartner_username() {
        return partner_username;
    }

    public void setPartner_username(String partner_username) {
        this.partner_username = partner_username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
