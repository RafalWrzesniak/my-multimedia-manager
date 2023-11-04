package wrzesniak.rafal.my.multimedia.manager.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.SimpleItemDtoWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.product.DefaultProductService;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductUserDetailsAbstract;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;
import wrzesniak.rafal.my.multimedia.manager.util.SimplePageRequest;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.springframework.data.domain.Sort.Direction;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@RequiredArgsConstructor
public abstract class BaseProductController<
        PRODUCT_WITH_USER_DETAILS,
        PRODUCT_USER_DETAILS extends ProductUserDetailsAbstract<PRODUCT_USER_DETAILS>,
        LIST_DETAILED_PRODUCTS,
        PRODUCT extends Product> {

    private static final String PAGE_SIZE = "20";

    private final DefaultProductService<PRODUCT_WITH_USER_DETAILS, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS, PRODUCT> defaultProductService;

    @PostMapping("/create")
    public PRODUCT_WITH_USER_DETAILS createProductFromUrl(@RequestParam String url,
                                        @RequestParam(required = false) @Size(min = 3, max = 30) String listId) {
        PRODUCT_WITH_USER_DETAILS product = defaultProductService.createFromUrl(toURL(url));
        Optional.ofNullable(listId).ifPresent(name -> defaultProductService.addProductToList(url, name));
        return product;
    }

    @GetMapping("")
    public Optional<PRODUCT_WITH_USER_DETAILS> findProductById(@RequestParam String id) {
        return defaultProductService.getById(id);
    }

    @GetMapping("/property")
    public List<PRODUCT_WITH_USER_DETAILS> findProductsByProperty(@RequestParam String listId,
                                                                  @RequestParam String propertyName,
                                                                  @RequestParam @Size(min = 2, max = 15) String value,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                                  @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                                  @RequestParam(defaultValue = "id") @Size(min = 2, max = 20) String sortKey,
                                                                  @RequestParam(defaultValue = "ASC") Direction direction) {
        SimplePageRequest pageRequest = new SimplePageRequest(page, pageSize, sortKey, direction);
        return defaultProductService.findByPropertyName(listId, propertyName, value.replaceAll("[^a-zA-Z0-9 ]", ""), pageRequest);
    }

    @GetMapping("/lastFinished")
    public List<PRODUCT_WITH_USER_DETAILS> findRecentlyFinishedProducts(@RequestParam(defaultValue = "20") Integer numberOfPositions) {
        return defaultProductService.findLastFinished(numberOfPositions);
    }

    @PostMapping("/details")
    public List<SimpleItemDtoWithUserDetails> getDetailsForSimpleItems(@RequestBody List<SimpleItem> items) {
        return defaultProductService.getDetailsForItems(items);
    }

    @PostMapping("/finish")
    public void markProductAsFinished(@RequestParam String id,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishDate) {
        defaultProductService.markProductAsFinished(id, finishDate);
    }

    @GetMapping("/list")
    public LIST_DETAILED_PRODUCTS findProductListById(@RequestParam String listId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                      @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                      @RequestParam(defaultValue = "id") String sortKey,
                                                      @RequestParam(defaultValue = "ASC") Direction direction) {
        SimplePageRequest pageRequest = new SimplePageRequest(page, pageSize, sortKey, direction);
        return defaultProductService.getListById(listId, pageRequest);
    }

    @PostMapping("/list")
    public ContentListDynamo addContentListToUser(@RequestParam String listName) {
        return defaultProductService.createContentList(listName);
    }

    @DeleteMapping("/list")
    public void removeContentList(@RequestParam String listId) {
        defaultProductService.removeContentList(listId);
    }

    @GetMapping("/list/with")
    public List<LIST_DETAILED_PRODUCTS> findListsContainingProduct(@RequestParam String productId) {
        return defaultProductService.findListsContainingProduct(productId);
    }

    @PostMapping("/list/add")
    public void addProductToContentList(@RequestParam String listId,
                                         @RequestParam String productId) {
        defaultProductService.addProductToList(productId, listId);
    }

    @DeleteMapping("/list/remove")
    public void removeProductFromList(@RequestParam String listId,
                                      @RequestParam String productId) {
        defaultProductService.removeProductFromContentList(productId, listId);
    }

}
