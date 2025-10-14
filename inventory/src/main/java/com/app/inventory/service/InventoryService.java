package com.app.inventory.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.app.inventory.model.Item;
import com.app.inventory.repository.InventoryRepository;

@Service
public class InventoryService {
    // dependency injection
    private final InventoryRepository repo;

    // constructor
    public InventoryService(InventoryRepository repo){
        this.repo = repo;
    }

    // method >> gunakan method yang representatif dan general
    public List<Item> getAllItems() {
        return repo.ambilSemua();
    }

    // method simpan
    public Item addItem(Item item){
        return repo.simpan(item);
    }

    // method get item by id
    public Item getItemById(Long id){
        return repo.ambilById(id);
    }
}
