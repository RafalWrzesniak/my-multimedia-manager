package wrzesniak.rafal.my.multimedia.manager.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.DefaultProductService;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toSnakeCase;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@RequiredArgsConstructor
public abstract class BaseProductController<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> {

    private static final String PAGE_SIZE = "20";

    private final DefaultProductService<PRODUCT_WITH_USER_DETAILS, PRODUCT, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS> defaultProductService;

    @PostMapping("/create")
    public PRODUCT createProductFromUrl(@RequestParam String url,
                                        @RequestParam(required = false) @Size(min = 3, max = 30) String listName) {
        PRODUCT product = defaultProductService.createFromUrl(toURL(url));
        defaultProductService.addProductToList(product, listName);
        return product;
    }

    @GetMapping("/{id}")
    public Optional<PRODUCT_WITH_USER_DETAILS> findProductById(@PathVariable long id) {
        return defaultProductService.findById(id);
    }

    @GetMapping("/property")
    public List<PRODUCT_WITH_USER_DETAILS> findProductsByProperty(@RequestParam String propertyName,
                                                                  @RequestParam @Size(min = 3, max = 15) String value,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                                  @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                                  @RequestParam(defaultValue = "id") @Size(min = 2, max = 20) String sortKey,
                                                                  @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, toSnakeCase(sortKey)));
        return defaultProductService.findByPropertyName(propertyName, value.replaceAll("[^a-zA-Z0-9 ]", ""), pageRequest);
    }

    @GetMapping("/lastFinished")
    public List<PRODUCT_WITH_USER_DETAILS> findRecentlyFinishedProducts(@RequestParam(defaultValue = "20") Integer numberOfPositions) {
        return defaultProductService.findLastFinished(numberOfPositions);
    }

    @GetMapping("/")
    public List<PRODUCT_WITH_USER_DETAILS> findAllProducts(@RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                           @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                           @RequestParam(defaultValue = "id") @Size(min = 2, max = 20) String sortKey,
                                                           @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, toSnakeCase(sortKey)));
        return defaultProductService.findAllUserProducts(pageRequest);
    }

    @PostMapping("/{id}/finish")
    public void markProductAsFinished(@PathVariable long id,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishDate) {
        defaultProductService.markProductAsFinished(id, finishDate);
    }

    @DeleteMapping("/{id}")
    public void removeProductFromDatabase(@PathVariable long id) {
        defaultProductService.removeProductFromDatabase(id);
    }

    @PostMapping("/move")
    public void moveProductsFromOneListToAnother(@RequestParam List<Long> productIds,
                                                 @RequestParam String originList,
                                                 @RequestParam String targetList,
                                                 @RequestParam boolean removeFromOriginal) {
        defaultProductService.moveProductToAnotherList(productIds, originList, targetList, removeFromOriginal);
    }

    @GetMapping("/list")
    public LIST_DETAILED_PRODUCTS findProductListByName(@RequestParam String listName,
                                                        @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                        @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                        @RequestParam(defaultValue = "id") String sortKey,
                                                        @RequestParam(defaultValue = "ASC") Sort.Direction direction) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by(direction, toSnakeCase(sortKey)));
        return defaultProductService.getContentListByName(listName, pageRequest);
    }

    @PostMapping("/list")
    public LIST_DETAILED_PRODUCTS addContentListToUser(@RequestParam String listName) {
        return defaultProductService.createContentList(listName);
    }

    @DeleteMapping("/list")
    public void removeContentList(@RequestParam String listName) {
        defaultProductService.removeContentList(listName);
    }

    @GetMapping("/list/with/{productId}")
    public List<LIST_DETAILED_PRODUCTS> findListsContainingProduct(@PathVariable long productId) {
        return defaultProductService.findListsContainingProduct(productId);
    }

    @PostMapping("/list/add")
    public void addProductToContentList(@RequestParam String listName,
                                         @RequestParam long productId) {
        defaultProductService.addProductToList(productId, listName);
    }

    @DeleteMapping("/list/remove")
    public void removeProductFromList(@RequestParam String listName,
                                      @RequestParam long productId) {
        defaultProductService.removeProductFromContentList(productId, listName);
    }

}
