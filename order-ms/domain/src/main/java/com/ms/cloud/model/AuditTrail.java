package com.ms.cloud.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public record AuditTrail(List<Entry> entries) {
    public static AuditTrail initial() {
        return new AuditTrail(Collections.emptyList());
    }

    public AuditTrail append(Entry entry) {
        List<Entry> newEntries = new ArrayList<>(this.entries);
        newEntries.add(entry);
        return new AuditTrail(Collections.unmodifiableList(newEntries));
    }

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
    }

//    @Value
//    public static class Entry {
//        private final Instant timestamp;
//        private final String actor; // "SYSTEM", "USER:123", "ADMIN:456"
//        private final String action;
//    }
}
