package com.hrms.backend.repository;

import com.hrms.backend.entity.Amenity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.UUID;

public interface AmenityRepository extends JpaRepository<Amenity, UUID> {
    @Query(value = """
            select amenity from Amenity amenity
            where amenity.property.id in :propertyIds
              and (:propertyId is null or amenity.property.id = :propertyId)
              and (:name is null or lower(amenity.name) like concat('%', lower(:name), '%'))
            """,
            countQuery = """
                    select count(amenity) from Amenity amenity
                    where amenity.property.id in :propertyIds
                      and (:propertyId is null or amenity.property.id = :propertyId)
                      and (:name is null or lower(amenity.name) like concat('%', lower(:name), '%'))
                    """)
    Page<Amenity> searchInProperties(
            @Param("propertyIds") Collection<UUID> propertyIds,
            @Param("propertyId") UUID propertyId,
            @Param("name") String name,
            Pageable pageable);

    boolean existsByPropertyIdAndNameIgnoreCase(UUID propertyId, String name);

    boolean existsByPropertyIdAndNameIgnoreCaseAndIdNot(UUID propertyId, String name, UUID id);
}
