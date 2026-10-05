package com.hrms.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "stays", indexes = {
        @Index(name = "idx_stays_property_status", columnList = "property_id,status"),
        @Index(name = "idx_stays_property_checkout", columnList = "property_id,expected_check_out_date"),
        @Index(name = "idx_stays_guest_id", columnList = "guest_id"),
        @Index(name = "idx_stays_room_id", columnList = "room_id")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uq_stays_property_scope", columnNames = {"property_id", "id"}),
        @UniqueConstraint(name = "uq_stays_active_room", columnNames = {"active_room_id"})
})
@Getter
@Setter
public class Stay {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "stay_number", nullable = false, length = 50)
    private String stayNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "property_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stays_property"))
    private Property property;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", foreignKey = @ForeignKey(name = "fk_stays_reservation"))
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "guest_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stays_guest"))
    private Guest guest;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "room_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stays_room"))
    private Room room;

    @Column(name = "active_room_id")
    private UUID activeRoomId;

    @Column(name = "actual_check_in", nullable = false)
    private OffsetDateTime actualCheckIn;

    @Column(name = "expected_check_out_date", nullable = false)
    private LocalDate expectedCheckOutDate;

    @Column(name = "actual_check_out")
    private OffsetDateTime actualCheckOut;

    @Column(nullable = false)
    private Integer adults = 1;

    @Column(nullable = false)
    private Integer children = 0;

    @Column(name = "nightly_rate", nullable = false, precision = 12, scale = 2)
    private BigDecimal nightlyRate;

    @Column(name = "deposit_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal depositAmount = BigDecimal.ZERO;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "currency_code", nullable = false, length = 3, columnDefinition = "char(3)")
    private String currencyCode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StayStatus status = StayStatus.ACTIVE;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name = "checkout_notes", columnDefinition = "text")
    private String checkoutNotes;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "checked_in_by", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stays_checked_in_by"))
    private HmsUser checkedInBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checked_out_by", foreignKey = @ForeignKey(name = "fk_stays_checked_out_by"))
    private HmsUser checkedOutBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
