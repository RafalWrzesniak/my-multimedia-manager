package wrzesniak.rafal.my.multimedia.manager.controller;


import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import wrzesniak.rafal.my.multimedia.manager.domain.content.ContentListDynamo;
import wrzesniak.rafal.my.multimedia.manager.domain.dto.SimpleItemDtoWithUserDetails;
import wrzesniak.rafal.my.multimedia.manager.domain.product.DefaultProductService;
import wrzesniak.rafal.my.multimedia.manager.domain.product.Product;
import wrzesniak.rafal.my.multimedia.manager.domain.product.ProductUserDetailsAbstract;
import wrzesniak.rafal.my.multimedia.manager.domain.product.SimpleItem;
import wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder;
import wrzesniak.rafal.my.multimedia.manager.util.SimplePageRequest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static wrzesniak.rafal.my.multimedia.manager.util.JwtTokenDecoder.TOKEN_HEADER;
import static wrzesniak.rafal.my.multimedia.manager.util.StringFunctions.toURL;

@RequiredArgsConstructor
public abstract class BaseProductController<
        PRODUCT_WITH_USER_DETAILS,
        PRODUCT_USER_DETAILS extends ProductUserDetailsAbstract<PRODUCT_USER_DETAILS>,
        LIST_DETAILED_PRODUCTS,
        PRODUCT extends Product> {

    private static final String PAGE_SIZE = "36";

    private final DefaultProductService<PRODUCT_WITH_USER_DETAILS, PRODUCT_USER_DETAILS, LIST_DETAILED_PRODUCTS, PRODUCT> defaultProductService;
    private final JwtTokenDecoder jwtTokenDecoder;

    @PostMapping("/create")
    public PRODUCT_WITH_USER_DETAILS createProductFromUrl(@RequestParam String url,
                                                          @RequestParam(required = false) @Size(min = 3, max = 50) String listId,
                                                          @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        PRODUCT_WITH_USER_DETAILS product = defaultProductService.createFromUrl(toURL(url), username);
        Optional.ofNullable(listId).ifPresent(listName -> defaultProductService.addProductToList(toURL(url).toString(), listName, username));
        return product;
    }

    @GetMapping("")
    public Optional<PRODUCT_WITH_USER_DETAILS> findProductById(@RequestParam String id,
                                                               @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return defaultProductService.getById(id, username);
    }

    @GetMapping("/property")
    public List<PRODUCT_WITH_USER_DETAILS> findProductsByProperty(@RequestParam String listId,
                                                                  @RequestParam String propertyName,
                                                                  @RequestParam @Size(min = 2, max = 15) String value,
                                                                  @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                                  @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                                  @RequestParam(defaultValue = "id") @Size(min = 2, max = 20) String sortKey,
                                                                  @RequestParam(defaultValue = "ASC") String direction,
                                                                  @RequestHeader(TOKEN_HEADER) String jwtToken) {
        SimplePageRequest pageRequest = new SimplePageRequest(page, pageSize, sortKey, direction);
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return defaultProductService.findByPropertyName(listId, propertyName, value.replaceAll("[^a-zA-Z0-9 ]", ""), pageRequest, username);
    }

    @GetMapping("/lastFinished")
    public List<PRODUCT_WITH_USER_DETAILS> findRecentlyFinishedProducts(@RequestParam(defaultValue = "30") Integer numberOfPositions,
                                                                        @RequestParam String productType,
                                                                        @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return defaultProductService.findLastFinished(numberOfPositions, username, productType);
    }

    @PostMapping("/details")
    public List<SimpleItemDtoWithUserDetails> getDetailsForSimpleItems(@RequestBody List<SimpleItem> items,
                                                                       @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return defaultProductService.getDetailsForItems(items, username);
    }

    @PostMapping("/finish")
    public void markProductAsFinished(@RequestParam String id,
                                      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate finishDate,
                                      @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        defaultProductService.markProductAsFinished(id, finishDate, username);
    }

    @GetMapping("/list")
    public LIST_DETAILED_PRODUCTS findProductListById(@RequestParam String listId,
                                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer page,
                                                      @RequestParam(defaultValue = PAGE_SIZE) @PositiveOrZero Integer pageSize,
                                                      @RequestParam(defaultValue = "id") String sortKey,
                                                      @RequestParam(defaultValue = "ASC") String direction,
                                                      @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        SimplePageRequest pageRequest = new SimplePageRequest(page, pageSize, sortKey, direction);
        return defaultProductService.getListById(listId, pageRequest, username);
    }

    @PostMapping("/list")
    public ContentListDynamo addContentListToUser(@RequestParam String listName,
                                                  @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return defaultProductService.createContentList(listName, username);
    }

    @DeleteMapping("/list")
    public void removeContentList(@RequestParam String listId,
                                  @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        defaultProductService.removeContentList(listId, username);
    }

    @GetMapping("/list/with")
    public List<LIST_DETAILED_PRODUCTS> findListsContainingProduct(@RequestParam String productId,
                                                                   @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        return defaultProductService.findListsContainingProduct(String.valueOf(toURL(productId)), username);
    }

    @PostMapping("/list/add")
    public void addProductToContentList(@RequestParam String listId,
                                        @RequestParam String productId,
                                        @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        defaultProductService.addProductToList(String.valueOf(toURL(productId)), listId, username);
    }

    @DeleteMapping("/list/remove")
    public void removeProductFromList(@RequestParam String listId,
                                      @RequestParam String productId,
                                      @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        defaultProductService.removeProductFromContentList(String.valueOf(toURL(productId)), listId, username);
    }

    @PostMapping("/list/rename")
    public void renameProductList(@RequestParam String listId,
                                  @RequestParam String newListName,
                                  @RequestHeader(TOKEN_HEADER) String jwtToken) {
        String username = jwtTokenDecoder.parseUsernameFromAuthorizationHeader(jwtToken);
        defaultProductService.renameList(listId, newListName, username);
    }
}
