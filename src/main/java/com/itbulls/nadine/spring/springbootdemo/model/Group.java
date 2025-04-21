
//Group.java
package com.itbulls.nadine.spring.springbootdemo.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "groups")
public class Group {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 private String name;

 @OneToMany(mappedBy = "group")
 private List<User> users;

 public Group() {}

 public Group(String name) {
     this.name = name;
 }

 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public String getName() {
     return name;
 }

 public void setName(String name) {
     this.name = name;
 }

 public List<User> getUsers() {
     return users;
 }

 public void setUsers(List<User> users) {
     this.users = users;
 }
}