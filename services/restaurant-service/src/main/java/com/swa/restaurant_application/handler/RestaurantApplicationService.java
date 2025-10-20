package com.swa.restaurant_application.handler;

import org.springframework.stereotype.Service;

import com.swa.restaurant_application.ports.input.IRestaurantApplicationService;
import com.swa.restaurant_application.ports.output.IRestaurantRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantApplicationService implements IRestaurantApplicationService {
    private final IRestaurantRepository _restaurantRepository;
}
