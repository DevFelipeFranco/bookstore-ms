package com.ms.cloud.model;

import java.time.Instant;
import java.util.Objects;

public record Entry(
        Instant timestamp,
        String actor,
        String action
) {

    public Entry {
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        Objects.requireNonNull(actor, "Actor cannot be null").trim();
        Objects.requireNonNull(action, "Action cannot be null").trim();

        if (actor.isEmpty()) {
            throw new IllegalArgumentException("Actor cannot be empty");
        }
        if (action.isEmpty()) {
            throw new IllegalArgumentException("Action cannot be empty");
        }
    }

//    public Entry entry(Instant timestamp, String actor, String action) {
//        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
//        this.actor = Objects.requireNonNull(actor, "Actor cannot be null").trim();
//        this.action = Objects.requireNonNull(action, "Action cannot be null").trim();
//
//        if (this.actor.isEmpty()) {
//            throw new IllegalArgumentException("Actor cannot be empty");
//        }
//        if (this.action.isEmpty()) {
//            throw new IllegalArgumentException("Action cannot be empty");
//        }
//    }
}
