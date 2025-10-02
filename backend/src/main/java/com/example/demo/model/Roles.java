package com.example.demo.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Roles {

    private Boolean can_create_users = false;
    private Boolean can_read_users = false;
    private Boolean can_update_users = false;
    private Boolean can_delete_users = false;

    private Boolean can_search_order = false;
    private Boolean can_place_order = false;
    private Boolean can_cancel_order = false;
    private Boolean can_track_order = false;
    private Boolean can_schedule_order = false;



}