package com.app.inventory.repository;

import java.util.*;
import org.springframework.stereotype.Repository;
import com.app.inventory.model.Item;

@Repository
public class InventoryRepository {
    // data
    private final Map<Long, Item> items = new HashMap<>();

    // counter
    private Long counter = 1L;

    // method2 akses ke database
    public List<Item> ambilSemua(){
        return new ArrayList<>(items.values());
    }

    // simpan data
    public Item simpan(Item item){
        item.setId(counter++);
        items.put(item.getId(), item);
        return item;
    }

    public Item ambilById(Long id) {
        return items.get(id);
    }
}
