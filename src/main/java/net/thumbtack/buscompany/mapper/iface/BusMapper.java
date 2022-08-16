package net.thumbtack.buscompany.mapper.iface;

import net.thumbtack.buscompany.dto.response.BusInfoDto;
import net.thumbtack.buscompany.model.Bus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(source = "busName", target = "busName")
    BusInfoDto busToBusInfoDto(Bus bus);

    Bus busInfoDtoToBus(BusInfoDto busInfoDto);

}

