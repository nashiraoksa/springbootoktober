package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    
    // handle request URL/client
    // getmapping adalah anotasi yang digunakan untuk menyesuaikan url/path yang kita gunakan
    @GetMapping("/cek_demo")
    public String cekDemo(){
        return "HALOO DARI SPRINGBOOT!!!!";
    }
}
