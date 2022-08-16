package net.thumbtack.buscompany.mapper.classes;

import net.thumbtack.buscompany.dto.response.BusInfoDto;
import net.thumbtack.buscompany.model.Bus;
import net.thumbtack.buscompany.repository.iface.BusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BusFromRepositoryMapper {

    @Autowired
    BusRepository busRepository;

    public Bus asBus(String busName) {
        return busRepository.findByBusName(busName);
    }

    public BusInfoDto asBusInfoDto(Bus bus) {
        return new BusInfoDto(bus.getBusName(), bus.getPlaceCount());
    }
}
