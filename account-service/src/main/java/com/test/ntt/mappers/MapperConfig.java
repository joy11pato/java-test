package com.test.ntt.mappers;

import com.test.ntt.models.dtos.AccountDto;
import com.test.ntt.models.dtos.MovementDto;
import com.test.ntt.models.dtos.MovementsReportsDto;
import com.test.ntt.models.entities.Account;
import com.test.ntt.models.entities.Movement;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MapperConfig {

    MapperConfig mapper = Mappers.getMapper(MapperConfig.class);

    @Mapping(source = "accountType.value", target = "accountType")
    AccountDto toAccountDto(Account account);

    @Mapping(source = "movementType.value", target = "movementType")
    MovementDto toMovementDto (Movement movement);

//    MovementsReportsDto toMovReportDto ()
}
