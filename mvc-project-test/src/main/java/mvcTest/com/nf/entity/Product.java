package mvcTest.com.nf.entity;

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
    private byte status;
    private Integer quantity;
    private Integer cid;
}
