package kitchenpos.menus.infra;

import kitchenpos.menus.domain.menu.MenuPrice;
import kitchenpos.menus.domain.menu.MenuProduct;
import kitchenpos.menus.domain.menu.ProductClient;
import kitchenpos.menus.domain.menu.ProductPrice;
import kitchenpos.products.domain.Product;
import kitchenpos.products.domain.ProductRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DefaultProductClient implements ProductClient {
    private final ProductRepository productRepository;

    public DefaultProductClient(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public ProductPrice getProductPrice(final UUID productId) {
        return productRepository.findById(productId)
                .map(Product::getPrice)
                .map(ProductPrice::of)
                .orElse(null);
    }

    @Override
    public void validateMenuProducts(final List<MenuProduct> menuProducts, final MenuPrice menuPrice) {
        if (Objects.isNull(menuProducts) || menuProducts.isEmpty()) {
            throw new IllegalArgumentException("메뉴상품이 비어 있습니다.");
        }

        final List<UUID> productIds = menuProducts.stream()
                .peek(this::validateMenuProductQuantity)
                .map(MenuProduct::getProductId)
                .collect(Collectors.toList());
        final List<Product> products = productRepository.findAllByIdIn(productIds);
        if (products.size() != menuProducts.size()) {
            throw new IllegalArgumentException("메뉴에 등록되지 않은 상품이 있습니다.");
        }

        validateMenuPrice(menuProducts, menuPrice);
    }

    @Override
    public void validateMenuPrice(final List<MenuProduct> menuProducts, final MenuPrice menuPrice) {
        BigDecimal sum = menuProducts.stream()
                .map(menuProduct -> menuProduct.getProductPrice().multiply(BigDecimal.valueOf(menuProduct.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (menuPrice.getPrice().compareTo(sum) > 0) {
            throw new IllegalArgumentException("메뉴의 가격이 메뉴에 속한 상품 금액의 합보다 큽니다.");
        }
    }

    private void validateMenuProductQuantity(final MenuProduct menuProduct) {
        if (menuProduct.getQuantity() < 0) {
            throw new IllegalArgumentException("메뉴 상품의 수량이 0보다 작습니다.");
        }
    }


}
