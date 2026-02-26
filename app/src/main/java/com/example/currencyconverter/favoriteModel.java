package com.example.currencyconverter;

import java.util.UUID;

public class favoriteModel {
    private String title;
    private int positionTo;
    private int positionFrom;

    public favoriteModel(String _title, int _positionTo, int _positionFrom)
    {
        this.title = _title;
        this.positionTo = _positionTo;
        this.positionFrom = _positionFrom;
    }
}