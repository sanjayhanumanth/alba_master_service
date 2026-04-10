package com.alba.master.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class PaginationResponse <T>{

    private List<T> data;
    private int currentPage;
    private int totalPage;
    private long totalElement;
    private boolean hasNext;
    private boolean hasPrevious;
}
