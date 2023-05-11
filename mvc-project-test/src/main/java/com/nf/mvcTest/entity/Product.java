package com.nf.mvcTest.entity;

import com.nf.mvc.file.MultipartFile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Product {
    private Integer id;
    private String name;
    private String image;
    private BigDecimal price;
    private Boolean status;
    private Integer quantity;
    private Integer cid;
    private MultipartFile multipartFile;
    private String typeName;
}
