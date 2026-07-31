package com.startupforge.auth.entity;

/**
 * User roles. Only USER is ever assigned or checked against in this phase.
 * ADMIN is modeled and carried as a JWT claim so the mechanism is real and
 * demonstrable, but no @PreAuthorize / authorization gate exists yet because
 * no admin-only operation exists anywhere in the current roadmap. Adding one
 * later is a small change (annotate the endpoint), not new infrastructure.
 */
public enum Role {
    USER,
    ADMIN
}
