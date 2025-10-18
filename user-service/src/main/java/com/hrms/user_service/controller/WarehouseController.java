package com.hrms.user_service.controller;

import com.hrms.user_service.dtos.WarehouseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class WarehouseController {

    @PostMapping("/product")
    public ResponseEntity<WarehouseDTO> addWarehouse(@Valid @RequestBody WarehouseDTO warehouseDTO){
        System.out.println(warehouseDTO);
        return new ResponseEntity<>(warehouseDTO, HttpStatus.OK);
    }
}
