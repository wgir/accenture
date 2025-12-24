package com.accenture.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "franchises")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Franchise {
    @Id
    private Long id;

    private String name;
}
