package bmstu.student.rybkin.lab2.hotelbookingsystem.reservationapplication.models;

import java.util.ArrayList;
import java.util.List;

public class PaginationResponse {

    private final Integer page;
    private final Integer pageSize;
    private final Integer totalElements;
    private final List<HotelResponse> items;

    public Integer getPage() {

        return this.page;

    }

    public Integer getPageSize() {

        return this.pageSize;

    }

    public Integer getTotalElements() {

        return this.totalElements;

    }

    public List<HotelResponse> getItems() {

        return this.items;

    }

    public PaginationResponse(Integer page, Integer pageSize, Integer totalElements, List<HotelResponse> items) {
        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.items = items;
    }

    public PaginationResponse(PaginationResponse paginationResponse) {

        this.page = paginationResponse.page;
        this.pageSize = paginationResponse.pageSize;
        this.totalElements = paginationResponse.totalElements;
        this.items = new ArrayList<>(paginationResponse.items);

    }


}
