package com.shopping.item.model.entities.response;


import org.parceler.Parcel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Parcel
public class BaseServerResponse {
    private String status_code;
    private boolean success;
    private boolean isAPIError;
    private String Message;
    private boolean isTokenExpired;
}
