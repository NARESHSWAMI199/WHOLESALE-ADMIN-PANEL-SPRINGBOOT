package com.sales.admin.repositories;


import com.sales.entities.Contact;
import com.sales.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer> {

    @Query("select contact from Contact where userId=:userId")
   List<User> getContactByUserId(Integer userId);

}
