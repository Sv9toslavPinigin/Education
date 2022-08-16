package net.thumbtack.buscompany.service;

import lombok.AllArgsConstructor;
import net.thumbtack.buscompany.dto.response.BusInfoDto;
import net.thumbtack.buscompany.mapper.iface.BusMapper;
import net.thumbtack.buscompany.repository.iface.BusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BusService {

    private BusMapper busMapper;
    private BusRepository busRepository;

    public List<BusInfoDto> getAllBuses() {
        return busRepository.findAll().stream()
                .map(busMapper::busToBusInfoDto)
                .collect(Collectors.toList());
    }
}
