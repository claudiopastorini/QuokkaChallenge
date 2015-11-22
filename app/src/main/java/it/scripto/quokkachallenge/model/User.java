/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Claudio Pastorini
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package it.scripto.quokkachallenge.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("picture")
    @Expose
    private String picture;

    @SerializedName("friends")
    @Expose
    private List<Object> friends = new ArrayList<>();

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("registered")
    @Expose
    private String registered;

    @SerializedName("name")
    @Expose
    private List<Name> nameList = new ArrayList<>();

    @SerializedName("age")
    @Expose
    private Integer age;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public List<Object> getFriends() {
        return friends;
    }

    public void setFriends(List<Object> friends) {
        this.friends = friends;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRegistered() {
        return registered;
    }

    public void setRegistered(String registered) {
        this.registered = registered;
    }

    public String getName() {
        Name name = nameList.get(0);
        return String.format("%s %s", name.getFirstName(), name.getLastName());
    }

    public String getFirstName() {
        Name name = nameList.get(0);
        return name.getFirstName();
    }

    public void setFirstName(String firstName) {
        if (this.nameList.size() == 0) {
            Name name = new Name();
            name.setFirstName(firstName);

            this.nameList.add(name);
        } else {
            this.nameList.get(0).setFirstName(firstName);
        }
    }

    public String getLastName() {
        Name name = nameList.get(0);
        return name.getLastName();
    }

    public void setLastName(String lastName) {
        if (this.nameList.size() == 0) {
            Name name = new Name();
            name.setLastName(lastName);

            this.nameList.add(name);
        } else {
            this.nameList.get(0).setLastName(lastName);
        }
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                ", friends=" + friends +
                ", description='" + description + '\'' +
                ", registered='" + registered + '\'' +
                ", nameList=" + nameList +
                ", age=" + age +
                '}';
    }
}

class Name {

    @SerializedName("first")
    @Expose
    private String firstName;

    @SerializedName("last")
    @Expose
    private String lastName;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Override
    public String toString() {
        return "Name{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }
}