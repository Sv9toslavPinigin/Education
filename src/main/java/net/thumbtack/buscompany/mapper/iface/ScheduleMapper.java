package net.thumbtack.buscompany.mapper.iface;

import net.thumbtack.buscompany.dto.request.ScheduleDto;
import net.thumbtack.buscompany.mapper.classes.DateMapper;
import net.thumbtack.buscompany.model.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = DateMapper.class)
public interface ScheduleMapper {

    ScheduleMapper INSTANCE = Mappers.getMapper(ScheduleMapper.class);

    Schedule fromScheduleDto(ScheduleDto scheduleDto);
}
