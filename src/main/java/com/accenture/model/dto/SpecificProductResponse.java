package com.accenture.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpecificProductResponse {
    private Long product_id;
    private String product_name;
    private Integer stock;
    private String branch_name;
}
