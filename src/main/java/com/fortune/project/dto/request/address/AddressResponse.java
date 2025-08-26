package com.fortune.project.dto.request.address;

import com.fortune.project.entity.AddressEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressResponse {
    private Long id;
    private String fullName;
    private String phoneNumber;
    private String streetAddress;
    private String ward;
    private String city;
    private Boolean isDefault;

    public static AddressResponse from(AddressEntity entity) {
        return new AddressResponse(
                entity.getId(),
                entity.getFullName(),
                entity.getPhoneNumber(),
                entity.getStreetAddress(),
                entity.getWard(),
                entity.getCity(),
                entity.getIsDefault()
        );
    }
}
