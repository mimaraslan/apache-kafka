package com.loloia.model;

import java.io.Serializable;
import java.util.Date;


public class Person implements Serializable {

	private static final long serialVersionUID = 1L;
private Integer id;
  private String name;
  private Date birthDate;

  public static Person newInstance(Integer id, String name, Date birthDate) {
      Person person = new Person();
      person.setId(id);
      person.setName(name);
      person.setBirthDate(birthDate);
      return person;
  }

  public Integer getId() {
      return id;
  }

  public void setId(Integer id) {
      this.id = id;
  }

  public String getName() {
      return name;
  }

  public void setName(String name) {
      this.name = name;
  }

  public Date getBirthDate() {
      return birthDate;
  }

  public void setBirthDate(Date birthDate) {
      this.birthDate = birthDate;
  }

  @Override
  public String toString() {
  	return id+ " " + name + " "+ birthDate;
  }
}