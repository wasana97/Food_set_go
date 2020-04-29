package com.shopping.item.model.dto;

import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Parcel
public class ShippingDetails {
    private String lblName;
    private String lblMobile;
    private String lblEmail;
    private String lblAddress1;
    private String lblAddress2;
    private String lblCity;
    private String lblState;
    private String lblPostalCode;
    private String lblCountry;
}
