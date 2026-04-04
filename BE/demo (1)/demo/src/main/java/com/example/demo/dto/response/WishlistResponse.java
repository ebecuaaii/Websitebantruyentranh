package com.example.demo.dto.response;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WishlistResponse {
    private List<ProductResponse> products;
}
