package com.jacobferrell.Key2Glory.dto;

import com.jacobferrell.Key2Glory.model.*;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TypingTestSortAndFilterDTO {
    public static final int MAX_SIZE = 50;
    public static final int MAX_QUERY_LENGTH = 100;

    private final List<String> creators;
    private final List<TypingTestType> types;
    private final SortTypes sort;
    private final OrderTypes order;
    private final MineFilterTypes mine;
    private final String query;
    private final String username;
    private final int page;
    private final int size;

    public TypingTestSortAndFilterDTO(String creators, String types, SortTypes sort, OrderTypes order,
                                      MineFilterTypes mine, String query, String username, int page, int size) {
        this.creators = splitParam(creators);
        this.types = parseTypes(types);
        this.sort = sort == null ? SortTypes.CREATED : sort;
        this.order = order == null ? OrderTypes.DESC : order;
        this.mine = mine;
        this.query = parseQuery(query);
        this.username = username;
        this.page = Math.max(page, 0);
        this.size = Math.max(1, Math.min(size, MAX_SIZE));
    }

    private static List<String> splitParam(String param) {
        if (param == null || param.isBlank()) {
            return null;
        }
        return Arrays.stream(param.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static List<TypingTestType> parseTypes(String types) {
        var separated = splitParam(types);
        if (separated == null) {
            return null;
        }
        return separated.stream().map(type -> {
            try {
                return TypingTestType.valueOf(type);
            } catch (IllegalArgumentException e) {
                // Handled as a 400 by GlobalErrorHandler
                throw new IllegalArgumentException("Unknown test type: " + type);
            }
        }).toList();
    }

    private static String parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return null;
        }
        String trimmed = query.trim();
        return trimmed.length() > MAX_QUERY_LENGTH ? trimmed.substring(0, MAX_QUERY_LENGTH) : trimmed;
    }

    public PageRequest getPageRequest() {
        return PageRequest.of(page, size);
    }

    public Specification<TypingTest> createSpecification() {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (creators != null && !creators.isEmpty()) {
                predicates.add(root.get("createdBy").in(creators));
            }
            if (types != null && !types.isEmpty()) {
                predicates.add(root.get("type").in(types));
            }
            if (this.query != null) {
                predicates.add(cb.like(cb.lower(root.get("text")), "%" + escapeLike(this.query.toLowerCase()) + "%", '\\'));
            }
            if (mine != null) {
                predicates.add(minePredicate(root, query, cb));
            }

            // Spring Data reuses the specification for the page's count query, which must not be ordered
            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(getOrders(root, cb));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private Predicate minePredicate(Root<TypingTest> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        if (username == null) {
            // "My tests" filters have no results when signed out
            return cb.disjunction();
        }
        return switch (mine) {
            case CREATED -> cb.equal(root.get("createdBy"), username);
            case FAVORITES -> {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Favorites> favorites = sub.from(Favorites.class);
                Join<Favorites, TypingTest> test = favorites.join("favorites");
                sub.select(test.get("id")).where(cb.equal(favorites.get("username"), username));
                yield root.get("id").in(sub);
            }
            case COMPLETED -> {
                Subquery<Long> sub = query.subquery(Long.class);
                Root<Score> score = sub.from(Score.class);
                sub.select(score.get("typingTest").get("id")).where(cb.equal(score.get("username"), username));
                yield root.get("id").in(sub);
            }
        };
    }

    private List<Order> getOrders(Root<TypingTest> root, CriteriaBuilder cb) {
        // Coalesce so unrated / never completed tests sort as zero instead of NULLS FIRST in Postgres
        Expression<?> sortBy = switch (sort) {
            case RATING -> cb.coalesce(root.<Double>get("rating"), 0.0);
            case COMPLETED -> cb.coalesce(root.<Long>get("scoresCount"), 0L);
            case LENGTH -> cb.coalesce(root.<Long>get("length"), 0L);
            case CREATED -> root.get("createdAt");
        };
        boolean ascending = order == OrderTypes.ASC;
        // Tie-break on id so pages are stable
        return List.of(
                ascending ? cb.asc(sortBy) : cb.desc(sortBy),
                ascending ? cb.asc(root.get("id")) : cb.desc(root.get("id")));
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }
}
