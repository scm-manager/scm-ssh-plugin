package com.cloudogu.scm.ssh;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@Getter
@Setter
@XmlRootElement(name = "ssh-config")
@XmlAccessorType(XmlAccessType.FIELD)
@ToString
public class Configuration {
  private String hostName;
  private int port = 2222;
}
