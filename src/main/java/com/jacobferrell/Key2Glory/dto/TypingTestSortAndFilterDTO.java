package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.OrderTypes;
import com.jacobferrell.Key2Glory.model.SortTypes;
import com.jacobferrell.Key2Glory.model.TypingTest;
import com.jacobferrell.Key2Glory.model.TypingTestType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.stream.Collectors;

public class TypingTestSortAndFilterDTO {
    private List<String> creators;
    int maxSize = 25;
    private List<TypingTestType> types;
    private SortTypes sort;
    private OrderTypes order;
    private int page;
    private int size;
    private PageRequest pageRequest;

    public TypingTestSortAndFilterDTO() {

    }

    public TypingTestSortAndFilterDTO(String creators, String types, SortTypes sort, OrderTypes order, int page, int size) {
        this.creators = parseCreators(creators);
        this.types = parseTypes(types);
        this.sort = sort;
        this.order = sort != null && order == null ? OrderTypes.DESC : order;
        this.page = page;
        this.size = Math.min(size, maxSize);
    }

    private List<String> parseCreators(String creators) {
        if (creators == null) {
            return null;
        }
        return List.of(creators.split(","));
    }
    private List<TypingTestType> parseTypes(String types) {
        if (types == null) {
            return null;
        }
        List<String> separated = List.of(types.split(","));
        return separated.stream()
                .map(TypingTestType::valueOf) // Convert each string to an enum value
                .collect(Collectors.toList());
    }

    public List<String> getCreators() {
        return creators;
    }

    public List<TypingTestType> getTypes() {
        return types;
    }


    public SortTypes getSort() {
        return sort;
    }
    public PageRequest getPageRequest() {
        return PageRequest.of(page, size);
    }

    public OrderTypes getOrder() {
        return order;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }
    public boolean hasFilters() {
        return creators != null || types != null;
    }
    public boolean hasSort() {
        return sort != null;
    }
    public Specification<TypingTest> createSpecification() {
        return (root, query, criteriaBuilder) -> {
            // Initialize the predicate
            Predicate predicate = criteriaBuilder.conjunction();

            // Add filter conditions

            if (creators != null && !creators.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("createdBy").in(creators));
            }

            if (types != null && !types.isEmpty()) {
                predicate = criteriaBuilder.and(predicate, root.get("type").in(types));
            }

            // Add sort conditions
            if (sort == null) {
                return predicate;
            }
            var sortBy = root.get(getSortString(sort));
            var orderBy = order == OrderTypes.ASC ? criteriaBuilder.asc(sortBy) : criteriaBuilder.desc(sortBy);
            query.orderBy(orderBy);
            return predicate;
        };
    }
    private String getSortString(SortTypes sort) {
        return switch (sort) {
            case RATING -> "rating";
            case CREATED -> "createdAt";
            case COMPLETED -> "scoresCount";
            case LENGTH -> "type";
            default -> null;
        };
    }


}
