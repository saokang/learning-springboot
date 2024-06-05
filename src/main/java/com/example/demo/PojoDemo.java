package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true, fluent = true)
public class PojoDemo {

    private Integer id;
    private String username;
    private String password;

}
