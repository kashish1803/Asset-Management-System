package com.java.asset.exception;


public class AssetNotFound extends Exception {

    public AssetNotFound() {
        super("Asset not found with the given ID.");
    }

    public AssetNotFound(String message) {
        super(message);
    }
}

