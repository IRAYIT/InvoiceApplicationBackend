package com.ikonicit.invoice.entity;


/**
 * Mirrors the four order statuses documented on fakturan.nu's Order module:
 * NOT_STARTED (Ej påbörjad), STARTED (Påbörjad), COMPLETED (Färdigbehandlad),
 * CANCELLED (Avbruten).
 *
 * NOT_STARTED  — order created, no updates yet.
 * STARTED      — picking has begun but items remain to be picked
 *                (can also be set manually).
 * COMPLETED    — set manually, or automatically once all ordered
 *                quantities have been delivered with no back-order left.
 * CANCELLED    — order can't be fulfilled (stock issue, customer cancelled, etc).
 */
public enum OrderStatus {
    NOT_STARTED,
    STARTED,
    COMPLETED,
    CANCELLED
}