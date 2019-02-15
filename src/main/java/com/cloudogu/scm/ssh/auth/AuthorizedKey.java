package com.cloudogu.scm.ssh.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sonia.scm.xml.XmlInstantAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.Instant;
import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@XmlAccessorType(XmlAccessType.FIELD)
class AuthorizedKey {
  private String id;
  private String displayName;
  private String raw;

  @XmlJavaTypeAdapter(XmlInstantAdapter.class)
  private Instant created;

  AuthorizedKey(String id) {
    this.id = id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    AuthorizedKey that = (AuthorizedKey) o;
    return Objects.equals(id, that.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
