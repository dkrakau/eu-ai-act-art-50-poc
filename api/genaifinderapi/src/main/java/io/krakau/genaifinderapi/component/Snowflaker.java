package io.krakau.genaifinderapi.component;

import org.springframework.stereotype.Component;

/**
 *
 * @author Dominik
 */
@Component
public class Snowflaker {

    private final long EPOCH = 1609459200000L; // Custom epoch (2021-01-01)
    private final long NODE_ID_BITS = 10L;
    private final long SEQUENCE_BITS = 12L;

    private final long MAX_NODE_ID = (1L << NODE_ID_BITS) - 1;
    private final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;

    private final long NODE_ID_SHIFT = SEQUENCE_BITS;
    private final long TIMESTAMP_SHIFT = NODE_ID_BITS + SEQUENCE_BITS;

    private final long nodeId = 1L; // Should be configurable in production
    private long lastTimestamp = -1L;
    private long sequence = 0L;

    public Snowflaker() {

    }

    public synchronized long id() {
        long timestamp = System.currentTimeMillis() - EPOCH;

        // Handle clock going backwards
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate ID.");
        }

        // If we're generating IDs in the same millisecond
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            // Sequence overflow in the same millisecond
            if (sequence == 0) {
                // Wait until next millisecond
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0;
        }

        lastTimestamp = timestamp;

        // Compose the ID from timestamp, nodeId, and sequence
        return (timestamp << TIMESTAMP_SHIFT)
                | (nodeId << NODE_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis() - EPOCH;
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis() - EPOCH;
        }
        return timestamp;
    }

}
