package kitchenpos.menus.tobe.domain;

import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import kitchenpos.products.tobe.domain.DisplayNameValidator;

@Embeddable
public class DisplayedName {

  @Column(name = "name", nullable = false)
  private String name;

  public static DisplayedName from(String name) {
    return new DisplayedName(name);
  }

  public static DisplayedName from(String name, DisplayNameValidator validator) {
    validator.validate(name);
    return new DisplayedName(name);
  }
  protected DisplayedName() {

  }

  private DisplayedName(String name) {
    if (Objects.isNull(name) || name.isBlank()) {
      throw new IllegalArgumentException();
    }
    this.name = name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    DisplayedName that = (DisplayedName) o;
    return Objects.equals(name, that.name);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name);
  }
}
