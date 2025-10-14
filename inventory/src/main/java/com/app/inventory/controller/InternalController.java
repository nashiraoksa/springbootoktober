package com.app.inventory.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.app.inventory.model.Item;
import com.app.inventory.service.InventoryService;

// anotasi rest controller agar dapat dibaca/dikenali oleh main program
@RestController
// url default
@RequestMapping("/api/internal")
public class InternalController {
    // service
    public final InventoryService service;

    public InternalController(InventoryService service){
        this.service = service;
    }

    // get data inventory
    @GetMapping
    public List<Item> getInventory(){
        return service.getAllItems();
    }
    // public String getInventory(){
    //     return "Selamat Datang di Aplikasi Inventory";
    // }

    // post data inventory
    @PostMapping
    public Item addItem(@RequestBody Item item){
        return service.addItem(item);
    }

    // getById
    @GetMapping("/{id}")
    public Item getById(@PathVariable Long id){
        return service.getItemById(id);
    }
}
