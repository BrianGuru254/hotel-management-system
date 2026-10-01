package com.hrms.backend.repository;

import com.hrms.backend.entity.Guest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface GuestRepository extends JpaRepository<Guest, UUID> {
    @Query(value = """
            select guest from Guest guest
            where guest.property.id in :propertyIds
              and (:propertyId is null or guest.property.id = :propertyId)
              and (:name is null or lower(concat(guest.firstName, ' ', coalesce(guest.middleName, ''), ' ', guest.lastName))
                   like concat('%', lower(:name), '%'))
              and (:phone is null or lower(guest.phone) like concat('%', lower(:phone), '%'))
              and (:email is null or lower(guest.email) like concat('%', lower(:email), '%'))
              and (:guestNumber is null or lower(guest.guestNumber) like concat('%', lower(:guestNumber), '%'))
              and (:identificationNumber is null or lower(guest.identificationNumber)
                   like concat('%', lower(:identificationNumber), '%'))
            """,
            countQuery = """
                    select count(guest) from Guest guest
                    where guest.property.id in :propertyIds
                      and (:propertyId is null or guest.property.id = :propertyId)
                      and (:name is null or lower(concat(guest.firstName, ' ', coalesce(guest.middleName, ''), ' ', guest.lastName))
                           like concat('%', lower(:name), '%'))
                      and (:phone is null or lower(guest.phone) like concat('%', lower(:phone), '%'))
                      and (:email is null or lower(guest.email) like concat('%', lower(:email), '%'))
                      and (:guestNumber is null or lower(guest.guestNumber) like concat('%', lower(:guestNumber), '%'))
                      and (:identificationNumber is null or lower(guest.identificationNumber)
                           like concat('%', lower(:identificationNumber), '%'))
                    """)
    Page<Guest> searchInProperties(
            @Param("propertyIds") Collection<UUID> propertyIds,
            @Param("propertyId") UUID propertyId,
            @Param("name") String name,
            @Param("phone") String phone,
            @Param("email") String email,
            @Param("guestNumber") String guestNumber,
            @Param("identificationNumber") String identificationNumber,
            Pageable pageable);

    boolean existsByPropertyIdAndIdentificationTypeIgnoreCaseAndIdentificationNumberIgnoreCase(
            UUID propertyId, String identificationType, String identificationNumber);

    boolean existsByPropertyIdAndIdentificationTypeIgnoreCaseAndIdentificationNumberIgnoreCaseAndIdNot(
            UUID propertyId, String identificationType, String identificationNumber, UUID id);
}
