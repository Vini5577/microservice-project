package br.com.vini.orderserviceapi.services.impl;

import br.com.vini.orderserviceapi.mapper.OrderMapper;
import br.com.vini.orderserviceapi.repositories.OrderRepository;
import br.com.vini.orderserviceapi.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import models.requests.CreateOrderRequest;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class OrderServiceImpl implements OrderService {

    private final OrderRepository repository;

    private final OrderMapper mapper;

    @Override
    public void save(CreateOrderRequest request) {
        final var entity = repository.save(mapper.fromRequest(request));
        log.info("Order created: {}", entity);
    }
}
