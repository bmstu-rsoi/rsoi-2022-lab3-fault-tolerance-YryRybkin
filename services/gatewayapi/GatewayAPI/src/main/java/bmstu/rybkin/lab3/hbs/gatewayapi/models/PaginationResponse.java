package bmstu.rybkin.lab3.hbs.gatewayapi.models;

import java.util.Arrays;
import java.util.Objects;

public class PaginationResponse {

    private final Integer page;
    private final Integer pageSize;
    private final Integer totalElements;
    private final HotelResponse[] items;

    public Integer getPage() {

        return this.page;

    }

    public Integer getPageSize() {

        return this.pageSize;

    }

    public Integer getTotalElements() {

        return this.totalElements;

    }

    public HotelResponse[] getItems() {

        return this.items;

    }

    public PaginationResponse(Integer page, Integer pageSize, Integer totalElements, HotelResponse[] items) {

        this.page = page;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.items = items.clone();

    }

    public PaginationResponse(PaginationResponse paginationResponse) {

        this.page = paginationResponse.page;
        this.pageSize = paginationResponse.pageSize;
        this.totalElements = paginationResponse.totalElements;
        this.items = paginationResponse.items.clone();

    }

    public boolean equals(Object comp) {

        if (this == comp) {

            return true;

        }

        if (!(comp instanceof PaginationResponse)) {

            return false;

        }

        PaginationResponse paginationResponse = (PaginationResponse) comp;

        return Objects.equals(this.page, paginationResponse.page) &&
                Objects.equals(this.pageSize, paginationResponse.pageSize) &&
                Objects.equals(this.totalElements, paginationResponse.totalElements) &&
                Arrays.equals(this.items, paginationResponse.items);

    }

    @Override
    public int hashCode() {

        int result = Objects.hash(this.page, this.pageSize, this.totalElements);
        result = 31 * result + Arrays.hashCode(this.items);
        return result;

    }

}
