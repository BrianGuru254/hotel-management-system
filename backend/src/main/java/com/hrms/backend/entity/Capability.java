package com.hrms.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "capabilities")
@Getter
@Setter
public class Capability {
    @Id
    @Column(name = "capability_key", length = 100)
    private String capabilityKey;

    @Column(nullable = false, length = 300)
    private String description;
}
