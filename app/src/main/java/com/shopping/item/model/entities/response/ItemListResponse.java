package com.shopping.item.model.entities.response;

import com.shopping.item.model.dto.Item;

import org.parceler.Parcel;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Parcel
public class ItemListResponse extends BaseServerResponse {
    private List<Item> item;
}
